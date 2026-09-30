package com.artms.payment.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * eSewa V2 Payment Gateway Adapter.
 *
 * Flow:
 *   1. Client calls initiate-payment API → backend creates PaymentRequest (form fields + gateway URL)
 *   2. Flutter opens eSewa in WebView/browser using those form fields
 *   3. On completion, eSewa redirects to success/failure URL with encoded_data parameter
 *   4. Backend verifies with eSewa transaction status API using transaction_uuid
 *
 * Test credentials (sandbox only):
 *   eSewa ID: 9711111111 / Password: Test@123 / MPIN: 1122
 *   Merchant Service Code: EPAYTEST
 *   Secret Key: 8gBm/:&EnhH.1/q
 *
 * IMPORTANT: All secrets come from environment/config – NEVER from client.
 *
 * Reference: https://developer.esewa.com.np/pages/Epay
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EsewaPaymentGateway implements PaymentGateway {

    private static final String GATEWAY_CODE = "ESEWA";
    private static final String ESEWA_SANDBOX_URL  = "https://rc-epay.esewa.com.np/api/epay/main/v2/form";
    private static final String ESEWA_PROD_URL      = "https://epay.esewa.com.np/api/epay/main/v2/form";
    private static final String ESEWA_SANDBOX_VERIFY = "https://rc.esewa.com.np/api/epay/transaction/status/";
    private static final String ESEWA_PROD_VERIFY    = "https://epay.esewa.com.np/api/epay/transaction/status/";

    @Value("${esewa.environment:SANDBOX}")
    private String esewaEnvironment;

    @Value("${esewa.merchant-id:EPAYTEST}")
    private String merchantId;

    @Value("${esewa.secret-key:8gBm/:&EnhH.1/q}")
    private String secretKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String getCode() {
        return GATEWAY_CODE;
    }

    /**
     * Build the eSewa V2 payment form fields.
     * Signature = HMAC-SHA256 of "total_amount,transaction_uuid,product_code"
     */
    @Override
    public PaymentRequest createPaymentRequest(PaymentInitParams params) {
        String transactionUuid = params.transactionRef();
        String totalAmount     = params.amount().toPlainString();
        String taxAmount       = "0";
        String productServiceCharge = "0";
        String productDeliveryCharge = "0";

        String signatureMessage = "total_amount=" + totalAmount +
            ",transaction_uuid=" + transactionUuid +
            ",product_code=" + merchantId;
        String signature = generateHmacSignature(signatureMessage);

        Map<String, String> formFields = new LinkedHashMap<>();
        formFields.put("amount",                   totalAmount);
        formFields.put("tax_amount",               taxAmount);
        formFields.put("total_amount",             totalAmount);
        formFields.put("transaction_uuid",         transactionUuid);
        formFields.put("product_code",             merchantId);
        formFields.put("product_service_charge",   productServiceCharge);
        formFields.put("product_delivery_charge",  productDeliveryCharge);
        formFields.put("success_url",              params.successUrl());
        formFields.put("failure_url",              params.failureUrl());
        formFields.put("signed_field_names",       "total_amount,transaction_uuid,product_code");
        formFields.put("signature",                signature);

        String gatewayUrl = isSandbox() ? ESEWA_SANDBOX_URL : ESEWA_PROD_URL;

        log.info("eSewa payment request created: transactionRef={} amount={} env={}",
            transactionUuid, totalAmount, esewaEnvironment);

        return new PaymentRequest(gatewayUrl, formFields, transactionUuid);
    }

    /**
     * Verify payment server-side using eSewa Transaction Status API.
     * Called after client receives success redirect.
     * Params must include "encoded_data" from eSewa redirect.
     *
     * Flow: decode encoded_data (base64) → extract transaction_uuid → call status API
     */
    @Override
    public VerificationResult verifyPayment(Map<String, String> params) {
        try {
            // 1. Decode eSewa's base64-encoded response
            String encodedData = params.get("encoded_data");
            if (encodedData == null || encodedData.isBlank()) {
                return failResult("encoded_data missing from callback", Map.of());
            }

            String decoded = new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);
            Map<String, Object> decodedMap = objectMapper.readValue(decoded, Map.class);

            log.info("eSewa decoded callback: {}", decodedMap);

            // 2. Extract transaction_uuid (eSewa's internal reference)
            String gatewayTxnRef  = String.valueOf(decodedMap.get("transaction_uuid"));
            String transactionCode = String.valueOf(decodedMap.get("transaction_code"));
            String status         = String.valueOf(decodedMap.get("status"));
            String totalAmount    = String.valueOf(decodedMap.get("total_amount"));

            if (!"COMPLETE".equalsIgnoreCase(status)) {
                return failResult("eSewa status not COMPLETE: " + status, decodedMap);
            }

            // 3. Call eSewa Transaction Status API for server-side verification
            String verifyUrl = (isSandbox() ? ESEWA_SANDBOX_VERIFY : ESEWA_PROD_VERIFY)
                + "?product_code=" + merchantId
                + "&total_amount=" + totalAmount
                + "&transaction_uuid=" + gatewayTxnRef;

            Map<String, Object> statusResponse;
            try {
                statusResponse = restTemplate.getForObject(verifyUrl, Map.class);
            } catch (Exception e) {
                log.error("eSewa verification API call failed: {}", e.getMessage());
                return failResult("Verification API error: " + e.getMessage(), decodedMap);
            }

            if (statusResponse == null) {
                return failResult("Empty verification response from eSewa", decodedMap);
            }

            log.info("eSewa verification response: {}", statusResponse);

            String verifiedStatus = String.valueOf(statusResponse.get("status"));
            if (!"COMPLETE".equalsIgnoreCase(verifiedStatus)) {
                return failResult("eSewa verification status: " + verifiedStatus, statusResponse);
            }

            // 4. Extract verified amount
            BigDecimal verifiedAmount = parseBigDecimal(
                String.valueOf(statusResponse.getOrDefault("total_amount", totalAmount)));

            // 5. Verify signature if present
            Object sigField = decodedMap.get("signature");
            if (sigField != null) {
                String expectedSig = generateHmacSignature(
                    "transaction_code=" + transactionCode +
                    ",status=" + status +
                    ",total_amount=" + totalAmount +
                    ",transaction_uuid=" + gatewayTxnRef +
                    ",product_code=" + merchantId +
                    ",signed_field_names=" + decodedMap.get("signed_field_names"));
                if (!expectedSig.equals(sigField.toString())) {
                    log.warn("eSewa signature mismatch! Rejecting.");
                    return failResult("Signature verification failed", decodedMap);
                }
            }

            return new VerificationResult(
                true, "SUCCESS", verifiedAmount, gatewayTxnRef, null, statusResponse);

        } catch (Exception e) {
            log.error("eSewa verification error: {}", e.getMessage(), e);
            return failResult("Verification exception: " + e.getMessage(), Map.of());
        }
    }

    // ── Internal helpers ─────────────────────────────────────────────────────

    public String generateHmacSignature(String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate HMAC signature", e);
        }
    }

    private boolean isSandbox() {
        return !"PRODUCTION".equalsIgnoreCase(esewaEnvironment);
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private VerificationResult failResult(String reason, Map<?, ?> raw) {
        return new VerificationResult(false, "FAILED", BigDecimal.ZERO, null, reason,
            raw instanceof Map ? (Map<String, Object>) raw : Map.of());
    }
}

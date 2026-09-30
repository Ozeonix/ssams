package com.artms.payment;

import com.artms.payment.gateway.EsewaPaymentGateway;
import com.artms.payment.gateway.PaymentGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EsewaPaymentGatewayTest {

    @Mock
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private EsewaPaymentGateway esewaGateway;

    @BeforeEach
    void setUp() {
        esewaGateway = new EsewaPaymentGateway(restTemplate, objectMapper);
        ReflectionTestUtils.setField(esewaGateway, "esewaEnvironment", "SANDBOX");
        ReflectionTestUtils.setField(esewaGateway, "merchantId", "EPAYTEST");
        ReflectionTestUtils.setField(esewaGateway, "secretKey", "8gBm/:&EnhH.1/q");
    }

    @Test
    @DisplayName("eSewa gateway code is ESEWA")
    void gatewayCodeIsEsewa() {
        assertThat(esewaGateway.getCode()).isEqualTo("ESEWA");
    }

    @Test
    @DisplayName("generateHmacSignature produces valid non-empty Base64 HMAC-SHA256 signature")
    void generateSignatureValid() {
        String message = "total_amount=100,transaction_uuid=11-201-13,product_code=EPAYTEST";
        String signature = esewaGateway.generateHmacSignature(message);
        assertThat(signature).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("createPaymentRequest creates complete form payload with signature for eSewa v2")
    void initiatePaymentSuccess() {
        PaymentGateway.PaymentInitParams params = new PaymentGateway.PaymentInitParams(
                "TXN-20260930-TEST",
                new BigDecimal("15000.00"),
                "NPR",
                "http://localhost:8080/api/v1/payments/callback/success",
                "http://localhost:8080/api/v1/payments/callback/failure",
                "Term 1 Fee",
                "EPAYTEST"
        );

        PaymentGateway.PaymentRequest request = esewaGateway.createPaymentRequest(params);

        assertThat(request.gatewayUrl()).contains("rc-epay.esewa.com.np");
        Map<String, String> fields = request.formFields();
        assertThat(fields.get("amount")).isEqualTo("15000.00");
        assertThat(fields.get("total_amount")).isEqualTo("15000.00");
        assertThat(fields.get("transaction_uuid")).isEqualTo("TXN-20260930-TEST");
        assertThat(fields.get("product_code")).isEqualTo("EPAYTEST");
        assertThat(fields.get("signed_field_names")).isEqualTo("total_amount,transaction_uuid,product_code");
        assertThat(fields.get("signature")).isNotBlank();
    }

    @Test
    @DisplayName("verifyPayment parses and confirms payment via Status API when status is COMPLETE")
    void verifyPaymentComplete() throws Exception {
        Map<String, Object> decodedCallback = Map.of(
                "transaction_uuid", "TXN-20260930-TEST",
                "transaction_code", "ESEWA-CODE-1234",
                "status", "COMPLETE",
                "total_amount", "15000.00"
        );
        String jsonPayload = objectMapper.writeValueAsString(decodedCallback);
        String base64Encoded = Base64.getEncoder().encodeToString(jsonPayload.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> mockStatusApiResponse = Map.of(
                "product_code", "EPAYTEST",
                "transaction_uuid", "TXN-20260930-TEST",
                "total_amount", "15000.00",
                "status", "COMPLETE",
                "ref_id", "ESEWA-REF-12345"
        );

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(mockStatusApiResponse);

        PaymentGateway.VerificationResult vr = esewaGateway.verifyPayment(
                Map.of("encoded_data", base64Encoded)
        );

        assertThat(vr.success()).isTrue();
        assertThat(vr.status()).isEqualTo("SUCCESS");
        assertThat(vr.verifiedAmount()).isEqualByComparingTo(new BigDecimal("15000.00"));
    }

    @Test
    @DisplayName("verifyPayment returns failure when encoded_data is missing or status is not COMPLETE")
    void verifyPaymentMissingData() {
        PaymentGateway.VerificationResult vr = esewaGateway.verifyPayment(Map.of());
        assertThat(vr.success()).isFalse();
        assertThat(vr.status()).isEqualTo("FAILED");
    }
}

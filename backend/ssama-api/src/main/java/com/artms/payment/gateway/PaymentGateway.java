package com.artms.payment.gateway;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Payment gateway abstraction.
 * The fee/payment system depends on this interface – not on eSewa-specific code.
 * Additional gateways (Khalti, IME Pay) can be plugged in without changing payment flow.
 */
public interface PaymentGateway {

    /** Unique gateway code (e.g. "ESEWA", "KHALTI"). */
    String getCode();

    /**
     * Create a payment request.
     * Returns a map containing all parameters needed to initiate payment
     * on the client side (redirect URL, form fields, etc.).
     */
    PaymentRequest createPaymentRequest(PaymentInitParams params);

    /**
     * Server-side verification of a completed payment.
     * MUST call the gateway's verification API – never trust client-supplied success.
     *
     * @param params  verification parameters received from gateway callback/redirect
     * @return        verification result with gateway-confirmed status and amount
     */
    VerificationResult verifyPayment(Map<String, String> params);

    // ── Inner types ──────────────────────────────────────────────────────────

    record PaymentInitParams(
        String transactionRef,
        BigDecimal amount,
        String currency,
        String successUrl,
        String failureUrl,
        String productName,
        String productCode
    ) {}

    record PaymentRequest(
        String gatewayUrl,
        Map<String, String> formFields,
        String gatewayOrderId
    ) {}

    record VerificationResult(
        boolean success,
        String status,
        BigDecimal verifiedAmount,
        String gatewayTxnRef,
        String failureReason,
        Map<String, Object> rawResponse
    ) {}
}

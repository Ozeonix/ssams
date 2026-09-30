package com.artms.payment.domain;

/**
 * Payment transaction lifecycle states.
 * Never trust client-supplied state – always server-determined.
 */
public enum PaymentStatus {
    CREATED,
    INITIATED,
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED,
    EXPIRED,
    REFUND_PENDING,
    REFUNDED
}

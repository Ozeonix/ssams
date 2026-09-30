package com.artms.payment.domain;

/**
 * Fee invoice lifecycle states.
 * Transitions: DRAFT → ISSUED → PARTIALLY_PAID / PAID / OVERDUE → CANCELLED
 */
public enum InvoiceStatus {
    DRAFT,
    ISSUED,
    PARTIALLY_PAID,
    PAID,
    OVERDUE,
    CANCELLED
}

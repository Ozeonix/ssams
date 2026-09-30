package com.artms.payment.domain;

/** Ledger entry types – positive amounts = charges, negative = credits. */
public enum LedgerEntryType {
    CHARGE,
    PAYMENT,
    ADJUSTMENT,
    DISCOUNT,
    LATE_FEE,
    REFUND,
    REVERSAL
}

package com.artms.payment.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Fee invoice for a student.
 * Immutable financial totals – only status and balance can change via service methods.
 * Uses state machine for status transitions.
 */
@Entity
@Table(name = "fee_invoice")
@Getter
@Setter
@NoArgsConstructor
public class FeeInvoice extends TenantBaseEntity {

    @Column(name = "invoice_number", nullable = false, unique = true, length = 64)
    private String invoiceNumber;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "academic_year_id")
    private UUID academicYearId;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate = LocalDate.now();

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "late_fee_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal lateFeeAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "notes")
    private String notes;

    @Column(name = "issued_by")
    private UUID issuedBy;

    @Column(name = "issued_at")
    private OffsetDateTime issuedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeeInvoiceItem> items = new ArrayList<>();

    // ── State machine transitions ───────────────────────────────────────────

    public void issue(UUID issuedBy) {
        if (this.status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT invoices can be issued");
        }
        this.status = InvoiceStatus.ISSUED;
        this.issuedBy = issuedBy;
        this.issuedAt = OffsetDateTime.now();
    }

    /**
     * Apply a payment amount. Must be called within a transaction.
     * Enforces paid_amount <= total_amount and updates status accordingly.
     */
    public void applyPayment(BigDecimal amount) {
        if (status == InvoiceStatus.PAID || status == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cannot apply payment to a " + status + " invoice");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (amount.compareTo(this.balance) > 0) {
            throw new IllegalArgumentException(
                "Payment amount " + amount + " exceeds outstanding balance " + this.balance);
        }
        this.paidAmount  = this.paidAmount.add(amount);
        this.balance     = this.totalAmount.subtract(this.paidAmount);
        this.status = this.balance.compareTo(BigDecimal.ZERO) == 0
            ? InvoiceStatus.PAID
            : InvoiceStatus.PARTIALLY_PAID;
    }

    public void cancel() {
        if (status == InvoiceStatus.PAID) {
            throw new IllegalStateException("Cannot cancel a PAID invoice");
        }
        this.status = InvoiceStatus.CANCELLED;
    }

    public void markOverdue() {
        if (status == InvoiceStatus.ISSUED || status == InvoiceStatus.PARTIALLY_PAID) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }

    /** Recalculate totals from line items. */
    public void recalculate() {
        this.subtotal = items.stream()
            .map(FeeInvoiceItem::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalAmount = subtotal.subtract(discountAmount).add(lateFeeAmount);
        if (this.totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.totalAmount = BigDecimal.ZERO;
        }
        this.balance = totalAmount.subtract(paidAmount);
        if (this.balance.compareTo(BigDecimal.ZERO) < 0) {
            this.balance = BigDecimal.ZERO;
        }
    }
}

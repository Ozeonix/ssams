package com.artms.payment.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * A payment transaction – one per student payment attempt.
 * Idempotency is enforced via UNIQUE(transaction_ref) and UNIQUE(gateway_txn_ref).
 * Status transitions are controlled server-side only.
 */
@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@NoArgsConstructor
public class PaymentTransaction extends TenantBaseEntity {

    @Column(name = "transaction_ref", nullable = false, unique = true, length = 128)
    private String transactionRef;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Column(name = "gateway_code", nullable = false, length = 64)
    private String gatewayCode = "ESEWA";

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 8)
    private String currency = "NPR";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private PaymentStatus status = PaymentStatus.CREATED;

    @Column(name = "gateway_order_id", length = 256)
    private String gatewayOrderId;

    @Column(name = "gateway_txn_ref", length = 256)
    private String gatewayTxnRef;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gateway_response", columnDefinition = "jsonb")
    private Map<String, Object> gatewayResponse;

    @Column(name = "initiated_at")
    private OffsetDateTime initiatedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "expired_at")
    private OffsetDateTime expiredAt;

    @Column(name = "failure_reason", length = 512)
    private String failureReason;

    @Column(name = "initiated_by_user")
    private UUID initiatedByUser;

    // ── State machine ────────────────────────────────────────────────────────

    public void markInitiated(String gatewayOrderId) {
        assertStatus(PaymentStatus.CREATED, "initiate");
        this.status        = PaymentStatus.INITIATED;
        this.gatewayOrderId = gatewayOrderId;
        this.initiatedAt   = OffsetDateTime.now();
    }

    public void markPending() {
        assertStatus(PaymentStatus.INITIATED, "mark pending");
        this.status = PaymentStatus.PENDING;
    }

    public void markSuccess(String gatewayTxnRef, Map<String, Object> gatewayResponse) {
        if (this.status == PaymentStatus.SUCCESS) return; // idempotent
        this.status           = PaymentStatus.SUCCESS;
        this.gatewayTxnRef    = gatewayTxnRef;
        this.gatewayResponse  = gatewayResponse;
        this.completedAt      = OffsetDateTime.now();
    }

    public void markFailed(String reason) {
        this.status        = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.completedAt   = OffsetDateTime.now();
    }

    public void markCancelled() {
        if (status == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot cancel a SUCCESS transaction");
        }
        this.status = PaymentStatus.CANCELLED;
    }

    public void markRefundPending() {
        assertStatus(PaymentStatus.SUCCESS, "mark refund pending");
        this.status = PaymentStatus.REFUND_PENDING;
    }

    public void markRefunded() {
        assertStatus(PaymentStatus.REFUND_PENDING, "mark refunded");
        this.status = PaymentStatus.REFUNDED;
    }

    private void assertStatus(PaymentStatus expected, String action) {
        if (this.status != expected) {
            throw new IllegalStateException(
                "Cannot " + action + " from status " + this.status);
        }
    }
}

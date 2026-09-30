package com.artms.payment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Payment receipt – generated ONLY after server-verified successful payment.
 * Linked 1:1 with a successful PaymentTransaction.
 * receipt_data JSONB snapshot ensures reproducibility.
 */
@Entity
@Table(name = "payment_receipt")
@Getter
@Setter
@NoArgsConstructor
public class PaymentReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "receipt_number", nullable = false, unique = true, length = 64)
    private String receiptNumber;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private UUID transactionId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false, length = 64)
    private String paymentMethod = "ESEWA";

    @Column(name = "gateway_ref", length = 256)
    private String gatewayRef;

    @CreationTimestamp
    @Column(name = "issued_at", nullable = false, updatable = false)
    private OffsetDateTime issuedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "receipt_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> receiptData;
}

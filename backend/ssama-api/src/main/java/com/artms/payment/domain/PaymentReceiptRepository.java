package com.artms.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, UUID> {
    Optional<PaymentReceipt> findByTransactionId(UUID transactionId);
    List<PaymentReceipt> findByStudentIdAndTenantId(UUID studentId, UUID tenantId);
    Optional<PaymentReceipt> findByReceiptNumber(String receiptNumber);
}

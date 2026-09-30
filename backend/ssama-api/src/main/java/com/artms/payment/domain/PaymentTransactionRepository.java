package com.artms.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    Optional<PaymentTransaction> findByTransactionRef(String transactionRef);

    Optional<PaymentTransaction> findByGatewayTxnRef(String gatewayTxnRef);

    List<PaymentTransaction> findByStudentIdAndTenantId(UUID studentId, UUID tenantId);

    List<PaymentTransaction> findByInvoiceIdAndStatus(UUID invoiceId, PaymentStatus status);

    @Query("SELECT t FROM PaymentTransaction t WHERE t.tenantId = :tenantId AND t.status = :status " +
           "ORDER BY t.createdAt DESC")
    List<PaymentTransaction> findByTenantAndStatus(UUID tenantId, PaymentStatus status);

    boolean existsByGatewayTxnRefAndStatus(String gatewayTxnRef, PaymentStatus status);
}

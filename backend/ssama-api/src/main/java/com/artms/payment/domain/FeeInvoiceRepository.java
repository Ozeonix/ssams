package com.artms.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeeInvoiceRepository extends JpaRepository<FeeInvoice, UUID> {

    List<FeeInvoice> findByStudentIdAndTenantId(UUID studentId, UUID tenantId);

    @Query("SELECT i FROM FeeInvoice i WHERE i.studentId = :studentId AND i.tenantId = :tenantId " +
           "AND i.status IN ('ISSUED','PARTIALLY_PAID','OVERDUE')")
    List<FeeInvoice> findOutstandingByStudent(UUID studentId, UUID tenantId);

    Optional<FeeInvoice> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT i FROM FeeInvoice i WHERE i.tenantId = :tenantId AND i.status = :status")
    List<FeeInvoice> findByTenantAndStatus(UUID tenantId, InvoiceStatus status);

    boolean existsByStudentIdAndAcademicYearIdAndStatus(
        UUID studentId, UUID academicYearId, InvoiceStatus status);
}

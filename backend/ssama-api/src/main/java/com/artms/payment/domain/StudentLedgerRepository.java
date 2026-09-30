package com.artms.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentLedgerRepository extends JpaRepository<StudentLedgerEntry, UUID> {

    List<StudentLedgerEntry> findByStudentIdAndTenantIdOrderByCreatedAtDesc(UUID studentId, UUID tenantId);

    /** Get current balance: sum of all amounts for this student. */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM StudentLedgerEntry e " +
           "WHERE e.studentId = :studentId AND e.tenantId = :tenantId")
    BigDecimal calculateBalance(UUID studentId, UUID tenantId);

    /** Get the latest balance snapshot. */
    Optional<StudentLedgerEntry> findTopByStudentIdAndTenantIdOrderByCreatedAtDesc(UUID studentId, UUID tenantId);
}

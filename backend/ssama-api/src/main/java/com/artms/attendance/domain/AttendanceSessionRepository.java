package com.artms.attendance.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, UUID> {

    Optional<AttendanceSession> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<AttendanceSession> findByTenantIdAndClassGroupIdAndSubjectIdAndSessionDateAndPeriod(
            UUID tenantId, UUID classGroupId, UUID subjectId, LocalDate sessionDate, String period);

    Page<AttendanceSession> findByTenantIdAndClassGroupIdOrderBySessionDateDesc(
            UUID tenantId, UUID classGroupId, Pageable pageable);

    List<AttendanceSession> findByTenantIdAndClassGroupIdAndSessionDateBetween(
            UUID tenantId, UUID classGroupId, LocalDate startDate, LocalDate endDate);

    @Query("""
        SELECT s FROM AttendanceSession s
        LEFT JOIN FETCH s.records
        WHERE s.tenantId = :tenantId AND s.id = :id
        """)
    Optional<AttendanceSession> findWithRecords(@Param("tenantId") UUID tenantId, @Param("id") UUID id);
}

package com.artms.attendance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {

    List<AttendanceRecord> findBySessionId(UUID sessionId);

    Optional<AttendanceRecord> findBySessionIdAndStudentId(UUID sessionId, UUID studentId);

    @Query("""
        SELECT r FROM AttendanceRecord r
        JOIN r.session s
        WHERE s.tenantId = :tenantId AND r.studentId = :studentId
        ORDER BY s.sessionDate DESC
        """)
    List<AttendanceRecord> findByTenantIdAndStudentId(
            @Param("tenantId") UUID tenantId,
            @Param("studentId") UUID studentId);

    @Query("""
        SELECT r FROM AttendanceRecord r
        JOIN r.session s
        WHERE s.tenantId = :tenantId
          AND r.studentId = :studentId
          AND s.sessionDate BETWEEN :startDate AND :endDate
        ORDER BY s.sessionDate DESC
        """)
    List<AttendanceRecord> findByTenantIdAndStudentIdAndDateRange(
            @Param("tenantId") UUID tenantId,
            @Param("studentId") UUID studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT r FROM AttendanceRecord r
        JOIN r.session s
        WHERE s.tenantId = :tenantId
          AND r.studentId = :studentId
          AND s.subjectId = :subjectId
        ORDER BY s.sessionDate DESC
        """)
    List<AttendanceRecord> findByTenantIdAndStudentIdAndSubjectId(
            @Param("tenantId") UUID tenantId,
            @Param("studentId") UUID studentId,
            @Param("subjectId") UUID subjectId);
}

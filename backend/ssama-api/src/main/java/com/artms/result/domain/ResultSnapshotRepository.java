package com.artms.result.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultSnapshotRepository extends JpaRepository<ResultSnapshot, UUID> {

    Optional<ResultSnapshot> findByTenantIdAndExamIdAndStudentIdAndResultVersion(
            UUID tenantId, UUID examId, UUID studentId, int resultVersion);

    List<ResultSnapshot> findByTenantIdAndExamId(UUID tenantId, UUID examId);

    List<ResultSnapshot> findByTenantIdAndStudentId(UUID tenantId, UUID studentId);

    List<ResultSnapshot> findByTenantIdAndStudentIdAndPublishedAtIsNotNull(UUID tenantId, UUID studentId);

    Optional<ResultSnapshot> findFirstByTenantIdAndExamIdAndStudentIdOrderByResultVersionDesc(
            UUID tenantId, UUID examId, UUID studentId);

    @Query("""
        SELECT r FROM ResultSnapshot r
        LEFT JOIN FETCH r.subjectSnapshots
        WHERE r.tenantId = :tenantId AND r.id = :id
        """)
    Optional<ResultSnapshot> findWithSubjectsByTenantIdAndId(
            @Param("tenantId") UUID tenantId, @Param("id") UUID id);

    @Query("""
        SELECT r FROM ResultSnapshot r
        LEFT JOIN FETCH r.subjectSnapshots
        WHERE r.tenantId = :tenantId AND r.examId = :examId
        ORDER BY r.studentId, r.resultVersion DESC
        """)
    List<ResultSnapshot> findWithSubjectsByTenantIdAndExamId(
            @Param("tenantId") UUID tenantId, @Param("examId") UUID examId);
}

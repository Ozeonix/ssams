package com.artms.enrollment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    Optional<Enrollment> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<Enrollment> findByTenantIdAndStudentIdAndAcademicYearId(
        UUID tenantId, UUID studentId, UUID academicYearId
    );

    boolean existsByTenantIdAndStudentIdAndAcademicYearId(
        UUID tenantId, UUID studentId, UUID academicYearId
    );

    Optional<Enrollment> findByTenantIdAndStudentIdAndClassGroupIdAndStatus(
        UUID tenantId, UUID studentId, UUID classGroupId, EnrollmentStatus status
    );

    List<Enrollment> findByTenantIdAndStudentId(UUID tenantId, UUID studentId);

    List<Enrollment> findByTenantIdAndClassGroupIdAndAcademicYearId(
        UUID tenantId, UUID classGroupId, UUID academicYearId
    );

    Page<Enrollment> findByTenantIdAndClassGroupIdAndAcademicYearId(
        UUID tenantId, UUID classGroupId, UUID academicYearId, Pageable pageable
    );

    int countByTenantIdAndClassGroupIdAndAcademicYearId(
        UUID tenantId, UUID classGroupId, UUID academicYearId
    );

    @Query("""
        SELECT e FROM Enrollment e
        LEFT JOIN FETCH e.subjectEnrollments
        WHERE e.id = :id AND e.tenantId = :tenantId
        """)
    Optional<Enrollment> findWithSubjects(@Param("tenantId") UUID tenantId, @Param("id") UUID id);
}

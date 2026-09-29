package com.artms.exam.domain;

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
public interface ExamRepository extends JpaRepository<Exam, UUID> {

    Optional<Exam> findByTenantIdAndId(UUID tenantId, UUID id);

    Page<Exam> findByTenantId(UUID tenantId, Pageable pageable);

    List<Exam> findByTenantIdAndAcademicYearId(UUID tenantId, UUID academicYearId);

    @Query("""
        SELECT e FROM Exam e
        LEFT JOIN FETCH e.examSubjects
        WHERE e.tenantId = :tenantId AND e.id = :id
        """)
    Optional<Exam> findWithSubjects(@Param("tenantId") UUID tenantId, @Param("id") UUID id);
}

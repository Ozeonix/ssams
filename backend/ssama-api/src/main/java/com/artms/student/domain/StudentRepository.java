package com.artms.student.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    Optional<Student> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<Student> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    Optional<Student> findByTenantIdAndAdmissionNo(UUID tenantId, String admissionNo);

    boolean existsByTenantIdAndAdmissionNo(UUID tenantId, String admissionNo);

    @Query("""
        SELECT s FROM Student s
        WHERE s.tenantId = :tenantId
          AND (:status IS NULL OR s.status = :status)
          AND (:search IS NULL OR
               LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(s.admissionNo) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(s.registrationNo) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Student> search(
            @Param("tenantId") UUID tenantId,
            @Param("status") StudentStatus status,
            @Param("search") String search,
            Pageable pageable);
}

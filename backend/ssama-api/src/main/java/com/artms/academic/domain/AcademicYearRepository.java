package com.artms.academic.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {

    Page<AcademicYear> findByTenantIdOrderByStartDateDesc(UUID tenantId, Pageable pageable);

    Optional<AcademicYear> findByTenantIdAndId(UUID tenantId, UUID id);

    boolean existsByTenantIdAndName(UUID tenantId, String name);

    Optional<AcademicYear> findByTenantIdAndStatus(UUID tenantId, AcademicYearStatus status);
}

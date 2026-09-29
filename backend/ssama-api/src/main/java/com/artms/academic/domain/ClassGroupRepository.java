package com.artms.academic.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, UUID> {
    List<ClassGroup> findByTenantIdAndAcademicYearIdOrderByGradeLevelAscSectionAsc(UUID tenantId, UUID academicYearId);
    Optional<ClassGroup> findByTenantIdAndId(UUID tenantId, UUID id);
    Page<ClassGroup> findByTenantId(UUID tenantId, Pageable pageable);
    Page<ClassGroup> findByTenantIdAndAcademicYearId(UUID tenantId, UUID academicYearId, Pageable pageable);
}

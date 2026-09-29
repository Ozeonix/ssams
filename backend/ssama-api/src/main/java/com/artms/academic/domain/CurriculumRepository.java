package com.artms.academic.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, UUID> {

    Page<Curriculum> findByTenantId(UUID tenantId, Pageable pageable);

    List<Curriculum> findByTenantIdAndProgramId(UUID tenantId, UUID programId);

    Optional<Curriculum> findByTenantIdAndProgramIdAndAcademicYearIdAndVersion(
        UUID tenantId, UUID programId, UUID academicYearId, int version
    );

    Optional<Curriculum> findFirstByTenantIdAndProgramIdAndAcademicYearIdAndStatusOrderByVersionDesc(
        UUID tenantId, UUID programId, UUID academicYearId, CurriculumStatus status
    );
}

package com.artms.academic.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {
    Page<Program> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Program> findByTenantIdAndStatus(UUID tenantId, String status, Pageable pageable);
    Optional<Program> findByTenantIdAndId(UUID tenantId, UUID id);
    boolean existsByTenantIdAndCode(UUID tenantId, String code);
}

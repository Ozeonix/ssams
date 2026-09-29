package com.artms.academic.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    Page<Subject> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Subject> findByTenantIdAndActive(UUID tenantId, boolean active, Pageable pageable);
    Optional<Subject> findByTenantIdAndId(UUID tenantId, UUID id);
    boolean existsByTenantIdAndCode(UUID tenantId, String code);
}

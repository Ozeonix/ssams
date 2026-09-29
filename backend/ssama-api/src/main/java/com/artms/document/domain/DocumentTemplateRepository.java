package com.artms.document.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, UUID> {

    Optional<DocumentTemplate> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<DocumentTemplate> findFirstByTenantIdAndTypeAndStatusOrderByVersionDesc(
            UUID tenantId, DocumentType type, DocumentTemplateStatus status);

    List<DocumentTemplate> findByTenantIdAndType(UUID tenantId, DocumentType type);

    Page<DocumentTemplate> findByTenantId(UUID tenantId, Pageable pageable);
}

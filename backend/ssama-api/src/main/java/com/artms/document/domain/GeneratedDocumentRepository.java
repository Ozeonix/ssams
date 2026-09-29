package com.artms.document.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, UUID> {

    Optional<GeneratedDocument> findByTenantIdAndId(UUID tenantId, UUID id);

    List<GeneratedDocument> findByTenantIdAndStudentId(UUID tenantId, UUID studentId);

    Page<GeneratedDocument> findByTenantId(UUID tenantId, Pageable pageable);

    Page<GeneratedDocument> findByTenantIdAndStudentId(UUID tenantId, UUID studentId, Pageable pageable);
}

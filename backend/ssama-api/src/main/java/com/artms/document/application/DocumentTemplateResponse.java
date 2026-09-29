package com.artms.document.application;

import com.artms.document.domain.DocumentTemplate;
import com.artms.document.domain.DocumentTemplateStatus;
import com.artms.document.domain.DocumentType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DocumentTemplateResponse(
    UUID id,
    UUID tenantId,
    DocumentType type,
    String name,
    int version,
    String templateBody,
    DocumentTemplateStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static DocumentTemplateResponse from(DocumentTemplate t) {
        return new DocumentTemplateResponse(
            t.getId(),
            t.getTenantId(),
            t.getType(),
            t.getName(),
            t.getVersion(),
            t.getTemplateBody(),
            t.getStatus(),
            t.getCreatedAt(),
            t.getUpdatedAt()
        );
    }
}

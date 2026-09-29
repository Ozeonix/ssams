package com.artms.document.application;

import com.artms.document.domain.DocumentType;
import com.artms.document.domain.GeneratedDocument;
import com.artms.document.domain.GeneratedDocumentStatus;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record GeneratedDocumentResponse(
    UUID id,
    UUID tenantId,
    UUID studentId,
    DocumentType documentType,
    String objectKey,
    String checksum,
    GeneratedDocumentStatus status,
    OffsetDateTime generatedAt,
    OffsetDateTime expiresAt,
    Map<String, Object> requestData,
    String content,
    OffsetDateTime createdAt
) {
    public static GeneratedDocumentResponse from(GeneratedDocument doc, String content) {
        return new GeneratedDocumentResponse(
            doc.getId(),
            doc.getTenantId(),
            doc.getStudentId(),
            doc.getDocumentType(),
            doc.getObjectKey(),
            doc.getChecksum(),
            doc.getStatus(),
            doc.getGeneratedAt(),
            doc.getExpiresAt(),
            doc.getRequestData(),
            content,
            doc.getCreatedAt()
        );
    }
}

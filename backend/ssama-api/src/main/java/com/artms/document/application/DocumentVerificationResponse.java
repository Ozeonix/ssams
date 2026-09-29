package com.artms.document.application;

import com.artms.document.domain.DocumentType;
import com.artms.document.domain.GeneratedDocumentStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DocumentVerificationResponse(
    boolean valid,
    UUID documentId,
    DocumentType documentType,
    GeneratedDocumentStatus status,
    String studentName,
    String admissionNo,
    String tenantName,
    OffsetDateTime generatedAt,
    String checksum,
    String message
) {}

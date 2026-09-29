package com.artms.document.application;

import com.artms.document.domain.DocumentType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GenerateDocumentRequest(
    @NotNull DocumentType documentType,
    @NotNull UUID studentId,
    UUID examId,
    UUID templateId
) {}

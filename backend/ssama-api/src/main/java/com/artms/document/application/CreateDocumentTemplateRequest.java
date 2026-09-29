package com.artms.document.application;

import com.artms.document.domain.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDocumentTemplateRequest(
    @NotNull DocumentType type,
    @NotBlank String name,
    @NotBlank String templateBody
) {}

package com.artms.result.application;

import com.artms.exam.application.MarkEntryDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ResultCorrectionRequest(
    @NotNull UUID studentId,
    @NotBlank String correctionReason,
    @NotNull UUID examSubjectId,
    @NotEmpty @Valid List<MarkEntryDto> entries
) {}

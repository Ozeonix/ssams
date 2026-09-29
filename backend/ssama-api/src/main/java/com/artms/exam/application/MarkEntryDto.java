package com.artms.exam.application;

import com.artms.exam.domain.MarkStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record MarkEntryDto(
    @NotNull UUID studentId,
    @NotNull UUID componentId,
    BigDecimal rawMarks,
    MarkStatus status,
    String remarks,
    Integer version
) {}

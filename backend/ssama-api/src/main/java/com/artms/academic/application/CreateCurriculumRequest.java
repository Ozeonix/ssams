package com.artms.academic.application;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCurriculumRequest(
    @NotNull UUID programId,
    @NotNull UUID academicYearId,
    Integer version
) {}

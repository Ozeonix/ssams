package com.artms.academic.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateClassGroupRequest(
    @NotNull UUID academicYearId,
    @NotNull UUID programId,
    @NotBlank @Size(max = 128) String name,
    @NotBlank @Size(max = 32) String section,
    @NotBlank @Size(max = 64) String gradeLevel,
    Integer capacity
) {}

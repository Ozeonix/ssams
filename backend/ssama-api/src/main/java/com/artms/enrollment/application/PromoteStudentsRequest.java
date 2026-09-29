package com.artms.enrollment.application;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PromoteStudentsRequest(
        @NotNull(message = "Source class group ID is required")
        UUID sourceClassGroupId,

        @NotNull(message = "Target class group ID is required")
        UUID targetClassGroupId,

        @NotNull(message = "Target academic year ID is required")
        UUID targetAcademicYearId,

        @NotEmpty(message = "At least one student must be selected for promotion")
        List<UUID> studentIds
) {}

package com.artms.enrollment.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record BatchEnrollClassRequest(
    @NotNull UUID classGroupId,
    @NotNull UUID academicYearId,
    @NotEmpty @Valid List<StudentEnrollmentDto> students
) {}

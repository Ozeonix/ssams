package com.artms.enrollment.application;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record EnrollStudentRequest(
    @NotNull UUID studentId,
    @NotNull UUID classGroupId,
    @NotNull UUID academicYearId,
    String rollNo,
    LocalDate enrolledAt
) {}

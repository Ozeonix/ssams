package com.artms.enrollment.application;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StudentEnrollmentDto(
    @NotNull UUID studentId,
    String rollNo
) {}

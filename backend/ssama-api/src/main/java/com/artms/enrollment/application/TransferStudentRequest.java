package com.artms.enrollment.application;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TransferStudentRequest(
        @NotNull(message = "Student ID is required")
        UUID studentId,

        @NotNull(message = "Current enrollment ID is required")
        UUID currentEnrollmentId,

        @NotNull(message = "Target class group ID is required")
        UUID targetClassGroupId,

        String reason,

        String newRollNo
) {}

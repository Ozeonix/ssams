package com.artms.staff.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTeacherRequest(
    @NotNull UUID userId,
    @Size(max = 64) String employeeCode,
    @NotBlank @Size(max = 128) String firstName,
    @NotBlank @Size(max = 128) String lastName,
    UUID departmentId,
    @Size(max = 128) String designation,
    @Size(max = 32) String phone,
    @Size(max = 255) String email
) {}

package com.artms.academic.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDepartmentRequest(
    @NotBlank @Size(max = 32) String code,
    @NotBlank @Size(max = 255) String name
) {}

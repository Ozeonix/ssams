package com.artms.student.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddGuardianRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 64) String relationship,
    @Size(max = 32) String phone,
    @Size(max = 255) String email,
    String address,
    boolean isPrimary
) {}

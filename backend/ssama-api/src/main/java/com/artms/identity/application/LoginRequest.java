package com.artms.identity.application;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String tenantCode,
    @NotBlank String username,
    @NotBlank String password
) {}

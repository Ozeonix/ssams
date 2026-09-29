package com.artms.identity.application;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "Tenant code is required")
        String tenantCode,

        @NotBlank(message = "Username or email is required")
        String usernameOrEmail
) {}

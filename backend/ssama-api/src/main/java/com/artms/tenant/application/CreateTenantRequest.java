package com.artms.tenant.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateTenantRequest(
    @NotBlank @Pattern(regexp = "^[a-z0-9\\-]{3,64}$", message = "Code must be lowercase alphanumeric or hyphens, 3-64 chars")
    String code,
    @NotBlank String name,
    String timezone,
    String locale
) {}

package com.artms.identity.application;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record CreateRoleRequest(
    @NotBlank String code,
    @NotBlank String name,
    String description,
    Set<String> permissionCodes
) {}

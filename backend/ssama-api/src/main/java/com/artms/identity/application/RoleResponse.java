package com.artms.identity.application;

import com.artms.identity.domain.Permission;
import com.artms.identity.domain.Role;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record RoleResponse(
    UUID id,
    UUID tenantId,
    String code,
    String name,
    String description,
    boolean isSystem,
    Set<String> permissions
) {
    public static RoleResponse from(Role role) {
        Set<String> perms = role.getPermissions() == null ? Set.of() :
            role.getPermissions().stream().map(Permission::getCode).collect(Collectors.toSet());

        return new RoleResponse(
            role.getId(),
            role.getTenantId(),
            role.getCode(),
            role.getName(),
            role.getDescription(),
            role.isSystem(),
            perms
        );
    }
}

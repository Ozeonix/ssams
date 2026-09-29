package com.artms.identity.application;

import com.artms.audit.application.AuditService;
import com.artms.identity.domain.Permission;
import com.artms.identity.domain.PermissionRepository;
import com.artms.identity.domain.Role;
import com.artms.identity.domain.RoleRepository;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        UUID tenantId = TenantContext.getTenantId();
        return roleRepository.findByTenantIdOrTenantIdIsNull(tenantId).stream()
                .map(RoleResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse getRole(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Role role = roleRepository.findWithPermissions(tenantId, id)
                .orElseThrow(() -> ResourceNotFoundException.of("Role", id));
        return RoleResponse.from(role);
    }

    @Transactional
    public RoleResponse createRole(CreateRoleRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        if (roleRepository.findByTenantIdAndCode(tenantId, req.code()).isPresent()) {
            throw new BusinessRuleException("ROLE_CODE_EXISTS",
                    "A role with code '" + req.code() + "' already exists in this institution");
        }

        Role role = new Role();
        role.setTenantId(tenantId);
        role.setCode(req.code().toUpperCase().strip());
        role.setName(req.name());
        role.setDescription(req.description());
        role.setSystem(false);

        if (req.permissionCodes() != null && !req.permissionCodes().isEmpty()) {
            Set<Permission> permissions = new HashSet<>();
            for (String code : req.permissionCodes()) {
                permissionRepository.findByCode(code).ifPresent(permissions::add);
            }
            role.setPermissions(permissions);
        }

        Role saved = roleRepository.save(role);

        auditService.record(tenantId, userId, "role:create", "Role", saved.getId().toString(),
                Map.of("code", saved.getCode(), "name", saved.getName()));

        return RoleResponse.from(saved);
    }

    @Transactional
    public RoleResponse updatePermissions(UUID roleId, Set<String> permissionCodes) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        Role role = roleRepository.findWithPermissions(tenantId, roleId)
                .orElseThrow(() -> ResourceNotFoundException.of("Role", roleId));

        if (role.isSystem()) {
            throw new BusinessRuleException("CANNOT_MODIFY_SYSTEM_ROLE",
                    "System roles have immutable permission definitions");
        }

        Set<Permission> permissions = new HashSet<>();
        if (permissionCodes != null) {
            for (String code : permissionCodes) {
                permissionRepository.findByCode(code).ifPresent(permissions::add);
            }
        }

        role.setPermissions(permissions);
        Role saved = roleRepository.save(role);

        auditService.record(tenantId, userId, "role:update_permissions", "Role", saved.getId().toString(),
                Map.of("permissionCount", permissions.size()));

        return RoleResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<Permission> listPermissions() {
        return permissionRepository.findAllByOrderByDomainAscCodeAsc();
    }
}

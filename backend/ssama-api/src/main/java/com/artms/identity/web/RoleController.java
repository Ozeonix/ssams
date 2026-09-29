package com.artms.identity.web;

import com.artms.identity.application.CreateRoleRequest;
import com.artms.identity.application.RoleResponse;
import com.artms.identity.application.RoleService;
import com.artms.identity.domain.Permission;
import com.artms.shared.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/api/v1/roles")
    @PreAuthorize("hasAuthority('PERM_role:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> listRoles() {
        return ResponseEntity.ok(ApiResponse.of(roleService.listRoles()));
    }

    @GetMapping("/api/v1/roles/{id}")
    @PreAuthorize("hasAuthority('PERM_role:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRole(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(roleService.getRole(id)));
    }

    @PostMapping("/api/v1/roles")
    @PreAuthorize("hasAuthority('PERM_role:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(
            @Valid @RequestBody CreateRoleRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @PutMapping("/api/v1/roles/{id}/permissions")
    @PreAuthorize("hasAuthority('PERM_role:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<RoleResponse>> updatePermissions(
            @PathVariable UUID id,
            @RequestBody Set<String> permissionCodes) {
        RoleResponse response = roleService.updatePermissions(id, permissionCodes);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/api/v1/permissions")
    @PreAuthorize("hasAuthority('PERM_role:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<Permission>>> listPermissions() {
        return ResponseEntity.ok(ApiResponse.of(roleService.listPermissions()));
    }
}

package com.artms.tenant.web;

import com.artms.shared.tenant.TenantContext;
import com.artms.shared.web.ApiResponse;
import com.artms.tenant.application.TenantService;
import com.artms.tenant.application.UpdateTenantRequest;
import com.artms.tenant.domain.Tenant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/institutions")
@RequiredArgsConstructor
public class InstitutionController {

    private final TenantService tenantService;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PERM_institution:read') or hasAuthority('PERM_settings:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<Tenant>> getMyInstitution() {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.of(tenantService.getById(tenantId)));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAuthority('PERM_institution:update') or hasAuthority('PERM_settings:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<Tenant>> updateMyInstitution(
            @Valid @RequestBody UpdateTenantRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.of(tenantService.updateSettings(tenantId, request)));
    }

    @GetMapping("/me/settings")
    @PreAuthorize("hasAuthority('PERM_institution:read') or hasAuthority('PERM_settings:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSettings() {
        UUID tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantService.getById(tenantId);
        return ResponseEntity.ok(ApiResponse.of(Map.of(
                "settings", tenant.getSettings() != null ? tenant.getSettings() : Map.of(),
                "branding", tenant.getBranding() != null ? tenant.getBranding() : Map.of(),
                "modules", tenant.getModules() != null ? tenant.getModules() : Map.of(),
                "timezone", tenant.getTimezone(),
                "locale", tenant.getLocale()
        )));
    }
}

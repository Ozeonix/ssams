package com.artms.audit.web;

import com.artms.audit.domain.AuditLog;
import com.artms.audit.domain.AuditLogRepository;
import com.artms.shared.tenant.TenantContext;
import com.artms.shared.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_audit:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> listAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) UUID actorUserId,
            @PageableDefault(size = 30) Pageable pageable) {

        UUID tenantId = TenantContext.getTenantId();
        Page<AuditLog> page;

        if (entityType != null && entityId != null) {
            page = auditLogRepository.findByTenantIdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
                    tenantId, entityType, entityId, pageable);
        } else if (actorUserId != null) {
            page = auditLogRepository.findByTenantIdAndActorUserIdOrderByCreatedAtDesc(
                    tenantId, actorUserId, pageable);
        } else {
            page = auditLogRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, pageable);
        }

        return ResponseEntity.ok(ApiResponse.of(page));
    }
}

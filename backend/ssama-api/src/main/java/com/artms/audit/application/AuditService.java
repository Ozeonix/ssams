package com.artms.audit.application;

import com.artms.audit.domain.AuditLog;
import com.artms.audit.domain.AuditLogRepository;
import com.artms.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * Audit service — records immutable audit events for sensitive operations.
 * Runs in a separate transaction to ensure audit records are persisted
 * even if the business transaction is retried.
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(AuditEvent event) {
        AuditLog log = AuditLog.builder()
                .tenantId(event.tenantId())
                .actorUserId(event.actorUserId())
                .action(event.action())
                .entityType(event.entityType())
                .entityId(event.entityId())
                .beforeData(event.beforeData())
                .afterData(event.afterData())
                .reason(event.reason())
                .correlationId(event.correlationId())
                .ipHash(event.ipHash())
                .build();
        auditLogRepository.save(log);
    }

    /**
     * Convenience method for simple action audit.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(UUID tenantId, UUID actorUserId, String action,
                       String entityType, String entityId, Map<String, Object> afterData) {
        record(new AuditEvent(tenantId, actorUserId, action, entityType, entityId,
                null, afterData, null, null, null));
    }
}

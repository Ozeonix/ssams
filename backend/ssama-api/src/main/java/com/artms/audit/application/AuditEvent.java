package com.artms.audit.application;

import java.util.Map;
import java.util.UUID;

public record AuditEvent(
    UUID tenantId,
    UUID actorUserId,
    String action,
    String entityType,
    String entityId,
    Map<String, Object> beforeData,
    Map<String, Object> afterData,
    String reason,
    UUID correlationId,
    String ipHash
) {}

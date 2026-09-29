package com.artms.notification.application;

import com.artms.notification.domain.Notification;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record NotificationResponse(
    UUID id,
    UUID tenantId,
    UUID userId,
    String type,
    String title,
    String body,
    Map<String, Object> data,
    OffsetDateTime readAt,
    boolean isRead,
    OffsetDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
            n.getId(),
            n.getTenantId(),
            n.getUserId(),
            n.getType(),
            n.getTitle(),
            n.getBody(),
            n.getData(),
            n.getReadAt(),
            n.isRead(),
            n.getCreatedAt()
        );
    }
}

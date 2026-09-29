package com.artms.notification.application;

import java.time.OffsetDateTime;
import java.util.List;

public record NotificationSyncResponse(
    int unreadCount,
    List<NotificationResponse> notifications,
    OffsetDateTime syncTimestamp
) {}

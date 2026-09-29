package com.artms.notification.application;

import com.artms.notification.domain.Notification;
import com.artms.notification.domain.NotificationRepository;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();
        return notificationRepository.findByTenantIdAndUserIdOrderByCreatedAtDesc(tenantId, userId, pageable)
                .map(NotificationResponse::from);
    }

    @Transactional
    public NotificationResponse markAsRead(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        Notification notification = notificationRepository.findByTenantIdAndUserIdAndId(tenantId, userId, id)
                .orElseThrow(() -> ResourceNotFoundException.of("Notification", id));

        notification.markAsRead();
        Notification saved = notificationRepository.save(notification);
        return NotificationResponse.from(saved);
    }

    @Transactional
    public int markAllAsRead() {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();
        return notificationRepository.markAllRead(tenantId, userId, OffsetDateTime.now());
    }

    @Transactional(readOnly = true)
    public int getUnreadCount() {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();
        return notificationRepository.countByTenantIdAndUserIdAndReadAtIsNull(tenantId, userId);
    }

    @Transactional(readOnly = true)
    public NotificationSyncResponse sync(OffsetDateTime since) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        OffsetDateTime cutoff = since != null ? since : OffsetDateTime.now().minusDays(30);
        List<Notification> missed = notificationRepository
                .findByTenantIdAndUserIdAndCreatedAtAfterOrderByCreatedAtDesc(tenantId, userId, cutoff);

        int unreadCount = notificationRepository.countByTenantIdAndUserIdAndReadAtIsNull(tenantId, userId);

        return new NotificationSyncResponse(
                unreadCount,
                missed.stream().map(NotificationResponse::from).toList(),
                OffsetDateTime.now()
        );
    }
}

package com.artms.notification.application;

import com.artms.notification.domain.Notification;
import com.artms.notification.domain.NotificationRepository;
import com.artms.notification.domain.OutboxEvent;
import com.artms.notification.domain.OutboxEventRepository;
import com.artms.notification.infrastructure.PushAdapter;
import com.artms.notification.infrastructure.RealtimeEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxEventRepository outboxEventRepository;
    private final NotificationRepository notificationRepository;
    private final RealtimeEventPublisher realtimeEventPublisher;
    private final PushAdapter pushAdapter;

    private static final int MAX_ATTEMPTS = 5;
    private static final int BATCH_SIZE = 50;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> pending = outboxEventRepository.findPendingEvents(MAX_ATTEMPTS, PageRequest.of(0, BATCH_SIZE));
        if (pending.isEmpty()) {
            return;
        }

        log.debug("Processing {} pending outbox events", pending.size());
        OffsetDateTime now = OffsetDateTime.now();

        for (OutboxEvent event : pending) {
            try {
                dispatchEvent(event);
                event.setPublishedAt(now);
            } catch (Exception e) {
                log.error("Failed to process outbox event: id={}, type={}", event.getId(), event.getEventType(), e);
                event.setAttempts(event.getAttempts() + 1);
                event.setLastError(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            }
        }

        outboxEventRepository.saveAll(pending);
    }

    @Scheduled(cron = "0 0 3 * * ?") // Daily at 3 AM
    @Transactional
    public void cleanupOldPublishedEvents() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(7);
        int deleted = outboxEventRepository.deletePublishedBefore(cutoff);
        if (deleted > 0) {
            log.info("Cleaned up {} published outbox events older than {}", deleted, cutoff);
        }
    }

    private void dispatchEvent(OutboxEvent event) {
        String aggregateType = event.getAggregateType();
        UUID tenantId = event.getTenantId();
        Map<String, Object> payload = event.getPayload();

        // 1. Broadcast to exam status topic if applicable
        if ("Exam".equalsIgnoreCase(aggregateType)) {
            try {
                UUID examId = UUID.fromString(event.getAggregateId());
                realtimeEventPublisher.sendToExamStatus(examId, event);
            } catch (IllegalArgumentException ignored) {}
        }

        // 2. Broadcast to tenant channel if applicable
        if (tenantId != null) {
            realtimeEventPublisher.sendToTenant(tenantId, "events", event);
        }

        // 3. User-specific in-app notification & push
        if (payload != null && payload.containsKey("userId")) {
            try {
                UUID userId = UUID.fromString(payload.get("userId").toString());
                String title = payload.getOrDefault("title", event.getEventType()).toString();
                String body = payload.getOrDefault("body", "").toString();

                Notification notification = new Notification();
                notification.setTenantId(tenantId != null ? tenantId : UUID.randomUUID());
                notification.setUserId(userId);
                notification.setType(event.getEventType());
                notification.setTitle(title);
                notification.setBody(body);
                notification.setData(payload);
                notificationRepository.save(notification);

                realtimeEventPublisher.sendToUser(userId, notification);
                pushAdapter.sendPush(userId, title, body, payload);
            } catch (IllegalArgumentException ignored) {}
        }
    }
}

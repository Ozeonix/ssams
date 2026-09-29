package com.artms.notification.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealtimeEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(UUID userId, Object payload) {
        log.debug("Sending real-time notification to user: {}", userId);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", payload);
    }

    public void sendToTenant(UUID tenantId, String channel, Object payload) {
        String destination = "/topic/tenant/" + tenantId + "/" + channel;
        log.debug("Sending real-time event to destination: {}", destination);
        messagingTemplate.convertAndSend(destination, payload);
    }

    public void sendToExamStatus(UUID examId, Object payload) {
        String destination = "/topic/exam/" + examId + "/status";
        log.debug("Sending real-time event to destination: {}", destination);
        messagingTemplate.convertAndSend(destination, payload);
    }

    public void sendToClass(UUID classId, String channel, Object payload) {
        String destination = "/topic/class/" + classId + "/" + channel;
        log.debug("Sending real-time event to destination: {}", destination);
        messagingTemplate.convertAndSend(destination, payload);
    }
}

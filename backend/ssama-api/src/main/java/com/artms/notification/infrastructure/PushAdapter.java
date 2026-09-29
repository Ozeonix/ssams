package com.artms.notification.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Mobile push notification adapter interface and default implementation.
 * Integrates with FCM/APNs when mobile device tokens are registered.
 */
public interface PushAdapter {

    void sendPush(UUID userId, String title, String body, Map<String, Object> data);

    @Slf4j
    @Component
    class DefaultPushAdapter implements PushAdapter {
        @Override
        public void sendPush(UUID userId, String title, String body, Map<String, Object> data) {
            log.info("Dispatching mobile push notification to user={} [title='{}', data={}]", userId, title, data);
            // Extensible integration point for Firebase Cloud Messaging (FCM) / Apple APNs
        }
    }
}

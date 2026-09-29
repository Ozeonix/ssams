package com.artms.notification.web;

import com.artms.notification.application.NotificationResponse;
import com.artms.notification.application.NotificationService;
import com.artms.notification.application.NotificationSyncResponse;
import com.artms.shared.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(notificationService.getMyNotifications(pageable)));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(notificationService.markAsRead(id)));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllAsRead() {
        int count = notificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.of(Map.of("markedRead", count)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUnreadCount() {
        int count = notificationService.getUnreadCount();
        return ResponseEntity.ok(ApiResponse.of(Map.of("unreadCount", count)));
    }

    @GetMapping("/sync")
    public ResponseEntity<ApiResponse<NotificationSyncResponse>> sync(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime since) {
        return ResponseEntity.ok(ApiResponse.of(notificationService.sync(since)));
    }
}

package com.artms.notification.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByTenantIdAndUserIdOrderByCreatedAtDesc(
            UUID tenantId, UUID userId, Pageable pageable);

    List<Notification> findByTenantIdAndUserIdAndCreatedAtAfterOrderByCreatedAtDesc(
            UUID tenantId, UUID userId, OffsetDateTime since);

    int countByTenantIdAndUserIdAndReadAtIsNull(UUID tenantId, UUID userId);

    Optional<Notification> findByTenantIdAndUserIdAndId(UUID tenantId, UUID userId, UUID id);

    @Modifying
    @Query("UPDATE Notification n SET n.readAt = :now WHERE n.tenantId = :tenantId AND n.userId = :userId AND n.readAt IS NULL")
    int markAllRead(@Param("tenantId") UUID tenantId, @Param("userId") UUID userId, @Param("now") OffsetDateTime now);
}

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
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query("""
        SELECT e FROM OutboxEvent e
        WHERE e.publishedAt IS NULL AND e.attempts < :maxAttempts
        ORDER BY e.occurredAt ASC
        """)
    List<OutboxEvent> findPendingEvents(@Param("maxAttempts") int maxAttempts, Pageable pageable);

    @Modifying
    @Query("DELETE FROM OutboxEvent e WHERE e.publishedAt IS NOT NULL AND e.publishedAt < :cutoff")
    int deletePublishedBefore(@Param("cutoff") OffsetDateTime cutoff);
}

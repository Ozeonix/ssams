package com.artms.grading.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradingSchemeRepository extends JpaRepository<GradingScheme, UUID> {

    Optional<GradingScheme> findByTenantIdAndId(UUID tenantId, UUID id);

    List<GradingScheme> findByTenantIdOrderByVersionDesc(UUID tenantId);

    Optional<GradingScheme> findByTenantIdAndStatus(UUID tenantId, GradingSchemeStatus status);

    @Query("""
        SELECT gs FROM GradingScheme gs
        LEFT JOIN FETCH gs.bands
        WHERE gs.id = :id AND gs.tenantId = :tenantId
        """)
    Optional<GradingScheme> findWithBands(@Param("tenantId") UUID tenantId, @Param("id") UUID id);

    boolean existsByTenantIdAndStatus(UUID tenantId, GradingSchemeStatus status);
}

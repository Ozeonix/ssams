package com.artms.identity.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTenantMembershipRepository extends JpaRepository<UserTenantMembership, UUID> {

    @Query("""
        SELECT m FROM UserTenantMembership m
        LEFT JOIN FETCH m.roles r
        LEFT JOIN FETCH r.permissions
        WHERE m.user.id = :userId AND m.tenant.id = :tenantId AND m.status = 'ACTIVE'
        """)
    Optional<UserTenantMembership> findActiveWithRolesAndPermissions(
            @Param("userId") UUID userId,
            @Param("tenantId") UUID tenantId);

    List<UserTenantMembership> findByUserId(UUID userId);

    boolean existsByUserIdAndTenantId(UUID userId, UUID tenantId);
}

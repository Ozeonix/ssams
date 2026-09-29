package com.artms.identity.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        SELECT ua FROM UserAccount ua
        JOIN ua.memberships m
        WHERE m.tenant.id = :tenantId
          AND (ua.username = :identifier OR ua.email = :identifier OR ua.phone = :identifier)
          AND m.status = 'ACTIVE'
        """)
    Optional<UserAccount> findByTenantAndIdentifier(
            @Param("tenantId") UUID tenantId,
            @Param("identifier") String identifier);
}

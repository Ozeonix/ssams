package com.artms.identity.infrastructure;

import lombok.Getter;

import java.util.UUID;

/**
 * Principal stored in Spring Security context.
 * Contains identifiers needed for authorization decisions without hitting DB again.
 */
@Getter
public class ArtmsPrincipal {

    private final UUID userId;
    private final UUID tenantId;
    private final String username;
    private final UUID membershipId;

    public ArtmsPrincipal(UUID userId, UUID tenantId, String username, UUID membershipId) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.username = username;
        this.membershipId = membershipId;
    }

    @Override
    public String toString() {
        return "ArtmsPrincipal{userId=" + userId + ", tenantId=" + tenantId + ", username='" + username + "'}";
    }
}

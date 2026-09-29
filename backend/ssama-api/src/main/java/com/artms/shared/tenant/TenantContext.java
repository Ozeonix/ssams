package com.artms.shared.tenant;

import java.util.UUID;

/**
 * Thread-local holder for the authenticated tenant context.
 * Populated by the security filter chain after authentication.
 * Must be cleared at the end of each request.
 *
 * CRITICAL: Never accept tenant ID from client request body.
 * Always derive from authenticated membership.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<UUID> USER_ID = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(UUID tenantId, UUID userId) {
        TENANT_ID.set(tenantId);
        USER_ID.set(userId);
    }

    public static UUID getTenantId() {
        UUID tenantId = TENANT_ID.get();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant context available for this thread");
        }
        return tenantId;
    }

    public static UUID getTenantIdOrNull() {
        return TENANT_ID.get();
    }

    public static UUID getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        USER_ID.remove();
    }

    public static boolean isSet() {
        return TENANT_ID.get() != null;
    }
}

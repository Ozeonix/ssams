package com.artms.identity.application;

import com.artms.identity.domain.RefreshToken;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionResponse(
        UUID id,
        String deviceInfo,
        String ipAddress,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt
) {
    public static SessionResponse from(RefreshToken rt) {
        return new SessionResponse(
                rt.getId(),
                rt.getDeviceInfo(),
                rt.getIpAddress(),
                rt.getCreatedAt(),
                rt.getExpiresAt()
        );
    }
}

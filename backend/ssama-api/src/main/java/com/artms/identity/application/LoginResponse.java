package com.artms.identity.application;

import java.util.Set;
import java.util.UUID;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    UserSummary user,
    Set<String> permissions
) {}

package com.artms.identity.infrastructure;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * JWT service for generating and parsing access tokens.
 * Tokens contain: sub (userId), tid (tenantId), jti, iat, exp.
 * Sensitive data (permissions) are NOT embedded to keep tokens small;
 * permissions are loaded from DB during authentication.
 */
@Service
@Slf4j
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenExpiryMinutes;

    public JwtService(
            @Value("${artms.jwt.secret}") String secret,
            @Value("${artms.jwt.access-token-expiry-minutes:15}") long accessTokenExpiryMinutes) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMinutes = accessTokenExpiryMinutes;
    }

    public String generateAccessToken(UUID userId, UUID tenantId, String username) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessTokenExpiryMinutes * 60);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claims(Map.of(
                    "tid", tenantId.toString(),
                    "usr", username
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {
        try {
            parseAndValidate(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public UUID extractUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public UUID extractTenantId(Claims claims) {
        return UUID.fromString(claims.get("tid", String.class));
    }

    public String extractUsername(Claims claims) {
        return claims.get("usr", String.class);
    }

    public long getAccessTokenExpiryMinutes() {
        return accessTokenExpiryMinutes;
    }
}

package com.artms.identity.application;

import com.artms.identity.domain.*;
import com.artms.identity.infrastructure.JwtService;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.tenant.domain.Tenant;
import com.artms.tenant.domain.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Authentication application service.
 * Handles login, token refresh, and logout.
 * Enforces account lockout after configured failed attempts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserTenantMembershipRepository membershipRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${artms.security.rate-limit.login-max-attempts:5}")
    private int maxLoginAttempts;

    @Value("${artms.jwt.refresh-token-expiry-days:30}")
    private long refreshTokenExpiryDays;

    @Transactional
    public LoginResponse login(LoginRequest request, String deviceInfo, String ipAddress) {
        // 1. Resolve tenant by code
        Tenant tenant = tenantRepository.findByCode(request.tenantCode())
                .orElseThrow(() -> new BusinessRuleException("INVALID_CREDENTIALS",
                        "Invalid credentials"));

        if (!tenant.isActive()) {
            throw new BusinessRuleException("TENANT_SUSPENDED", "Institution access is suspended");
        }

        // 2. Find user within this tenant
        UserAccount user = userAccountRepository.findByTenantAndIdentifier(
                tenant.getId(), request.username())
                .orElseThrow(() -> new BusinessRuleException("INVALID_CREDENTIALS",
                        "Invalid credentials"));

        // 3. Check account lockout
        if (user.isLocked()) {
            throw new BusinessRuleException("ACCOUNT_LOCKED",
                    "Account is temporarily locked due to multiple failed login attempts");
        }

        if (!user.isActive()) {
            throw new BusinessRuleException("ACCOUNT_INACTIVE", "Account is inactive");
        }

        // 4. Verify password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.incrementFailedAttempts();
            if (user.getFailedAttempts() >= maxLoginAttempts) {
                user.setStatus(UserStatus.LOCKED);
                user.setLockedUntil(OffsetDateTime.now().plusMinutes(30));
                log.warn("Account locked after {} failed attempts: userId={}", maxLoginAttempts, user.getId());
            }
            userAccountRepository.save(user);
            throw new BusinessRuleException("INVALID_CREDENTIALS", "Invalid credentials");
        }

        // 5. Reset failed attempts on success
        user.resetFailedAttempts();
        user.setLastLoginAt(OffsetDateTime.now());
        userAccountRepository.save(user);

        // 6. Load membership with roles/permissions
        UserTenantMembership membership = membershipRepository
                .findActiveWithRolesAndPermissions(user.getId(), tenant.getId())
                .orElseThrow(() -> new BusinessRuleException("NO_MEMBERSHIP",
                        "No active membership for this institution"));

        // 7. Issue tokens
        String accessToken = jwtService.generateAccessToken(
                user.getId(), tenant.getId(), user.getUsername());

        String rawRefreshToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setTenantId(tenant.getId());
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setExpiresAt(OffsetDateTime.now().plusDays(refreshTokenExpiryDays));
        refreshTokenRepository.save(refreshToken);

        // 8. Build permission set for response
        Set<String> permissions = membership.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet());

        log.info("Login successful: userId={}, tenantId={}", user.getId(), tenant.getId());

        return new LoginResponse(
                accessToken,
                rawRefreshToken,
                (long) jwtService.getAccessTokenExpiryMinutes() * 60,
                new UserSummary(user.getId(), user.getUsername(), user.getEmail()),
                permissions
        );
    }

    @Transactional
    public RefreshResponse refresh(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessRuleException("INVALID_TOKEN", "Invalid or expired token"));

        if (!refreshToken.isValid()) {
            throw new BusinessRuleException("INVALID_TOKEN", "Token has expired or been revoked");
        }

        UserAccount user = userAccountRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BusinessRuleException("INVALID_TOKEN", "User not found"));

        if (!user.isActive()) {
            throw new BusinessRuleException("ACCOUNT_INACTIVE", "Account is inactive");
        }

        // Rotate: revoke old, issue new
        refreshToken.setRevokedAt(OffsetDateTime.now());
        refreshTokenRepository.save(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(
                user.getId(), refreshToken.getTenantId(), user.getUsername());

        String newRawRefreshToken = UUID.randomUUID().toString();
        String newTokenHash = hashToken(newRawRefreshToken);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setUserId(user.getId());
        newRefreshToken.setTenantId(refreshToken.getTenantId());
        newRefreshToken.setTokenHash(newTokenHash);
        newRefreshToken.setDeviceInfo(refreshToken.getDeviceInfo());
        newRefreshToken.setIpAddress(refreshToken.getIpAddress());
        newRefreshToken.setExpiresAt(OffsetDateTime.now().plusDays(refreshTokenExpiryDays));
        refreshTokenRepository.save(newRefreshToken);

        return new RefreshResponse(newAccessToken, newRawRefreshToken,
                (long) jwtService.getAccessTokenExpiryMinutes() * 60);
    }

    @Transactional
    public void logout(String rawRefreshToken, UUID userId, UUID tenantId) {
        String tokenHash = hashToken(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(rt -> {
                    rt.setRevokedAt(OffsetDateTime.now());
                    refreshTokenRepository.save(rt);
                });
        log.info("Logout: userId={}, tenantId={}", userId, tenantId);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}

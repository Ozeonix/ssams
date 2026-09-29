package com.artms.identity.web;

import com.artms.identity.application.*;
import com.artms.identity.infrastructure.ArtmsPrincipal;
import com.artms.shared.web.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST controller.
 * POST /api/v1/auth/login   — login with tenantCode + username + password
 * POST /api/v1/auth/refresh — rotate refresh token
 * POST /api/v1/auth/logout  — revoke session
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = resolveClientIp(httpRequest);

        LoginResponse response = authService.login(request, deviceInfo, ipAddress);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(
            @RequestBody RefreshTokenRequest request) {

        RefreshResponse response = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody RefreshTokenRequest request,
            @AuthenticationPrincipal ArtmsPrincipal principal) {

        authService.logout(request.refreshToken(), principal.getUserId(), principal.getTenantId());
        return ResponseEntity.noContent().build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

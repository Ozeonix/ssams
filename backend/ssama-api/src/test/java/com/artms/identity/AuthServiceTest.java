package com.artms.identity;

import com.artms.identity.application.*;
import com.artms.identity.domain.*;
import com.artms.identity.infrastructure.JwtService;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.tenant.domain.Tenant;
import com.artms.tenant.domain.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private UserTenantMembershipRepository membershipRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UUID tenantId;
    private UUID userId;
    private Tenant tenant;
    private UserAccount user;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        userId = UUID.randomUUID();

        tenant = new Tenant();
        tenant.setId(tenantId);
        tenant.setCode("CMIS");
        tenant.setName("Central Model International School");

        user = new UserAccount();
        user.setId(userId);
        user.setUsername("john.doe");
        user.setEmail("john.doe@example.com");
        user.setPasswordHash("hashed_pw");
        user.setStatus(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should successfully initiate password reset and return token")
    void testInitiatePasswordReset() {
        when(tenantRepository.findByCode("CMIS")).thenReturn(Optional.of(tenant));
        when(userAccountRepository.findByTenantAndIdentifier(tenantId, "john.doe")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

        ForgotPasswordRequest req = new ForgotPasswordRequest("CMIS", "john.doe");
        String resetToken = authService.initiatePasswordReset(req);

        assertThat(resetToken).isNotBlank();
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should successfully reset password with valid token and revoke sessions")
    void testResetPasswordSuccess() {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setId(UUID.randomUUID());
        resetToken.setUserId(userId);
        resetToken.setTenantId(tenantId);
        resetToken.setExpiresAt(OffsetDateTime.now().plusHours(1));

        when(passwordResetTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(resetToken));
        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewSecretPassword123")).thenReturn("new_hashed_pw");

        ResetPasswordRequest req = new ResetPasswordRequest("sample_token", "NewSecretPassword123");
        authService.resetPassword(req);

        assertThat(user.getPasswordHash()).isEqualTo("new_hashed_pw");
        assertThat(resetToken.isUsed()).isTrue();
        verify(refreshTokenRepository, times(1)).revokeAllByUserAndTenant(eq(userId), eq(tenantId), any());
    }

    @Test
    @DisplayName("Should reject password reset if token is expired")
    void testResetPasswordExpiredToken() {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setId(UUID.randomUUID());
        resetToken.setUserId(userId);
        resetToken.setTenantId(tenantId);
        resetToken.setExpiresAt(OffsetDateTime.now().minusMinutes(5));

        when(passwordResetTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(resetToken));

        ResetPasswordRequest req = new ResetPasswordRequest("expired_token", "NewSecretPassword123");
        assertThatThrownBy(() -> authService.resetPassword(req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("Should list active sessions for user")
    void testListSessions() {
        RefreshToken rt1 = new RefreshToken();
        rt1.setId(UUID.randomUUID());
        rt1.setDeviceInfo("Mozilla/5.0 Chrome");
        rt1.setIpAddress("192.168.1.1");
        rt1.setExpiresAt(OffsetDateTime.now().plusDays(10));
        rt1.setCreatedAt(OffsetDateTime.now());

        when(refreshTokenRepository.findByUserIdAndTenantIdAndRevokedAtIsNullOrderByCreatedAtDesc(userId, tenantId))
                .thenReturn(List.of(rt1));

        List<SessionResponse> sessions = authService.listSessions(userId, tenantId);
        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).deviceInfo()).isEqualTo("Mozilla/5.0 Chrome");
    }

    @Test
    @DisplayName("Should revoke specific session")
    void testRevokeSession() {
        UUID sessionId = UUID.randomUUID();
        RefreshToken rt = new RefreshToken();
        rt.setId(sessionId);

        when(refreshTokenRepository.findByIdAndUserIdAndTenantId(sessionId, userId, tenantId))
                .thenReturn(Optional.of(rt));

        authService.revokeSession(sessionId, userId, tenantId);
        assertThat(rt.isRevoked()).isTrue();
        verify(refreshTokenRepository, times(1)).save(rt);
    }
}

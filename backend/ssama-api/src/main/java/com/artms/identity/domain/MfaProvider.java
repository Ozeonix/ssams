package com.artms.identity.domain;

import java.util.UUID;

/**
 * MFA extension point for multi-factor authentication strategies (TOTP, SMS OTP, Email OTP).
 */
public interface MfaProvider {

    /**
     * Determines whether multi-factor authentication is required for the user.
     */
    boolean isMfaRequired(UserAccount user);

    /**
     * Generates a secret or challenge token for setting up MFA.
     */
    String generateSecret(UUID userId);

    /**
     * Validates a provided MFA verification code.
     */
    boolean verifyCode(UUID userId, String code);
}

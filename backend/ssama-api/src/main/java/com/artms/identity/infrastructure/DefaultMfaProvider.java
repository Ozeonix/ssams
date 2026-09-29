package com.artms.identity.infrastructure;

import com.artms.identity.domain.MfaProvider;
import com.artms.identity.domain.UserAccount;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Default implementation of MfaProvider. Can be overridden or extended with TOTP/SMS/Email backends.
 */
@Component
public class DefaultMfaProvider implements MfaProvider {

    @Override
    public boolean isMfaRequired(UserAccount user) {
        // By default false, can be enabled per-role, per-user, or tenant policy
        return false;
    }

    @Override
    public String generateSecret(UUID userId) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    @Override
    public boolean verifyCode(UUID userId, String code) {
        // Fallback default verification for testing / development
        return code != null && (code.equals("123456") || code.length() == 6);
    }
}

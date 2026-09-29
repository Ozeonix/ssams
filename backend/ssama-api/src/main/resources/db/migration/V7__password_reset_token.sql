-- V7: Password Reset Token Schema

CREATE TABLE password_reset_token (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    token_hash VARCHAR(512) NOT NULL UNIQUE,
    user_id    UUID         NOT NULL REFERENCES user_account(id),
    tenant_id  UUID         NOT NULL REFERENCES tenant(id),
    expires_at TIMESTAMPTZ  NOT NULL,
    used_at    TIMESTAMPTZ,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_password_reset_token_hash ON password_reset_token (token_hash);
CREATE INDEX idx_password_reset_user ON password_reset_token (user_id, tenant_id);

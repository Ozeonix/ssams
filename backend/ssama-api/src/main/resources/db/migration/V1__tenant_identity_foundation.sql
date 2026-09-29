-- V1: Tenant and Identity Foundation
-- Migration: V1__tenant_identity_foundation.sql

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- TENANT
-- ============================================================
CREATE TABLE tenant (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(64) NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    status      VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, INACTIVE
    timezone    VARCHAR(64)  NOT NULL DEFAULT 'Asia/Kathmandu',
    locale      VARCHAR(16)  NOT NULL DEFAULT 'en',
    branding    JSONB,
    modules     JSONB,        -- enabled feature flags
    settings    JSONB,        -- generic institution settings
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT tenant_status_chk CHECK (status IN ('ACTIVE','SUSPENDED','INACTIVE'))
);

CREATE INDEX idx_tenant_code ON tenant (code);
CREATE INDEX idx_tenant_status ON tenant (status);

-- ============================================================
-- USER ACCOUNT
-- ============================================================
CREATE TABLE user_account (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    username        VARCHAR(128) NOT NULL UNIQUE,
    email           VARCHAR(255),
    phone           VARCHAR(32),
    password_hash   VARCHAR(512) NOT NULL,
    status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, LOCKED, INACTIVE
    failed_attempts INTEGER      NOT NULL DEFAULT 0,
    locked_until    TIMESTAMPTZ,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT user_status_chk CHECK (status IN ('ACTIVE','LOCKED','INACTIVE')),
    CONSTRAINT user_email_or_phone CHECK (email IS NOT NULL OR phone IS NOT NULL)
);

CREATE INDEX idx_user_email ON user_account (email) WHERE email IS NOT NULL;
CREATE INDEX idx_user_phone ON user_account (phone) WHERE phone IS NOT NULL;
CREATE INDEX idx_user_status ON user_account (status);

-- ============================================================
-- USER TENANT MEMBERSHIP
-- ============================================================
CREATE TABLE user_tenant_membership (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL REFERENCES tenant(id),
    user_id     UUID        NOT NULL REFERENCES user_account(id),
    status      VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, REVOKED
    joined_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, user_id),
    CONSTRAINT membership_status_chk CHECK (status IN ('ACTIVE','INACTIVE','REVOKED'))
);

CREATE INDEX idx_membership_tenant_user ON user_tenant_membership (tenant_id, user_id);
CREATE INDEX idx_membership_user ON user_tenant_membership (user_id);

-- ============================================================
-- REFRESH TOKEN
-- ============================================================
CREATE TABLE refresh_token (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    token_hash      VARCHAR(512) NOT NULL UNIQUE,
    user_id         UUID         NOT NULL REFERENCES user_account(id),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    device_info     VARCHAR(512),
    ip_address      VARCHAR(64),
    expires_at      TIMESTAMPTZ  NOT NULL,
    revoked_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_token_hash ON refresh_token (token_hash);
CREATE INDEX idx_refresh_token_user ON refresh_token (user_id);
CREATE INDEX idx_refresh_token_expires ON refresh_token (expires_at);

-- ============================================================
-- ROLE
-- ============================================================
CREATE TABLE role (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        REFERENCES tenant(id),  -- NULL = platform role
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(128) NOT NULL,
    description TEXT,
    is_system   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, code)
);

CREATE INDEX idx_role_tenant ON role (tenant_id);

-- ============================================================
-- PERMISSION
-- ============================================================
CREATE TABLE permission (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(128) NOT NULL UNIQUE,  -- e.g. result:approve
    description TEXT,
    domain      VARCHAR(64)  NOT NULL          -- e.g. result
);

CREATE INDEX idx_permission_domain ON permission (domain);

-- ============================================================
-- ROLE <-> PERMISSION
-- ============================================================
CREATE TABLE role_permission (
    role_id       UUID NOT NULL REFERENCES role(id),
    permission_id UUID NOT NULL REFERENCES permission(id),
    PRIMARY KEY (role_id, permission_id)
);

-- ============================================================
-- USER ROLE (via membership)
-- ============================================================
CREATE TABLE user_role (
    membership_id UUID NOT NULL REFERENCES user_tenant_membership(id),
    role_id       UUID NOT NULL REFERENCES role(id),
    PRIMARY KEY (membership_id, role_id)
);

CREATE INDEX idx_user_role_membership ON user_role (membership_id);

-- ============================================================
-- AUDIT LOG
-- ============================================================
CREATE TABLE audit_log (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID        REFERENCES tenant(id),
    actor_user_id   UUID        REFERENCES user_account(id),
    action          VARCHAR(128) NOT NULL,
    entity_type     VARCHAR(128) NOT NULL,
    entity_id       VARCHAR(128),
    before_data     JSONB,
    after_data      JSONB,
    reason          TEXT,
    correlation_id  UUID,
    ip_hash         VARCHAR(128),
    metadata        JSONB,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_tenant_created ON audit_log (tenant_id, created_at DESC);
CREATE INDEX idx_audit_entity ON audit_log (entity_type, entity_id);
CREATE INDEX idx_audit_actor ON audit_log (actor_user_id);
CREATE INDEX idx_audit_correlation ON audit_log (correlation_id);

-- ============================================================
-- OUTBOX EVENT
-- ============================================================
CREATE TABLE outbox_event (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID        REFERENCES tenant(id),
    aggregate_type  VARCHAR(128) NOT NULL,
    aggregate_id    VARCHAR(128) NOT NULL,
    event_type      VARCHAR(128) NOT NULL,
    payload         JSONB        NOT NULL,
    occurred_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    published_at    TIMESTAMPTZ,
    attempts        INTEGER      NOT NULL DEFAULT 0,
    last_error      TEXT
);

CREATE INDEX idx_outbox_unpublished ON outbox_event (occurred_at) WHERE published_at IS NULL;
CREATE INDEX idx_outbox_tenant ON outbox_event (tenant_id, occurred_at);

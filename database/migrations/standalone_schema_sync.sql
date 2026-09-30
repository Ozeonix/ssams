-- ============================================================================
-- SSAMS / ARTMS – Standalone Migration DDL Synchronization
-- database/migrations/standalone_schema_sync.sql
--
-- Purpose:
--   This file provides an IDEMPOTENT, standalone DDL script that can be used
--   to synchronise an external/legacy PostgreSQL database to the current ARTMS
--   schema – WITHOUT using Flyway.  Useful for:
--     • CI schema-drift detection
--     • Manual inspection / review
--     • Legacy database migration path
--
-- Usage:
--   psql -h <host> -U <user> -d <db> -f standalone_schema_sync.sql
--
-- Safety:
--   All statements use CREATE TABLE IF NOT EXISTS, ADD COLUMN IF NOT EXISTS,
--   and CREATE INDEX IF NOT EXISTS – so running this against an already-migrated
--   database is safe.
-- ============================================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- V1: TENANT & IDENTITY FOUNDATION
-- ============================================================================

CREATE TABLE IF NOT EXISTS tenant (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(64)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    status      VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    timezone    VARCHAR(64)  NOT NULL DEFAULT 'Asia/Kathmandu',
    locale      VARCHAR(16)  NOT NULL DEFAULT 'en',
    branding    JSONB,
    modules     JSONB,
    settings    JSONB,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT tenant_status_chk CHECK (status IN ('ACTIVE','SUSPENDED','INACTIVE'))
);
CREATE INDEX IF NOT EXISTS idx_tenant_code   ON tenant (code);
CREATE INDEX IF NOT EXISTS idx_tenant_status ON tenant (status);

CREATE TABLE IF NOT EXISTS user_account (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    username        VARCHAR(128) NOT NULL UNIQUE,
    email           VARCHAR(255),
    phone           VARCHAR(32),
    password_hash   VARCHAR(512) NOT NULL,
    status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    failed_attempts INTEGER      NOT NULL DEFAULT 0,
    locked_until    TIMESTAMPTZ,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT user_status_chk       CHECK (status IN ('ACTIVE','LOCKED','INACTIVE')),
    CONSTRAINT user_email_or_phone   CHECK (email IS NOT NULL OR phone IS NOT NULL)
);
CREATE INDEX IF NOT EXISTS idx_user_email  ON user_account (email)  WHERE email IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_user_phone  ON user_account (phone)  WHERE phone IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_user_status ON user_account (status);

CREATE TABLE IF NOT EXISTS user_tenant_membership (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL REFERENCES tenant(id),
    user_id     UUID        NOT NULL REFERENCES user_account(id),
    status      VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    joined_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, user_id)
);

CREATE TABLE IF NOT EXISTS app_role (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        REFERENCES tenant(id),
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(128) NOT NULL,
    is_system   BOOLEAN      NOT NULL DEFAULT false,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, code)
);

CREATE TABLE IF NOT EXISTS permission (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(128) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS role_permission (
    role_id       UUID NOT NULL REFERENCES app_role(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS user_role (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    role_id     UUID        NOT NULL REFERENCES app_role(id) ON DELETE CASCADE,
    tenant_id   UUID        REFERENCES tenant(id),
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, role_id, tenant_id)
);

CREATE TABLE IF NOT EXISTS refresh_token (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    token_hash      VARCHAR(512) NOT NULL UNIQUE,
    device_info     VARCHAR(512),
    ip_address      VARCHAR(64),
    issued_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    expires_at      TIMESTAMPTZ  NOT NULL,
    revoked_at      TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user   ON refresh_token (user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token_hash   ON refresh_token (token_hash);
CREATE INDEX IF NOT EXISTS idx_refresh_token_expiry ON refresh_token (expires_at) WHERE revoked_at IS NULL;

CREATE TABLE IF NOT EXISTS password_reset_token (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    token_hash  VARCHAR(512) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ  NOT NULL,
    used_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_prt_user_id  ON password_reset_token (user_id);
CREATE INDEX IF NOT EXISTS idx_prt_expiry   ON password_reset_token (expires_at) WHERE used_at IS NULL;

-- ============================================================================
-- V2: ACADEMIC CORE
-- ============================================================================

CREATE TABLE IF NOT EXISTS academic_year (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL REFERENCES tenant(id),
    name        VARCHAR(64)  NOT NULL,
    start_date  DATE         NOT NULL,
    end_date    DATE         NOT NULL,
    status      VARCHAR(32)  NOT NULL DEFAULT 'PLANNING',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, name),
    CONSTRAINT academic_year_dates_chk  CHECK (end_date > start_date),
    CONSTRAINT academic_year_status_chk CHECK (status IN ('PLANNING','ACTIVE','COMPLETED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_academic_year_tenant ON academic_year (tenant_id, status);

CREATE TABLE IF NOT EXISTS term (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL REFERENCES tenant(id),
    academic_year_id UUID         NOT NULL REFERENCES academic_year(id),
    name             VARCHAR(64)  NOT NULL,
    sequence         INTEGER      NOT NULL DEFAULT 1,
    start_date       DATE,
    end_date         DATE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (academic_year_id, sequence)
);
CREATE INDEX IF NOT EXISTS idx_term_academic_year ON term (tenant_id, academic_year_id);

CREATE TABLE IF NOT EXISTS department (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL REFERENCES tenant(id),
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(255) NOT NULL,
    status      VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, code)
);
CREATE INDEX IF NOT EXISTS idx_department_tenant ON department (tenant_id, status);

CREATE TABLE IF NOT EXISTS program (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    department_id   UUID         NOT NULL REFERENCES department(id),
    code            VARCHAR(64)  NOT NULL,
    name            VARCHAR(255) NOT NULL,
    duration_years  INTEGER,
    level           VARCHAR(32),
    status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, code)
);
CREATE INDEX IF NOT EXISTS idx_program_tenant     ON program (tenant_id, status);
CREATE INDEX IF NOT EXISTS idx_program_department ON program (department_id);

CREATE TABLE IF NOT EXISTS class_section (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    program_id      UUID         NOT NULL REFERENCES program(id),
    academic_year_id UUID        NOT NULL REFERENCES academic_year(id),
    name            VARCHAR(64)  NOT NULL,
    capacity        INTEGER,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, program_id, academic_year_id, name)
);
CREATE INDEX IF NOT EXISTS idx_class_section_tenant ON class_section (tenant_id, academic_year_id);

CREATE TABLE IF NOT EXISTS subject (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL REFERENCES tenant(id),
    department_id UUID       REFERENCES department(id),
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(255) NOT NULL,
    credit_hours NUMERIC(4,2),
    subject_type VARCHAR(32) NOT NULL DEFAULT 'THEORY',
    status      VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, code)
);
CREATE INDEX IF NOT EXISTS idx_subject_tenant ON subject (tenant_id, status);

CREATE TABLE IF NOT EXISTS grading_scheme (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL REFERENCES tenant(id),
    name        VARCHAR(128) NOT NULL,
    is_default  BOOLEAN      NOT NULL DEFAULT false,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, name)
);

CREATE TABLE IF NOT EXISTS grade_band (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    grading_scheme_id UUID       NOT NULL REFERENCES grading_scheme(id) ON DELETE CASCADE,
    grade_letter     VARCHAR(8)   NOT NULL,
    min_percentage   NUMERIC(5,2) NOT NULL,
    max_percentage   NUMERIC(5,2) NOT NULL,
    grade_point      NUMERIC(3,2) NOT NULL,
    remarks          VARCHAR(64),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ============================================================================
-- V3: STUDENT & ENROLLMENT
-- ============================================================================

CREATE TABLE IF NOT EXISTS student (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID         NOT NULL REFERENCES tenant(id),
    student_code        VARCHAR(64)  NOT NULL,
    user_id             UUID         REFERENCES user_account(id),
    first_name          VARCHAR(128) NOT NULL,
    middle_name         VARCHAR(128),
    last_name           VARCHAR(128) NOT NULL,
    date_of_birth       DATE,
    gender              VARCHAR(16),
    nationality         VARCHAR(64),
    phone               VARCHAR(32),
    email               VARCHAR(255),
    address             JSONB,
    status              VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    admission_date      DATE         NOT NULL DEFAULT CURRENT_DATE,
    photo_url           VARCHAR(512),
    documents           JSONB,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, student_code),
    CONSTRAINT student_status_chk CHECK (status IN ('ACTIVE','SUSPENDED','GRADUATED','TRANSFERRED','WITHDRAWN'))
);
CREATE INDEX IF NOT EXISTS idx_student_tenant      ON student (tenant_id, status);
CREATE INDEX IF NOT EXISTS idx_student_code        ON student (tenant_id, student_code);
CREATE INDEX IF NOT EXISTS idx_student_user        ON student (user_id) WHERE user_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_student_name_search ON student USING gin(to_tsvector('english', first_name || ' ' || last_name));

CREATE TABLE IF NOT EXISTS guardian (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    student_id      UUID         NOT NULL REFERENCES student(id) ON DELETE CASCADE,
    first_name      VARCHAR(128) NOT NULL,
    last_name       VARCHAR(128) NOT NULL,
    relationship    VARCHAR(64),
    phone           VARCHAR(32),
    email           VARCHAR(255),
    is_primary      BOOLEAN      NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_guardian_student ON guardian (student_id);

CREATE TABLE IF NOT EXISTS enrollment (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    student_id      UUID         NOT NULL REFERENCES student(id),
    class_section_id UUID        NOT NULL REFERENCES class_section(id),
    academic_year_id UUID        NOT NULL REFERENCES academic_year(id),
    term_id         UUID         REFERENCES term(id),
    enrolled_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (student_id, class_section_id, academic_year_id),
    CONSTRAINT enrollment_status_chk CHECK (status IN ('ACTIVE','DROPPED','COMPLETED','TRANSFERRED'))
);
CREATE INDEX IF NOT EXISTS idx_enrollment_student       ON enrollment (student_id, status);
CREATE INDEX IF NOT EXISTS idx_enrollment_class_section ON enrollment (class_section_id, status);

-- ============================================================================
-- V4: EXAMS, MARKS & RESULTS
-- ============================================================================

CREATE TABLE IF NOT EXISTS exam (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    academic_year_id UUID        NOT NULL REFERENCES academic_year(id),
    term_id         UUID         REFERENCES term(id),
    name            VARCHAR(255) NOT NULL,
    exam_type       VARCHAR(64)  NOT NULL DEFAULT 'TERMINAL',
    start_date      DATE,
    end_date        DATE,
    status          VARCHAR(32)  NOT NULL DEFAULT 'DRAFT',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS exam_subject (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_id         UUID         NOT NULL REFERENCES exam(id) ON DELETE CASCADE,
    subject_id      UUID         NOT NULL REFERENCES subject(id),
    full_marks      NUMERIC(6,2) NOT NULL,
    pass_marks      NUMERIC(6,2) NOT NULL,
    exam_date       DATE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (exam_id, subject_id)
);

CREATE TABLE IF NOT EXISTS student_mark (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    exam_subject_id UUID         NOT NULL REFERENCES exam_subject(id) ON DELETE CASCADE,
    student_id      UUID         NOT NULL REFERENCES student(id),
    marks_obtained  NUMERIC(6,2),
    is_absent       BOOLEAN      NOT NULL DEFAULT false,
    is_exempt       BOOLEAN      NOT NULL DEFAULT false,
    submitted_at    TIMESTAMPTZ,
    verified_at     TIMESTAMPTZ,
    verified_by     UUID         REFERENCES user_account(id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (exam_subject_id, student_id)
);
CREATE INDEX IF NOT EXISTS idx_student_mark_student ON student_mark (student_id);
CREATE INDEX IF NOT EXISTS idx_student_mark_exam    ON student_mark (exam_subject_id);

CREATE TABLE IF NOT EXISTS result_snapshot (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    student_id      UUID         NOT NULL REFERENCES student(id),
    exam_id         UUID         NOT NULL REFERENCES exam(id),
    gpa             NUMERIC(4,3),
    total_marks     NUMERIC(8,2),
    percentage      NUMERIC(5,2),
    rank            INTEGER,
    status          VARCHAR(32)  NOT NULL DEFAULT 'DRAFT',
    published_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (student_id, exam_id)
);

-- ============================================================================
-- V5: ATTENDANCE, TIMETABLE, NOTICES, DOCUMENTS
-- ============================================================================

CREATE TABLE IF NOT EXISTS attendance_session (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    class_section_id UUID        NOT NULL REFERENCES class_section(id),
    subject_id      UUID         REFERENCES subject(id),
    teacher_id      UUID         REFERENCES user_account(id),
    session_date    DATE         NOT NULL,
    start_time      TIME,
    end_time        TIME,
    status          VARCHAR(32)  NOT NULL DEFAULT 'OPEN',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_attendance_session_class ON attendance_session (class_section_id, session_date);
CREATE INDEX IF NOT EXISTS idx_attendance_session_date  ON attendance_session (tenant_id, session_date);

CREATE TABLE IF NOT EXISTS attendance_record (
    id                    UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    attendance_session_id UUID         NOT NULL REFERENCES attendance_session(id) ON DELETE CASCADE,
    student_id            UUID         NOT NULL REFERENCES student(id),
    status                VARCHAR(16)  NOT NULL DEFAULT 'PRESENT',
    remarks               VARCHAR(255),
    recorded_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    corrected_at          TIMESTAMPTZ,
    corrected_by          UUID         REFERENCES user_account(id),
    UNIQUE (attendance_session_id, student_id),
    CONSTRAINT attendance_status_chk CHECK (status IN ('PRESENT','ABSENT','LATE','EXCUSED'))
);
CREATE INDEX IF NOT EXISTS idx_attendance_record_student ON attendance_record (student_id);

CREATE TABLE IF NOT EXISTS document_template (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    name            VARCHAR(255) NOT NULL,
    template_type   VARCHAR(64)  NOT NULL,
    content         TEXT,
    is_active       BOOLEAN      NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS generated_document (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    student_id      UUID         NOT NULL REFERENCES student(id),
    template_id     UUID         REFERENCES document_template(id),
    document_type   VARCHAR(64)  NOT NULL,
    file_url        VARCHAR(512),
    verification_code VARCHAR(64) UNIQUE,
    generated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    expires_at      TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS idx_generated_document_student ON generated_document (student_id);

CREATE TABLE IF NOT EXISTS notification_outbox (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    recipient_id    UUID         NOT NULL REFERENCES user_account(id),
    event_type      VARCHAR(128) NOT NULL,
    payload         JSONB        NOT NULL,
    status          VARCHAR(32)  NOT NULL DEFAULT 'PENDING',
    attempts        INTEGER      NOT NULL DEFAULT 0,
    scheduled_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    sent_at         TIMESTAMPTZ,
    failed_at       TIMESTAMPTZ,
    error_message   TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_notification_outbox_status ON notification_outbox (status, scheduled_at)
    WHERE status = 'PENDING';

-- ============================================================================
-- V6: PLATFORM ROLES & PERMISSIONS SEED
-- ============================================================================

INSERT INTO permission (code, description)
VALUES
    ('PERM_student:create',    'Create student records'),
    ('PERM_student:read',      'Read student records'),
    ('PERM_student:update',    'Update student records'),
    ('PERM_student:delete',    'Delete student records'),
    ('PERM_enrollment:manage', 'Manage student enrollments'),
    ('PERM_attendance:mark',   'Mark attendance sessions'),
    ('PERM_attendance:read',   'Read attendance reports'),
    ('PERM_exam:manage',       'Manage exam setup and marks'),
    ('PERM_result:publish',    'Publish student results'),
    ('PERM_document:generate', 'Generate student documents'),
    ('PERM_tenant:manage',     'Manage tenant settings'),
    ('PERM_user:manage',       'Manage user accounts'),
    ('PERM_role:manage',       'Manage roles and permissions'),
    ('PERM_report:view',       'View system reports')
ON CONFLICT (code) DO NOTHING;

-- ============================================================================
-- V8: STUDENT FEE & PAYMENT SYSTEM
-- ============================================================================

CREATE TABLE IF NOT EXISTS fee_category (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL REFERENCES tenant(id),
    code        VARCHAR(64) NOT NULL,
    name        VARCHAR(128) NOT NULL,
    description VARCHAR(512),
    is_active   BOOLEAN     NOT NULL DEFAULT true,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, code)
);
CREATE INDEX IF NOT EXISTS idx_fee_category_tenant ON fee_category (tenant_id, is_active);

CREATE TABLE IF NOT EXISTS fee_structure (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL REFERENCES tenant(id),
    fee_category_id  UUID         NOT NULL REFERENCES fee_category(id),
    academic_year_id UUID         REFERENCES academic_year(id),
    program_id       UUID         REFERENCES program(id),
    class_section_id UUID         REFERENCES class_section(id),
    amount           NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
    due_date         DATE,
    late_fee_per_day NUMERIC(8,2) DEFAULT 0,
    is_active        BOOLEAN      NOT NULL DEFAULT true,
    created_by       UUID         REFERENCES user_account(id),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_fee_structure_tenant    ON fee_structure (tenant_id, is_active);
CREATE INDEX IF NOT EXISTS idx_fee_structure_acad_year ON fee_structure (tenant_id, academic_year_id);

CREATE TABLE IF NOT EXISTS fee_invoice (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_number   VARCHAR(64)  NOT NULL UNIQUE,
    tenant_id        UUID         NOT NULL REFERENCES tenant(id),
    student_id       UUID         NOT NULL REFERENCES student(id),
    academic_year_id UUID         REFERENCES academic_year(id),
    invoice_date     DATE         NOT NULL DEFAULT CURRENT_DATE,
    due_date         DATE,
    subtotal         NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount_amount  NUMERIC(12,2) NOT NULL DEFAULT 0,
    late_fee_amount  NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_amount     NUMERIC(12,2) NOT NULL DEFAULT 0,
    paid_amount      NUMERIC(12,2) NOT NULL DEFAULT 0,
    balance          NUMERIC(12,2) NOT NULL DEFAULT 0,
    status           VARCHAR(32)  NOT NULL DEFAULT 'DRAFT',
    notes            TEXT,
    issued_by        UUID         REFERENCES user_account(id),
    issued_at        TIMESTAMPTZ,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT fee_invoice_status_chk CHECK (
        status IN ('DRAFT','ISSUED','PARTIALLY_PAID','PAID','OVERDUE','CANCELLED')
    ),
    CONSTRAINT fee_invoice_balance_chk CHECK (balance >= 0),
    CONSTRAINT fee_invoice_paid_chk    CHECK (paid_amount >= 0),
    CONSTRAINT fee_invoice_total_chk   CHECK (total_amount >= 0)
);
CREATE INDEX IF NOT EXISTS idx_fee_invoice_student ON fee_invoice (student_id, status);
CREATE INDEX IF NOT EXISTS idx_fee_invoice_tenant  ON fee_invoice (tenant_id, status, due_date);
CREATE INDEX IF NOT EXISTS idx_fee_invoice_number  ON fee_invoice (invoice_number);

CREATE TABLE IF NOT EXISTS fee_invoice_item (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id      UUID         NOT NULL REFERENCES fee_invoice(id) ON DELETE CASCADE,
    fee_category_id UUID         NOT NULL REFERENCES fee_category(id),
    description     VARCHAR(512),
    quantity        INTEGER      NOT NULL DEFAULT 1,
    unit_amount     NUMERIC(12,2) NOT NULL,
    total_amount    NUMERIC(12,2) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_invoice_item_invoice ON fee_invoice_item (invoice_id);

CREATE TABLE IF NOT EXISTS payment_gateway_config (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID        NOT NULL REFERENCES tenant(id),
    gateway_code    VARCHAR(64) NOT NULL,
    display_name    VARCHAR(128) NOT NULL,
    environment     VARCHAR(32) NOT NULL DEFAULT 'SANDBOX',
    merchant_id     VARCHAR(256),
    config_json     JSONB,
    is_active       BOOLEAN     NOT NULL DEFAULT false,
    created_by      UUID        REFERENCES user_account(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, gateway_code),
    CONSTRAINT gateway_environment_chk CHECK (environment IN ('SANDBOX','PRODUCTION'))
);
CREATE INDEX IF NOT EXISTS idx_gateway_config_tenant ON payment_gateway_config (tenant_id, is_active);

CREATE TABLE IF NOT EXISTS payment_transaction (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_ref     VARCHAR(128) NOT NULL UNIQUE,
    tenant_id           UUID         NOT NULL REFERENCES tenant(id),
    student_id          UUID         NOT NULL REFERENCES student(id),
    invoice_id          UUID         NOT NULL REFERENCES fee_invoice(id),
    gateway_code        VARCHAR(64)  NOT NULL DEFAULT 'ESEWA',
    amount              NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    currency            VARCHAR(8)   NOT NULL DEFAULT 'NPR',
    status              VARCHAR(32)  NOT NULL DEFAULT 'CREATED',
    gateway_order_id    VARCHAR(256),
    gateway_txn_ref     VARCHAR(256),
    gateway_response    JSONB,
    initiated_at        TIMESTAMPTZ,
    completed_at        TIMESTAMPTZ,
    expired_at          TIMESTAMPTZ,
    failure_reason      VARCHAR(512),
    initiated_by_user   UUID         REFERENCES user_account(id),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT payment_txn_status_chk CHECK (
        status IN ('CREATED','INITIATED','PENDING','SUCCESS','FAILED','CANCELLED','EXPIRED','REFUND_PENDING','REFUNDED')
    )
);
CREATE INDEX IF NOT EXISTS idx_payment_txn_student ON payment_transaction (student_id, status);
CREATE INDEX IF NOT EXISTS idx_payment_txn_invoice ON payment_transaction (invoice_id, status);
CREATE INDEX IF NOT EXISTS idx_payment_txn_ref     ON payment_transaction (transaction_ref);

CREATE TABLE IF NOT EXISTS student_ledger_entry (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    student_id      UUID         NOT NULL REFERENCES student(id),
    invoice_id      UUID         REFERENCES fee_invoice(id),
    transaction_id  UUID         REFERENCES payment_transaction(id),
    entry_type      VARCHAR(32)  NOT NULL,
    amount          NUMERIC(12,2) NOT NULL,
    description     VARCHAR(512),
    reference       VARCHAR(128),
    balance_after   NUMERIC(12,2) NOT NULL,
    created_by      UUID         REFERENCES user_account(id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ledger_entry_type_chk CHECK (
        entry_type IN ('CHARGE','PAYMENT','ADJUSTMENT','DISCOUNT','LATE_FEE','REFUND','REVERSAL')
    )
);
CREATE INDEX IF NOT EXISTS idx_ledger_student ON student_ledger_entry (student_id, created_at DESC);

CREATE TABLE IF NOT EXISTS payment_receipt (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    receipt_number  VARCHAR(64)  NOT NULL UNIQUE,
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    transaction_id  UUID         NOT NULL UNIQUE REFERENCES payment_transaction(id),
    student_id      UUID         NOT NULL REFERENCES student(id),
    invoice_id      UUID         NOT NULL REFERENCES fee_invoice(id),
    amount          NUMERIC(12,2) NOT NULL,
    payment_method  VARCHAR(64)  NOT NULL DEFAULT 'ESEWA',
    gateway_ref     VARCHAR(256),
    issued_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    receipt_data    JSONB        NOT NULL DEFAULT '{}'
);
CREATE INDEX IF NOT EXISTS idx_receipt_student     ON payment_receipt (student_id);
CREATE INDEX IF NOT EXISTS idx_receipt_transaction ON payment_receipt (transaction_id);
CREATE INDEX IF NOT EXISTS idx_receipt_number      ON payment_receipt (receipt_number);

CREATE TABLE IF NOT EXISTS refund (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    transaction_id  UUID         NOT NULL REFERENCES payment_transaction(id),
    student_id      UUID         NOT NULL REFERENCES student(id),
    invoice_id      UUID         REFERENCES fee_invoice(id),
    amount          NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    reason          TEXT         NOT NULL,
    status          VARCHAR(32)  NOT NULL DEFAULT 'PENDING',
    requested_by    UUID         NOT NULL REFERENCES user_account(id),
    approved_by     UUID         REFERENCES user_account(id),
    gateway_ref     VARCHAR(256),
    notes           TEXT,
    requested_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    approved_at     TIMESTAMPTZ,
    processed_at    TIMESTAMPTZ,
    CONSTRAINT refund_status_chk CHECK (
        status IN ('PENDING','APPROVED','PROCESSING','COMPLETED','REJECTED','CANCELLED')
    )
);
CREATE INDEX IF NOT EXISTS idx_refund_transaction ON refund (transaction_id);
CREATE INDEX IF NOT EXISTS idx_refund_student     ON refund (student_id, status);

CREATE TABLE IF NOT EXISTS reconciliation_batch (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID        NOT NULL REFERENCES tenant(id),
    gateway_code    VARCHAR(64) NOT NULL,
    period_from     DATE        NOT NULL,
    period_to       DATE        NOT NULL,
    status          VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    total_gateway   INTEGER     DEFAULT 0,
    total_local     INTEGER     DEFAULT 0,
    matched         INTEGER     DEFAULT 0,
    mismatched      INTEGER     DEFAULT 0,
    missing_local   INTEGER     DEFAULT 0,
    missing_gateway INTEGER     DEFAULT 0,
    initiated_by    UUID        REFERENCES user_account(id),
    completed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT recon_status_chk CHECK (status IN ('PENDING','RUNNING','COMPLETED','FAILED'))
);
CREATE INDEX IF NOT EXISTS idx_recon_batch_tenant ON reconciliation_batch (tenant_id, period_from);

CREATE TABLE IF NOT EXISTS reconciliation_item (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    batch_id            UUID        NOT NULL REFERENCES reconciliation_batch(id) ON DELETE CASCADE,
    transaction_id      UUID        REFERENCES payment_transaction(id),
    gateway_txn_ref     VARCHAR(256),
    local_amount        NUMERIC(12,2),
    gateway_amount      NUMERIC(12,2),
    reconciliation_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT recon_item_status_chk CHECK (
        reconciliation_status IN ('MATCHED','AMOUNT_MISMATCH','MISSING_LOCAL','MISSING_GATEWAY','DUPLICATE','PENDING')
    )
);
CREATE INDEX IF NOT EXISTS idx_recon_item_batch ON reconciliation_item (batch_id, reconciliation_status);

INSERT INTO permission (code, description) VALUES
    ('PERM_fee:manage',              'Create and manage fee structures'),
    ('PERM_fee:read',                'Read fee structures and categories'),
    ('PERM_invoice:create',          'Create fee invoices'),
    ('PERM_invoice:read',            'Read fee invoices'),
    ('PERM_invoice:cancel',          'Cancel fee invoices'),
    ('PERM_payment:initiate',        'Initiate payment for own invoice'),
    ('PERM_payment:read',            'Read payment transactions'),
    ('PERM_payment:manage',          'Manage all payment transactions'),
    ('PERM_receipt:read',            'Read payment receipts'),
    ('PERM_refund:request',          'Request refund'),
    ('PERM_refund:approve',          'Approve refund'),
    ('PERM_reconciliation:manage',   'Run and manage reconciliation'),
    ('PERM_gateway:configure',       'Configure payment gateway settings'),
    ('PERM_ledger:read',             'Read student ledger entries')
ON CONFLICT (code) DO NOTHING;

-- ============================================================================
-- Flyway metadata stub (skip if using Flyway directly)
-- ============================================================================
-- If applying this script to a database that Flyway will later manage,
-- insert a baseline record to prevent Flyway from trying to re-apply migrations:
--
--   INSERT INTO flyway_schema_history (
--       installed_rank, version, description, type, script,
--       checksum, installed_by, execution_time, success
--   ) VALUES (
--       1, '8', 'Standalone baseline', 'BASELINE', 'standalone_schema_sync.sql',
--       0, current_user, 0, true
--   ) ON CONFLICT DO NOTHING;

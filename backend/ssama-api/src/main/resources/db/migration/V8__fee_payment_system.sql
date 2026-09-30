-- V8: Student Fee & Payment System
-- Migration: V8__fee_payment_system.sql
-- Adds all payment/fee tables without touching existing student/academic tables.

-- ============================================================
-- FEE CATEGORIES  (configurable by institution admin)
-- ============================================================
CREATE TABLE fee_category (
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
CREATE INDEX idx_fee_category_tenant ON fee_category (tenant_id, is_active);

-- ============================================================
-- FEE STRUCTURES  (amount per program/class per academic year)
-- ============================================================
CREATE TABLE fee_structure (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL REFERENCES tenant(id),
    fee_category_id  UUID         NOT NULL REFERENCES fee_category(id),
    academic_year_id UUID         REFERENCES academic_year(id),
    program_id       UUID         REFERENCES program(id),        -- NULL = applies to all
    class_section_id UUID         REFERENCES class_group(id),    -- NULL = applies to all sections
    amount           NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
    due_date         DATE,
    late_fee_per_day NUMERIC(8,2) DEFAULT 0,
    is_active        BOOLEAN      NOT NULL DEFAULT true,
    created_by       UUID         REFERENCES user_account(id),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_fee_structure_tenant      ON fee_structure (tenant_id, is_active);
CREATE INDEX idx_fee_structure_acad_year   ON fee_structure (tenant_id, academic_year_id);
CREATE INDEX idx_fee_structure_program     ON fee_structure (program_id);

-- ============================================================
-- FEE INVOICES
-- ============================================================
CREATE TABLE fee_invoice (
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
CREATE INDEX idx_fee_invoice_student ON fee_invoice (student_id, status);
CREATE INDEX idx_fee_invoice_tenant  ON fee_invoice (tenant_id, status, due_date);
CREATE INDEX idx_fee_invoice_number  ON fee_invoice (invoice_number);

-- ============================================================
-- FEE INVOICE LINE ITEMS
-- ============================================================
CREATE TABLE fee_invoice_item (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id      UUID         NOT NULL REFERENCES fee_invoice(id) ON DELETE CASCADE,
    fee_category_id UUID         NOT NULL REFERENCES fee_category(id),
    description     VARCHAR(512),
    quantity        INTEGER      NOT NULL DEFAULT 1,
    unit_amount     NUMERIC(12,2) NOT NULL,
    total_amount    NUMERIC(12,2) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_invoice_item_invoice ON fee_invoice_item (invoice_id);

-- ============================================================
-- PAYMENT GATEWAY CONFIGS  (one per gateway per tenant)
-- ============================================================
CREATE TABLE payment_gateway_config (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID        NOT NULL REFERENCES tenant(id),
    gateway_code    VARCHAR(64) NOT NULL,   -- 'ESEWA', 'KHALTI', etc.
    display_name    VARCHAR(128) NOT NULL,
    environment     VARCHAR(32) NOT NULL DEFAULT 'SANDBOX', -- SANDBOX, PRODUCTION
    merchant_id     VARCHAR(256),            -- stored encrypted in application
    config_json     JSONB,                   -- non-secret config keys
    is_active       BOOLEAN     NOT NULL DEFAULT false,
    created_by      UUID        REFERENCES user_account(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, gateway_code),
    CONSTRAINT gateway_environment_chk CHECK (environment IN ('SANDBOX','PRODUCTION'))
);
CREATE INDEX idx_gateway_config_tenant ON payment_gateway_config (tenant_id, is_active);

-- ============================================================
-- PAYMENT TRANSACTIONS  (one per payment attempt)
-- ============================================================
CREATE TABLE payment_transaction (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_ref     VARCHAR(128) NOT NULL UNIQUE, -- our internal ref
    tenant_id           UUID         NOT NULL REFERENCES tenant(id),
    student_id          UUID         NOT NULL REFERENCES student(id),
    invoice_id          UUID         NOT NULL REFERENCES fee_invoice(id),
    gateway_code        VARCHAR(64)  NOT NULL DEFAULT 'ESEWA',
    amount              NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    currency            VARCHAR(8)   NOT NULL DEFAULT 'NPR',
    status              VARCHAR(32)  NOT NULL DEFAULT 'CREATED',
    gateway_order_id    VARCHAR(256),  -- eSewa product_code or order id
    gateway_txn_ref     VARCHAR(256),  -- eSewa transaction_uuid / reference
    gateway_response    JSONB,          -- raw gateway response
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
CREATE INDEX idx_payment_txn_student   ON payment_transaction (student_id, status);
CREATE INDEX idx_payment_txn_invoice   ON payment_transaction (invoice_id, status);
CREATE INDEX idx_payment_txn_ref       ON payment_transaction (transaction_ref);
CREATE INDEX idx_payment_txn_gateway   ON payment_transaction (gateway_code, gateway_txn_ref)
    WHERE gateway_txn_ref IS NOT NULL;

-- ============================================================
-- PAYMENT VERIFICATION LOGS  (all verification attempts)
-- ============================================================
CREATE TABLE payment_verification_log (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id      UUID        NOT NULL REFERENCES payment_transaction(id),
    verification_type   VARCHAR(32) NOT NULL DEFAULT 'CALLBACK', -- CALLBACK, POLLING
    request_payload     JSONB,
    response_payload    JSONB,
    http_status         INTEGER,
    verification_result VARCHAR(32), -- SUCCESS, FAILED, AMOUNT_MISMATCH, etc.
    verified_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_verification_log_txn ON payment_verification_log (transaction_id);

-- ============================================================
-- PAYMENT ALLOCATIONS  (links transaction → invoice, supports partial)
-- ============================================================
CREATE TABLE payment_allocation (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    transaction_id  UUID         NOT NULL REFERENCES payment_transaction(id),
    invoice_id      UUID         NOT NULL REFERENCES fee_invoice(id),
    allocated_amount NUMERIC(12,2) NOT NULL CHECK (allocated_amount > 0),
    allocated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (transaction_id, invoice_id)
);
CREATE INDEX idx_payment_allocation_txn     ON payment_allocation (transaction_id);
CREATE INDEX idx_payment_allocation_invoice ON payment_allocation (invoice_id);

-- ============================================================
-- STUDENT LEDGER ENTRIES  (immutable financial history)
-- ============================================================
CREATE TABLE student_ledger_entry (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    student_id      UUID         NOT NULL REFERENCES student(id),
    invoice_id      UUID         REFERENCES fee_invoice(id),
    transaction_id  UUID         REFERENCES payment_transaction(id),
    entry_type      VARCHAR(32)  NOT NULL,  -- CHARGE, PAYMENT, ADJUSTMENT, DISCOUNT, LATE_FEE, REFUND, REVERSAL
    amount          NUMERIC(12,2) NOT NULL, -- positive = charge, negative = credit
    description     VARCHAR(512),
    reference       VARCHAR(128), -- external reference
    balance_after   NUMERIC(12,2) NOT NULL, -- running balance snapshot
    created_by      UUID         REFERENCES user_account(id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ledger_entry_type_chk CHECK (
        entry_type IN ('CHARGE','PAYMENT','ADJUSTMENT','DISCOUNT','LATE_FEE','REFUND','REVERSAL')
    )
);
CREATE INDEX idx_ledger_student ON student_ledger_entry (student_id, created_at DESC);
CREATE INDEX idx_ledger_invoice  ON student_ledger_entry (invoice_id) WHERE invoice_id IS NOT NULL;

-- ============================================================
-- PAYMENT RECEIPTS
-- ============================================================
CREATE TABLE payment_receipt (
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
    receipt_data    JSONB         NOT NULL DEFAULT '{}'  -- snapshot for reproducibility
);
CREATE INDEX idx_receipt_student     ON payment_receipt (student_id);
CREATE INDEX idx_receipt_transaction ON payment_receipt (transaction_id);
CREATE INDEX idx_receipt_number      ON payment_receipt (receipt_number);

-- ============================================================
-- REFUNDS
-- ============================================================
CREATE TABLE refund (
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
CREATE INDEX idx_refund_transaction ON refund (transaction_id);
CREATE INDEX idx_refund_student     ON refund (student_id, status);

-- ============================================================
-- RECONCILIATION BATCHES
-- ============================================================
CREATE TABLE reconciliation_batch (
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
CREATE INDEX idx_recon_batch_tenant ON reconciliation_batch (tenant_id, period_from);

-- ============================================================
-- RECONCILIATION ITEMS  (one row per transaction comparison)
-- ============================================================
CREATE TABLE reconciliation_item (
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
CREATE INDEX idx_recon_item_batch ON reconciliation_item (batch_id, reconciliation_status);

-- ============================================================
-- PAYMENT AUDIT LOG  (immutable, append-only)
-- ============================================================
CREATE TABLE payment_audit_log (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID        NOT NULL REFERENCES tenant(id),
    actor_id        UUID        REFERENCES user_account(id),
    action          VARCHAR(128) NOT NULL,
    entity_type     VARCHAR(64)  NOT NULL,  -- INVOICE, TRANSACTION, REFUND, etc.
    entity_id       UUID         NOT NULL,
    before_state    JSONB,
    after_state     JSONB,
    ip_address      VARCHAR(64),
    occurred_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_payment_audit_entity ON payment_audit_log (entity_type, entity_id);
CREATE INDEX idx_payment_audit_tenant ON payment_audit_log (tenant_id, occurred_at DESC);

-- ============================================================
-- ADD PAYMENT PERMISSIONS (extend existing V6 seed)
-- ============================================================
INSERT INTO permission (code, description, domain) VALUES
    ('PERM_fee:manage',              'Create and manage fee structures',     'fee'),
    ('PERM_fee:read',                'Read fee structures and categories',   'fee'),
    ('PERM_invoice:create',          'Create fee invoices',                  'invoice'),
    ('PERM_invoice:read',            'Read fee invoices',                    'invoice'),
    ('PERM_invoice:cancel',          'Cancel fee invoices',                  'invoice'),
    ('PERM_payment:initiate',        'Initiate payment for own invoice',     'payment'),
    ('PERM_payment:read',            'Read payment transactions',            'payment'),
    ('PERM_payment:manage',          'Manage all payment transactions',      'payment'),
    ('PERM_receipt:read',            'Read payment receipts',                'receipt'),
    ('PERM_refund:request',          'Request refund',                       'refund'),
    ('PERM_refund:approve',          'Approve refund',                       'refund'),
    ('PERM_reconciliation:manage',   'Run and manage reconciliation',        'reconciliation'),
    ('PERM_gateway:configure',       'Configure payment gateway settings',   'gateway'),
    ('PERM_ledger:read',             'Read student ledger entries',          'ledger')
ON CONFLICT (code) DO NOTHING;

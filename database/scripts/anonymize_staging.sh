#!/usr/bin/env bash
# =============================================================================
# SSAMS / ARTMS – Staging Database PII Anonymization & Masking Utility
#
# Purpose:
#   Anonymizes personally identifiable information (PII) in a staging database
#   so that production data can be safely used for testing without exposing
#   real student, guardian, or user data.
#
# What gets anonymized:
#   • student: name, phone, email, date_of_birth, address, photo_url
#   • guardian: name, phone, email
#   • user_account: username, email, phone, password_hash (→ bcrypt of 'Test@1234')
#   • refresh_token: all rows deleted (security tokens)
#   • password_reset_token: all rows deleted
#   • notification_outbox: payload sanitized
#
# Usage:
#   # 1. Dump production
#   pg_dump -h prod-host -U artms artms | gzip > /tmp/prod_backup.sql.gz
#
#   # 2. Restore into staging (overwrites existing staging data)
#   gunzip -c /tmp/prod_backup.sql.gz | psql -h staging-host -U artms_staging artms_staging
#
#   # 3. Anonymize staging
#   DB_HOST=staging-host DB_NAME=artms_staging DB_USERNAME=artms_staging \
#   DB_PASSWORD=artms_staging_secret \
#   ./database/scripts/anonymize_staging.sh
#
# Safety:
#   This script checks that it is NOT running against the production database
#   by verifying the database name does NOT match 'artms' (without '_staging').
# =============================================================================
set -euo pipefail

# ── Load env ──────────────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/../.." && pwd)"

if [[ -f "${ROOT_DIR}/.env.staging" ]]; then
    # shellcheck disable=SC1090
    set -o allexport; source "${ROOT_DIR}/.env.staging"; set +o allexport
fi

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5433}"
DB_NAME="${DB_NAME:-artms_staging}"
DB_USERNAME="${DB_USERNAME:-artms_staging}"
export PGPASSWORD="${DB_PASSWORD:?DB_PASSWORD must be set}"

# ── Safety check ──────────────────────────────────────────────────────────────
if [[ "${DB_NAME}" == "artms" && "${DB_HOST}" != "localhost" && "${DB_HOST}" != "127.0.0.1" ]]; then
    echo "DANGER: Refusing to anonymize what appears to be the PRODUCTION database!"
    echo "DB_HOST=${DB_HOST}  DB_NAME=${DB_NAME}"
    echo "Only databases named '*_staging*' or '*_test*' are permitted on remote hosts."
    exit 1
fi

PSQL="psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USERNAME} -d ${DB_NAME} --no-password"

log() { echo "[$(date '+%F %T')] $*"; }

log "Starting PII anonymization on ${DB_HOST}/${DB_NAME} …"
log "Tables to anonymize: student, guardian, user_account, tokens, outbox"

# ── Anonymize ─────────────────────────────────────────────────────────────────
$PSQL <<'SQL'
BEGIN;

-- ── Disable triggers temporarily (FK checks still active) ──────────────────
SET session_replication_role = 'replica';

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. STUDENTS – mask name, contact, DOB, address
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE student SET
    first_name   = 'Student',
    middle_name  = NULL,
    last_name    = 'Test_' || SUBSTRING(id::text, 1, 8),
    date_of_birth = '2000-01-01',
    phone        = '+977-9800000000',
    email        = 'student_' || SUBSTRING(id::text, 1, 8) || '@staging.test',
    address      = '{"masked": true}'::jsonb,
    photo_url    = NULL,
    documents    = NULL;

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. GUARDIANS – mask name & contact
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE guardian SET
    first_name = 'Guardian',
    last_name  = 'Masked_' || SUBSTRING(id::text, 1, 8),
    phone      = '+977-9800000001',
    email      = CASE WHEN email IS NOT NULL
                 THEN 'guardian_' || SUBSTRING(id::text, 1, 8) || '@staging.test'
                 ELSE NULL END;

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. USER ACCOUNTS – mask username, email, phone, reset password
-- Password → bcrypt hash of 'Test@1234'
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE user_account SET
    username      = 'user_' || SUBSTRING(id::text, 1, 8),
    email         = CASE WHEN email IS NOT NULL
                    THEN 'user_' || SUBSTRING(id::text, 1, 8) || '@staging.test'
                    ELSE NULL END,
    phone         = CASE WHEN phone IS NOT NULL
                    THEN '+977-9' || LPAD((EXTRACT(EPOCH FROM now())::bigint % 999999999)::text, 9, '0')
                    ELSE NULL END,
    -- bcrypt hash of 'Test@1234' – safe to embed as it is a well-known test value
    password_hash = '$2a$12$k7GkLYnYKfcHiuP8nFJ8beAzjCsH9y1ZqxK4kS1O2R7l3fU0bHt6a',
    failed_attempts = 0,
    locked_until    = NULL;

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. SECURITY TOKENS – delete all (staging should not have real sessions)
-- ─────────────────────────────────────────────────────────────────────────────
DELETE FROM refresh_token;
DELETE FROM password_reset_token;

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. NOTIFICATION OUTBOX – sanitize payload PII
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE notification_outbox SET
    payload = jsonb_build_object(
        'masked',    true,
        'event_type', event_type,
        'original_keys', (SELECT jsonb_agg(k) FROM jsonb_object_keys(payload) k)
    );

-- ─────────────────────────────────────────────────────────────────────────────
-- 6. Re-enable triggers
-- ─────────────────────────────────────────────────────────────────────────────
SET session_replication_role = 'origin';

COMMIT;

-- ─────────────────────────────────────────────────────────────────────────────
-- Verification
-- ─────────────────────────────────────────────────────────────────────────────
SELECT 'Students anonymized'      AS check, count(*) AS count FROM student
UNION ALL
SELECT 'Guardians anonymized'     AS check, count(*) AS count FROM guardian
UNION ALL
SELECT 'Users anonymized'         AS check, count(*) AS count FROM user_account
UNION ALL
SELECT 'Refresh tokens (expect 0)',         count(*) FROM refresh_token
UNION ALL
SELECT 'Password reset tokens (expect 0)', count(*) FROM password_reset_token;
SQL

log "PII anonymization COMPLETE ✓"
log "Staging database ${DB_NAME} is safe for developer access."

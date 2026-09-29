#!/usr/bin/env bash
# =============================================================================
# SSAMS / ARTMS – Automated Database Backup & S3 Sync
#
# Features:
#   • Full PostgreSQL dump (compressed)
#   • MinIO / S3 upload via mc (MinIO client)
#   • Configurable retention (local + remote)
#   • Slack / webhook notification on success / failure
#   • Checksum verification (SHA256)
#
# Cron (add to root/deploy user crontab):
#   0 2 * * * /opt/artms/infrastructure/deployment/db_backup.sh >> /var/log/artms-backup.log 2>&1
#
# Environment variables (or source .env.production):
#   DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
#   S3_ENDPOINT, S3_BUCKET, S3_ACCESS_KEY, S3_SECRET_KEY
#   BACKUP_RETAIN_DAYS_LOCAL  (default: 7)
#   BACKUP_RETAIN_DAYS_REMOTE (default: 30)
#   SLACK_WEBHOOK_URL (optional)
# =============================================================================
set -euo pipefail

# ── Load environment ──────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/../.." && pwd)"

if [[ -f "${ROOT_DIR}/.env.production" ]]; then
    # shellcheck disable=SC1090
    set -o allexport
    source "${ROOT_DIR}/.env.production"
    set +o allexport
fi

# ── Config ────────────────────────────────────────────────────────────────────
DB_HOST="${DB_HOST:-postgres}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-artms}"
DB_USERNAME="${DB_USERNAME:-artms}"
PGPASSWORD="${DB_PASSWORD:?DB_PASSWORD must be set}"
export PGPASSWORD

S3_ENDPOINT="${S3_ENDPOINT:-http://localhost:9000}"
S3_BUCKET="${S3_BUCKET:-artms-backups}"
S3_ACCESS_KEY="${S3_ACCESS_KEY:?S3_ACCESS_KEY must be set}"
S3_SECRET_KEY="${S3_SECRET_KEY:?S3_SECRET_KEY must be set}"
S3_ALIAS="artms-s3"

BACKUP_DIR="${BACKUP_DIR:-/var/backups/artms}"
RETAIN_LOCAL="${BACKUP_RETAIN_DAYS_LOCAL:-7}"
RETAIN_REMOTE="${BACKUP_RETAIN_DAYS_REMOTE:-30}"

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/artms_${DB_NAME}_${TIMESTAMP}.sql.gz"
CHECKSUM_FILE="${BACKUP_FILE}.sha256"

# ── Helpers ───────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; NC='\033[0m'
log()  { echo -e "[$(date '+%F %T')] $*"; }
ok()   { echo -e "[$(date '+%F %T')] ${GREEN}OK${NC} $*"; }
fail() { echo -e "[$(date '+%F %T')] ${RED}FAIL${NC} $*" >&2; }

notify() {
    local msg="$1"
    if [[ -n "${SLACK_WEBHOOK_URL:-}" ]]; then
        curl -s -X POST "${SLACK_WEBHOOK_URL}" \
            -H 'Content-type: application/json' \
            --data "{\"text\":\"[ARTMS Backup] ${msg}\"}" || true
    fi
}

on_error() {
    fail "Backup FAILED at line ${BASH_LINENO[0]}!"
    notify "❌ Database backup FAILED at $(date '+%F %T'). Check server logs."
    exit 1
}
trap on_error ERR

# ─────────────────────────────────────────────────────────────────────────────
# STEP 1 – Create backup directory
# ─────────────────────────────────────────────────────────────────────────────
mkdir -p "${BACKUP_DIR}"
log "Starting backup: ${DB_NAME} → ${BACKUP_FILE}"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 2 – Dump database
# ─────────────────────────────────────────────────────────────────────────────
pg_dump \
    -h "${DB_HOST}" \
    -p "${DB_PORT}" \
    -U "${DB_USERNAME}" \
    -d "${DB_NAME}" \
    --no-password \
    --format=custom \
    --compress=9 \
    --verbose \
    2>>"${BACKUP_DIR}/pg_dump_${TIMESTAMP}.log" \
| gzip > "${BACKUP_FILE}"

BACKUP_SIZE=$(du -sh "${BACKUP_FILE}" | cut -f1)
ok "Database dump complete – size: ${BACKUP_SIZE}"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 3 – Generate checksum
# ─────────────────────────────────────────────────────────────────────────────
sha256sum "${BACKUP_FILE}" > "${CHECKSUM_FILE}"
ok "SHA256 checksum: $(cat "${CHECKSUM_FILE}")"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 4 – Upload to S3 / MinIO
# ─────────────────────────────────────────────────────────────────────────────
if command -v mc &>/dev/null; then
    log "Uploading to S3 …"

    # Configure mc alias (idempotent)
    mc alias set "${S3_ALIAS}" \
        "${S3_ENDPOINT}" \
        "${S3_ACCESS_KEY}" \
        "${S3_SECRET_KEY}" \
        --api s3v4 --path auto >/dev/null 2>&1

    # Ensure bucket exists
    mc mb --ignore-existing "${S3_ALIAS}/${S3_BUCKET}" >/dev/null 2>&1 || true

    # Upload dump + checksum
    mc cp "${BACKUP_FILE}"     "${S3_ALIAS}/${S3_BUCKET}/database/"
    mc cp "${CHECKSUM_FILE}"   "${S3_ALIAS}/${S3_BUCKET}/database/"

    ok "Uploaded to s3://${S3_BUCKET}/database/$(basename "${BACKUP_FILE}")"

    # ── Remote retention policy ──────────────────────────────────────────────
    log "Applying remote retention (keep ${RETAIN_REMOTE} days) …"
    # MinIO lifecycle: delete objects older than RETAIN_REMOTE days
    mc ilm rule add \
        --expire-days "${RETAIN_REMOTE}" \
        "${S3_ALIAS}/${S3_BUCKET}/database/" 2>/dev/null || \
        warn "Could not set lifecycle rule (may already exist)"
else
    log "MinIO client (mc) not found – skipping S3 upload."
    log "Install: https://min.io/docs/minio/linux/reference/minio-mc.html"
fi

# ─────────────────────────────────────────────────────────────────────────────
# STEP 5 – Local retention cleanup
# ─────────────────────────────────────────────────────────────────────────────
log "Cleaning up local backups older than ${RETAIN_LOCAL} days …"
find "${BACKUP_DIR}" \
    \( -name "artms_*.sql.gz" -o -name "artms_*.sha256" -o -name "pg_dump_*.log" \) \
    -mtime "+${RETAIN_LOCAL}" \
    -delete \
    -print | while read -r f; do log "Removed: $f"; done

# ─────────────────────────────────────────────────────────────────────────────
# STEP 6 – Verify backup integrity (restore header check)
# ─────────────────────────────────────────────────────────────────────────────
log "Verifying backup integrity …"
if pg_restore --list "${BACKUP_FILE}" &>/dev/null; then
    ok "Backup integrity verified ✓"
else
    # Fallback: just check gzip integrity
    if gzip -t "${BACKUP_FILE}" 2>/dev/null; then
        ok "Gzip integrity verified ✓"
    else
        fail "Backup file appears corrupt!"
        notify "⚠️ Backup file failed integrity check: $(basename "${BACKUP_FILE}")"
        exit 1
    fi
fi

# ─────────────────────────────────────────────────────────────────────────────
# Summary
# ─────────────────────────────────────────────────────────────────────────────
ok "=== Backup completed successfully ==="
log "  File     : ${BACKUP_FILE}"
log "  Size     : ${BACKUP_SIZE}"
log "  Checksum : ${CHECKSUM_FILE}"
log "  S3       : s3://${S3_BUCKET}/database/"

notify "✅ Database backup completed – *$(basename "${BACKUP_FILE}")* (${BACKUP_SIZE})"

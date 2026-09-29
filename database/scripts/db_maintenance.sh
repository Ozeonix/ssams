#!/usr/bin/env bash
# =============================================================================
# SSAMS / ARTMS – Automated Database Maintenance Script
#
# Operations:
#   1. ANALYZE all tables (update planner statistics)
#   2. VACUUM ANALYZE high-churn tables (attendance, notifications, tokens)
#   3. REINDEX tables with significant bloat (>20% dead tuple ratio)
#   4. Bloat report (tables with >10% dead tuples)
#   5. Tablespace size report
#   6. Index usage report (unused indexes)
#
# Cron (weekly, Sunday 03:00):
#   0 3 * * 0 /opt/artms/database/scripts/db_maintenance.sh >> /var/log/artms-db-maintenance.log 2>&1
#
# Environment variables:
#   DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
#   SLACK_WEBHOOK_URL (optional)
#   DRY_RUN=1 (print SQL but don't execute)
# =============================================================================
set -euo pipefail

# ── Load env ──────────────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/../.." && pwd)"

if [[ -f "${ROOT_DIR}/.env.production" ]]; then
    set -o allexport; source "${ROOT_DIR}/.env.production"; set +o allexport
fi

DB_HOST="${DB_HOST:-postgres}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-artms}"
DB_USERNAME="${DB_USERNAME:-artms}"
export PGPASSWORD="${DB_PASSWORD:?DB_PASSWORD must be set}"
DRY_RUN="${DRY_RUN:-0}"

PSQL="psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USERNAME} -d ${DB_NAME} --no-password"
LOG_PREFIX="[$(date '+%F %T')]"

log()    { echo "${LOG_PREFIX} $*"; }
notify() {
    if [[ -n "${SLACK_WEBHOOK_URL:-}" ]]; then
        curl -s -X POST "${SLACK_WEBHOOK_URL}" \
            -H 'Content-type: application/json' \
            --data "{\"text\":\"[ARTMS DB Maintenance] $1\"}" || true
    fi
}

exec_sql() {
    local desc="$1"; local sql="$2"
    log "  → ${desc}"
    if [[ "${DRY_RUN}" == "1" ]]; then
        echo "    [DRY RUN] ${sql}"
    else
        $PSQL -c "${sql}" 2>&1 | sed 's/^/    /'
    fi
}

log "======================================================"
log "ARTMS Database Maintenance – ${DB_HOST}/${DB_NAME}"
log "======================================================"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 1 – ANALYZE all tables (update statistics)
# ─────────────────────────────────────────────────────────────────────────────
log "Step 1: ANALYZE all tables …"
exec_sql "Analyze all" "ANALYZE VERBOSE;"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 2 – VACUUM ANALYZE high-churn tables
# ─────────────────────────────────────────────────────────────────────────────
log "Step 2: VACUUM ANALYZE high-churn tables …"
HIGH_CHURN_TABLES=(
    "attendance_record"
    "notification_outbox"
    "refresh_token"
    "student_mark"
    "enrollment"
    "result_snapshot"
)

for tbl in "${HIGH_CHURN_TABLES[@]}"; do
    exec_sql "VACUUM ANALYZE ${tbl}" "VACUUM ANALYZE ${tbl};"
done

# ─────────────────────────────────────────────────────────────────────────────
# STEP 3 – REINDEX tables with bloat > 20%
# ─────────────────────────────────────────────────────────────────────────────
log "Step 3: Checking for index bloat (>20% dead tuples) …"

BLOATED_TABLES=$($PSQL -t -A -c "
    SELECT relname
    FROM pg_stat_user_tables
    WHERE n_dead_tup > 0
      AND n_live_tup > 0
      AND (n_dead_tup::float / (n_live_tup + n_dead_tup)) > 0.20
    ORDER BY n_dead_tup DESC;
" 2>/dev/null || echo "")

if [[ -z "${BLOATED_TABLES}" ]]; then
    log "  No significant index bloat detected."
else
    log "  Tables with >20% dead tuples:"
    echo "${BLOATED_TABLES}" | while read -r tbl; do
        log "    • ${tbl}"
        exec_sql "REINDEX TABLE CONCURRENTLY ${tbl}" \
            "REINDEX TABLE CONCURRENTLY ${tbl};"
    done
fi

# ─────────────────────────────────────────────────────────────────────────────
# STEP 4 – Bloat report
# ─────────────────────────────────────────────────────────────────────────────
log "Step 4: Table bloat report …"
$PSQL <<'SQL'
SELECT
    relname                               AS table_name,
    n_live_tup                            AS live_rows,
    n_dead_tup                            AS dead_rows,
    ROUND(
        100.0 * n_dead_tup
        / NULLIF(n_live_tup + n_dead_tup, 0), 2
    )                                     AS dead_pct,
    pg_size_pretty(pg_total_relation_size(relid)) AS total_size,
    last_vacuum,
    last_autovacuum
FROM pg_stat_user_tables
WHERE n_dead_tup > 100
ORDER BY dead_pct DESC NULLS LAST
LIMIT 20;
SQL

# ─────────────────────────────────────────────────────────────────────────────
# STEP 5 – Tablespace size report
# ─────────────────────────────────────────────────────────────────────────────
log "Step 5: Table sizes …"
$PSQL <<'SQL'
SELECT
    relname                                                AS table_name,
    pg_size_pretty(pg_total_relation_size(relid))          AS total_size,
    pg_size_pretty(pg_relation_size(relid))                AS table_size,
    pg_size_pretty(pg_total_relation_size(relid)
                   - pg_relation_size(relid))              AS index_size
FROM pg_stat_user_tables
ORDER BY pg_total_relation_size(relid) DESC
LIMIT 20;
SQL

# ─────────────────────────────────────────────────────────────────────────────
# STEP 6 – Index usage report (unused indexes)
# ─────────────────────────────────────────────────────────────────────────────
log "Step 6: Unused indexes (idx_scan = 0, not primary key) …"
$PSQL <<'SQL'
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
JOIN pg_index USING (indexrelid)
WHERE idx_scan = 0
  AND NOT indisprimary
  AND NOT indisunique
ORDER BY pg_relation_size(indexrelid) DESC
LIMIT 20;
SQL

# ─────────────────────────────────────────────────────────────────────────────
# STEP 7 – Long-running autovacuum detection
# ─────────────────────────────────────────────────────────────────────────────
log "Step 7: Checking for blocked autovacuum …"
$PSQL <<'SQL'
SELECT
    pid,
    now() - pg_stat_activity.query_start AS duration,
    query
FROM pg_stat_activity
WHERE state   = 'active'
  AND query   LIKE '%autovacuum%'
  AND now() - pg_stat_activity.query_start > INTERVAL '5 minutes';
SQL

# ─────────────────────────────────────────────────────────────────────────────
# Summary
# ─────────────────────────────────────────────────────────────────────────────
log "======================================================"
log "Maintenance COMPLETE"
log "======================================================"
notify "✅ Weekly database maintenance completed for ${DB_NAME}."

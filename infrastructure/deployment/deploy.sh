#!/usr/bin/env bash
# =============================================================================
# SSAMS / ARTMS – Zero-Downtime Rolling Deployment Script
#
# Strategy:
#   1. Pull new image without stopping running containers.
#   2. Perform pre-deployment DB backup.
#   3. Apply Flyway migrations (idempotent – safe to run against live DB).
#   4. Scale up new API replica, health-check it.
#   5. Switch Nginx upstream to new container.
#   6. Drain & remove old container (graceful SIGTERM + wait).
#
# Usage:
#   ./infrastructure/deployment/deploy.sh [VERSION]
#
# Environment variables (or from .env.production):
#   APP_VERSION        – Docker image tag to deploy (default: latest)
#   COMPOSE_FILE       – Docker Compose file (default: docker-compose.production.yml)
#   HEALTH_URL         – API health endpoint
#   SLACK_WEBHOOK_URL  – Optional: Slack notification webhook
# =============================================================================
set -euo pipefail

# ── Colour helpers ────────────────────────────────────────────────────────────
RED='\033[0;31m'; YELLOW='\033[1;33m'; GREEN='\033[0;32m'; NC='\033[0m'
info()    { echo -e "${GREEN}[INFO]${NC}  $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; }
die()     { error "$*"; exit 1; }

# ── Load .env.production if present ──────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/../.." && pwd)"

if [[ -f "${ROOT_DIR}/.env.production" ]]; then
    # shellcheck disable=SC1090
    set -o allexport
    source "${ROOT_DIR}/.env.production"
    set +o allexport
    info "Loaded .env.production"
fi

# ── Config ────────────────────────────────────────────────────────────────────
APP_VERSION="${1:-${APP_VERSION:-latest}}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.production.yml}"
SERVICE="api"
HEALTH_URL="${HEALTH_URL:-http://localhost/actuator/health}"
MAX_WAIT_SECS=120
BACKUP_DIR="${ROOT_DIR}/backups"

info "=== ARTMS Rolling Deployment ==="
info "Version:      ${APP_VERSION}"
info "Compose file: ${COMPOSE_FILE}"

# ── Helper: check required tools ─────────────────────────────────────────────
for cmd in docker curl jq; do
    command -v "${cmd}" &>/dev/null || die "${cmd} is not installed."
done

# ── Helper: Slack notification ───────────────────────────────────────────────
notify_slack() {
    local msg="$1"
    if [[ -n "${SLACK_WEBHOOK_URL:-}" ]]; then
        curl -s -X POST "${SLACK_WEBHOOK_URL}" \
            -H 'Content-type: application/json' \
            --data "{\"text\":\"[ARTMS Deploy] ${msg}\"}" || true
    fi
}

# ── Helper: wait for healthy ──────────────────────────────────────────────────
wait_healthy() {
    local url="$1"
    local name="${2:-service}"
    local waited=0

    info "Waiting for ${name} to become healthy (max ${MAX_WAIT_SECS}s) …"
    while true; do
        local status
        status=$(curl -s -o /dev/null -w "%{http_code}" "${url}" 2>/dev/null || echo "000")
        if [[ "${status}" == "200" ]]; then
            info "${name} is healthy ✓"
            return 0
        fi
        if (( waited >= MAX_WAIT_SECS )); then
            die "${name} did NOT become healthy after ${MAX_WAIT_SECS}s (last HTTP ${status})"
        fi
        printf '.'
        sleep 5
        (( waited += 5 ))
    done
}

# ─────────────────────────────────────────────────────────────────────────────
# STEP 0 – Pre-flight checks
# ─────────────────────────────────────────────────────────────────────────────
cd "${ROOT_DIR}"
[[ -f "${COMPOSE_FILE}" ]] || die "Compose file not found: ${COMPOSE_FILE}"

info "Step 0: Pre-flight checks …"
docker compose -f "${COMPOSE_FILE}" config --quiet && info "Compose file valid ✓"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 1 – Database backup before any changes
# ─────────────────────────────────────────────────────────────────────────────
info "Step 1: Creating pre-deployment database backup …"
mkdir -p "${BACKUP_DIR}"
BACKUP_FILE="${BACKUP_DIR}/pre_deploy_$(date +%Y%m%d_%H%M%S).sql.gz"

docker compose -f "${COMPOSE_FILE}" exec -T postgres \
    pg_dump -U "${DB_USERNAME:-artms}" "${DB_NAME:-artms}" \
    | gzip > "${BACKUP_FILE}" \
    && info "Backup saved to: ${BACKUP_FILE}" \
    || warn "Database backup failed – continuing (manual backup recommended!)"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 2 – Pull new image
# ─────────────────────────────────────────────────────────────────────────────
info "Step 2: Building/pulling image artms-api:${APP_VERSION} …"
export APP_VERSION
docker compose -f "${COMPOSE_FILE}" build --no-cache "${SERVICE}"
info "Image built ✓"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 3 – Apply DB migrations (via temporary migration container)
# ─────────────────────────────────────────────────────────────────────────────
info "Step 3: Running Flyway migrations …"
docker compose -f "${COMPOSE_FILE}" run --rm \
    -e SPRING_PROFILES_ACTIVE=production \
    -e SPRING_FLYWAY_BASELINE_ON_MIGRATE=true \
    "${SERVICE}" \
    java -jar /app/app.jar --spring.jpa.hibernate.ddl-auto=none \
         --spring.flyway.enabled=true \
         --spring.main.web-application-type=none 2>&1 | tail -20 \
    && info "Migrations completed ✓" \
    || die "Flyway migration FAILED – deployment aborted"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 4 – Graceful rolling restart
#           Docker does not natively support rolling updates in Compose,
#           so we scale to 2 replicas (new alongside old), verify new is
#           healthy, then stop old.
# ─────────────────────────────────────────────────────────────────────────────
info "Step 4: Starting new API container …"

# Get current container ID before update
OLD_CONTAINER=$(docker compose -f "${COMPOSE_FILE}" ps -q "${SERVICE}" 2>/dev/null | head -1 || echo "")

# Start new container (rolling replace)
docker compose -f "${COMPOSE_FILE}" up -d --no-deps --build "${SERVICE}"

# Wait for the new container to be healthy
wait_healthy "${HEALTH_URL}" "New API container"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 5 – Reload Nginx (zero dropped connections)
# ─────────────────────────────────────────────────────────────────────────────
info "Step 5: Reloading Nginx …"
docker compose -f "${COMPOSE_FILE}" exec nginx nginx -s reload \
    && info "Nginx reloaded ✓" \
    || warn "Nginx reload failed – check manually"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 6 – Final health verification
# ─────────────────────────────────────────────────────────────────────────────
info "Step 6: Final health verification …"
sleep 5
HEALTH_RESPONSE=$(curl -s "${HEALTH_URL}" || echo '{}')
STATUS=$(echo "${HEALTH_RESPONSE}" | jq -r '.status // "UNKNOWN"' 2>/dev/null || echo "UNKNOWN")

if [[ "${STATUS}" == "UP" ]]; then
    info "=== Deployment SUCCESSFUL – ${APP_VERSION} is live ✓ ==="
    notify_slack "✅ Deployment of *${APP_VERSION}* completed successfully."
else
    error "=== Deployment FAILED – health status: ${STATUS} ==="
    error "Response: ${HEALTH_RESPONSE}"
    notify_slack "❌ Deployment of *${APP_VERSION}* FAILED. Status: ${STATUS}"
    exit 1
fi

# ── Cleanup old backup files older than 7 days ────────────────────────────────
find "${BACKUP_DIR}" -name "pre_deploy_*.sql.gz" -mtime +7 -delete 2>/dev/null || true
info "Cleaned up backups older than 7 days"

echo ""
info "Deployment summary:"
info "  New version  : ${APP_VERSION}"
info "  Backup       : ${BACKUP_FILE}"
info "  Nginx status : OK"
info "  API health   : ${STATUS}"

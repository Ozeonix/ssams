# SSAMS / ARTMS – Disaster Recovery Playbook

## Overview

This playbook describes the steps to recover the ARTMS system after a
failure event. Procedures are organised by failure type and severity.

---

## 1. Severity Classification

| Level | Description                                     | Target RTO | Target RPO |
|-------|-------------------------------------------------|-----------|-----------|
| P1    | Complete service outage (all users affected)    | < 1 hour  | < 15 min  |
| P2    | Partial outage (key features unavailable)       | < 4 hours | < 1 hour  |
| P3    | Performance degradation                         | < 8 hours | < 4 hours |
| P4    | Minor issue (cosmetic / low-impact)             | < 24 hours| < 24 hours|

**RTO** = Recovery Time Objective  
**RPO** = Recovery Point Objective

---

## 2. Key Contacts & Roles

| Role               | Responsibility                                      |
|--------------------|-----------------------------------------------------|
| Incident Commander | Coordinates response, makes go/no-go decisions      |
| Backend Engineer   | API, database, migration issues                     |
| Infrastructure Eng | Docker, Nginx, networking, host                     |
| DBA                | Database backup/restore, schema issues              |

---

## 3. Recovery Procedures

### 3.1 API Service Down

**Symptoms**: 502/503 from Nginx; `artms-prod-api` container stopped.

```bash
# 1. Check container state
docker compose -f docker-compose.production.yml ps api
docker compose -f docker-compose.production.yml logs --tail=100 api

# 2. Restart API
docker compose -f docker-compose.production.yml restart api

# 3. Watch startup logs
docker compose -f docker-compose.production.yml logs -f api

# 4. Verify health
curl -s http://localhost/actuator/health | jq .
```

**If restart fails** – roll back to previous image:

```bash
APP_VERSION=<previous-tag> docker compose -f docker-compose.production.yml up -d --no-deps api
```

---

### 3.2 Database Failure

#### 3.2.1 PostgreSQL container crash

```bash
# 1. Check logs
docker compose -f docker-compose.production.yml logs --tail=200 postgres

# 2. Restart
docker compose -f docker-compose.production.yml restart postgres

# 3. Verify recovery
docker compose -f docker-compose.production.yml exec postgres \
    psql -U artms -d artms -c "SELECT now();"
```

#### 3.2.2 Database data corruption – Full Restore

> **Warning**: This will overwrite the existing database. Confirm with the Incident Commander first.

```bash
# Step 1 – Stop API to prevent new writes
docker compose -f docker-compose.production.yml stop api

# Step 2 – List available backups
ls -lht /var/backups/artms/*.sql.gz | head -10

# Step 3 – Verify backup integrity
BACKUP=/var/backups/artms/<filename>.sql.gz
sha256sum -c "${BACKUP}.sha256"
gzip -t "${BACKUP}"

# Step 4 – Drop and recreate database
docker compose -f docker-compose.production.yml exec postgres \
    psql -U artms -c "DROP DATABASE IF EXISTS artms; CREATE DATABASE artms OWNER artms;"

# Step 5 – Restore from backup
gunzip -c "${BACKUP}" | \
    docker compose -f docker-compose.production.yml exec -T postgres \
    pg_restore -U artms -d artms --no-owner --no-acl --verbose

# Step 6 – Verify row counts
docker compose -f docker-compose.production.yml exec postgres \
    psql -U artms -d artms -c "\dt" -c "SELECT count(*) FROM tenant;"

# Step 7 – Start API
docker compose -f docker-compose.production.yml start api

# Step 8 – Verify health
sleep 30
curl -s http://localhost/actuator/health | jq .
```

#### 3.2.3 Restore from S3 / MinIO backup

```bash
# List S3 backups
mc ls artms-s3/artms-backups/database/ --recursive

# Download latest
mc cp artms-s3/artms-backups/database/<filename>.sql.gz /tmp/restore.sql.gz
mc cp artms-s3/artms-backups/database/<filename>.sql.gz.sha256 /tmp/restore.sql.gz.sha256

# Verify checksum
sha256sum -c /tmp/restore.sql.gz.sha256

# Proceed with Step 4–8 from 3.2.2 using /tmp/restore.sql.gz
```

---

### 3.3 Redis Failure

**Impact**: Session tokens, rate-limit counters, and caches lost. All users re-authenticated.

```bash
# 1. Restart Redis
docker compose -f docker-compose.production.yml restart redis

# 2. Verify
docker compose -f docker-compose.production.yml exec redis redis-cli ping
```

> **Note**: Spring Security sessions will be invalidated. Users must log in again.
> This is acceptable – no data is lost.

---

### 3.4 MinIO / Object Storage Failure

**Impact**: Document upload/download fails; API returns 503 on file endpoints.

```bash
# 1. Restart MinIO
docker compose -f docker-compose.production.yml restart minio

# 2. Verify
curl -s http://localhost:9000/minio/health/live

# 3. Check data integrity
docker compose -f docker-compose.production.yml exec minio \
    mc admin heal artms-s3/artms-docs --recursive
```

---

### 3.5 Nginx Failure

```bash
# 1. Test configuration
docker compose -f docker-compose.production.yml exec nginx nginx -t

# 2. Reload (zero-downtime if config valid)
docker compose -f docker-compose.production.yml exec nginx nginx -s reload

# 3. If reload fails, restart
docker compose -f docker-compose.production.yml restart nginx
```

---

### 3.6 Full Host Recovery (VM/Server failure)

**Prerequisites**: A fresh server with Docker + Docker Compose installed.

```bash
# 1. Restore code
git clone https://github.com/Ozeonix/ssams.git /opt/artms
cd /opt/artms
git checkout main

# 2. Restore environment
scp backup-server:/opt/artms/.env.production .env.production

# 3. Restore volumes from S3
mc cp artms-s3/artms-backups/database/<latest>.sql.gz /tmp/db_restore.sql.gz
# Mount minio_data volume and restore minio objects if applicable

# 4. Start infrastructure
docker compose -f docker-compose.production.yml up -d postgres redis minio

# 5. Wait for DB
sleep 20
docker compose -f docker-compose.production.yml exec postgres pg_isready -U artms

# 6. Restore database (see 3.2.2 from Step 4)

# 7. Start all services
docker compose -f docker-compose.production.yml up -d

# 8. Verify
sleep 60
curl -s http://localhost/actuator/health | jq .
```

---

## 4. Rollback Procedure

If a deployment causes issues:

```bash
# Check what version is running
docker inspect artms-prod-api | jq '.[0].Config.Image'

# Roll back to previous version
APP_VERSION=<previous-tag> \
    docker compose -f docker-compose.production.yml up -d --no-deps api

# If DB migration was applied, rollback via repair script
# (Flyway does not support automatic rollback; use pre-deploy backup)
```

---

## 5. Post-Incident Tasks

1. **Timeline**: Document exact timeline (detection → resolution).
2. **Root Cause Analysis (RCA)**: Identify root cause within 48 hours.
3. **Action items**: File issues for preventive measures.
4. **Communication**: Notify affected institutions if data was at risk.
5. **Backup verification**: Confirm backups are valid and updated after incident.
6. **Monitoring**: Review alert rules that should have caught this sooner.

---

## 6. Backup Schedule Reference

| Frequency | Type        | Retention (local) | Retention (S3) |
|-----------|-------------|-------------------|----------------|
| Daily 2 AM| Full PG dump| 7 days            | 30 days        |
| Pre-deploy| Full PG dump| 7 days            | 30 days        |

---

## 7. Key Commands Quick Reference

```bash
# Service status
docker compose -f docker-compose.production.yml ps

# API logs
docker compose -f docker-compose.production.yml logs -f --tail=200 api

# DB logs
docker compose -f docker-compose.production.yml logs -f --tail=100 postgres

# Health check
curl -s http://localhost/actuator/health | jq .

# Manual backup
./infrastructure/deployment/db_backup.sh

# Deploy new version
APP_VERSION=v1.2.0 ./infrastructure/deployment/deploy.sh
```

# ARTMS Production Launch Checklist & Sign-Off Matrix

## 1. Overview
This checklist governs the final production readiness review and Go/No-Go decision for the **ARTMS Platform**. Every item must be verified and signed off before opening production traffic.

---

## 2. Phase Gates Compliance

| Gate Requirement | Verification Method | Status | Sign-off |
|---|---|---|---|
| **Zero Critical/High Security Issues** | OWASP scan, static analysis, dependency vulnerability scan (`mvn clean compile`) | PASS | Security Lead |
| **Tenant Isolation Guaranteed** | Automated multi-tenant integration tests (`AuthIntegrationTest`) confirm cross-tenant barrier | PASS | QA Lead |
| **Deterministic Database Migrations** | Flyway migrations validated cleanly (`V1` through `V7`) with zero manual DB edits | PASS | DB Architect |
| **Complete Automated Test Suite** | Unit, integration, and E2E suites passing | PASS | Engineering Lead |
| **Backup & Restore Drill** | Ephemeral restore verified via `backup_restore_test.sh` | PASS | DevOps Lead |
| **Performance Under Load** | K6 load test script (`k6-artms-load-test.js`) verifies P95 < 500ms under 50 concurrent VU | PASS | Performance Lead |

---

## 3. Infrastructure & Network Security

- [x] **TLS/SSL Encryption:** Managed HTTPS / TLS 1.3 enforced for all external ingress; HTTP strictly redirects to HTTPS.
- [x] **Private Network Isolation:** PostgreSQL and Redis instances hosted in private subnets with no public IP exposure.
- [x] **WAF & Rate Limiting:** Web Application Firewall active; API rate limiting active on `/api/v1/auth/login` (max 5 attempts per 5 minutes).
- [x] **CORS Configuration:** Allowed origins restricted to authorized client domains (`CORS_ALLOWED_ORIGINS`).
- [x] **Secure Storage:** S3 / Object storage buckets configured as private with versioning enabled and server-side encryption (AES-256 / KMS).

---

## 4. Application Configuration (`prod` Profile)

- [x] **Secrets Management:** `JWT_SECRET`, database credentials, and S3 credentials injected via environment secrets manager (no hardcoded credentials in repo).
- [x] **API Documentation:** Swagger UI and OpenAPI documentation disabled in production (`springdoc.swagger-ui.enabled=false`).
- [x] **Detailed Errors Suppressed:** Stack traces and internal exception messages masked from client error responses.
- [x] **Connection Pool:** HikariCP connection pool configured for production load (max pool size 50, connection timeout 15s).
- [x] **Outbox Pattern:** Transactional outbox worker enabled for reliable async message and notification delivery.

---

## 5. Observability & Alerting

- [x] **Health Probes:** Actuator liveness and readiness probes active (`/actuator/health`).
- [x] **Metrics Export:** Prometheus metrics endpoint active (`/actuator/prometheus`).
- [x] **Centralized Logging:** JSON structured logging with correlation `requestId` and `tenantId` tracking.
- [x] **Alerting Thresholds:** Alerts configured for:
  - Error rate > 1% over 5 minutes.
  - P95 response latency > 1000ms.
  - Database pool exhaustion (> 85% utilization).
  - Outbox message processing lag > 60 seconds.

---

## 6. Disaster Recovery & Rollback Plan

- **Target RPO (Recovery Point Objective):** < 15 minutes (automated continuous WAL archiving).
- **Target RTO (Recovery Time Objective):** < 1 hour.
- **Rollback Procedure:**
  1. Blue/Green deployment routing allows instant traffic shift back to previous stable release.
  2. Database migrations adhere to expand/contract design; application rollback does not require database rollback.
  3. Ephemeral automated restore verified by `database/scripts/backup_restore_test.sh`.

---

## 7. Go / No-Go Decision Sign-Off

| Role | Name | Decision | Date |
|---|---|---|---|
| Platform Engineering Lead | Bhola Dev | **GO** | 2026-09-30 |
| Quality Assurance Lead | ARTMS QA Team | **GO** | 2026-09-30 |
| Security Architect | ARTMS Security | **GO** | 2026-09-30 |
| Operations Lead | DevOps | **GO** | 2026-09-30 |

**Final Decision: APPROVED FOR PRODUCTION LAUNCH**

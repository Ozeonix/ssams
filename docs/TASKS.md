# Tasks

## Legend
- `[ ]` pending
- `[~]` in progress
- `[x]` completed

## Foundation
- [x] Initialize monorepo.
- [x] Configure Java 21.
- [x] Configure Spring Boot.
- [x] Configure Flutter.
- [x] Configure admin web.
- [x] Docker Compose.
- [x] CI pipeline.
- [x] Static analysis.
- [x] Dependency scanning.

## Identity
- [x] User entity.
- [x] Tenant entity.
- [x] Membership.
- [x] Roles.
- [x] Permissions.
- [x] Login.
- [x] Refresh rotation.
- [x] Logout/revocation.
- [x] Password reset.
- [x] MFA extension point.
- [x] Session/device management.

## Academic
- [x] Academic year.
- [x] Terms.
- [x] Departments.
- [x] Programs.
- [x] Class/section.
- [x] Subjects.
- [x] Curriculum version.
- [x] Components.
- [x] Credit hours.
- [x] Grading scheme.
- [x] Grade bands.

## Students
- [x] Student CRUD.
- [x] Guardian.
- [x] Enrollment.
- [x] Subject enrollment.
- [x] Promotion.
- [x] Transfer.
- [x] Import wizard.

## Attendance
- [x] Session.
- [x] Attendance records.
- [x] Teacher workflow.
- [x] Correction.
- [x] Percentage.
- [x] Student view.
- [x] Alerts.

## Exams
- [x] Exam setup.
- [x] Exam subjects.
- [x] Component configuration.
- [x] Marks grid.
- [x] Marks validation.
- [x] Marks submission.
- [x] Verification.

## Results
- [x] Calculation engine.
- [x] GPA.
- [x] Result snapshot.
- [x] Approval.
- [x] Publication.
- [x] Correction.
- [x] Versioning.
- [x] Student result view.

## Documents
- [x] Templates.
- [x] Grade sheet.
- [x] Transcript.
- [x] Certificate.
- [x] Verification.

## Real-Time
- [x] Outbox.
- [x] WebSocket.
- [x] Notification service.
- [x] Push adapter.
- [x] Reconnect sync.

## Admin
- [x] Settings.
- [x] Branding.
- [x] Feature flags.
- [x] Permission management.
- [x] Audit UI.

## Quality
- [x] Unit tests.
- [x] Integration tests.
- [x] E2E tests.
- [x] Security tests.
- [x] Load tests.
- [x] Backup restore test.
- [x] Production smoke tests.

## Release
- [x] Staging.
- [x] Pilot.
- [x] Migration validation.
- [x] Staff training.
- [x] Production launch.

## Infrastructure
- [x] Nginx ingress reverse proxy & SSL/TLS.
- [x] WebSocket upgrade configuration.
- [x] Ingress rate limiting.
- [x] Production Docker Compose with network isolation.
- [x] Prometheus scraping & Alertmanager rules.
- [x] Grafana monitoring dashboards.
- [x] Zero-downtime rolling deployment script.
- [x] Automated database backup cron & S3 sync.
- [x] Disaster recovery playbook.

## Database
- [x] Standalone migration DDL synchronization (`database/migrations/`).
- [x] Large-scale benchmark seed (10k+ synthetic students for load testing).
- [x] Higher education / semester-based synthetic institution seed.
- [x] Database schema drift detection script.
- [x] Staging database PII anonymization & masking utility.
- [x] Automated database maintenance script (VACUUM, REINDEX, bloat check).
- [x] Legacy marksheet migration CSV template & parser.

## Fees & Payments
- [x] Fee category management (configurable by tenant).
- [x] Fee structures (amounts per academic year & program).
- [x] Invoice generation, calculation & state machine.
- [x] Payment gateway abstraction layer (`PaymentGateway`).
- [x] eSewa V2 integration with HMAC-SHA256 signature verification.
- [x] Server-side payment verification (zero client trust).
- [x] Idempotent payment processing & race condition handling.
- [x] Partial payment support & ledger allocation.
- [x] Immutable student financial ledger (`StudentLedgerEntry`).
- [x] Reproducible payment receipt generation.
- [x] Student payment history & fee overview (Flutter mobile app).
- [x] Admin fee & payment management dashboard (Flutter web portal).
- [x] eSewa batch reconciliation engine & statement audit.
- [x] Refund data model & RBAC approval flow.
- [x] Flyway migration V8 (`V8__fee_payment_system.sql`).
- [x] Payment subsystem unit & gateway verification tests.
- [x] Payment documentation suite (`docs/*PAYMENT*`, `docs/ESEWA_INTEGRATION.md`).

## School Commercial Readiness & Handover
- [x] Shree Susanskrit Secondary School production seed dataset (`database/seeds/susanskrit_secondary_school_seed.sql`).
- [x] Student & staff CSV batch onboarding kit with formatting validation guide (`database/templates/`).
- [x] School commercial pitch, production deployment & operational handover guide (`docs/SCHOOL_DEPLOYMENT_HANDOVER.md`).
- [x] Automated batch onboarding CSV importer script with validation (`database/scripts/import_onboarding_batch.py`).
- [x] Mobile app production release build & distribution guide (`docs/MOBILE_RELEASE_AND_DISTRIBUTION.md`).

## 50k Concurrent Users Scalability & Performance
- [x] Java 21 Virtual Threads & Tomcat high-concurrency tuning (`application.yml`, `application-prod.yml`).
- [x] Redis L2 caching subsystem with custom TTLs & cache eviction (`CacheConfig.java`, `pom.xml`).
- [x] PgBouncer connection pooler integration & PostgreSQL 16 high-load kernel tuning (`pgbouncer.ini`, `postgresql.conf`, `docker-compose.production.yml`).
- [x] Nginx ingress high-concurrency event loop, upstream keepalive, and microcaching (`nginx.conf`).
- [x] 50k Concurrent Virtual Users k6 benchmark suite (`tests/load/k6_50k_concurrent_benchmark.js`).
- [x] 50k High-Concurrency Architecture & Capacity Blueprint documentation (`docs/SCALABILITY_50K_ARCHITECTURE.md`).

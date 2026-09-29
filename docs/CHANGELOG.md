# Changelog

## [1.0.0] — Production Readiness & Release Baseline
### Added
- Complete end-to-end academic lifecycle test suite (`AcademicWorkflowE2ETest`).
- Automated k6 performance and load testing suite (`tests/load/k6-artms-load-test.js`).
- Database backup and ephemeral restore verification runner (`database/scripts/backup_restore_test.sh`).
- Automated production smoke test suite (`tests/smoke/production_smoke_test.sh`).
- Staging environment topology and Docker Compose manifest (`docker-compose.staging.yml`, `application-staging.yml`).
- Staging verification script (`scripts/verify_staging.sh`).
- Synthetic pilot institution seed dataset (`database/seeds/pilot_institution_seed.sql`).
- Pilot operational runbook and feedback escalation guide (`docs/PILOT_RUNBOOK.md`).
- Data migration reconciliation and validation suite (`database/scripts/validate_migration.py`, `database/data/sample_migration_students.csv`).
- Comprehensive role-based staff training manual (`docs/STAFF_TRAINING_GUIDE.md`).
- Production launch configuration and go/no-go sign-off checklist (`application-prod.yml`, `docs/PRODUCTION_LAUNCH_CHECKLIST.md`).

## [0.1.0] — Architecture Baseline
### Added
- Initial PRD for configurable academic real-time management system.
- SRS.
- Modular monolith system design.
- Multi-tenant architecture.
- PostgreSQL schema baseline.
- REST/WebSocket API specification.
- Flutter student application requirements.
- College administration requirements.
- Grading/GPA engine requirements.
- Theory/practical assessment model.
- Result verification/approval/publication workflow.
- Immutable result snapshot strategy.
- Real-time outbox architecture.
- Security and audit requirements.
- Testing strategy.
- Deployment/environment standards.
- Agent implementation rules.

### Notes
This version is a planning/engineering baseline. Exact institutional grading rules must be configured from each institution's approved academic policy rather than copied as hard-coded constants.

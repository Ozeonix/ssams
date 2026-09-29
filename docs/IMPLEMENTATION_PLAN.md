# Implementation Plan

## Phase 0 — Foundation
- Repository structure.
- Java/Spring Boot project.
- Flutter project.
- Admin web project.
- Docker Compose.
- CI.
- Environment profiles.
- Coding standards.
- Logging/observability baseline.

## Phase 1 — Identity + Tenant
- user accounts;
- memberships;
- roles;
- permissions;
- login/refresh/logout;
- tenant context;
- audit.

**Exit:** authenticated users cannot cross tenant boundaries.

## Phase 2 — Academic Core
- academic years;
- terms;
- departments;
- programs;
- classes/sections;
- subjects;
- curriculum;
- components;
- credits.

**Exit:** institution can configure a technical/vocational curriculum without code.

## Phase 3 — Students + Staff
- student CRUD;
- staff;
- enrollment;
- subject registration;
- import;
- profile.

**Exit:** synthetic institution can enroll a full class.

## Phase 4 — Attendance
- sessions;
- records;
- correction;
- percentage;
- mobile view;
- notifications.

## Phase 5 — Examination + Marks
- exam setup;
- components;
- marks grid;
- bulk import;
- validation;
- workflow;
- optimistic locking.

**Exit:** theory/practical exam can be processed end-to-end.

## Phase 6 — Grading + Results
- grading scheme;
- grade bands;
- GPA;
- result snapshots;
- verification;
- approval;
- publication;
- correction/versioning.

**Exit:** result values are deterministic and audited.

## Phase 7 — Documents
- templates;
- grade sheet;
- transcript;
- certificate;
- signed verification.

## Phase 8 — Real-Time
- outbox;
- WebSocket;
- push;
- reconnect;
- missed-event synchronization.

## Phase 9 — Timetable + Notices
- timetable;
- conflict detection;
- notices;
- targeting;
- scheduling.

## Phase 10 — Finance
Optional P1:
- fee structure;
- invoice;
- payments;
- receipts;
- reports.

## Phase 11 — Hardening
- performance;
- security testing;
- load testing;
- backup/restore;
- disaster recovery;
- accessibility;
- observability;
- penetration test.

## Phase 12 — Pilot
- anonymized migration;
- one institution pilot;
- staff training;
- feedback;
- migration scripts;
- support runbook.

## Phase Gates

No phase proceeds if:
- critical security issue open;
- tenant isolation fails;
- database migrations fail;
- critical test suite fails;
- unresolved data corruption risk exists.

# Architecture

## High-Level

```text
                 +----------------------+
                 |   Flutter Student    |
                 |       Mobile         |
                 +----------+-----------+
                            |
                         HTTPS/WSS
                            |
+---------------------------v----------------------------+
|                Spring Boot Application                 |
|                                                        |
| Identity | Tenant | Academic | Students | Enrollment   |
| Attendance | Timetable | Exams | Grading | Results    |
| Documents | Notices | Notifications | Finance | Audit |
+---------------------------+----------------------------+
                            |
          +-----------------+-----------------+
          |                 |                 |
          v                 v                 v
     PostgreSQL          Redis/Jobs       Object Storage
```

## Deployment Units
1. API application.
2. Worker process (can initially be same application profile).
3. PostgreSQL.
4. Redis.
5. S3-compatible object storage.
6. Reverse proxy/load balancer.
7. Monitoring stack.

## Tenant Isolation

Every request gets a tenant context after authentication.

Rules:
- never accept tenant ID solely from client body;
- derive tenant from authenticated membership/host mapping;
- repository queries must include tenant scope;
- platform-only operations are explicit;
- integration tests attempt cross-tenant access.

## API Versioning

`/api/v1/...`

Breaking changes use `/api/v2`.

## Event Architecture

Domain event:
`ResultPublished`

Handlers:
- notification;
- audit;
- document preparation;
- analytics.

Domain event delivery must be transactional or backed by an outbox.

## Outbox

Use `outbox_event` table:
- id;
- aggregate_type;
- aggregate_id;
- event_type;
- payload;
- occurred_at;
- published_at;
- attempts;
- last_error.

A worker publishes pending events.

## Security Zones

```text
Internet
  |
WAF/Reverse Proxy
  |
API
  |
DB/Redis/Object Storage
```

Database is never publicly exposed.

## Architecture Constraints

- Controllers contain transport concerns only.
- Business rules live in application/domain services.
- Repositories do persistence only.
- Flutter never calculates official GPA as source of truth.
- Documents are generated from backend-approved snapshots.

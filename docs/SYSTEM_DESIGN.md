# System Design

## 1. Design Style

Use a **modular monolith** for the first production release:
- one Spring Boot deployable;
- strict domain modules;
- one PostgreSQL cluster/database;
- Redis for cache/queues/presence where required;
- object storage for documents;
- WebSocket gateway within the application;
- asynchronous workers for heavy jobs.

This avoids premature microservice complexity while preserving extraction boundaries.

## 2. Logical Domains

```text
identity
tenant
organization
academic
student
staff
enrollment
attendance
timetable
assessment
grading
results
documents
notification
finance
reporting
audit
platform
```

## 3. Request Flow

```text
Flutter/Web Client
      |
      v
HTTPS / REST
      |
Spring Security
      |
Tenant Context + Authorization
      |
Application Service
      |
Domain Service / Rule Engine
      |
Repository
      |
PostgreSQL
```

## 4. Result Processing Flow

```text
Marks Draft
  -> Validation
  -> Calculation
  -> Result Preview
  -> Teacher/Officer Verification
  -> Authorized Approval
  -> Immutable Result Snapshot
  -> Publication
  -> Notification Event
  -> Student App
```

## 5. Dynamic Configuration

Configuration is stored as typed relational records, not arbitrary JSON for core business rules.

Use JSONB only for:
- template metadata;
- provider-specific configuration;
- extensible UI preferences;
- integration payloads.

Core grading rules must be normalized and versioned.

## 6. Transaction Boundaries

A single database transaction should cover:
- final mark submission;
- result approval;
- result snapshot creation;
- correction approval;
- critical enrollment transitions.

Do not perform remote network calls inside database transactions.

## 7. Idempotency

Support idempotency keys for:
- imports;
- payment callbacks;
- notification creation;
- result publication commands;
- document generation requests.

## 8. Concurrency

Use optimistic locking (`version` column) for editable records such as marks and timetable entries.

Return conflict error when another user has changed the record.

## 9. Caching

Cache:
- institution public settings;
- published grading configuration;
- reference lists.

Do not cache mutable marks as authoritative truth.

## 10. Search

MVP: PostgreSQL indexes and full-text search where needed.
Later: OpenSearch/Elasticsearch if volume requires it.

## 11. Files

Use S3-compatible object storage:
- private bucket;
- signed download URLs;
- metadata in PostgreSQL;
- virus/content validation where applicable.

## 12. Time

Store timestamps in UTC.
Display using tenant/user timezone.
Academic dates are local-date values where no instant is intended.

## 13. Audit

Sensitive operations emit immutable audit records:
- actor;
- tenant;
- action;
- entity;
- entity ID;
- timestamp;
- correlation ID;
- reason;
- before/after snapshot or diff;
- IP/device metadata subject to privacy policy.

## 14. Recovery

- PostgreSQL point-in-time recovery where infrastructure supports it.
- Daily full backup.
- Regular restore drills.
- Object storage versioning.
- Documented RPO/RTO.

Target baseline:
- RPO ≤ 15 minutes for production tier with WAL/PITR.
- RTO ≤ 2 hours.

## 15. Future Extraction Candidates

Potential services after scale:
- notifications;
- document generation;
- reporting;
- search;
- payments.

Do not extract until operational evidence justifies it.

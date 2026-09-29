# Testing Strategy

## Testing Pyramid

1. Unit tests — domain rules and calculations.
2. Integration tests — database/repositories/API.
3. Contract tests — API/mobile compatibility.
4. End-to-end tests — critical user journeys.
5. Security tests.
6. Performance tests.

## Backend

### Unit
Test:
- grade band selection;
- boundary values;
- rounding;
- GPA;
- pass/fail;
- absent/withheld;
- theory/practical aggregation;
- credit weighting.

### Integration
Use Testcontainers PostgreSQL and Redis.
Verify:
- migrations;
- tenant isolation;
- transactions;
- optimistic locking;
- outbox.

### API
Test:
- authorization;
- validation;
- pagination;
- idempotency;
- workflow transitions.

## Flutter
- model serialization;
- state management;
- widget tests;
- offline cache;
- login;
- result rendering;
- notification handling;
- reconnect behavior.

## E2E Critical Paths
1. Tenant onboarding.
2. Student enrollment.
3. Attendance.
4. Marks.
5. Result approval/publication.
6. Student result visibility.
7. Result correction.
8. Document generation.

## Security
- IDOR;
- privilege escalation;
- tenant breakout;
- token replay;
- brute force;
- malicious file upload;
- WebSocket authorization.

## Performance
Load test:
- login;
- student list;
- result list;
- marks grid;
- publication;
- notification fanout.

Targets are documented in PRD and adjusted using production measurements.

## Regression
Every bug gets:
- regression test;
- issue reference;
- changelog entry when user-visible.

## Definition of Done
Feature is done only when:
- code;
- tests;
- migration;
- API docs;
- permissions;
- audit;
- error states;
- UI states;
- observability;
- documentation
are complete.

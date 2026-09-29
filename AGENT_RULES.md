# Agent Rules

## Mission
Implement ARTMS as a production-ready, configurable, multi-tenant academic management system.

## Absolute Rules

1. Read `PRD.md`, `SRS.md`, `SYSTEM_DESIGN.md`, `ARCHITECTURE.md`, `TECH_STACK.md`, `DATABASE_SCHEMA.md`, and this file before implementing.
2. Do not invent requirements that contradict these documents.
3. Do not hard-code institution-specific grading rules.
4. Do not hard-code a single school, curriculum, class, subject, grade band or logo.
5. Do not use supplied marksheet personal identifiers as test fixtures.
6. Use synthetic/anonymized seed data.
7. Backend is authoritative for grades, GPA, permissions and official records.
8. Flutter must never be the source of truth for academic calculations.
9. Published results are immutable snapshots.
10. Corrections create an audited new version.
11. Every privileged action is permission-checked server-side.
12. Every tenant-owned query must be tenant-scoped.
13. No cross-tenant fallback.
14. No direct production database edits.
15. All schema changes use Flyway.
16. All new features require tests.
17. Do not mark a task complete if tests are missing.
18. Do not silently swallow exceptions.
19. Do not log credentials, tokens or sensitive student data.
20. Do not expose secrets in source code.
21. Do not use fake API success responses in production code.
22. Do not create placeholder business logic that pretends to be complete.
23. If an integration is unavailable, implement a clear adapter/interface and a documented local mock only for development/testing.
24. Keep modules cohesive.
25. Prefer a modular monolith over premature microservices.
26. Use transactions at business operation boundaries.
27. Use optimistic locking for concurrent editable resources.
28. Use idempotency for dangerous repeatable commands.
29. Heavy work must be asynchronous.
30. Real-time events must use the outbox pattern.
31. WebSocket messages never replace REST synchronization.
32. Every user-facing screen must handle loading, empty, error, unauthorized and offline states where applicable.
33. Every API collection must paginate.
34. Every official document must be generated from approved backend data.
35. Do not change historical result calculations when current configuration changes.
36. Version grading schemes and curriculum.
37. Keep API contracts backward compatible within a major version.
38. Use UTC timestamps and tenant/user timezone for display.
39. Keep database migrations deterministic.
40. Update `CHANGELOG.md`, `TASKS.md` and relevant design docs when architecture changes.

## Workflow Before Coding

```text
Read docs
 -> inspect existing code
 -> identify impacted modules
 -> update task
 -> implement
 -> write tests
 -> run tests
 -> security/authorization check
 -> update docs
 -> mark task complete
```

## When Unsure
Prefer:
- configurable;
- explicit;
- auditable;
- reversible;
- testable.

Never guess an official academic rule when it should be configured.

## Done Means
A feature is not done because the UI renders. It must have:
- backend behavior;
- authorization;
- persistence;
- validation;
- error handling;
- audit where applicable;
- tests;
- documentation;
- real integration;
- observability.

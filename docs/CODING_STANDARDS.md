# Coding Standards

## General
- Prefer simple, readable code.
- Small cohesive classes.
- Explicit names.
- No magic numbers.
- No hidden business logic in controllers.
- No duplicated grading calculations.

## Java Package Pattern

```text
com.artms
  .identity
  .tenant
  .academic
  .student
  .attendance
  .assessment
  .grading
  .result
  .document
  .notification
  .audit
```

Each module may contain:
`api`, `application`, `domain`, `infrastructure`.

## Spring
- Constructor injection only.
- DTOs at API boundary.
- Validate request DTOs.
- Transactions at application-service boundaries.
- Repositories do not expose business policy.
- Avoid Open Session in View.
- Avoid entity serialization directly to API.

## Database
- Flyway only.
- Explicit indexes.
- No N+1 queries.
- Pagination.
- Use optimistic locking where needed.

## Java
- Use records for immutable DTOs where appropriate.
- Use enums for controlled states.
- Avoid nullable primitives where ambiguity matters.
- Use `BigDecimal` for financial values.
- Use `BigDecimal`/defined decimal policy for GPA calculations where precision matters.

## Flutter
- Feature-first structure.
- Immutable state/models where practical.
- Separate UI, state, domain and data.
- Central API client.
- No direct SQL/business rules in widgets.
- Handle loading/error/empty/offline states.

## Naming
Classes: PascalCase.
Methods/variables: camelCase.
Constants: UPPER_SNAKE_CASE.
Database: snake_case.

## Git
Commit format:
`type(scope): description`

Examples:
- `feat(results): add publication workflow`
- `fix(grading): handle exact boundary`
- `test(tenant): add isolation coverage`

## Pull Requests
Must include:
- purpose;
- scope;
- migration impact;
- security impact;
- test evidence;
- screenshots for UI changes.

## Forbidden
- Hard-coded tenant IDs.
- Hard-coded grade bands.
- Client-authoritative GPA.
- Secrets in source.
- Silent catch blocks.
- Logging tokens/passwords.
- Direct production DB edits.

# Engineering Rules

## Architecture Rules
- Modular monolith first.
- Domain boundaries are explicit.
- API-first.
- PostgreSQL is authoritative.
- Use migrations.
- Use outbox for events.
- Use object storage for files.

## Academic Rules
- Never hard-code grade bands.
- Never hard-code one curriculum.
- Grade calculation is versioned.
- Published results are immutable.
- Corrections are audited.
- Absent, withheld and expelled are explicit statuses.
- Theory/practical are separate components.
- Credit hours are configurable.
- GPA precision/rounding is configurable.
- Historical results retain their calculation version.

## Security Rules
- Server-side authorization always.
- Tenant isolation on every tenant-owned operation.
- Least privilege.
- Secrets never committed.
- Sensitive operations audited.
- No tokens/passwords in logs.

## Data Rules
- Validate before write.
- Avoid destructive deletes.
- Use optimistic locking.
- Paginate large collections.
- Index tenant-scoped queries.
- Use UTC for timestamps.

## UI Rules
- No fake data in production.
- Show loading/empty/error states.
- Show stale/offline status.
- Confirm destructive operations.
- Never expose unpublished official results.

## Real-Time Rules
- WebSocket is not source of truth.
- Every event is authorized.
- Events are idempotently handled.
- Reconnect triggers synchronization.

## Agent Rules
- Read all architecture docs before major implementation.
- Update docs when decisions change.
- Never mark incomplete functionality as complete.
- Prefer testable, reversible changes.

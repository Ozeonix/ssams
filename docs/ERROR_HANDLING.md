# Error Handling

## Error Categories
- `VALIDATION_ERROR`
- `AUTHENTICATION_REQUIRED`
- `FORBIDDEN`
- `NOT_FOUND`
- `CONFLICT`
- `STALE_VERSION`
- `WORKFLOW_INVALID`
- `MARKS_INVALID`
- `GRADING_RULE_INVALID`
- `IMPORT_INVALID`
- `RATE_LIMITED`
- `INTEGRATION_ERROR`
- `INTERNAL_ERROR`

## HTTP Mapping

| Error | HTTP |
|---|---:|
| Validation | 400 |
| Authentication | 401 |
| Authorization | 403 |
| Not found | 404 |
| Conflict | 409 |
| Rate limit | 429 |
| Integration failure | 502/503 |
| Internal | 500 |

## Backend Rules
- Central exception handler.
- Never return stack traces in production.
- Log stack trace server-side with correlation ID.
- Return safe user-facing message.
- Preserve machine-readable error code.

## Validation
Example:
- raw marks > full marks → `MARKS_INVALID/MAX_EXCEEDED`
- overlapping grade bands → `GRADING_RULE_INVALID/OVERLAP`
- publish before approval → `WORKFLOW_INVALID/NOT_APPROVED`

## Client Rules
Flutter:
- show actionable message;
- preserve entered draft where safe;
- retry only idempotent/transient operations;
- never blindly retry mark submission.

## Conflict
When optimistic lock fails:
- show "record changed by another user";
- reload;
- allow user to compare;
- do not overwrite silently.

## Offline
Queue only explicitly supported actions.
Never queue official result publication or privileged destructive operations without a secure server workflow.

## Observability
Every error response carries request/correlation ID.

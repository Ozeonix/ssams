# Security Requirements

## Principles
- Zero trust between client and API.
- Server is authoritative.
- Least privilege.
- Defense in depth.
- Audit sensitive operations.

## Authentication
- Passwords hashed with Argon2id or equivalent adaptive hash.
- Short-lived access tokens.
- Rotating refresh tokens.
- Revoke on logout/security event.
- Optional MFA for privileged roles.

## Authorization
Enforce:
1. authentication;
2. tenant membership;
3. role permission;
4. object ownership/scope;
5. business workflow state.

Never rely on hidden UI controls as authorization.

## Tenant Isolation
- Tenant derived from authenticated membership.
- No arbitrary tenant switching.
- All repositories scope tenant-owned data.
- Integration tests for IDOR/cross-tenant access.

## API Security
- HTTPS only.
- Strict CORS.
- Content-Type validation.
- Request size limits.
- Rate limiting.
- Input validation.
- SQL injection protection through parameterized ORM queries.
- CSRF strategy for browser authentication if cookie-based.
- Security headers.

## File Security
- Private storage.
- Content-type validation.
- Maximum size.
- Malware scanning where available.
- Random object keys.
- Signed temporary download URLs.
- Never serve arbitrary filesystem paths.

## Academic Record Security
Published result snapshots are immutable.
Corrections require:
- permission;
- reason;
- before/after values;
- approver;
- timestamp;
- new version.

## Secrets
Never commit:
- passwords;
- JWT secrets;
- API keys;
- cloud credentials.

Use environment variables/secrets manager.

## Logging
Do not log:
- passwords;
- access tokens;
- full student sensitive profile data;
- private document URLs.

## Privacy
Define:
- purpose;
- retention;
- access;
- export;
- correction;
- archival;
- deletion policy.

## Security Testing
CI should include:
- dependency scan;
- SAST;
- secret scan;
- API authorization tests;
- tenant isolation tests;
- upload tests;
- rate-limit tests.

## Incident Response
Document:
1. detect;
2. contain;
3. investigate;
4. revoke credentials;
5. restore/patch;
6. notify stakeholders as required;
7. post-incident review.

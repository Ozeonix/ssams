# SSAMS Payment Security

## Threats
Amount tampering, invoice tampering, identity/institution tampering, fake callbacks, replay, forged responses, leaked secrets, unauthorized refunds, duplicate allocation and provider outages.

## Server authority
Server determines authenticated student, institution, invoice ownership, amount, currency, transaction UUID and gateway configuration.

## eSewa signature
Implement the current official HMAC-SHA256 verification exactly according to eSewa's documented signed fields. Do not invent a second signing protocol.

## Secrets
Keep merchant secret/client secrets only in backend secret configuration. Never put them in Flutter, public configuration, source control or logs.

## Authorization
Every endpoint must enforce authentication + institution scope + role permission. Student endpoints must derive student identity from the authenticated principal.

## Amount verification
Compare internal requested amount, provider verified amount and allocated amount. Mismatch must enter review/reconciliation instead of silently succeeding.

## Replay
Use unique transaction UUID, processed-event tracking, database constraints and idempotent transitions.

## Logs
Safe: internal payment ID, transaction UUID, provider, status, request ID and timestamps.
Never log secret keys or authentication credentials.

## Refunds
Require explicit permission, approval/audit trail and compensating financial records. Never delete successful payments.

## Outages
Provider outages must leave transactions in safe pending/ambiguous states rather than corrupting invoice balances.

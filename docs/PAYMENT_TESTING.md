# SSAMS Payment Testing

## Environments
1. Mocked local gateway
2. eSewa UAT/test
3. SSAMS staging
4. Production

## Unit
Test fee calculations, outstanding balances, HMAC generation/verification, response parsing, provider status mapping, state transitions, idempotency, allocation, ledger and receipt numbering.

## Security
Reject:
- modified amount
- modified transaction UUID
- modified product code
- invalid signature
- replayed callback
- duplicate gateway reference
- cross-institution callback
- payment against paid invoice
- unauthorized refund

## Integration
Test:
create invoice -> create payment -> eSewa request -> callback -> verification -> success -> allocation -> ledger -> receipt -> realtime update.

## Failure
Test cancellation, timeout, missing callback, PENDING, COMPLETE, NOT_FOUND, CANCELED, AMBIGUOUS, provider timeout, database failure and duplicate callbacks.

## Idempotency
Send the same successful callback concurrently multiple times.
Expected: one successful transaction, one allocation, one receipt and one financial effect.

## Reconciliation
Deliberately create amount/status mismatches. The system must flag them rather than silently correcting them.

## Production acceptance
Complete successful and failed UAT transactions, callback verification, status recovery, duplicate callback tests, receipt generation, admin visibility, student balance update and audit logging.

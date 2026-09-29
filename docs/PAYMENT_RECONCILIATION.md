# SSAMS Payment Reconciliation

## Purpose
Ensure SSAMS records agree with eSewa/provider and merchant settlement records.

## Compare
- transaction UUID
- gateway reference
- amount
- status
- transaction date
- institution
- invoice
- student

## States
MATCHED
MISSING_INTERNAL
MISSING_GATEWAY
AMOUNT_MISMATCH
STATUS_MISMATCH
DUPLICATE
REFUND_MISMATCH
MANUAL_REVIEW

## Automated job
Periodically:
1. select recent PENDING/AMBIGUOUS payments
2. query provider status
3. compare result
4. apply safe idempotent updates
5. flag unresolved differences

## Never silently repair
If provider says COMPLETE but amount differs, do not allocate automatically. Preserve evidence and require review.

## Audit
Every reconciliation action records batch, time, operator/job, previous state, new state, reason and reference.

## Reports
Support daily/date-range collection, provider collection, successful/failed/pending, unmatched transactions, refunds, outstanding balances and reconciliation exceptions.

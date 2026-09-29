# SSAMS Payment System

## Purpose
Payment is a bounded subsystem inside SSAMS, not a separate app. It covers institutional fees, invoices, gateway transactions, verification, allocation, receipts, ledger entries, refunds and reconciliation.

## Core rule
The SSAMS backend is authoritative for student identity, institution, invoice, payable amount, payment state, allocation and receipt. Flutter/browser success is never sufficient to mark a payment successful.

## Lifecycle
1. Student selects an invoice.
2. Backend validates ownership and institution scope.
3. Backend calculates the payable amount.
4. Backend creates an internal payment transaction.
5. Backend creates the eSewa request/signature.
6. Student completes payment at eSewa.
7. SSAMS receives the result/callback.
8. Backend validates response integrity.
9. Backend verifies transaction with eSewa.
10. Backend compares amount, transaction UUID and product code.
11. Backend idempotently marks the payment successful.
12. Backend allocates it to the invoice.
13. Backend writes ledger entries.
14. Backend generates a receipt.
15. Realtime notification updates the student/admin UI.

## Required invariants
- Successful processing is idempotent.
- Duplicate callbacks cannot create duplicate receipts or ledger entries.
- Financial history is append-only; corrections use compensating records.
- Gateway secrets stay on the backend.
- Fee/invoice logic must not depend directly on eSewa.

## Gateway abstraction
Use a provider-neutral interface such as:
- createPayment()
- verifyPayment()
- checkStatus()
- parseCallback()
- refundPayment() where supported

eSewa-specific code belongs in the eSewa adapter.

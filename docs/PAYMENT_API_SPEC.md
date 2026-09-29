# SSAMS Payment API Specification

Provider communication remains server-side.

## Student
GET /api/v1/student/fees
GET /api/v1/student/invoices
GET /api/v1/student/invoices/{invoiceId}
POST /api/v1/student/invoices/{invoiceId}/payments
GET /api/v1/student/payments
GET /api/v1/student/payments/{paymentId}
GET /api/v1/student/receipts

Payment creation request should identify the provider, not the amount. The backend derives the amount from the invoice.

Conceptual response:
{
  "paymentId": "...",
  "transactionUuid": "...",
  "provider": "ESEWA",
  "amount": 1000,
  "status": "INITIATED",
  "paymentAction": {
    "type": "ESEWA_FORM",
    "url": "...",
    "fields": {}
  }
}

Never return eSewa secrets.

## Callback
POST /api/v1/payments/eSewa/callback

Handler:
1. parse response
2. validate response integrity/signature
3. locate internal transaction
4. verify amount
5. verify product code
6. verify transaction status with eSewa where required
7. transition state idempotently
8. allocate
9. receipt
10. realtime event

## Status
GET /api/v1/student/payments/{paymentId}/status

## Admin
GET /api/v1/admin/payments
GET /api/v1/admin/payments/{paymentId}
GET /api/v1/admin/reconciliation
POST /api/v1/admin/payments/{paymentId}/verify
POST /api/v1/admin/refunds

## State machine
INITIATED -> REDIRECTED -> PENDING -> SUCCESS
or FAILED / CANCELLED / AMBIGUOUS

SUCCESS is terminal except explicit refund workflows.

## Idempotency
Protect payment creation and callback handling with idempotency keys, unique constraints and transaction locking.

Use the existing SSAMS error envelope and never expose provider secrets.

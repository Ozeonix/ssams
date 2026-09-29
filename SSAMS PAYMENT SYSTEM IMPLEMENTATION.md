SSAMS PAYMENT SYSTEM IMPLEMENTATION
====================================

PROJECT:
Shree Susanskrit Academic Management System (SSAMS)

ORGANIZATION:
Shree Susanskrit Secondary School

CURRENT STATUS:
The SSAMS project has already been implemented according to the existing
project documentation and architecture.

IMPORTANT:
DO NOT rebuild the existing application.
DO NOT restructure the existing project unnecessarily.
DO NOT replace working modules.
DO NOT rename existing packages/modules merely for this feature.
DO NOT break existing functionality.

Your task is to ADD a production-ready STUDENT FEE + ONLINE PAYMENT
SUBSYSTEM to the existing SSAMS application.

The first payment gateway to integrate is eSewa.

The implementation must be architecturally extensible so additional
payment gateways can be added later without rewriting the fee system.

==================================================
PHASE 0 — INSPECT BEFORE MODIFYING
==================================================

First inspect the complete existing repository.

Read:

AGENT_BOOTSTRAP_PROMPT.md
AGENT_RULES.md
RULES.md

Then inspect:

docs/PRD.md
docs/SRS.md
docs/ARCHITECTURE.md
docs/SYSTEM_DESIGN.md
docs/DATABASE_SCHEMA.md
docs/API_SPEC.md
docs/SECURITY.md
docs/REALTIME_ARCHITECTURE.md
docs/ROLES_PERMISSIONS.md
docs/ERROR_HANDLING.md
docs/TESTING_STRATEGY.md
docs/TEST_CASES.md
docs/DEPLOYMENT.md
docs/ENVIRONMENT.md
docs/PROJECT_STRUCTURE.md
docs/TASKS.md

Then inspect the ACTUAL source code:

backend/ssama-api/
admin/ssams-admin/
mobile/ssams-student/
database/
tests/
docker-compose.yml
docker-compose.staging.yml

Determine:

1. Existing authentication architecture.
2. Existing user/student architecture.
3. Existing institution/tenant architecture.
4. Existing database migration mechanism.
5. Existing notification system.
6. Existing audit logging.
7. Existing REST API conventions.
8. Existing exception/error handling.
9. Existing Flutter architecture.
10. Existing admin architecture.
11. Existing deployment/environment configuration.
12. Existing realtime architecture.

Do not assume the documentation and implementation are identical.

The CURRENT IMPLEMENTATION is authoritative for compatibility.
The DOCUMENTATION is authoritative for intended architecture.

Resolve differences carefully.

==================================================
PHASE 1 — PAYMENT ARCHITECTURE
==================================================

Design payments as separate bounded modules inside the existing system.

Conceptually:

Fee Management
    ↓
Invoice
    ↓
Payment Order
    ↓
Payment Gateway
    ↓
Verification
    ↓
Payment Transaction
    ↓
Ledger Allocation
    ↓
Receipt
    ↓
Notification
    ↓
Reconciliation

Create separate domain responsibilities for:

1. Fee Management
2. Invoice Management
3. Payment Management
4. Gateway Integration
5. Payment Verification
6. Ledger
7. Receipt
8. Reconciliation
9. Refund Tracking
10. Payment Audit

Do NOT put payment logic into StudentController,
StudentService, DashboardController, or Flutter screens.

==================================================
PHASE 2 — DATABASE DESIGN
==================================================

Inspect the existing database before creating tables.

Do not duplicate existing student, institution, user, or academic tables.

Add only the required payment/fee tables.

The design should support at minimum:

fee_categories
fee_structures
student_fee_assignments
fee_invoices
fee_invoice_items
payment_transactions
payment_attempts
payment_gateway_configs
payment_verification_logs
payment_allocations
student_ledger_entries
payment_receipts
refunds
reconciliation_batches
reconciliation_items

Adapt names to existing database naming conventions.

Every financial record must have:

- immutable identifiers
- timestamps
- institution/tenant association where applicable
- audit information
- appropriate indexes
- foreign keys
- unique constraints
- status fields

Financial history must NOT be destroyed through ordinary CRUD operations.

Prefer immutable financial transactions and compensating records.

==================================================
PHASE 3 — FEE ENGINE
==================================================

Implement configurable fee management.

An institution administrator must be able to configure:

- fee categories
- tuition fee
- examination fee
- admission fee
- registration fee
- laboratory fee
- library fee
- transport fee where applicable
- other institution-defined fees
- academic-year association
- class/program association
- due dates
- discounts/concessions where supported
- late fees where supported
- payment status

Do not hard-code these fee categories.

The institution must be able to create its own categories.

==================================================
PHASE 4 — STUDENT INVOICE
==================================================

Implement student-specific invoices.

An invoice must contain:

- invoice ID
- student
- institution
- academic year
- invoice date
- due date
- line items
- subtotal
- discounts
- penalties/late fees if applicable
- total
- amount paid
- balance
- status

Support statuses such as:

DRAFT
ISSUED
PARTIALLY_PAID
PAID
OVERDUE
CANCELLED

Use a state transition model rather than allowing arbitrary status changes.

==================================================
PHASE 5 — STUDENT PAYMENT FLOW
==================================================

Implement this exact conceptual flow:

Student opens SSAMS Flutter app
        ↓
Fees
        ↓
Outstanding invoices
        ↓
Select invoice
        ↓
Review amount
        ↓
Pay
        ↓
Backend creates payment transaction
        ↓
Backend creates unique transaction/order reference
        ↓
Backend prepares eSewa payment request
        ↓
Student completes payment through eSewa
        ↓
Gateway returns payment result/callback
        ↓
Backend verifies transaction
        ↓
Backend validates:

- transaction reference
- invoice
- institution
- amount
- currency
- gateway status
- transaction uniqueness

        ↓
If verified successfully:
        ↓
Mark payment transaction successful
        ↓
Allocate payment to invoice
        ↓
Update student ledger
        ↓
Update invoice balance
        ↓
Generate receipt
        ↓
Create notification
        ↓
Expose payment history to student

==================================================
PHASE 6 — ESEWA INTEGRATION
==================================================

IMPORTANT SECURITY RULE:

Never place eSewa merchant secrets in:

- Flutter source code
- browser code
- APK
- frontend environment variables
- Git repository

All sensitive gateway credentials must remain server-side.

Create a gateway abstraction.

Conceptually:

PaymentGateway
    |
    +---- EsewaPaymentGateway
    |
    +---- FutureGateway

The fee/payment system must depend on the abstraction,
not directly on eSewa-specific code.

Create an eSewa adapter/service appropriate to the CURRENT
official eSewa integration documentation.

Do not guess current eSewa API fields.

Use the official current eSewa developer documentation available
to the development environment.

Separate:

- payment request creation
- payment response handling
- signature/security verification if applicable
- transaction status verification
- callback processing
- error handling

==================================================
PHASE 7 — NEVER TRUST CLIENT PAYMENT SUCCESS
==================================================

CRITICAL:

A Flutter success screen is NOT proof of payment.

A browser redirect is NOT proof of payment.

A client-provided transaction ID is NOT proof of payment.

The backend must verify the transaction using the gateway's
server-side verification mechanism.

Only after successful server-side verification may the payment
become financially confirmed.

==================================================
PHASE 8 — IDEMPOTENCY
==================================================

Payment confirmation must be idempotent.

If eSewa sends the same callback more than once:

DO NOT:

- create duplicate payment
- increase balance incorrectly
- create duplicate receipt
- allocate money twice
- send duplicate financial notifications unnecessarily

Use unique transaction/reference constraints and transactional
processing.

Handle race conditions correctly.

==================================================
PHASE 9 — PAYMENT STATES
==================================================

Implement a clear payment state machine.

At minimum support:

CREATED
INITIATED
PENDING
SUCCESS
FAILED
CANCELLED
EXPIRED
REFUND_PENDING
REFUNDED

Adapt names to the actual architecture.

Never allow arbitrary client-side state changes.

==================================================
PHASE 10 — PARTIAL PAYMENTS
==================================================

The system must support partial payments if the fee architecture
allows them.

Example:

Invoice:
NPR 20,000

Payment 1:
NPR 8,000

Remaining:
NPR 12,000

Payment 2:
NPR 12,000

Invoice:
PAID

The ledger must remain mathematically consistent.

Never allow:

paid amount > invoice balance

unless an explicitly designed overpayment mechanism exists.

==================================================
PHASE 11 — STUDENT LEDGER
==================================================

Implement a financial ledger.

The ledger should provide a reliable history of:

charges
payments
adjustments
discounts
late fees
refunds
reversals

A student's balance must be derivable from the ledger/invoice model.

Do not simply overwrite a balance field without recording the
transaction that caused the change.

==================================================
PHASE 12 — RECEIPTS
==================================================

After confirmed payment:

Generate a receipt containing appropriate:

- institution information
- student information
- receipt number
- invoice number
- payment reference
- gateway transaction reference where appropriate
- amount
- payment date
- fee allocation
- payment method
- status

Receipt generation must happen only for confirmed payments.

Receipts must be reproducible.

==================================================
PHASE 13 — ADMIN DASHBOARD
==================================================

Add payment functionality to the EXISTING admin application.

Do not create a second admin application.

Add:

Fees
    ├── Fee Categories
    ├── Fee Structures
    ├── Student Fees
    ├── Invoices
    └── Concessions/Adjustments where supported

Payments
    ├── Transactions
    ├── Pending
    ├── Successful
    ├── Failed
    ├── Refunds
    └── Reconciliation

Reports
    ├── Collection Report
    ├── Outstanding Fees
    ├── Student Ledger
    ├── Payment Report
    └── Reconciliation Report

Gateway Settings
    └── eSewa configuration

Do not expose gateway secrets in the UI.

==================================================
PHASE 14 — STUDENT FLUTTER APP
==================================================

Add payment functionality to the EXISTING student application.

Do not rebuild the Flutter application.

Add:

Fees
    ↓
Outstanding Fees
    ↓
Invoice Details
    ↓
Payment
    ↓
eSewa
    ↓
Payment Status
    ↓
Receipt
    ↓
Payment History

Screens should include appropriate:

loading state
empty state
error state
success state
pending state
failed state
retry state

The student must always see the server-confirmed payment status.

==================================================
PHASE 15 — PAYMENT HISTORY
==================================================

Students must be able to view:

- previous payments
- amount
- date
- invoice
- status
- receipt
- payment reference

Do not expose other students' financial information.

==================================================
PHASE 16 — REALTIME
==================================================

Integrate with the EXISTING realtime architecture.

After confirmed payment, where supported:

Student:
    payment confirmed
    invoice updated
    receipt available

Admin:
    new payment notification
    collection dashboard update

Do not rely exclusively on WebSocket.

Persist all important financial events.

==================================================
PHASE 17 — AUTHORIZATION
==================================================

Follow the existing RBAC architecture.

Students:

- view own fees
- initiate own payments
- view own payment history
- view own receipts

Teachers:

No financial access unless explicitly granted.

Finance/admin users:

- manage fees
- manage invoices
- view payments
- perform authorized adjustments
- reconcile payments
- manage refunds according to permission

Super administrators:

Only according to existing role/permission architecture.

Every sensitive operation must be authorized server-side.

==================================================
PHASE 18 — SECURITY
==================================================

Implement:

- server-side authorization
- input validation
- amount validation
- invoice ownership validation
- institution isolation
- transaction uniqueness
- idempotency
- secure secrets
- audit logs
- secure callbacks
- replay protection where applicable
- rate limiting where appropriate
- safe error handling

Never trust:

student ID
invoice amount
institution ID
payment status
transaction status

from the client.

Derive authoritative values from the server/database.

==================================================
PHASE 19 — RECONCILIATION
==================================================

Implement a reconciliation mechanism.

The college must be able to compare:

SSAMS payment records
        VS
Gateway transaction records

Support identifying:

- matched payments
- missing payments
- duplicate references
- amount mismatch
- pending transactions
- failed transactions

Do not automatically alter financial records during reconciliation
without explicit safe business rules.

==================================================
PHASE 20 — REFUNDS
==================================================

Design refund support even if automatic gateway refunds are not
implemented initially.

A refund record must preserve:

- original payment
- amount
- reason
- requested by
- approved by
- status
- gateway reference where applicable
- timestamps
- audit trail

Do not delete the original payment.

==================================================
PHASE 21 — DOCUMENTATION
==================================================

Create/update:

docs/PAYMENT_SYSTEM.md
docs/ESEWA_INTEGRATION.md
docs/FEE_MANAGEMENT.md
docs/PAYMENT_DATABASE_SCHEMA.md
docs/PAYMENT_API_SPEC.md
docs/PAYMENT_SECURITY.md
docs/PAYMENT_TESTING.md
docs/PAYMENT_RECONCILIATION.md

Also update relevant existing:

PRD.md
SRS.md
DATABASE_SCHEMA.md
API_SPEC.md
SECURITY.md
FEATURES.md
ROLES_PERMISSIONS.md
USER_FLOWS.md
TEST_CASES.md
IMPLEMENTATION_PLAN.md
TASKS.md
CHANGELOG.md

==================================================
PHASE 22 — ENVIRONMENT
==================================================

Add payment configuration safely to environment configuration.

Never commit real credentials.

Use placeholders/example configuration.

For example, conceptually:

ESEWA_ENVIRONMENT
ESEWA_MERCHANT_ID
ESEWA_SECRET
ESEWA_BASE_URL
ESEWA_PAYMENT_URL
ESEWA_VERIFICATION_URL

Use the exact configuration required by the current official
eSewa integration documentation.

Do not invent configuration names if the existing project has
an established configuration convention.

==================================================
PHASE 23 — TESTING
==================================================

Implement tests for:

Fee creation
Fee assignment
Invoice creation
Invoice calculation
Partial payment
Full payment
Overdue invoice
Payment initiation
Payment verification
Successful payment
Failed payment
Cancelled payment
Duplicate callback
Duplicate transaction
Amount mismatch
Invoice mismatch
Unauthorized student
Cross-student access attempt
Cross-institution access attempt
Receipt generation
Ledger calculation
Payment history
Reconciliation
Refund record
Gateway failure
Network timeout
Retry behavior

Critical financial tests must use transactions and realistic
database integration tests where appropriate.

==================================================
PHASE 24 — PAYMENT SANDBOX
==================================================

Do not immediately use production credentials.

Create a development/staging configuration for the gateway.

The implementation must clearly distinguish:

development/sandbox
staging
production

Never accidentally use production payment configuration during
local development.

==================================================
PHASE 25 — DO NOT BREAK EXISTING SYSTEM
==================================================

After each major payment implementation:

Run the existing backend tests.

Run the existing Flutter tests.

Run the existing admin tests.

Run relevant integration tests.

Verify existing:

authentication
student management
academic management
attendance
examination
results
notifications
dashboard

still work.

Payment implementation must be additive and backward compatible.

==================================================
PHASE 26 — FINAL VERIFICATION
==================================================

Before declaring completion:

[ ] Fee management implemented
[ ] Invoice management implemented
[ ] Student fee view implemented
[ ] Payment gateway abstraction implemented
[ ] eSewa integration implemented
[ ] Server-side verification implemented
[ ] Payment idempotency implemented
[ ] Payment state machine implemented
[ ] Student ledger implemented
[ ] Receipt generation implemented
[ ] Payment history implemented
[ ] Admin payment dashboard implemented
[ ] Reconciliation implemented
[ ] Refund model implemented
[ ] RBAC implemented
[ ] Audit logging implemented
[ ] Security checks implemented
[ ] Database migrations tested
[ ] Backend tests pass
[ ] Flutter tests pass
[ ] Existing tests still pass
[ ] Payment integration tests pass
[ ] Documentation updated
[ ] Environment configuration documented
[ ] Sandbox/staging configuration documented
[ ] Production configuration documented
[ ] No real secrets committed
[ ] No fake payment success logic exists
[ ] No client-controlled payment confirmation exists
[ ] No duplicate payment processing exists

==================================================
EXECUTION RULE
==================================================

Do all of the above autonomously.

Do not stop after creating documentation.

Do not stop after creating database tables.

Do not stop after creating API endpoints.

Do not stop after creating UI screens.

Continue until implementation, integration, testing and documentation
are complete.

If a build/test error is caused by your implementation, diagnose and
fix it.

If an existing architecture requires adaptation, make the smallest
safe change.

If a requirement is ambiguous, choose the safest production-ready
implementation and document the decision.

DO NOT ASK FOR THE NEXT PROMPT.

START BY INSPECTING THE EXISTING SSAMS IMPLEMENTATION.
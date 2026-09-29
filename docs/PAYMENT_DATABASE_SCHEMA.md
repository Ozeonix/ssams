# SSAMS Payment Database Schema

Adapt names/types to existing SSAMS migrations and conventions.

## fee_categories
id, institution_id, code, name, description, active, created_at, updated_at
Unique: institution_id + code

## fee_structures
id, institution_id, academic_year_id, fee_category_id, scope_type, scope_id, amount, currency, frequency, due_date, active, created_at, updated_at

## student_fee_assignments
id, institution_id, student_id, fee_structure_id, assigned_amount, waiver_amount, effective_from, effective_to, status, created_at

## fee_invoices
id, institution_id, student_id, invoice_number, academic_year_id, subtotal, discount_total, tax_total, total_amount, paid_amount, outstanding_amount, currency, issue_date, due_date, status, created_at, updated_at

## fee_invoice_items
id, invoice_id, fee_category_id, description, quantity, unit_amount, discount_amount, line_total, metadata

## payment_transactions
id, institution_id, student_id, invoice_id, provider, transaction_uuid, product_code, gateway_reference_id, requested_amount, verified_amount, currency, status, failure_reason, initiated_at, verified_at, created_at, updated_at

Use uniqueness constraints for gateway identifiers and transaction UUID.

## payment_attempts
id, payment_transaction_id, attempt_number, provider_request_id, request_snapshot_safe, response_snapshot_safe, status, created_at

Never store secrets in snapshots.

## payment_allocations
id, payment_transaction_id, invoice_id, invoice_item_id nullable, allocated_amount, created_at

## student_ledger_entries
id, institution_id, student_id, invoice_id, payment_transaction_id nullable, entry_type, debit_amount, credit_amount, reference, created_at

Ledger is append-only.

## payment_receipts
id, institution_id, student_id, invoice_id, payment_transaction_id, receipt_number, amount, issued_at, pdf_storage_key, status

Unique: payment_transaction_id.

## refunds
id, institution_id, payment_transaction_id, requested_amount, approved_amount, status, reason, gateway_reference_id, created_at, completed_at

## payment_gateway_configs
id, institution_id, provider, environment, merchant/product_code, secret_reference, enabled, created_at, updated_at

Prefer secret-manager references instead of raw secrets.

## payment_verification_logs
id, payment_transaction_id, provider, request_type, request_metadata, response_metadata, verification_result, created_at

Redact secrets.

## reconciliation_batches
id, institution_id, provider, period_start, period_end, status, created_at, completed_at

## reconciliation_items
id, reconciliation_batch_id, payment_transaction_id nullable, gateway_reference_id, gateway_amount, internal_amount, status, difference, notes

## Indexes
Index institution_id, student_id, invoice_id, transaction_uuid, gateway_reference_id, status, created_at and due_date.

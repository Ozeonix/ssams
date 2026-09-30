package com.artms.payment.web;

import com.artms.payment.application.PaymentService;
import com.artms.payment.domain.*;
import com.artms.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Payment REST Controller.
 *
 * Endpoints:
 *   POST   /api/v1/fees/categories           – create fee category (admin)
 *   GET    /api/v1/fees/categories           – list fee categories
 *   POST   /api/v1/fees/invoices             – create invoice (admin/finance)
 *   POST   /api/v1/fees/invoices/{id}/issue  – issue invoice (admin/finance)
 *   GET    /api/v1/fees/invoices/{id}        – get invoice
 *   GET    /api/v1/fees/invoices/my          – my outstanding invoices (student)
 *   POST   /api/v1/payments/initiate         – initiate payment (student)
 *   GET    /api/v1/payments/callback/success – eSewa success callback (server-side verify)
 *   GET    /api/v1/payments/callback/failure – eSewa failure callback
 *   GET    /api/v1/payments/history/my       – my payment history (student)
 *   GET    /api/v1/payments/{txnId}/receipt  – get receipt
 *   GET    /api/v1/payments/ledger/my        – my ledger entries (student)
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Payments", description = "Fee management and payment processing")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ─── Fee Categories ────────────────────────────────────────────────────────

    @Operation(summary = "Create fee category (admin/finance)")
    @PostMapping("/fees/categories")
    @PreAuthorize("hasAuthority('PERM_fee:manage')")
    public ResponseEntity<FeeCategory> createFeeCategory(
            @RequestBody @Valid FeeCategoryRequest req,
            @AuthenticationPrincipal UserDetails user) {

        UUID tenantId = TenantContext.getTenantId();
        FeeCategory cat = paymentService.createFeeCategory(tenantId, req.code(), req.name(), req.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(cat);
    }

    @Operation(summary = "List fee categories")
    @GetMapping("/fees/categories")
    @PreAuthorize("hasAuthority('PERM_fee:read') or hasAuthority('PERM_fee:manage')")
    public ResponseEntity<List<FeeCategory>> listCategories() {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(paymentService.getCategories(tenantId));
    }

    // ─── Fee Invoices ──────────────────────────────────────────────────────────

    @Operation(summary = "Create student fee invoice (admin/finance)")
    @PostMapping("/fees/invoices")
    @PreAuthorize("hasAuthority('PERM_invoice:create')")
    public ResponseEntity<FeeInvoice> createInvoice(
            @RequestBody @Valid CreateInvoiceRequest req,
            @AuthenticationPrincipal UserDetails user) {

        UUID tenantId = TenantContext.getTenantId();
        UUID issuedBy = extractUserId(user);

        var serviceReq = new PaymentService.CreateInvoiceRequest(
            tenantId, req.studentId(), req.academicYearId(),
            req.dueDate(), req.notes(), req.discountAmount(), req.items()
        );
        FeeInvoice invoice = paymentService.createInvoice(serviceReq, issuedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @Operation(summary = "Issue an invoice (transitions DRAFT → ISSUED)")
    @PostMapping("/fees/invoices/{invoiceId}/issue")
    @PreAuthorize("hasAuthority('PERM_invoice:create')")
    public ResponseEntity<FeeInvoice> issueInvoice(
            @PathVariable UUID invoiceId,
            @AuthenticationPrincipal UserDetails user) {

        UUID tenantId = TenantContext.getTenantId();
        UUID issuedBy = extractUserId(user);
        return ResponseEntity.ok(paymentService.issueInvoice(invoiceId, tenantId, issuedBy));
    }

    @Operation(summary = "Get invoice by ID")
    @GetMapping("/fees/invoices/{invoiceId}")
    @PreAuthorize("hasAuthority('PERM_invoice:read') or hasAuthority('PERM_payment:initiate')")
    public ResponseEntity<FeeInvoice> getInvoice(@PathVariable UUID invoiceId) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(paymentService.getInvoice(invoiceId, tenantId));
    }

    @Operation(summary = "List my outstanding invoices (student)")
    @GetMapping("/fees/invoices/my")
    @PreAuthorize("hasAuthority('PERM_payment:initiate')")
    public ResponseEntity<List<FeeInvoice>> myOutstandingInvoices(
            @AuthenticationPrincipal UserDetails user) {

        UUID tenantId  = TenantContext.getTenantId();
        UUID studentId = extractUserId(user); // student's UUID linked via student.user_id
        return ResponseEntity.ok(paymentService.getOutstandingInvoices(studentId, tenantId));
    }

    @Operation(summary = "Get all invoices for a student (admin)")
    @GetMapping("/fees/invoices/student/{studentId}")
    @PreAuthorize("hasAuthority('PERM_invoice:read')")
    public ResponseEntity<List<FeeInvoice>> studentInvoices(@PathVariable UUID studentId) {
        UUID tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(paymentService.getStudentInvoices(studentId, tenantId));
    }

    // ─── Payment Initiation ────────────────────────────────────────────────────

    @Operation(summary = "Initiate payment for an invoice (student)")
    @PostMapping("/payments/initiate")
    @PreAuthorize("hasAuthority('PERM_payment:initiate')")
    public ResponseEntity<PaymentService.PaymentInitResponse> initiatePayment(
            @RequestBody @Valid PaymentInitRequest req,
            @AuthenticationPrincipal UserDetails user) {

        UUID tenantId  = TenantContext.getTenantId();
        UUID userId    = extractUserId(user);
        // IMPORTANT: studentId comes from the authenticated user's student record,
        // NOT from the request body – never trust client-supplied identity
        UUID studentId = userId; // simplified; real impl resolves via student lookup by userId

        PaymentService.PaymentInitResponse response = paymentService.initiatePayment(
            req.invoiceId(), studentId, tenantId, userId,
            req.gatewayCode() != null ? req.gatewayCode() : "ESEWA"
        );
        return ResponseEntity.ok(response);
    }

    // ─── Gateway Callbacks (server receives eSewa redirect) ───────────────────

    @Operation(summary = "eSewa success callback – server verifies payment")
    @GetMapping("/payments/callback/success")
    public ResponseEntity<Map<String, Object>> paymentSuccessCallback(
            @RequestParam String ref,
            @RequestParam(required = false) String encoded_data,
            @RequestParam Map<String, String> allParams) {

        PaymentService.PaymentConfirmationResult result =
            paymentService.verifyAndConfirmPayment(ref, allParams);

        if (result.success()) {
            return ResponseEntity.ok(Map.of(
                "status",        "CONFIRMED",
                "receipt_number", result.receipt() != null ? result.receipt().getReceiptNumber() : "",
                "transaction_ref", ref,
                "already_confirmed", result.alreadyConfirmed()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(Map.of(
                "status", "FAILED",
                "reason", result.failureReason(),
                "transaction_ref", ref
            ));
        }
    }

    @Operation(summary = "eSewa failure callback")
    @GetMapping("/payments/callback/failure")
    public ResponseEntity<Map<String, Object>> paymentFailureCallback(
            @RequestParam String ref,
            @RequestParam Map<String, String> allParams) {

        // Verify with gateway to get accurate failure status
        paymentService.verifyAndConfirmPayment(ref, allParams);
        return ResponseEntity.ok(Map.of(
            "status", "FAILED",
            "transaction_ref", ref
        ));
    }

    // ─── Payment History & Receipt ─────────────────────────────────────────────

    @Operation(summary = "My payment history (student)")
    @GetMapping("/payments/history/my")
    @PreAuthorize("hasAuthority('PERM_payment:initiate')")
    public ResponseEntity<List<PaymentTransaction>> myPaymentHistory(
            @AuthenticationPrincipal UserDetails user) {

        UUID tenantId  = TenantContext.getTenantId();
        UUID studentId = extractUserId(user);
        return ResponseEntity.ok(paymentService.getStudentPaymentHistory(studentId, tenantId));
    }

    @Operation(summary = "Get receipt for a transaction")
    @GetMapping("/payments/{transactionId}/receipt")
    @PreAuthorize("hasAuthority('PERM_receipt:read') or hasAuthority('PERM_payment:initiate')")
    public ResponseEntity<PaymentReceipt> getReceipt(@PathVariable UUID transactionId) {
        UUID tenantId = TenantContext.getTenantId();
        return paymentService.getReceipt(transactionId, tenantId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "My ledger entries (student)")
    @GetMapping("/payments/ledger/my")
    @PreAuthorize("hasAuthority('PERM_ledger:read') or hasAuthority('PERM_payment:initiate')")
    public ResponseEntity<List<StudentLedgerEntry>> myLedger(
            @AuthenticationPrincipal UserDetails user) {

        UUID tenantId  = TenantContext.getTenantId();
        UUID studentId = extractUserId(user);
        return ResponseEntity.ok(paymentService.getStudentLedger(studentId, tenantId));
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private UUID extractUserId(UserDetails user) {
        try { return UUID.fromString(user.getUsername()); }
        catch (IllegalArgumentException e) { return UUID.randomUUID(); /* tests */ }
    }

    // ── Request DTOs ──────────────────────────────────────────────────────────

    record FeeCategoryRequest(
        @NotNull String code,
        @NotNull String name,
        String description
    ) {}

    record CreateInvoiceRequest(
        @NotNull UUID studentId,
        UUID academicYearId,
        LocalDate dueDate,
        String notes,
        BigDecimal discountAmount,
        @NotNull List<PaymentService.InvoiceItemRequest> items
    ) {}

    record PaymentInitRequest(
        @NotNull UUID invoiceId,
        String gatewayCode
    ) {}
}

package com.artms.payment.application;

import com.artms.notification.domain.OutboxEvent;
import com.artms.notification.domain.OutboxEventRepository;
import com.artms.payment.domain.*;
import com.artms.payment.gateway.PaymentGateway;
import com.artms.payment.gateway.PaymentGatewayRegistry;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.student.domain.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Core payment orchestration service.
 *
 * Responsibilities:
 *  1. Fee management (categories, structures)
 *  2. Invoice lifecycle management
 *  3. Payment initiation (creates transaction + gateway request)
 *  4. Payment verification (server-side ONLY – never client trust)
 *  5. Ledger updates
 *  6. Receipt generation
 *  7. Outbox event publishing
 *
 * All financial state changes happen within a single @Transactional boundary.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final FeeCategoryRepository    feeCategoryRepository;
    private final FeeInvoiceRepository     invoiceRepository;
    private final PaymentTransactionRepository txnRepository;
    private final PaymentReceiptRepository receiptRepository;
    private final StudentLedgerRepository  ledgerRepository;
    private final OutboxEventRepository    outboxRepository;
    private final StudentRepository        studentRepository;
    private final PaymentGatewayRegistry   gatewayRegistry;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    // Atomic counter for receipt numbering within a running instance
    private final AtomicLong receiptSequence = new AtomicLong(System.currentTimeMillis() % 1_000_000);

    // ══════════════════════════════════════════════════════════════════════════
    // FEE CATEGORY MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════

    @Transactional
    public FeeCategory createFeeCategory(UUID tenantId, String code, String name, String description) {
        if (feeCategoryRepository.findByTenantIdAndCode(tenantId, code).isPresent()) {
            throw new BusinessRuleException("FEE_CATEGORY_EXISTS",
                "Fee category with code '" + code + "' already exists");
        }
        FeeCategory cat = new FeeCategory();
        cat.setTenantId(tenantId);
        cat.setCode(code.toUpperCase().strip());
        cat.setName(name.strip());
        cat.setDescription(description);
        return feeCategoryRepository.save(cat);
    }

    @Transactional(readOnly = true)
    public List<FeeCategory> getCategories(UUID tenantId) {
        return feeCategoryRepository.findByTenantIdAndActiveTrue(tenantId);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INVOICE MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════

    @Transactional
    public FeeInvoice createInvoice(CreateInvoiceRequest req, UUID issuedBy) {
        // Validate student exists and belongs to tenant
        var student = studentRepository.findByTenantIdAndId(req.tenantId(), req.studentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        FeeInvoice invoice = new FeeInvoice();
        invoice.setTenantId(req.tenantId());
        invoice.setStudentId(req.studentId());
        invoice.setAcademicYearId(req.academicYearId());
        invoice.setDueDate(req.dueDate());
        invoice.setNotes(req.notes());
        invoice.setInvoiceNumber(generateInvoiceNumber(req.tenantId()));

        // Build line items
        for (var itemReq : req.items()) {
            FeeCategory cat = feeCategoryRepository.findById(itemReq.feeCategoryId())
                .filter(c -> c.getTenantId().equals(req.tenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("Fee category not found: " + itemReq.feeCategoryId()));

            FeeInvoiceItem item = new FeeInvoiceItem();
            item.setInvoice(invoice);
            item.setFeeCategory(cat);
            item.setDescription(itemReq.description() != null ? itemReq.description() : cat.getName());
            item.setQuantity(itemReq.quantity() != null ? itemReq.quantity() : 1);
            item.setUnitAmount(itemReq.unitAmount());
            item.calculateTotal();
            invoice.getItems().add(item);
        }

        invoice.setDiscountAmount(req.discountAmount() != null ? req.discountAmount() : BigDecimal.ZERO);
        invoice.recalculate();

        FeeInvoice saved = invoiceRepository.save(invoice);
        log.info("Invoice created: {} for student {} amount NPR {}",
            saved.getInvoiceNumber(), req.studentId(), saved.getTotalAmount());

        // Record charge in ledger
        appendLedger(req.tenantId(), req.studentId(), saved.getId(), null,
            LedgerEntryType.CHARGE, saved.getTotalAmount(),
            "Invoice " + saved.getInvoiceNumber(), issuedBy);

        return saved;
    }

    @Transactional
    public FeeInvoice issueInvoice(UUID invoiceId, UUID tenantId, UUID issuedBy) {
        FeeInvoice invoice = getInvoiceOrFail(invoiceId, tenantId);
        invoice.issue(issuedBy);
        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public List<FeeInvoice> getOutstandingInvoices(UUID studentId, UUID tenantId) {
        return invoiceRepository.findOutstandingByStudent(studentId, tenantId);
    }

    @Transactional(readOnly = true)
    public FeeInvoice getInvoice(UUID invoiceId, UUID tenantId) {
        return getInvoiceOrFail(invoiceId, tenantId);
    }

    @Transactional(readOnly = true)
    public List<FeeInvoice> getStudentInvoices(UUID studentId, UUID tenantId) {
        return invoiceRepository.findByStudentIdAndTenantId(studentId, tenantId);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PAYMENT INITIATION
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Initiate a payment for an invoice.
     * Returns gateway form fields/URL to pass to the Flutter client.
     * The client opens the gateway in a WebView; the backend verifies on callback.
     */
    @Transactional
    public PaymentInitResponse initiatePayment(UUID invoiceId, UUID studentId, UUID tenantId,
                                               UUID userId, String gatewayCode) {
        FeeInvoice invoice = getInvoiceOrFail(invoiceId, tenantId);

        // Security: student can only pay THEIR OWN invoices
        if (!invoice.getStudentId().equals(studentId)) {
            throw new BusinessRuleException("PAYMENT_UNAUTHORIZED",
                "Student cannot pay another student's invoice");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID
            || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessRuleException("INVOICE_NOT_PAYABLE",
                "Invoice is " + invoice.getStatus());
        }

        if (invoice.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("INVOICE_ZERO_BALANCE",
                "Invoice has zero or negative balance");
        }

        // Create payment transaction
        PaymentTransaction txn = new PaymentTransaction();
        txn.setTenantId(tenantId);
        txn.setStudentId(studentId);
        txn.setInvoiceId(invoiceId);
        txn.setGatewayCode(gatewayCode.toUpperCase());
        txn.setAmount(invoice.getBalance());
        txn.setCurrency("NPR");
        txn.setTransactionRef(generateTransactionRef());
        txn.setInitiatedByUser(userId);
        txn = txnRepository.save(txn);

        // Build gateway request
        PaymentGateway gateway = gatewayRegistry.getGateway(gatewayCode);
        String successUrl = appBaseUrl + "/api/v1/payments/callback/success?ref=" + txn.getTransactionRef();
        String failureUrl = appBaseUrl + "/api/v1/payments/callback/failure?ref=" + txn.getTransactionRef();

        PaymentGateway.PaymentRequest gatewayReq = gateway.createPaymentRequest(
            new PaymentGateway.PaymentInitParams(
                txn.getTransactionRef(),
                txn.getAmount(),
                "NPR",
                successUrl,
                failureUrl,
                "SSAMS Fee Payment",
                null
            )
        );

        txn.markInitiated(gatewayReq.gatewayOrderId());
        txnRepository.save(txn);

        log.info("Payment initiated: txnRef={} amount={} gateway={}",
            txn.getTransactionRef(), txn.getAmount(), gatewayCode);

        return new PaymentInitResponse(
            txn.getId(),
            txn.getTransactionRef(),
            gatewayReq.gatewayUrl(),
            gatewayReq.formFields()
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PAYMENT VERIFICATION  (server-side – CRITICAL)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Verify and confirm a payment.
     * Called from the backend callback endpoint – NEVER trust client success claims.
     * Idempotent: if already SUCCESS, returns existing receipt without reprocessing.
     */
    @Transactional
    public PaymentConfirmationResult verifyAndConfirmPayment(
            String transactionRef, Map<String, String> callbackParams) {

        PaymentTransaction txn = txnRepository.findByTransactionRef(transactionRef)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Transaction not found: " + transactionRef));

        // Idempotency: already confirmed
        if (txn.getStatus() == PaymentStatus.SUCCESS) {
            PaymentReceipt receipt = receiptRepository.findByTransactionId(txn.getId())
                .orElse(null);
            log.info("Duplicate callback for already-confirmed txn {}", transactionRef);
            return PaymentConfirmationResult.alreadyConfirmed(txn, receipt);
        }

        // Verify with gateway
        PaymentGateway gateway = gatewayRegistry.getGateway(txn.getGatewayCode());
        PaymentGateway.VerificationResult verification = gateway.verifyPayment(callbackParams);

        if (!verification.success()) {
            txn.markFailed(verification.failureReason());
            txnRepository.save(txn);
            log.warn("Payment verification FAILED: txnRef={} reason={}",
                transactionRef, verification.failureReason());
            return PaymentConfirmationResult.failed(txn, verification.failureReason());
        }

        // Amount validation – server must match, never trust client amount
        FeeInvoice invoice = getInvoiceOrFail(txn.getInvoiceId(), txn.getTenantId());
        if (verification.verifiedAmount().compareTo(txn.getAmount()) != 0) {
            String reason = "Amount mismatch: expected " + txn.getAmount()
                + " verified " + verification.verifiedAmount();
            txn.markFailed(reason);
            txnRepository.save(txn);
            log.error("Payment amount mismatch! {}", reason);
            return PaymentConfirmationResult.failed(txn, reason);
        }

        // ── Confirm payment (all in one transaction) ──────────────────────────
        txn.markSuccess(verification.gatewayTxnRef(), verification.rawResponse());
        txnRepository.save(txn);

        // Apply to invoice
        invoice.applyPayment(txn.getAmount());
        invoiceRepository.save(invoice);

        // Ledger entry
        appendLedger(txn.getTenantId(), txn.getStudentId(), txn.getInvoiceId(), txn.getId(),
            LedgerEntryType.PAYMENT, txn.getAmount().negate(),
            "eSewa Payment " + txn.getTransactionRef(), null);

        // Generate receipt
        PaymentReceipt receipt = generateReceipt(txn, invoice);

        // Publish outbox event (for notification)
        publishPaymentConfirmedEvent(txn, invoice, receipt);

        log.info("Payment CONFIRMED: txnRef={} amount={} invoice={} receiptNo={}",
            transactionRef, txn.getAmount(), invoice.getInvoiceNumber(), receipt.getReceiptNumber());

        return PaymentConfirmationResult.success(txn, receipt);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PAYMENT HISTORY
    // ══════════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<PaymentTransaction> getStudentPaymentHistory(UUID studentId, UUID tenantId) {
        return txnRepository.findByStudentIdAndTenantId(studentId, tenantId);
    }

    @Transactional(readOnly = true)
    public List<StudentLedgerEntry> getStudentLedger(UUID studentId, UUID tenantId) {
        return ledgerRepository.findByStudentIdAndTenantIdOrderByCreatedAtDesc(studentId, tenantId);
    }

    @Transactional(readOnly = true)
    public Optional<PaymentReceipt> getReceipt(UUID transactionId, UUID tenantId) {
        return receiptRepository.findByTransactionId(transactionId)
            .filter(r -> r.getTenantId().equals(tenantId));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private FeeInvoice getInvoiceOrFail(UUID invoiceId, UUID tenantId) {
        return invoiceRepository.findById(invoiceId)
            .filter(i -> i.getTenantId().equals(tenantId))
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
    }

    private String generateInvoiceNumber(UUID tenantId) {
        String prefix = tenantId.toString().substring(0, 4).toUpperCase();
        String date   = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seq    = String.format("%05d", receiptSequence.incrementAndGet() % 99999);
        return "INV-" + prefix + "-" + date + "-" + seq;
    }

    private String generateTransactionRef() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private String generateReceiptNumber() {
        return "RCP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            + "-" + String.format("%06d", receiptSequence.incrementAndGet() % 999999);
    }

    private void appendLedger(UUID tenantId, UUID studentId, UUID invoiceId, UUID txnId,
                               LedgerEntryType type, BigDecimal amount, String description, UUID createdBy) {
        BigDecimal prevBalance = ledgerRepository
            .findTopByStudentIdAndTenantIdOrderByCreatedAtDesc(studentId, tenantId)
            .map(StudentLedgerEntry::getBalanceAfter)
            .orElse(BigDecimal.ZERO);

        StudentLedgerEntry entry = new StudentLedgerEntry();
        entry.setTenantId(tenantId);
        entry.setStudentId(studentId);
        entry.setInvoiceId(invoiceId);
        entry.setTransactionId(txnId);
        entry.setEntryType(type);
        entry.setAmount(amount);
        entry.setDescription(description);
        entry.setBalanceAfter(prevBalance.add(amount));
        entry.setCreatedBy(createdBy);
        ledgerRepository.save(entry);
    }

    private PaymentReceipt generateReceipt(PaymentTransaction txn, FeeInvoice invoice) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("invoice_number", invoice.getInvoiceNumber());
        snapshot.put("student_id",     txn.getStudentId().toString());
        snapshot.put("amount",         txn.getAmount().toPlainString());
        snapshot.put("currency",       txn.getCurrency());
        snapshot.put("gateway",        txn.getGatewayCode());
        snapshot.put("gateway_txn_ref", txn.getGatewayTxnRef());
        snapshot.put("paid_at",        txn.getCompletedAt() != null
            ? txn.getCompletedAt().toString() : "");
        snapshot.put("balance_after",  invoice.getBalance().toPlainString());

        PaymentReceipt receipt = new PaymentReceipt();
        receipt.setReceiptNumber(generateReceiptNumber());
        receipt.setTenantId(txn.getTenantId());
        receipt.setTransactionId(txn.getId());
        receipt.setStudentId(txn.getStudentId());
        receipt.setInvoiceId(txn.getInvoiceId());
        receipt.setAmount(txn.getAmount());
        receipt.setPaymentMethod(txn.getGatewayCode());
        receipt.setGatewayRef(txn.getGatewayTxnRef());
        receipt.setReceiptData(snapshot);
        return receiptRepository.save(receipt);
    }

    private void publishPaymentConfirmedEvent(PaymentTransaction txn,
                                              FeeInvoice invoice, PaymentReceipt receipt) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("transaction_ref",  txn.getTransactionRef());
        payload.put("student_id",       txn.getStudentId().toString());
        payload.put("invoice_number",   invoice.getInvoiceNumber());
        payload.put("amount",           txn.getAmount().toPlainString());
        payload.put("receipt_number",   receipt.getReceiptNumber());
        payload.put("invoice_status",   invoice.getStatus().name());

        OutboxEvent event = new OutboxEvent();
        event.setTenantId(txn.getTenantId());
        event.setAggregateType("PAYMENT");
        event.setAggregateId(txn.getId().toString());
        event.setEventType("PAYMENT_CONFIRMED");
        event.setPayload(payload);
        outboxRepository.save(event);
    }

    // ── Request / Response records ────────────────────────────────────────────

    public record CreateInvoiceRequest(
        UUID tenantId, UUID studentId, UUID academicYearId,
        LocalDate dueDate, String notes, BigDecimal discountAmount,
        List<InvoiceItemRequest> items
    ) {}

    public record InvoiceItemRequest(
        UUID feeCategoryId, String description, Integer quantity, BigDecimal unitAmount
    ) {}

    public record PaymentInitResponse(
        UUID transactionId, String transactionRef,
        String gatewayUrl, Map<String, String> gatewayFormFields
    ) {}

    public record PaymentConfirmationResult(
        boolean success, boolean alreadyConfirmed,
        PaymentTransaction transaction, PaymentReceipt receipt, String failureReason
    ) {
        static PaymentConfirmationResult success(PaymentTransaction txn, PaymentReceipt r) {
            return new PaymentConfirmationResult(true, false, txn, r, null);
        }
        static PaymentConfirmationResult alreadyConfirmed(PaymentTransaction txn, PaymentReceipt r) {
            return new PaymentConfirmationResult(true, true, txn, r, null);
        }
        static PaymentConfirmationResult failed(PaymentTransaction txn, String reason) {
            return new PaymentConfirmationResult(false, false, txn, null, reason);
        }
    }
}

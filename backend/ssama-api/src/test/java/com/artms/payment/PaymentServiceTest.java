package com.artms.payment;

import com.artms.notification.domain.OutboxEventRepository;
import com.artms.payment.application.PaymentService;
import com.artms.payment.domain.*;
import com.artms.payment.gateway.PaymentGateway;
import com.artms.payment.gateway.PaymentGatewayRegistry;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.tenant.TenantContext;
import com.artms.student.domain.Student;
import com.artms.student.domain.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private FeeCategoryRepository feeCategoryRepository;
    @Mock private FeeInvoiceRepository invoiceRepository;
    @Mock private PaymentTransactionRepository txnRepository;
    @Mock private PaymentReceiptRepository receiptRepository;
    @Mock private StudentLedgerRepository ledgerRepository;
    @Mock private OutboxEventRepository outboxRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private PaymentGatewayRegistry gatewayRegistry;

    @InjectMocks
    private PaymentService paymentService;

    private UUID tenantId;
    private UUID userId;
    private UUID studentId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        userId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        TenantContext.set(tenantId, userId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("CreateFeeCategory persists and returns active fee category")
    void createFeeCategorySuccess() {
        when(feeCategoryRepository.findByTenantIdAndCode(tenantId, "TUITION")).thenReturn(Optional.empty());
        when(feeCategoryRepository.save(any(FeeCategory.class))).thenAnswer(inv -> inv.getArgument(0));

        FeeCategory cat = paymentService.createFeeCategory(tenantId, "TUITION", "Tuition Fee", "Classroom fee");

        assertThat(cat).isNotNull();
        assertThat(cat.getCode()).isEqualTo("TUITION");
        assertThat(cat.getName()).isEqualTo("Tuition Fee");
        assertThat(cat.isActive()).isTrue();
        verify(feeCategoryRepository).save(any(FeeCategory.class));
    }

    @Test
    @DisplayName("CreateFeeCategory throws exception when category code already exists")
    void createFeeCategoryDuplicateCode() {
        FeeCategory existing = new FeeCategory();
        existing.setCode("TUITION");
        when(feeCategoryRepository.findByTenantIdAndCode(tenantId, "TUITION")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> paymentService.createFeeCategory(tenantId, "TUITION", "Tuition Fee", "Classroom fee"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("CreateInvoice validates student and calculates item line totals")
    void createInvoiceSuccess() {
        Student mockStudent = new Student();
        mockStudent.setId(studentId);
        mockStudent.setTenantId(tenantId);
        when(studentRepository.findByTenantIdAndId(tenantId, studentId)).thenReturn(Optional.of(mockStudent));

        UUID catId = UUID.randomUUID();
        FeeCategory cat = new FeeCategory();
        cat.setId(catId);
        cat.setTenantId(tenantId);
        cat.setName("Tuition");
        when(feeCategoryRepository.findById(catId)).thenReturn(Optional.of(cat));
        when(invoiceRepository.save(any(FeeInvoice.class))).thenAnswer(inv -> inv.getArgument(0));

        var itemReq = new PaymentService.InvoiceItemRequest(catId, "Term 1 Tuition", 1, new BigDecimal("12000.00"));
        var req = new PaymentService.CreateInvoiceRequest(
                tenantId, studentId, UUID.randomUUID(), LocalDate.now().plusDays(15), "Regular tuition", BigDecimal.ZERO, List.of(itemReq)
        );

        FeeInvoice invoice = paymentService.createInvoice(req, userId);

        assertThat(invoice).isNotNull();
        assertThat(invoice.getTotalAmount()).isEqualByComparingTo(new BigDecimal("12000.00"));
        assertThat(invoice.getBalance()).isEqualByComparingTo(new BigDecimal("12000.00"));
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.DRAFT);
        assertThat(invoice.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("InitiatePayment creates PaymentTransaction and calls gateway")
    void initiatePaymentSuccess() {
        UUID invoiceId = UUID.randomUUID();
        FeeInvoice invoice = new FeeInvoice();
        invoice.setId(invoiceId);
        invoice.setTenantId(tenantId);
        invoice.setStudentId(studentId);
        invoice.setBalance(new BigDecimal("10000.00"));
        invoice.setStatus(InvoiceStatus.ISSUED);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(txnRepository.save(any(PaymentTransaction.class))).thenAnswer(inv -> {
            PaymentTransaction pt = inv.getArgument(0);
            if (pt.getId() == null) pt.setId(UUID.randomUUID());
            return pt;
        });

        PaymentGateway mockGateway = mock(PaymentGateway.class);
        when(mockGateway.createPaymentRequest(any(PaymentGateway.PaymentInitParams.class)))
                .thenReturn(new PaymentGateway.PaymentRequest(
                        "https://rc-epay.esewa.com.np/api/epay/main/v2/form",
                        Map.of("product_code", "EPAYTEST", "total_amount", "10000.00"),
                        "TXN-123456"
                ));

        when(gatewayRegistry.getGateway("ESEWA")).thenReturn(mockGateway);

        PaymentService.PaymentInitResponse result = paymentService.initiatePayment(
                invoiceId, studentId, tenantId, userId, "ESEWA"
        );

        assertThat(result.gatewayUrl()).isNotBlank();
        assertThat(result.transactionRef()).isNotBlank();
        verify(txnRepository, times(2)).save(any(PaymentTransaction.class));
    }

    @Test
    @DisplayName("InitiatePayment rejects attempt by unauthorized student on another student's invoice")
    void initiatePaymentUnauthorizedStudent() {
        UUID invoiceId = UUID.randomUUID();
        UUID otherStudentId = UUID.randomUUID();

        FeeInvoice invoice = new FeeInvoice();
        invoice.setId(invoiceId);
        invoice.setTenantId(tenantId);
        invoice.setStudentId(otherStudentId); // Belongs to different student
        invoice.setBalance(new BigDecimal("5000.00"));
        invoice.setStatus(InvoiceStatus.ISSUED);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        assertThatThrownBy(() -> paymentService.initiatePayment(
                invoiceId, studentId, tenantId, userId, "ESEWA"
        )).isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Student cannot pay another student's invoice");
    }

    @Test
    @DisplayName("VerifyAndConfirmPayment confirms payment, updates invoice balance, creates ledger and receipt")
    void verifyAndConfirmPaymentSuccess() {
        String txnRef = "TXN-20260930-REF1";
        UUID invoiceId = UUID.randomUUID();

        FeeInvoice invoice = new FeeInvoice();
        invoice.setId(invoiceId);
        invoice.setTenantId(tenantId);
        invoice.setStudentId(studentId);
        invoice.setInvoiceNumber("INV-001");
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setTotalAmount(new BigDecimal("10000.00"));
        invoice.setBalance(new BigDecimal("10000.00"));
        invoice.setStatus(InvoiceStatus.ISSUED);

        PaymentTransaction txn = new PaymentTransaction();
        txn.setId(UUID.randomUUID());
        txn.setTenantId(tenantId);
        txn.setStudentId(studentId);
        txn.setInvoiceId(invoiceId);
        txn.setTransactionRef(txnRef);
        txn.setAmount(new BigDecimal("10000.00"));
        txn.setGatewayCode("ESEWA");
        txn.setStatus(PaymentStatus.INITIATED);

        when(txnRepository.findByTransactionRef(txnRef)).thenReturn(Optional.of(txn));
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        PaymentGateway mockGateway = mock(PaymentGateway.class);
        when(mockGateway.verifyPayment(any()))
                .thenReturn(new PaymentGateway.VerificationResult(
                        true, "COMPLETE", new BigDecimal("10000.00"), "ESEWA-TXN-999", null, Map.of()
                ));
        when(gatewayRegistry.getGateway("ESEWA")).thenReturn(mockGateway);

        when(receiptRepository.save(any(PaymentReceipt.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentService.PaymentConfirmationResult result = paymentService.verifyAndConfirmPayment(txnRef, Map.of());

        assertThat(result.success()).isTrue();
        assertThat(result.transaction().getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.transaction().getGatewayTxnRef()).isEqualTo("ESEWA-TXN-999");
        assertThat(invoice.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);

        // Verify ledger entry created
        verify(ledgerRepository).save(any(StudentLedgerEntry.class));
        // Verify receipt created
        verify(receiptRepository).save(any(PaymentReceipt.class));
        // Verify outbox event published
        verify(outboxRepository).save(any());
    }

    @Test
    @DisplayName("Duplicate callback returns already confirmed result without double allocation")
    void verifyAndConfirmPaymentDuplicateIdempotent() {
        String txnRef = "TXN-20260930-DUP";

        PaymentTransaction txn = new PaymentTransaction();
        txn.setId(UUID.randomUUID());
        txn.setTransactionRef(txnRef);
        txn.setStatus(PaymentStatus.SUCCESS); // Already confirmed!

        when(txnRepository.findByTransactionRef(txnRef)).thenReturn(Optional.of(txn));
        when(receiptRepository.findByTransactionId(txn.getId())).thenReturn(Optional.of(new PaymentReceipt()));

        PaymentService.PaymentConfirmationResult result = paymentService.verifyAndConfirmPayment(txnRef, Map.of());

        assertThat(result.success()).isTrue();
        assertThat(result.alreadyConfirmed()).isTrue();
        // Verify no extra ledger entries saved
        verify(ledgerRepository, never()).save(any());
    }
}

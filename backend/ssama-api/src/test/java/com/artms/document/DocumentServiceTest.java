package com.artms.document;

import com.artms.audit.application.AuditService;
import com.artms.document.application.*;
import com.artms.document.domain.*;
import com.artms.exam.domain.Exam;
import com.artms.exam.domain.ExamRepository;
import com.artms.result.domain.ResultSnapshot;
import com.artms.result.domain.ResultSnapshotRepository;
import com.artms.result.domain.ResultStatus;
import com.artms.result.domain.ResultSubjectSnapshot;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.tenant.TenantContext;
import com.artms.student.domain.Student;
import com.artms.student.domain.StudentRepository;
import com.artms.tenant.domain.Tenant;
import com.artms.tenant.domain.TenantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock private DocumentTemplateRepository templateRepository;
    @Mock private GeneratedDocumentRepository generatedDocumentRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private TenantRepository tenantRepository;
    @Mock private ExamRepository examRepository;
    @Mock private ResultSnapshotRepository resultSnapshotRepository;
    @Mock private AuditService auditService;

    @InjectMocks
    private DocumentService documentService;

    private UUID tenantId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        userId = UUID.randomUUID();
        TenantContext.set(tenantId, userId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should block grade sheet generation if no published result exists")
    void testBlockGradeSheetWithoutPublishedResult() {
        UUID studentId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        Student student = new Student();
        student.setId(studentId);
        student.setFirstName("Alice");
        student.setLastName("Smith");

        Tenant tenant = new Tenant();
        tenant.setName("Apex Academy");

        when(studentRepository.findByTenantIdAndId(tenantId, studentId)).thenReturn(Optional.of(student));
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(examRepository.findByTenantIdAndId(tenantId, examId)).thenReturn(Optional.of(new Exam()));
        when(resultSnapshotRepository.findFirstByTenantIdAndExamIdAndStudentIdOrderByResultVersionDesc(
                tenantId, examId, studentId)).thenReturn(Optional.empty());

        GenerateDocumentRequest req = new GenerateDocumentRequest(
                DocumentType.GRADE_SHEET, studentId, examId, null);

        assertThatThrownBy(() -> documentService.generateDocument(req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("No published result found");
    }

    @Test
    @DisplayName("Should successfully generate Grade Sheet and compute checksum")
    void testGenerateGradeSheetSuccessfully() {
        UUID studentId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        Student student = new Student();
        student.setId(studentId);
        student.setFirstName("Alice");
        student.setLastName("Smith");
        student.setAdmissionNo("ADM-101");

        Tenant tenant = new Tenant();
        tenant.setName("Apex Academy");

        Exam exam = new Exam();
        exam.setId(examId);
        exam.setName("Annual Final 2026");

        ResultSnapshot snapshot = new ResultSnapshot();
        snapshot.setId(UUID.randomUUID());
        snapshot.setGpa(new BigDecimal("3.85"));
        snapshot.setTotalCredits(new BigDecimal("24.0"));
        snapshot.setResultStatus(ResultStatus.PASS);
        snapshot.setResultVersion(1);
        snapshot.setPublishedAt(OffsetDateTime.now());
        snapshot.setImmutableHash("abcd1234efgh5678");

        ResultSubjectSnapshot sub = new ResultSubjectSnapshot();
        sub.setSubjectNameSnapshot("Physics");
        sub.setCreditHours(new BigDecimal("4.0"));
        sub.setTotalFullMarks(new BigDecimal("100.00"));
        sub.setTotalObtained(new BigDecimal("88.00"));
        sub.setLetterGrade("A");
        sub.setGradePoint(new BigDecimal("3.6"));
        snapshot.setSubjectSnapshots(List.of(sub));

        when(studentRepository.findByTenantIdAndId(tenantId, studentId)).thenReturn(Optional.of(student));
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(examRepository.findByTenantIdAndId(tenantId, examId)).thenReturn(Optional.of(exam));
        when(resultSnapshotRepository.findFirstByTenantIdAndExamIdAndStudentIdOrderByResultVersionDesc(
                tenantId, examId, studentId)).thenReturn(Optional.of(snapshot));
        when(resultSnapshotRepository.findWithSubjectsByTenantIdAndId(tenantId, snapshot.getId()))
                .thenReturn(Optional.of(snapshot));

        when(generatedDocumentRepository.save(any(GeneratedDocument.class))).thenAnswer(inv -> inv.getArgument(0));

        GenerateDocumentRequest req = new GenerateDocumentRequest(
                DocumentType.GRADE_SHEET, studentId, examId, null);

        GeneratedDocumentResponse res = documentService.generateDocument(req);

        assertThat(res).isNotNull();
        assertThat(res.status()).isEqualTo(GeneratedDocumentStatus.READY);
        assertThat(res.checksum()).isNotBlank();
        assertThat(res.checksum()).hasSize(64); // SHA-256 hex string
        assertThat(res.content()).contains("OFFICIAL GRADE SHEET");
        assertThat(res.content()).contains("Alice Smith");
        assertThat(res.content()).contains("Physics");
        verify(auditService).record(eq(tenantId), eq(userId), eq("document:generate"), eq("GeneratedDocument"), anyString(), anyMap());
    }

    @Test
    @DisplayName("Should block Certificate generation when result status is FAIL")
    void testBlockCertificateWhenFail() {
        UUID studentId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        Student student = new Student();
        student.setId(studentId);
        student.setFirstName("Bob");
        student.setLastName("Jones");

        Tenant tenant = new Tenant();
        tenant.setName("Apex Academy");

        ResultSnapshot failSnapshot = new ResultSnapshot();
        failSnapshot.setResultStatus(ResultStatus.FAIL);

        when(studentRepository.findByTenantIdAndId(tenantId, studentId)).thenReturn(Optional.of(student));
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(examRepository.findByTenantIdAndId(tenantId, examId)).thenReturn(Optional.of(new Exam()));
        when(resultSnapshotRepository.findFirstByTenantIdAndExamIdAndStudentIdOrderByResultVersionDesc(
                tenantId, examId, studentId)).thenReturn(Optional.of(failSnapshot));

        GenerateDocumentRequest req = new GenerateDocumentRequest(
                DocumentType.CERTIFICATE, studentId, examId, null);

        assertThatThrownBy(() -> documentService.generateDocument(req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot issue certificate for result status: FAIL");
    }

    @Test
    @DisplayName("Should publicly verify authentic and revoked documents")
    void testVerifyDocument() {
        UUID validDocId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        Student student = new Student();
        student.setId(studentId);
        student.setFirstName("Alice");
        student.setLastName("Smith");
        student.setAdmissionNo("ADM-101");

        Tenant tenant = new Tenant();
        tenant.setName("Apex Academy");

        GeneratedDocument validDoc = new GeneratedDocument();
        validDoc.setId(validDocId);
        validDoc.setTenantId(tenantId);
        validDoc.setStudentId(studentId);
        validDoc.setDocumentType(DocumentType.GRADE_SHEET);
        validDoc.setStatus(GeneratedDocumentStatus.READY);
        validDoc.setChecksum("hash12345");
        validDoc.setGeneratedAt(OffsetDateTime.now());

        when(generatedDocumentRepository.findById(validDocId)).thenReturn(Optional.of(validDoc));
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        DocumentVerificationResponse validRes = documentService.verifyDocument(validDocId);
        assertThat(validRes.valid()).isTrue();
        assertThat(validRes.message()).contains("authentic and valid");
        assertThat(validRes.studentName()).isEqualTo("Alice Smith");

        // Now test revoked document
        validDoc.setStatus(GeneratedDocumentStatus.REVOKED);
        DocumentVerificationResponse revokedRes = documentService.verifyDocument(validDocId);
        assertThat(revokedRes.valid()).isFalse();
        assertThat(revokedRes.message()).contains("REVOKED");

        // Non-existent document
        UUID randomId = UUID.randomUUID();
        when(generatedDocumentRepository.findById(randomId)).thenReturn(Optional.empty());
        DocumentVerificationResponse notFoundRes = documentService.verifyDocument(randomId);
        assertThat(notFoundRes.valid()).isFalse();
        assertThat(notFoundRes.message()).contains("Document not found");
    }
}

package com.artms.exam;

import com.artms.academic.domain.*;
import com.artms.audit.application.AuditService;
import com.artms.enrollment.domain.EnrollmentRepository;
import com.artms.exam.application.*;
import com.artms.exam.domain.*;
import com.artms.grading.domain.GradingScheme;
import com.artms.grading.domain.GradingSchemeRepository;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.OptimisticLockConflictException;
import com.artms.shared.exception.ValidationException;
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
class ExamServiceTest {

    @Mock private ExamRepository examRepository;
    @Mock private ExamSubjectRepository examSubjectRepository;
    @Mock private MarkEntryRepository markEntryRepository;
    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private GradingSchemeRepository gradingSchemeRepository;
    @Mock private ClassGroupRepository classGroupRepository;
    @Mock private CurriculumSubjectRepository curriculumSubjectRepository;
    @Mock private SubjectComponentRepository subjectComponentRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private AuditService auditService;

    @InjectMocks
    private ExamService examService;

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
    @DisplayName("Should create exam with status DRAFT")
    void testCreateExam() {
        UUID yearId = UUID.randomUUID();
        UUID schemeId = UUID.randomUUID();

        when(academicYearRepository.findByTenantIdAndId(tenantId, yearId))
                .thenReturn(Optional.of(new AcademicYear()));
        when(gradingSchemeRepository.findByTenantIdAndId(tenantId, schemeId))
                .thenReturn(Optional.of(new GradingScheme()));

        when(examRepository.save(any(Exam.class))).thenAnswer(inv -> {
            Exam e = inv.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        CreateExamRequest req = new CreateExamRequest(
                yearId, null, "First Term Examination", ExamType.TERMINAL,
                schemeId, null, LocalDate.now(), LocalDate.now().plusDays(10)
        );

        ExamResponse res = examService.createExam(req);

        assertThat(res).isNotNull();
        assertThat(res.status()).isEqualTo(ExamStatus.DRAFT);
        assertThat(res.name()).isEqualTo("First Term Examination");
        verify(auditService).record(eq(tenantId), eq(userId), eq("exam:create"), eq("Exam"), anyString(), anyMap());
    }

    @Test
    @DisplayName("Should reject invalid status transition")
    void testInvalidStatusTransition() {
        UUID examId = UUID.randomUUID();
        Exam exam = new Exam();
        exam.setId(examId);
        exam.setTenantId(tenantId);
        exam.setStatus(ExamStatus.DRAFT);

        when(examRepository.findByTenantIdAndId(tenantId, examId)).thenReturn(Optional.of(exam));

        ExamWorkflowRequest req = new ExamWorkflowRequest(ExamStatus.PUBLISHED, "Skipping ahead");

        assertThatThrownBy(() -> examService.updateWorkflowStatus(examId, req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot transition exam");
    }

    @Test
    @DisplayName("Should reject marks that exceed component full marks")
    void testMarksExceedFullMarks() {
        UUID examId = UUID.randomUUID();
        UUID examSubId = UUID.randomUUID();
        UUID currSubId = UUID.randomUUID();
        UUID compId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        Exam exam = new Exam();
        exam.setId(examId);
        exam.setTenantId(tenantId);
        exam.setStatus(ExamStatus.MARK_ENTRY);

        ExamSubject es = new ExamSubject();
        es.setId(examSubId);
        es.setExam(exam);
        es.setCurriculumSubjectId(currSubId);
        es.setStatus(ExamSubjectStatus.ACTIVE);

        SubjectComponent comp = new SubjectComponent();
        comp.setId(compId);
        comp.setName("Theory");
        comp.setFullMarks(new BigDecimal("100.00"));

        when(examRepository.findByTenantIdAndId(tenantId, examId)).thenReturn(Optional.of(exam));
        when(examSubjectRepository.findById(examSubId)).thenReturn(Optional.of(es));
        when(subjectComponentRepository.findByCurriculumSubjectIdOrderBySequenceAsc(currSubId))
                .thenReturn(List.of(comp));

        BatchMarkEntryRequest req = new BatchMarkEntryRequest(
                examSubId,
                List.of(new MarkEntryDto(studentId, compId, new BigDecimal("105.00"), MarkStatus.DRAFT, null, null))
        );

        assertThatThrownBy(() -> examService.enterMarksBatch(examId, req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Raw marks must be between 0 and 100.00");
    }

    @Test
    @DisplayName("Should detect optimistic locking conflict during mark entry")
    void testOptimisticLockConflict() {
        UUID examId = UUID.randomUUID();
        UUID examSubId = UUID.randomUUID();
        UUID currSubId = UUID.randomUUID();
        UUID compId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        Exam exam = new Exam();
        exam.setId(examId);
        exam.setTenantId(tenantId);
        exam.setStatus(ExamStatus.MARK_ENTRY);

        ExamSubject es = new ExamSubject();
        es.setId(examSubId);
        es.setExam(exam);
        es.setCurriculumSubjectId(currSubId);
        es.setStatus(ExamSubjectStatus.ACTIVE);

        SubjectComponent comp = new SubjectComponent();
        comp.setId(compId);
        comp.setName("Theory");
        comp.setFullMarks(new BigDecimal("100.00"));

        MarkEntry existing = new MarkEntry();
        existing.setId(UUID.randomUUID());
        existing.setTenantId(tenantId);
        existing.setExamSubjectId(examSubId);
        existing.setStudentId(studentId);
        existing.setComponentId(compId);
        existing.setVersion(2);

        when(examRepository.findByTenantIdAndId(tenantId, examId)).thenReturn(Optional.of(exam));
        when(examSubjectRepository.findById(examSubId)).thenReturn(Optional.of(es));
        when(subjectComponentRepository.findByCurriculumSubjectIdOrderBySequenceAsc(currSubId))
                .thenReturn(List.of(comp));
        when(studentRepository.findByTenantIdAndId(tenantId, studentId))
                .thenReturn(Optional.of(new Student()));
        when(markEntryRepository.findByTenantIdAndExamSubjectIdAndStudentIdAndComponentId(
                tenantId, examSubId, studentId, compId)).thenReturn(Optional.of(existing));

        // Client supplies version 1, but database has version 2
        BatchMarkEntryRequest req = new BatchMarkEntryRequest(
                examSubId,
                List.of(new MarkEntryDto(studentId, compId, new BigDecimal("85.00"), MarkStatus.DRAFT, null, 1))
        );

        assertThatThrownBy(() -> examService.enterMarksBatch(examId, req))
                .isInstanceOf(OptimisticLockConflictException.class)
                .hasMessageContaining("Concurrent modification detected");
    }
}

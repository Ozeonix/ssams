package com.artms.academic;

import com.artms.academic.application.*;
import com.artms.academic.domain.*;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurriculumServiceTest {

    @Mock
    private CurriculumRepository curriculumRepository;
    @Mock
    private CurriculumSubjectRepository curriculumSubjectRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private AcademicYearRepository academicYearRepository;

    @InjectMocks
    private CurriculumService curriculumService;

    private UUID tenantId;
    private UUID programId;
    private UUID academicYearId;
    private UUID subjectId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        programId = UUID.randomUUID();
        academicYearId = UUID.randomUUID();
        subjectId = UUID.randomUUID();
        TenantContext.set(tenantId, UUID.randomUUID());
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Exit Criterion: Institution can configure technical/vocational curriculum with theory and practical components without code")
    void testConfigureTechnicalVocationalCurriculum() {
        // 1. Setup program and academic year existence
        Program program = new Program();
        program.setId(programId);
        program.setTenantId(tenantId);
        program.setCode("DIPLOMA_CS");
        program.setName("Diploma in Computer Engineering");

        AcademicYear year = new AcademicYear();
        year.setId(academicYearId);
        year.setTenantId(tenantId);
        year.setName("2025/2026");

        Subject mixedSubject = new Subject();
        mixedSubject.setId(subjectId);
        mixedSubject.setTenantId(tenantId);
        mixedSubject.setCode("CS-201");
        mixedSubject.setName("Data Structures & Algorithms");
        mixedSubject.setSubjectType(SubjectType.MIXED);

        when(programRepository.findById(programId)).thenReturn(Optional.of(program));
        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(year));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mixedSubject));

        when(curriculumRepository.findByTenantIdAndProgramIdAndAcademicYearIdAndVersion(tenantId, programId, academicYearId, 1))
            .thenReturn(Optional.empty());

        when(curriculumRepository.save(any(Curriculum.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(curriculumSubjectRepository.save(any(CurriculumSubject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Create curriculum
        CreateCurriculumRequest createReq = new CreateCurriculumRequest(programId, academicYearId, 1);
        Curriculum curriculum = curriculumService.createCurriculum(createReq);
        curriculum.setId(UUID.randomUUID());

        assertThat(curriculum.getStatus()).isEqualTo(CurriculumStatus.DRAFT);
        assertThat(curriculum.getVersion()).isEqualTo(1);

        when(curriculumRepository.findById(curriculum.getId())).thenReturn(Optional.of(curriculum));

        // 3. Add vocational/technical subject with Theory and Practical components
        AddCurriculumSubjectRequest addSubjectReq = new AddCurriculumSubjectRequest(
            subjectId,
            "Year 2",
            1,
            true,
            new BigDecimal("4.0"),
            null,
            List.of(
                new AddCurriculumSubjectRequest.ComponentDto("TH", "Theory Assessment", AssessmentType.THEORY,
                    new BigDecimal("60.00"), new BigDecimal("24.00"), new BigDecimal("60.00"), new BigDecimal("2.5"), 1),
                new AddCurriculumSubjectRequest.ComponentDto("PR", "Practical Lab Assessment", AssessmentType.PRACTICAL,
                    new BigDecimal("40.00"), new BigDecimal("16.00"), new BigDecimal("40.00"), new BigDecimal("1.5"), 2)
            )
        );

        CurriculumSubject configuredSubject = curriculumService.addSubject(curriculum.getId(), addSubjectReq);

        assertThat(configuredSubject.getComponents()).hasSize(2);
        assertThat(configuredSubject.getCreditHours()).isEqualByComparingTo("4.0");

        SubjectComponent th = configuredSubject.getComponents().get(0);
        assertThat(th.getCode()).isEqualTo("TH");
        assertThat(th.getFullMarks()).isEqualByComparingTo("60.00");
        assertThat(th.getPassMarks()).isEqualByComparingTo("24.00");

        SubjectComponent pr = configuredSubject.getComponents().get(1);
        assertThat(pr.getCode()).isEqualTo("PR");
        assertThat(pr.getFullMarks()).isEqualByComparingTo("40.00");
        assertThat(pr.getPassMarks()).isEqualByComparingTo("16.00");

        // 4. Publish curriculum
        Curriculum published = curriculumService.publishCurriculum(curriculum.getId());
        assertThat(published.getStatus()).isEqualTo(CurriculumStatus.PUBLISHED);
        assertThat(published.isPublished()).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid component configuration where pass marks exceed full marks")
    void testRejectInvalidPassMarks() {
        Curriculum curriculum = new Curriculum();
        curriculum.setId(UUID.randomUUID());
        curriculum.setTenantId(tenantId);
        curriculum.setStatus(CurriculumStatus.DRAFT);

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setTenantId(tenantId);

        when(curriculumRepository.findById(curriculum.getId())).thenReturn(Optional.of(curriculum));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        AddCurriculumSubjectRequest invalidReq = new AddCurriculumSubjectRequest(
            subjectId,
            "Year 1",
            1,
            true,
            new BigDecimal("3.0"),
            null,
            List.of(
                new AddCurriculumSubjectRequest.ComponentDto("TH", "Theory", AssessmentType.THEORY,
                    new BigDecimal("50.00"), new BigDecimal("60.00"), new BigDecimal("100.00"), new BigDecimal("3.0"), 1)
            )
        );

        assertThatThrownBy(() -> curriculumService.addSubject(curriculum.getId(), invalidReq))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("pass marks (60.00) cannot exceed full marks (50.00)");
    }
}

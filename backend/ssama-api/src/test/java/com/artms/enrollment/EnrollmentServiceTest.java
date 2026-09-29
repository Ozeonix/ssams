package com.artms.enrollment;

import com.artms.academic.domain.*;
import com.artms.enrollment.application.*;
import com.artms.enrollment.domain.*;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ClassGroupRepository classGroupRepository;
    @Mock
    private AcademicYearRepository academicYearRepository;
    @Mock
    private CurriculumRepository curriculumRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private UUID tenantId;
    private UUID programId;
    private UUID academicYearId;
    private UUID classGroupId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        programId = UUID.randomUUID();
        academicYearId = UUID.randomUUID();
        classGroupId = UUID.randomUUID();
        TenantContext.set(tenantId, UUID.randomUUID());
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    private ClassGroup createClassGroup(int capacity) {
        ClassGroup cg = new ClassGroup();
        cg.setId(classGroupId);
        cg.setTenantId(tenantId);
        cg.setProgramId(programId);
        cg.setAcademicYearId(academicYearId);
        cg.setGradeLevel("Grade 10");
        cg.setSection("A");
        cg.setCapacity(capacity);
        return cg;
    }

    private AcademicYear createAcademicYear() {
        AcademicYear ay = new AcademicYear();
        ay.setId(academicYearId);
        ay.setTenantId(tenantId);
        ay.setName("2026/2027");
        return ay;
    }

    private Student createStudent(UUID id, String admissionNo) {
        Student s = new Student();
        s.setId(id);
        s.setTenantId(tenantId);
        s.setAdmissionNo(admissionNo);
        s.setFirstName("Student");
        s.setLastName(admissionNo);
        return s;
    }

    private Curriculum createPublishedCurriculum() {
        Curriculum curriculum = new Curriculum();
        curriculum.setId(UUID.randomUUID());
        curriculum.setTenantId(tenantId);
        curriculum.setProgramId(programId);
        curriculum.setAcademicYearId(academicYearId);
        curriculum.setStatus(CurriculumStatus.PUBLISHED);

        CurriculumSubject cs1 = new CurriculumSubject();
        cs1.setId(UUID.randomUUID());
        cs1.setCurriculum(curriculum);
        cs1.setGradeLevel("Grade 10");
        cs1.setMandatory(true);
        cs1.setCreditHours(new BigDecimal("4.0"));

        CurriculumSubject cs2 = new CurriculumSubject();
        cs2.setId(UUID.randomUUID());
        cs2.setCurriculum(curriculum);
        cs2.setGradeLevel("Grade 10");
        cs2.setMandatory(true);
        cs2.setCreditHours(new BigDecimal("3.0"));

        CurriculumSubject csElective = new CurriculumSubject();
        csElective.setId(UUID.randomUUID());
        csElective.setCurriculum(curriculum);
        csElective.setGradeLevel("Grade 10");
        csElective.setMandatory(false);
        csElective.setCreditHours(new BigDecimal("2.0"));

        curriculum.getSubjects().addAll(List.of(cs1, cs2, csElective));
        return curriculum;
    }

    @Test
    @DisplayName("Exit Criterion: Enrolling student triggers automatic subject registration driven by published curriculum")
    void testEnrollStudentWithCurriculumSubjects() {
        UUID studentId = UUID.randomUUID();
        Student student = createStudent(studentId, "ADM-001");
        ClassGroup classGroup = createClassGroup(40);
        AcademicYear academicYear = createAcademicYear();
        Curriculum curriculum = createPublishedCurriculum();

        when(studentRepository.findByTenantIdAndId(tenantId, studentId)).thenReturn(Optional.of(student));
        when(classGroupRepository.findByTenantIdAndId(tenantId, classGroupId)).thenReturn(Optional.of(classGroup));
        when(academicYearRepository.findByTenantIdAndId(tenantId, academicYearId)).thenReturn(Optional.of(academicYear));
        when(enrollmentRepository.existsByTenantIdAndStudentIdAndAcademicYearId(tenantId, studentId, academicYearId)).thenReturn(false);
        when(enrollmentRepository.countByTenantIdAndClassGroupIdAndAcademicYearId(tenantId, classGroupId, academicYearId)).thenReturn(0);
        when(curriculumRepository.findFirstByTenantIdAndProgramIdAndAcademicYearIdAndStatusOrderByVersionDesc(
                tenantId, programId, academicYearId, CurriculumStatus.PUBLISHED)).thenReturn(Optional.of(curriculum));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EnrollStudentRequest request = new EnrollStudentRequest(studentId, classGroupId, academicYearId, "10-A-01", null);
        EnrollmentResponse response = enrollmentService.enrollStudent(tenantId, request);

        assertThat(response).isNotNull();
        assertThat(response.studentId()).isEqualTo(studentId);
        assertThat(response.classGroupId()).isEqualTo(classGroupId);
        assertThat(response.rollNo()).isEqualTo("10-A-01");
        assertThat(response.status()).isEqualTo(EnrollmentStatus.ACTIVE);
        // Only the 2 mandatory subjects should be automatically enrolled
        assertThat(response.subjectEnrollments()).hasSize(2);
        assertThat(response.subjectEnrollments())
                .extracting(EnrollmentResponse.SubjectEnrollmentResponse::status)
                .containsOnly(SubjectEnrollmentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Exit Criterion: Synthetic institution can batch enroll a full class cohort with automatic curriculum registration")
    void testBatchEnrollFullClass() {
        ClassGroup classGroup = createClassGroup(30);
        AcademicYear academicYear = createAcademicYear();
        Curriculum curriculum = createPublishedCurriculum();

        UUID s1Id = UUID.randomUUID();
        UUID s2Id = UUID.randomUUID();
        UUID s3Id = UUID.randomUUID();

        when(classGroupRepository.findByTenantIdAndId(tenantId, classGroupId)).thenReturn(Optional.of(classGroup));
        when(academicYearRepository.findByTenantIdAndId(tenantId, academicYearId)).thenReturn(Optional.of(academicYear));
        when(enrollmentRepository.countByTenantIdAndClassGroupIdAndAcademicYearId(tenantId, classGroupId, academicYearId)).thenReturn(0);

        when(studentRepository.findByTenantIdAndId(tenantId, s1Id)).thenReturn(Optional.of(createStudent(s1Id, "ADM-101")));
        when(studentRepository.findByTenantIdAndId(tenantId, s2Id)).thenReturn(Optional.of(createStudent(s2Id, "ADM-102")));
        when(studentRepository.findByTenantIdAndId(tenantId, s3Id)).thenReturn(Optional.of(createStudent(s3Id, "ADM-103")));

        when(curriculumRepository.findFirstByTenantIdAndProgramIdAndAcademicYearIdAndStatusOrderByVersionDesc(
                tenantId, programId, academicYearId, CurriculumStatus.PUBLISHED)).thenReturn(Optional.of(curriculum));

        when(enrollmentRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        BatchEnrollClassRequest batchRequest = new BatchEnrollClassRequest(
                classGroupId,
                academicYearId,
                List.of(
                        new StudentEnrollmentDto(s1Id, "ROLL-01"),
                        new StudentEnrollmentDto(s2Id, "ROLL-02"),
                        new StudentEnrollmentDto(s3Id, "ROLL-03")
                )
        );

        List<EnrollmentResponse> responses = enrollmentService.batchEnrollClass(tenantId, batchRequest);

        assertThat(responses).hasSize(3);
        assertThat(responses).extracting(EnrollmentResponse::rollNo).containsExactly("ROLL-01", "ROLL-02", "ROLL-03");
        for (EnrollmentResponse res : responses) {
            assertThat(res.status()).isEqualTo(EnrollmentStatus.ACTIVE);
            assertThat(res.subjectEnrollments()).hasSize(2);
        }
    }

    @Test
    @DisplayName("Should reject enrollment when class group capacity is reached")
    void testRejectWhenCapacityExceeded() {
        UUID studentId = UUID.randomUUID();
        Student student = createStudent(studentId, "ADM-005");
        ClassGroup classGroup = createClassGroup(20);
        AcademicYear academicYear = createAcademicYear();

        when(studentRepository.findByTenantIdAndId(tenantId, studentId)).thenReturn(Optional.of(student));
        when(classGroupRepository.findByTenantIdAndId(tenantId, classGroupId)).thenReturn(Optional.of(classGroup));
        when(academicYearRepository.findByTenantIdAndId(tenantId, academicYearId)).thenReturn(Optional.of(academicYear));
        when(enrollmentRepository.countByTenantIdAndClassGroupIdAndAcademicYearId(tenantId, classGroupId, academicYearId)).thenReturn(20);

        EnrollStudentRequest request = new EnrollStudentRequest(studentId, classGroupId, academicYearId, "21", null);

        assertThatThrownBy(() -> enrollmentService.enrollStudent(tenantId, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Class group capacity reached: 20");
    }

    @Test
    @DisplayName("Should reject enrollment if student is already enrolled in the same academic year")
    void testRejectDuplicateEnrollmentInSameYear() {
        UUID studentId = UUID.randomUUID();
        Student student = createStudent(studentId, "ADM-006");
        ClassGroup classGroup = createClassGroup(50);
        AcademicYear academicYear = createAcademicYear();

        when(studentRepository.findByTenantIdAndId(tenantId, studentId)).thenReturn(Optional.of(student));
        when(classGroupRepository.findByTenantIdAndId(tenantId, classGroupId)).thenReturn(Optional.of(classGroup));
        when(academicYearRepository.findByTenantIdAndId(tenantId, academicYearId)).thenReturn(Optional.of(academicYear));
        when(enrollmentRepository.existsByTenantIdAndStudentIdAndAcademicYearId(tenantId, studentId, academicYearId)).thenReturn(true);

        EnrollStudentRequest request = new EnrollStudentRequest(studentId, classGroupId, academicYearId, "01", null);

        assertThatThrownBy(() -> enrollmentService.enrollStudent(tenantId, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Student is already enrolled for academic year");
    }
}

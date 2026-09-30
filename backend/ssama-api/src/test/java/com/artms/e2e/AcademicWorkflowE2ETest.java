package com.artms.e2e;

import com.artms.IntegrationTestBase;
import com.artms.academic.domain.*;
import com.artms.attendance.application.AttendanceEntryDto;
import com.artms.attendance.application.AttendanceService;
import com.artms.attendance.application.SubmitAttendanceRequest;
import com.artms.attendance.domain.AttendanceStatus;
import com.artms.enrollment.application.EnrollStudentRequest;
import com.artms.enrollment.application.EnrollmentResponse;
import com.artms.enrollment.application.EnrollmentService;
import com.artms.grading.domain.GradingScheme;
import com.artms.grading.domain.GradingSchemeRepository;
import com.artms.grading.domain.GradingSchemeStatus;
import com.artms.grading.domain.RoundingMode;
import com.artms.shared.tenant.TenantContext;
import com.artms.student.application.CreateStudentRequest;
import com.artms.student.application.StudentService;
import com.artms.student.domain.Gender;
import com.artms.student.domain.Student;
import com.artms.tenant.domain.Tenant;
import com.artms.tenant.domain.TenantRepository;
import com.artms.tenant.domain.TenantStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-End API and Domain Integration Test.
 * Simulates a full institutional lifecycle:
 * Tenant -> Academic Setup -> Student Registration -> Enrollment -> Attendance -> Verification.
 */
class AcademicWorkflowE2ETest extends IntegrationTestBase {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ClassGroupRepository classGroupRepository;

    @Autowired
    private GradingSchemeRepository gradingSchemeRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private com.artms.identity.domain.UserAccountRepository userAccountRepository;

    private Tenant tenant;
    private AcademicYear academicYear;
    private Program program;
    private ClassGroup classGroup;
    private GradingScheme gradingScheme;

    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setCode("E2E" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        tenant.setName("E2E Test Academy");
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant = tenantRepository.save(tenant);

        com.artms.identity.domain.UserAccount actor = new com.artms.identity.domain.UserAccount();
        String uid = UUID.randomUUID().toString().substring(0, 8);
        actor.setUsername("e2e_actor_" + uid);
        actor.setEmail("e2e_" + uid + "@example.com");
        actor.setPasswordHash("dummy_hash");
        actor = userAccountRepository.save(actor);

        TenantContext.set(tenant.getId(), actor.getId());

        academicYear = new AcademicYear();
        academicYear.setTenantId(tenant.getId());
        academicYear.setName("2026-2027 E2E");
        academicYear.setStartDate(LocalDate.of(2026, 4, 1));
        academicYear.setEndDate(LocalDate.of(2027, 3, 31));
        academicYear = academicYearRepository.save(academicYear);

        program = new Program();
        program.setTenantId(tenant.getId());
        program.setCode("SEC-E2E");
        program.setName("Secondary Education");
        program = programRepository.save(program);

        classGroup = new ClassGroup();
        classGroup.setTenantId(tenant.getId());
        classGroup.setProgramId(program.getId());
        classGroup.setAcademicYearId(academicYear.getId());
        classGroup.setGradeLevel("Grade 10");
        classGroup.setSection("A");
        classGroup.setCapacity(40);
        classGroup = classGroupRepository.save(classGroup);

        gradingScheme = new GradingScheme();
        gradingScheme.setTenantId(tenant.getId());
        gradingScheme.setName("E2E Standard Scheme");
        gradingScheme.setStatus(GradingSchemeStatus.ACTIVE);
        gradingScheme.setGpaRounding(RoundingMode.HALF_UP);
        gradingScheme = gradingSchemeRepository.save(gradingScheme);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @WithMockUser(authorities = {"PERM_student:create", "PERM_student:read", "PERM_student:update"})
    @DisplayName("E2E: Execute full student lifecycle from admission, enrollment to attendance session tracking")
    void testFullAcademicStudentLifecycle() {
        // 1. Create Student
        String admissionNo = "ADM-E2E-" + System.currentTimeMillis();
        CreateStudentRequest createReq = new CreateStudentRequest(
                admissionNo,
                "Aarav",
                null,
                "Sharma",
                LocalDate.of(2010, 5, 15),
                Gender.MALE,
                "+9779800000001",
                "aarav@example.com",
                "Kathmandu, Nepal",
                "REG-E2E-001",
                "SYM-E2E-001"
        );
        Student student = studentService.create(createReq);
        assertThat(student).isNotNull();
        assertThat(student.getId()).isNotNull();
        assertThat(student.getFullName()).isEqualTo("Aarav Sharma");

        // 2. Enroll Student in Class Group
        EnrollStudentRequest enrollReq = new EnrollStudentRequest(
                student.getId(),
                classGroup.getId(),
                academicYear.getId(),
                "10-A-01",
                LocalDate.now()
        );
        EnrollmentResponse enrollment = enrollmentService.enrollStudent(tenant.getId(), enrollReq);
        assertThat(enrollment).isNotNull();
        assertThat(enrollment.studentId()).isEqualTo(student.getId());
        assertThat(enrollment.rollNo()).isEqualTo("10-A-01");

        // 3. Submit Attendance for Class Session
        SubmitAttendanceRequest attendanceReq = new SubmitAttendanceRequest(
                classGroup.getId(),
                null,
                LocalDate.now(),
                "P1",
                null,
                List.of(new AttendanceEntryDto(student.getId(), AttendanceStatus.PRESENT, "On time"))
        );
        var attendanceSession = attendanceService.submitAttendance(tenant.getId(), attendanceReq);
        assertThat(attendanceSession).isNotNull();
        assertThat(attendanceSession.classGroupId()).isEqualTo(classGroup.getId());
        assertThat(attendanceSession.records()).hasSize(1);
        assertThat(attendanceSession.records().get(0).status()).isEqualTo(AttendanceStatus.PRESENT);

        // 4. Verify Student Attendance Summary
        var summary = attendanceService.getStudentAttendanceSummary(
                tenant.getId(), student.getId(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertThat(summary.totalSessions()).isEqualTo(1);
        assertThat(summary.presentCount()).isEqualTo(1);
        assertThat(summary.attendancePercentage().doubleValue()).isEqualTo(100.0);
    }
}

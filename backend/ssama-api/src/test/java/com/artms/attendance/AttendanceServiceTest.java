package com.artms.attendance;

import com.artms.academic.domain.ClassGroup;
import com.artms.academic.domain.ClassGroupRepository;
import com.artms.academic.domain.Subject;
import com.artms.academic.domain.SubjectRepository;
import com.artms.attendance.application.*;
import com.artms.attendance.domain.*;
import com.artms.audit.application.AuditEvent;
import com.artms.audit.application.AuditService;
import com.artms.notification.domain.OutboxEvent;
import com.artms.notification.domain.OutboxEventRepository;
import com.artms.shared.tenant.TenantContext;
import com.artms.student.domain.Student;
import com.artms.student.domain.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceSessionRepository sessionRepository;
    @Mock
    private AttendanceRecordRepository recordRepository;
    @Mock
    private ClassGroupRepository classGroupRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private AuditService auditService;
    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private UUID tenantId;
    private UUID classGroupId;
    private UUID subjectId;
    private UUID student1Id;
    private UUID student2Id;
    private UUID student3Id;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        classGroupId = UUID.randomUUID();
        subjectId = UUID.randomUUID();
        student1Id = UUID.randomUUID();
        student2Id = UUID.randomUUID();
        student3Id = UUID.randomUUID();

        TenantContext.set(tenantId, UUID.randomUUID());
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Exit Criterion: Submit attendance creates session, records, and generates absence alert via outbox")
    void testSubmitAttendance() {
        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(classGroupId);
        classGroup.setTenantId(tenantId);

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setTenantId(tenantId);

        Student s1 = new Student();
        s1.setId(student1Id);
        s1.setTenantId(tenantId);

        Student s2 = new Student();
        s2.setId(student2Id);
        s2.setTenantId(tenantId);

        when(classGroupRepository.findById(classGroupId)).thenReturn(Optional.of(classGroup));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(studentRepository.findById(student1Id)).thenReturn(Optional.of(s1));
        when(studentRepository.findById(student2Id)).thenReturn(Optional.of(s2));

        when(sessionRepository.findByTenantIdAndClassGroupIdAndSubjectIdAndSessionDateAndPeriod(
                any(), any(), any(), any(), any())).thenReturn(Optional.empty());

        when(sessionRepository.save(any(AttendanceSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SubmitAttendanceRequest request = new SubmitAttendanceRequest(
                classGroupId,
                subjectId,
                LocalDate.now(),
                "P1",
                null,
                List.of(
                        new AttendanceEntryDto(student1Id, AttendanceStatus.PRESENT, "On time"),
                        new AttendanceEntryDto(student2Id, AttendanceStatus.ABSENT, "Unexcused")
                )
        );

        AttendanceSessionResponse response = attendanceService.submitAttendance(tenantId, request);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(AttendanceSessionStatus.SUBMITTED);
        assertThat(response.records()).hasSize(2);

        // Verify outbox alert generated for the absent student
        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository, times(1)).save(outboxCaptor.capture());
        OutboxEvent event = outboxCaptor.getValue();
        assertThat(event.getEventType()).isEqualTo("STUDENT_ABSENCE_ALERT");
        assertThat(event.getAggregateId()).isEqualTo(student2Id.toString());
    }

    @Test
    @DisplayName("Exit Criterion: Correct attendance record updates status, marks session CORRECTED, and records immutable audit event")
    void testCorrectAttendanceRecordWithAudit() {
        UUID recordId = UUID.randomUUID();

        AttendanceSession session = new AttendanceSession();
        session.setId(UUID.randomUUID());
        session.setTenantId(tenantId);
        session.setStatus(AttendanceSessionStatus.SUBMITTED);

        AttendanceRecord record = new AttendanceRecord();
        record.setId(recordId);
        record.setSession(session);
        record.setStudentId(student1Id);
        record.setStatus(AttendanceStatus.ABSENT);
        record.setRemarks("Sick note pending");
        record.setCorrected(false);

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(record));
        when(recordRepository.save(any(AttendanceRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        CorrectAttendanceRecordRequest correctReq = new CorrectAttendanceRecordRequest(
                AttendanceStatus.EXCUSED,
                "Medical certificate submitted by parent",
                "Doctor certified medical leave"
        );

        AttendanceRecordResponse corrected = attendanceService.correctRecord(tenantId, recordId, correctReq);

        assertThat(corrected.status()).isEqualTo(AttendanceStatus.EXCUSED);
        assertThat(corrected.corrected()).isTrue();
        assertThat(session.getStatus()).isEqualTo(AttendanceSessionStatus.CORRECTED);

        // Verify audit event captured
        ArgumentCaptor<AuditEvent> auditCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditService, times(1)).record(auditCaptor.capture());
        AuditEvent audit = auditCaptor.getValue();
        assertThat(audit.action()).isEqualTo("CORRECT_ATTENDANCE");
        assertThat(audit.entityId()).isEqualTo(recordId.toString());
        assertThat(audit.reason()).contains("Medical certificate");
        assertThat(audit.beforeData().get("status")).isEqualTo("ABSENT");
        assertThat(audit.afterData().get("status")).isEqualTo("EXCUSED");
    }

    @Test
    @DisplayName("Exit Criterion: Calculate student attendance percentage and detect low attendance warning (< 75%)")
    void testStudentAttendanceSummaryWithLowAttendanceWarning() {
        Student student = new Student();
        student.setId(student1Id);
        student.setTenantId(tenantId);

        when(studentRepository.findById(student1Id)).thenReturn(Optional.of(student));

        AttendanceSession session = new AttendanceSession();
        session.setId(UUID.randomUUID());
        session.setTenantId(tenantId);
        session.setSubjectId(subjectId);
        session.setSessionDate(LocalDate.now());

        // 10 sessions: 6 PRESENT, 1 LATE, 3 ABSENT -> 7 / 10 = 70.00%
        AttendanceRecord r1 = createRecord(session, student1Id, AttendanceStatus.PRESENT);
        AttendanceRecord r2 = createRecord(session, student1Id, AttendanceStatus.PRESENT);
        AttendanceRecord r3 = createRecord(session, student1Id, AttendanceStatus.PRESENT);
        AttendanceRecord r4 = createRecord(session, student1Id, AttendanceStatus.PRESENT);
        AttendanceRecord r5 = createRecord(session, student1Id, AttendanceStatus.PRESENT);
        AttendanceRecord r6 = createRecord(session, student1Id, AttendanceStatus.PRESENT);
        AttendanceRecord r7 = createRecord(session, student1Id, AttendanceStatus.LATE);
        AttendanceRecord r8 = createRecord(session, student1Id, AttendanceStatus.ABSENT);
        AttendanceRecord r9 = createRecord(session, student1Id, AttendanceStatus.ABSENT);
        AttendanceRecord r10 = createRecord(session, student1Id, AttendanceStatus.ABSENT);

        List<AttendanceRecord> records = List.of(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
        when(recordRepository.findByTenantIdAndStudentId(tenantId, student1Id)).thenReturn(records);

        StudentAttendanceSummaryDto summary = attendanceService.getStudentAttendanceSummary(tenantId, student1Id, null, null);

        assertThat(summary.totalSessions()).isEqualTo(10);
        assertThat(summary.presentCount()).isEqualTo(6);
        assertThat(summary.lateCount()).isEqualTo(1);
        assertThat(summary.absentCount()).isEqualTo(3);
        assertThat(summary.attendancePercentage()).isEqualByComparingTo(new BigDecimal("70.00"));
        assertThat(summary.lowAttendanceWarning()).isTrue();
        assertThat(summary.recentAbsences()).hasSize(3);
        assertThat(summary.subjectStats()).hasSize(1);
        assertThat(summary.subjectStats().get(0).attendancePercentage()).isEqualByComparingTo(new BigDecimal("70.00"));
    }

    @Test
    @DisplayName("Should report no warning when attendance meets or exceeds 75% threshold")
    void testStudentAttendanceSummaryAboveThreshold() {
        Student student = new Student();
        student.setId(student1Id);
        student.setTenantId(tenantId);

        when(studentRepository.findById(student1Id)).thenReturn(Optional.of(student));

        AttendanceSession session = new AttendanceSession();
        session.setId(UUID.randomUUID());
        session.setTenantId(tenantId);
        session.setSubjectId(subjectId);
        session.setSessionDate(LocalDate.now());

        // 10 sessions: 9 PRESENT, 1 ABSENT -> 90.00%
        List<AttendanceRecord> records = List.of(
                createRecord(session, student1Id, AttendanceStatus.PRESENT),
                createRecord(session, student1Id, AttendanceStatus.PRESENT),
                createRecord(session, student1Id, AttendanceStatus.PRESENT),
                createRecord(session, student1Id, AttendanceStatus.PRESENT),
                createRecord(session, student1Id, AttendanceStatus.PRESENT),
                createRecord(session, student1Id, AttendanceStatus.PRESENT),
                createRecord(session, student1Id, AttendanceStatus.PRESENT),
                createRecord(session, student1Id, AttendanceStatus.PRESENT),
                createRecord(session, student1Id, AttendanceStatus.PRESENT),
                createRecord(session, student1Id, AttendanceStatus.ABSENT)
        );
        when(recordRepository.findByTenantIdAndStudentId(tenantId, student1Id)).thenReturn(records);

        StudentAttendanceSummaryDto summary = attendanceService.getStudentAttendanceSummary(tenantId, student1Id, null, null);

        assertThat(summary.attendancePercentage()).isEqualByComparingTo(new BigDecimal("90.00"));
        assertThat(summary.lowAttendanceWarning()).isFalse();
    }

    private AttendanceRecord createRecord(AttendanceSession session, UUID studentId, AttendanceStatus status) {
        AttendanceRecord r = new AttendanceRecord();
        r.setId(UUID.randomUUID());
        r.setSession(session);
        r.setStudentId(studentId);
        r.setStatus(status);
        return r;
    }
}

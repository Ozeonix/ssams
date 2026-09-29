package com.artms.attendance.application;

import com.artms.academic.domain.ClassGroupRepository;
import com.artms.academic.domain.SubjectRepository;
import com.artms.attendance.domain.*;
import com.artms.audit.application.AuditEvent;
import com.artms.audit.application.AuditService;
import com.artms.notification.domain.OutboxEvent;
import com.artms.notification.domain.OutboxEventRepository;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import com.artms.staff.domain.TeacherRepository;
import com.artms.student.domain.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceRecordRepository recordRepository;
    private final ClassGroupRepository classGroupRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final AuditService auditService;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public AttendanceSessionResponse submitAttendance(SubmitAttendanceRequest req) {
        return submitAttendance(TenantContext.getTenantId(), req);
    }

    @Transactional
    public AttendanceSessionResponse submitAttendance(UUID tenantId, SubmitAttendanceRequest req) {
        log.info("Submitting attendance for classGroup={}, date={}, period={}",
                req.classGroupId(), req.sessionDate(), req.period());

        classGroupRepository.findById(req.classGroupId())
                .filter(cg -> cg.getTenantId().equals(tenantId))
                .orElseThrow(() -> ResourceNotFoundException.of("ClassGroup", req.classGroupId()));

        if (req.subjectId() != null) {
            subjectRepository.findById(req.subjectId())
                    .filter(s -> s.getTenantId().equals(tenantId))
                    .orElseThrow(() -> ResourceNotFoundException.of("Subject", req.subjectId()));
        }

        if (req.teacherId() != null) {
            teacherRepository.findById(req.teacherId())
                    .filter(t -> t.getTenantId().equals(tenantId))
                    .orElseThrow(() -> ResourceNotFoundException.of("Teacher", req.teacherId()));
        }

        AttendanceSession session = sessionRepository
                .findByTenantIdAndClassGroupIdAndSubjectIdAndSessionDateAndPeriod(
                        tenantId, req.classGroupId(), req.subjectId(), req.sessionDate(), req.period())
                .orElseGet(() -> {
                    AttendanceSession s = new AttendanceSession();
                    s.setTenantId(tenantId);
                    s.setClassGroupId(req.classGroupId());
                    s.setSubjectId(req.subjectId());
                    s.setSessionDate(req.sessionDate());
                    s.setPeriod(req.period());
                    s.setTeacherId(req.teacherId());
                    return s;
                });

        session.setStatus(AttendanceSessionStatus.SUBMITTED);
        session.setSubmittedAt(OffsetDateTime.now());

        Map<UUID, AttendanceRecord> existingRecords = session.getRecords().stream()
                .collect(Collectors.toMap(AttendanceRecord::getStudentId, r -> r, (r1, r2) -> r1));

        for (AttendanceEntryDto entry : req.records()) {
            studentRepository.findById(entry.studentId())
                    .filter(st -> st.getTenantId().equals(tenantId))
                    .orElseThrow(() -> ResourceNotFoundException.of("Student", entry.studentId()));

            AttendanceRecord record = existingRecords.get(entry.studentId());
            if (record == null) {
                record = new AttendanceRecord();
                record.setSession(session);
                record.setStudentId(entry.studentId());
                session.getRecords().add(record);
            }
            record.setStatus(entry.status());
            record.setRemarks(entry.remarks());

            if (entry.status() == AttendanceStatus.ABSENT) {
                outboxEventRepository.save(OutboxEvent.of(
                        tenantId,
                        "AttendanceRecord",
                        entry.studentId().toString(),
                        "STUDENT_ABSENCE_ALERT",
                        Map.of(
                                "studentId", entry.studentId().toString(),
                                "sessionDate", req.sessionDate().toString(),
                                "period", req.period() != null ? req.period() : "FULL_DAY",
                                "classGroupId", req.classGroupId().toString()
                        )
                ));
            }
        }

        AttendanceSession saved = sessionRepository.save(session);
        return AttendanceSessionResponse.from(saved);
    }

    @Transactional
    public AttendanceRecordResponse correctRecord(UUID recordId, CorrectAttendanceRecordRequest req) {
        return correctRecord(TenantContext.getTenantId(), recordId, req);
    }

    @Transactional
    public AttendanceRecordResponse correctRecord(UUID tenantId, UUID recordId, CorrectAttendanceRecordRequest req) {
        AttendanceRecord record = recordRepository.findById(recordId)
                .filter(r -> r.getSession() != null && r.getSession().getTenantId().equals(tenantId))
                .orElseThrow(() -> ResourceNotFoundException.of("AttendanceRecord", recordId));

        AttendanceStatus oldStatus = record.getStatus();
        String oldRemarks = record.getRemarks();

        auditService.record(new AuditEvent(
                tenantId,
                TenantContext.getUserId(),
                "CORRECT_ATTENDANCE",
                "AttendanceRecord",
                recordId.toString(),
                Map.of("status", oldStatus.name(), "remarks", oldRemarks != null ? oldRemarks : ""),
                Map.of("status", req.newStatus().name(), "remarks", req.remarks() != null ? req.remarks() : ""),
                req.reason(),
                UUID.randomUUID(),
                null
        ));

        record.setStatus(req.newStatus());
        if (req.remarks() != null) {
            record.setRemarks(req.remarks());
        }
        record.setCorrected(true);

        AttendanceSession session = record.getSession();
        session.setStatus(AttendanceSessionStatus.CORRECTED);
        sessionRepository.save(session);

        AttendanceRecord saved = recordRepository.save(record);
        return AttendanceRecordResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public StudentAttendanceSummaryDto getStudentAttendanceSummary(UUID studentId, LocalDate startDate, LocalDate endDate) {
        return getStudentAttendanceSummary(TenantContext.getTenantId(), studentId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public StudentAttendanceSummaryDto getStudentAttendanceSummary(UUID tenantId, UUID studentId, LocalDate startDate, LocalDate endDate) {
        studentRepository.findById(studentId)
                .filter(s -> s.getTenantId().equals(tenantId))
                .orElseThrow(() -> ResourceNotFoundException.of("Student", studentId));

        List<AttendanceRecord> records;
        if (startDate != null && endDate != null) {
            records = recordRepository.findByTenantIdAndStudentIdAndDateRange(tenantId, studentId, startDate, endDate);
        } else {
            records = recordRepository.findByTenantIdAndStudentId(tenantId, studentId);
        }

        int totalSessions = records.size();
        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;
        int excusedCount = 0;

        List<AttendanceRecordResponse> recentAbsences = new ArrayList<>();

        for (AttendanceRecord r : records) {
            switch (r.getStatus()) {
                case PRESENT -> presentCount++;
                case LATE -> lateCount++;
                case ABSENT -> {
                    absentCount++;
                    if (recentAbsences.size() < 10) {
                        recentAbsences.add(AttendanceRecordResponse.from(r));
                    }
                }
                case EXCUSED -> excusedCount++;
            }
        }

        int attended = presentCount + lateCount;
        int countable = totalSessions - excusedCount;
        BigDecimal percentage;
        if (countable <= 0) {
            percentage = new BigDecimal("100.00");
        } else {
            percentage = BigDecimal.valueOf(attended * 100.0)
                    .divide(BigDecimal.valueOf(countable), 2, RoundingMode.HALF_UP);
        }

        boolean lowAttendanceWarning = percentage.compareTo(new BigDecimal("75.00")) < 0;

        Map<UUID, List<AttendanceRecord>> bySubject = records.stream()
                .filter(r -> r.getSession() != null && r.getSession().getSubjectId() != null)
                .collect(Collectors.groupingBy(r -> r.getSession().getSubjectId()));

        List<SubjectAttendanceStatDto> subjectStats = new ArrayList<>();
        for (Map.Entry<UUID, List<AttendanceRecord>> entry : bySubject.entrySet()) {
            UUID subjId = entry.getKey();
            List<AttendanceRecord> subRecords = entry.getValue();
            int subTotal = subRecords.size();
            int subPresent = (int) subRecords.stream()
                    .filter(r -> r.getStatus() == AttendanceStatus.PRESENT || r.getStatus() == AttendanceStatus.LATE)
                    .count();
            int subAbsent = (int) subRecords.stream()
                    .filter(r -> r.getStatus() == AttendanceStatus.ABSENT)
                    .count();
            int subExcused = (int) subRecords.stream()
                    .filter(r -> r.getStatus() == AttendanceStatus.EXCUSED)
                    .count();

            int subCountable = subTotal - subExcused;
            BigDecimal subPct = subCountable <= 0
                    ? new BigDecimal("100.00")
                    : BigDecimal.valueOf(subPresent * 100.0).divide(BigDecimal.valueOf(subCountable), 2, RoundingMode.HALF_UP);

            subjectStats.add(new SubjectAttendanceStatDto(subjId, subTotal, subPresent, subAbsent, subPct));
        }

        return new StudentAttendanceSummaryDto(
                studentId,
                totalSessions,
                presentCount,
                absentCount,
                lateCount,
                excusedCount,
                percentage,
                lowAttendanceWarning,
                subjectStats,
                recentAbsences
        );
    }

    @Transactional(readOnly = true)
    public AttendanceSessionResponse getSession(UUID sessionId) {
        return getSession(TenantContext.getTenantId(), sessionId);
    }

    @Transactional(readOnly = true)
    public AttendanceSessionResponse getSession(UUID tenantId, UUID sessionId) {
        AttendanceSession session = sessionRepository.findWithRecords(tenantId, sessionId)
                .orElseThrow(() -> ResourceNotFoundException.of("AttendanceSession", sessionId));
        return AttendanceSessionResponse.from(session);
    }

    @Transactional(readOnly = true)
    public Page<AttendanceSessionResponse> listSessionsByClass(UUID classGroupId, Pageable pageable) {
        return listSessionsByClass(TenantContext.getTenantId(), classGroupId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AttendanceSessionResponse> listSessionsByClass(UUID tenantId, UUID classGroupId, Pageable pageable) {
        return sessionRepository.findByTenantIdAndClassGroupIdOrderBySessionDateDesc(tenantId, classGroupId, pageable)
                .map(AttendanceSessionResponse::from);
    }
}

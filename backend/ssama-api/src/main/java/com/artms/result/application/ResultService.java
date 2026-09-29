package com.artms.result.application;

import com.artms.academic.domain.*;
import com.artms.audit.application.AuditService;
import com.artms.enrollment.domain.Enrollment;
import com.artms.enrollment.domain.EnrollmentRepository;
import com.artms.exam.application.MarkEntryDto;
import com.artms.exam.domain.*;
import com.artms.grading.domain.GradingScheme;
import com.artms.grading.domain.GradingSchemeRepository;
import com.artms.grading.engine.ComponentMark;
import com.artms.grading.engine.GradingEngine;
import com.artms.grading.engine.StudentResult;
import com.artms.grading.engine.SubjectResult;
import com.artms.result.domain.ResultSnapshot;
import com.artms.result.domain.ResultSnapshotRepository;
import com.artms.result.domain.ResultStatus;
import com.artms.result.domain.ResultSubjectSnapshot;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.exception.ValidationException;
import com.artms.shared.tenant.TenantContext;
import com.artms.student.domain.Student;
import com.artms.student.domain.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultSnapshotRepository resultSnapshotRepository;
    private final ExamRepository examRepository;
    private final ExamSubjectRepository examSubjectRepository;
    private final MarkEntryRepository markEntryRepository;
    private final GradingSchemeRepository gradingSchemeRepository;
    private final CurriculumSubjectRepository curriculumSubjectRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectComponentRepository subjectComponentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<ResultSnapshotResponse> calculatePreview(UUID examId) {
        UUID tenantId = TenantContext.getTenantId();
        Exam exam = examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        GradingScheme scheme = gradingSchemeRepository.findByTenantIdAndId(tenantId, exam.getGradingSchemeId())
                .orElseThrow(() -> ResourceNotFoundException.of("GradingScheme", exam.getGradingSchemeId()));

        List<ExamSubject> activeSubjects = examSubjectRepository.findByExamIdAndStatus(examId, ExamSubjectStatus.ACTIVE);
        if (activeSubjects.isEmpty()) {
            throw new ValidationException("Exam has no active subjects to calculate results");
        }

        Set<UUID> studentIds = getExamStudentIds(tenantId, exam, activeSubjects);
        List<ResultSnapshotResponse> previews = new ArrayList<>();

        for (UUID studentId : studentIds) {
            CalculationBundle bundle = calculateForStudent(tenantId, exam, activeSubjects, studentId, scheme, 1);
            ResultSnapshot snapshot = bundle.snapshot();
            previews.add(ResultSnapshotResponse.from(snapshot));
        }

        return previews;
    }

    @Transactional
    public List<ResultSnapshotResponse> publishResults(UUID examId) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        Exam exam = examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        if (exam.getStatus() != ExamStatus.VERIFIED && exam.getStatus() != ExamStatus.APPROVED
                && exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new BusinessRuleException("EXAM_NOT_READY_FOR_PUBLICATION",
                    "Exam must be in VERIFIED or APPROVED status before publishing (current: " + exam.getStatus() + ")");
        }

        GradingScheme scheme = gradingSchemeRepository.findByTenantIdAndId(tenantId, exam.getGradingSchemeId())
                .orElseThrow(() -> ResourceNotFoundException.of("GradingScheme", exam.getGradingSchemeId()));

        List<ExamSubject> activeSubjects = examSubjectRepository.findByExamIdAndStatus(examId, ExamSubjectStatus.ACTIVE);
        if (activeSubjects.isEmpty()) {
            throw new ValidationException("Exam has no active subjects to publish");
        }

        Set<UUID> studentIds = getExamStudentIds(tenantId, exam, activeSubjects);
        List<ResultSnapshot> publishedSnapshots = new ArrayList<>();
        OffsetDateTime publishTime = OffsetDateTime.now();

        for (UUID studentId : studentIds) {
            Optional<ResultSnapshot> latestOpt = resultSnapshotRepository
                    .findFirstByTenantIdAndExamIdAndStudentIdOrderByResultVersionDesc(tenantId, examId, studentId);

            int version = latestOpt.map(r -> r.getResultVersion() + 1).orElse(1);
            UUID prevVersionId = latestOpt.map(ResultSnapshot::getId).orElse(null);

            CalculationBundle bundle = calculateForStudent(tenantId, exam, activeSubjects, studentId, scheme, version);
            ResultSnapshot snapshot = bundle.snapshot();
            snapshot.setPreviousVersionId(prevVersionId);
            snapshot.setPublishedBy(userId);
            snapshot.setPublishedAt(publishTime);

            publishedSnapshots.add(resultSnapshotRepository.save(snapshot));
        }

        exam.setStatus(ExamStatus.PUBLISHED);
        examRepository.save(exam);

        auditService.record(tenantId, userId, "result:publish", "Exam", examId.toString(),
                Map.of("examName", exam.getName(), "studentCount", publishedSnapshots.size()));

        return publishedSnapshots.stream().map(ResultSnapshotResponse::from).toList();
    }

    @Transactional
    public ResultSnapshotResponse correctResult(UUID examId, ResultCorrectionRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        Exam exam = examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        if (exam.getStatus() != ExamStatus.PUBLISHED && exam.getStatus() != ExamStatus.LOCKED) {
            throw new BusinessRuleException("EXAM_NOT_PUBLISHED",
                    "Result corrections can only be made on PUBLISHED or LOCKED exams");
        }

        ResultSnapshot previousSnapshot = resultSnapshotRepository
                .findFirstByTenantIdAndExamIdAndStudentIdOrderByResultVersionDesc(tenantId, examId, req.studentId())
                .orElseThrow(() -> new BusinessRuleException("NO_PUBLISHED_RESULT_TO_CORRECT",
                        "No published result found for student " + req.studentId() + " in this exam"));

        // Validate and apply updated marks
        ExamSubject examSubject = examSubjectRepository.findById(req.examSubjectId())
                .orElseThrow(() -> ResourceNotFoundException.of("ExamSubject", req.examSubjectId()));

        List<SubjectComponent> components = subjectComponentRepository
                .findByCurriculumSubjectIdOrderBySequenceAsc(examSubject.getCurriculumSubjectId());
        Map<UUID, SubjectComponent> compMap = components.stream()
                .collect(Collectors.toMap(SubjectComponent::getId, c -> c));

        for (MarkEntryDto entry : req.entries()) {
            SubjectComponent comp = compMap.get(entry.componentId());
            if (comp == null) {
                throw new ValidationException("Component " + entry.componentId() + " does not belong to subject");
            }
            if (entry.rawMarks() != null) {
                if (entry.rawMarks().compareTo(BigDecimal.ZERO) < 0 || entry.rawMarks().compareTo(comp.getFullMarks()) > 0) {
                    throw new ValidationException("Marks must be between 0 and " + comp.getFullMarks());
                }
            }

            MarkEntry me = markEntryRepository
                    .findByTenantIdAndExamSubjectIdAndStudentIdAndComponentId(
                            tenantId, req.examSubjectId(), req.studentId(), entry.componentId())
                    .orElseGet(() -> {
                        MarkEntry newMe = new MarkEntry();
                        newMe.setTenantId(tenantId);
                        newMe.setExamSubjectId(req.examSubjectId());
                        newMe.setStudentId(req.studentId());
                        newMe.setComponentId(entry.componentId());
                        return newMe;
                    });

            me.setRawMarks(entry.rawMarks());
            me.setStatus(com.artms.exam.domain.MarkStatus.VERIFIED);
            me.setVerifiedBy(userId);
            me.setVerifiedAt(OffsetDateTime.now());
            me.setRemarks("Corrected: " + req.correctionReason());
            markEntryRepository.save(me);
        }

        GradingScheme scheme = gradingSchemeRepository.findByTenantIdAndId(tenantId, exam.getGradingSchemeId())
                .orElseThrow(() -> ResourceNotFoundException.of("GradingScheme", exam.getGradingSchemeId()));

        List<ExamSubject> activeSubjects = examSubjectRepository.findByExamIdAndStatus(examId, ExamSubjectStatus.ACTIVE);
        int newVersion = previousSnapshot.getResultVersion() + 1;

        CalculationBundle bundle = calculateForStudent(tenantId, exam, activeSubjects, req.studentId(), scheme, newVersion);
        ResultSnapshot newSnapshot = bundle.snapshot();
        newSnapshot.setPreviousVersionId(previousSnapshot.getId());
        newSnapshot.setCorrectionReason(req.correctionReason());
        newSnapshot.setPublishedBy(userId);
        newSnapshot.setPublishedAt(OffsetDateTime.now());

        ResultSnapshot saved = resultSnapshotRepository.save(newSnapshot);

        auditService.record(tenantId, userId, "result:correct", "ResultSnapshot", saved.getId().toString(),
                Map.of("examId", examId, "studentId", req.studentId(), "previousVersion", previousSnapshot.getResultVersion(),
                        "newVersion", newVersion, "reason", req.correctionReason()));

        return ResultSnapshotResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ResultSnapshotResponse> getExamResults(UUID examId) {
        UUID tenantId = TenantContext.getTenantId();
        examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        List<ResultSnapshot> snapshots = resultSnapshotRepository.findWithSubjectsByTenantIdAndExamId(tenantId, examId);
        // Keep only latest version per student
        Map<UUID, ResultSnapshot> latestByStudent = new LinkedHashMap<>();
        for (ResultSnapshot s : snapshots) {
            latestByStudent.putIfAbsent(s.getStudentId(), s);
        }

        return latestByStudent.values().stream().map(ResultSnapshotResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ResultSnapshotResponse getResultById(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        ResultSnapshot snapshot = resultSnapshotRepository.findWithSubjectsByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> ResourceNotFoundException.of("ResultSnapshot", id));
        return ResultSnapshotResponse.from(snapshot);
    }

    @Transactional(readOnly = true)
    public List<ResultSnapshotResponse> getStudentPublishedResults(UUID studentId) {
        UUID tenantId = TenantContext.getTenantId();
        List<ResultSnapshot> list = resultSnapshotRepository
                .findByTenantIdAndStudentIdAndPublishedAtIsNotNull(tenantId, studentId);

        // Filter latest per exam
        Map<UUID, ResultSnapshot> latestByExam = new LinkedHashMap<>();
        for (ResultSnapshot r : list) {
            ResultSnapshot current = latestByExam.get(r.getExamId());
            if (current == null || r.getResultVersion() > current.getResultVersion()) {
                latestByExam.put(r.getExamId(), r);
            }
        }

        return latestByExam.values().stream().map(ResultSnapshotResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ResultSnapshotResponse> getMyResults() {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        Student student = studentRepository.findByTenantIdAndUserId(tenantId, userId)
                .orElseThrow(() -> new BusinessRuleException("USER_NOT_A_STUDENT", "Authenticated user is not linked to a student"));

        return getStudentPublishedResults(student.getId());
    }

    private CalculationBundle calculateForStudent(UUID tenantId, Exam exam, List<ExamSubject> activeSubjects,
                                                  UUID studentId, GradingScheme scheme, int version) {
        List<ComponentMark> engineMarks = new ArrayList<>();
        Map<UUID, SubjectInfo> subjectInfoMap = new HashMap<>();

        for (ExamSubject es : activeSubjects) {
            CurriculumSubject cs = curriculumSubjectRepository.findById(es.getCurriculumSubjectId()).orElse(null);
            if (cs == null) continue;

            Subject subject = subjectRepository.findById(cs.getSubjectId()).orElse(null);
            String subjectName = subject != null ? subject.getName() : "Subject";
            BigDecimal creditHours = cs.getCreditHours();

            subjectInfoMap.put(cs.getSubjectId(), new SubjectInfo(cs.getSubjectId(), subjectName, creditHours));

            List<SubjectComponent> components = subjectComponentRepository
                    .findByCurriculumSubjectIdOrderBySequenceAsc(cs.getId());

            for (SubjectComponent comp : components) {
                Optional<MarkEntry> meOpt = markEntryRepository
                        .findByTenantIdAndExamSubjectIdAndStudentIdAndComponentId(
                                tenantId, es.getId(), studentId, comp.getId());

                com.artms.grading.engine.MarkStatus engineStatus;
                BigDecimal rawMarks = null;

                if (meOpt.isPresent()) {
                    MarkEntry me = meOpt.get();
                    rawMarks = me.getRawMarks();
                    engineStatus = mapMarkStatus(me.getStatus(), rawMarks);
                } else {
                    engineStatus = com.artms.grading.engine.MarkStatus.MISSING;
                }

                BigDecimal compCredit = (comp.getCreditHours() != null && comp.getCreditHours().compareTo(BigDecimal.ZERO) > 0)
                        ? comp.getCreditHours() : creditHours;

                engineMarks.add(new ComponentMark(
                        comp.getId(),
                        cs.getSubjectId(),
                        rawMarks,
                        comp.getFullMarks(),
                        comp.getPassMarks(),
                        comp.getWeight(),
                        compCredit,
                        comp.getAssessmentType() != null ? comp.getAssessmentType().name() : "THEORY",
                        engineStatus
                ));
            }
        }

        StudentResult engineResult = GradingEngine.calculate(engineMarks, scheme);

        ResultSnapshot snapshot = new ResultSnapshot();
        snapshot.setTenantId(tenantId);
        snapshot.setExamId(exam.getId());
        snapshot.setStudentId(studentId);
        snapshot.setResultVersion(version);
        snapshot.setTotalCredits(engineResult.totalCreditHours());
        snapshot.setEarnedPoints(engineResult.earnedPoints());
        snapshot.setGpa(engineResult.gpa());
        snapshot.setResultStatus(mapOverallStatus(engineResult.overallStatus()));
        snapshot.setGradingSchemeId(scheme.getId());
        snapshot.setGradingSchemeVersion(scheme.getVersion());

        List<ResultSubjectSnapshot> subjectSnapshots = new ArrayList<>();
        for (SubjectResult sr : engineResult.subjectResults()) {
            SubjectInfo info = subjectInfoMap.get(sr.subjectId());
            ResultSubjectSnapshot rss = new ResultSubjectSnapshot();
            rss.setResultSnapshot(snapshot);
            rss.setSubjectId(sr.subjectId());
            rss.setSubjectNameSnapshot(info != null ? info.name() : "Subject");
            rss.setCreditHours(sr.creditHours() != null ? sr.creditHours() : BigDecimal.ZERO);
            rss.setTotalFullMarks(sr.totalFullMarks() != null ? sr.totalFullMarks() : BigDecimal.ZERO);
            rss.setTotalObtained(sr.totalObtained() != null ? sr.totalObtained() : BigDecimal.ZERO);
            rss.setPercentage(sr.percentage());
            rss.setLetterGrade(sr.letterGrade());
            rss.setGradePoint(sr.gradePoint());
            rss.setFinalGrade(sr.status() != null ? sr.status().name() : (sr.passed() ? "PASS" : "FAIL"));

            subjectSnapshots.add(rss);
        }
        snapshot.setSubjectSnapshots(subjectSnapshots);

        String hash = computeHash(exam.getId(), studentId, version, snapshot.getTotalCredits(),
                snapshot.getEarnedPoints(), snapshot.getGpa(), snapshot.getResultStatus(), subjectSnapshots);
        snapshot.setImmutableHash(hash);

        return new CalculationBundle(snapshot);
    }

    private Set<UUID> getExamStudentIds(UUID tenantId, Exam exam, List<ExamSubject> activeSubjects) {
        Set<UUID> studentIds = new LinkedHashSet<>();
        if (exam.getClassGroupId() != null) {
            List<Enrollment> enrollments = enrollmentRepository.findByTenantIdAndClassGroupIdAndAcademicYearId(
                    tenantId, exam.getClassGroupId(), exam.getAcademicYearId());
            enrollments.forEach(e -> studentIds.add(e.getStudentId()));
        }

        for (ExamSubject es : activeSubjects) {
            List<MarkEntry> marks = markEntryRepository.findByTenantIdAndExamSubjectId(tenantId, es.getId());
            marks.forEach(m -> studentIds.add(m.getStudentId()));
        }
        return studentIds;
    }

    private com.artms.grading.engine.MarkStatus mapMarkStatus(
            com.artms.exam.domain.MarkStatus domainStatus, BigDecimal rawMarks) {
        if (domainStatus == null) {
            return rawMarks != null ? com.artms.grading.engine.MarkStatus.PRESENT : com.artms.grading.engine.MarkStatus.MISSING;
        }
        return switch (domainStatus) {
            case ABSENT -> com.artms.grading.engine.MarkStatus.ABSENT;
            case WITHHELD -> com.artms.grading.engine.MarkStatus.WITHHELD;
            case EXPELLED -> com.artms.grading.engine.MarkStatus.EXPELLED;
            case NOT_APPLICABLE -> com.artms.grading.engine.MarkStatus.NOT_APPLICABLE;
            default -> rawMarks != null ? com.artms.grading.engine.MarkStatus.PRESENT : com.artms.grading.engine.MarkStatus.MISSING;
        };
    }

    private ResultStatus mapOverallStatus(com.artms.grading.engine.OverallResultStatus engineStatus) {
        if (engineStatus == null) return ResultStatus.INCOMPLETE;
        return switch (engineStatus) {
            case PASS -> ResultStatus.PASS;
            case FAIL -> ResultStatus.FAIL;
            case CONDITIONAL_PASS -> ResultStatus.CONDITIONAL_PASS;
            case INCOMPLETE -> ResultStatus.INCOMPLETE;
            case WITHHELD -> ResultStatus.WITHHELD;
            case EXPELLED -> ResultStatus.EXPELLED;
            case ABSENT -> ResultStatus.ABSENT;
        };
    }

    private String computeHash(UUID examId, UUID studentId, int version, BigDecimal totalCredits,
                                BigDecimal earnedPoints, BigDecimal gpa, ResultStatus status,
                                List<ResultSubjectSnapshot> subjects) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            sb.append(examId).append(":")
              .append(studentId).append(":")
              .append(version).append(":")
              .append(totalCredits).append(":")
              .append(earnedPoints).append(":")
              .append(gpa).append(":")
              .append(status).append(";");

            for (ResultSubjectSnapshot s : subjects) {
                sb.append(s.getSubjectId()).append(",")
                  .append(s.getTotalObtained()).append(",")
                  .append(s.getLetterGrade()).append(",")
                  .append(s.getGradePoint()).append(";");
            }

            byte[] hash = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private record SubjectInfo(UUID subjectId, String name, BigDecimal creditHours) {}
    private record CalculationBundle(ResultSnapshot snapshot) {}
}

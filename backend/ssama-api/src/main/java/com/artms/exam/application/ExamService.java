package com.artms.exam.application;

import com.artms.academic.domain.*;
import com.artms.audit.application.AuditService;
import com.artms.enrollment.domain.Enrollment;
import com.artms.enrollment.domain.EnrollmentRepository;
import com.artms.exam.domain.*;
import com.artms.grading.domain.GradingSchemeRepository;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.OptimisticLockConflictException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.exception.ValidationException;
import com.artms.shared.tenant.TenantContext;
import com.artms.student.domain.Student;
import com.artms.student.domain.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamSubjectRepository examSubjectRepository;
    private final MarkEntryRepository markEntryRepository;
    private final AcademicYearRepository academicYearRepository;
    private final GradingSchemeRepository gradingSchemeRepository;
    private final ClassGroupRepository classGroupRepository;
    private final CurriculumSubjectRepository curriculumSubjectRepository;
    private final SubjectComponentRepository subjectComponentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final AuditService auditService;

    @Transactional
    public ExamResponse createExam(CreateExamRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        academicYearRepository.findByTenantIdAndId(tenantId, req.academicYearId())
                .orElseThrow(() -> ResourceNotFoundException.of("AcademicYear", req.academicYearId()));

        gradingSchemeRepository.findByTenantIdAndId(tenantId, req.gradingSchemeId())
                .orElseThrow(() -> ResourceNotFoundException.of("GradingScheme", req.gradingSchemeId()));

        if (req.classGroupId() != null) {
            classGroupRepository.findById(req.classGroupId())
                    .filter(cg -> cg.getTenantId().equals(tenantId))
                    .orElseThrow(() -> ResourceNotFoundException.of("ClassGroup", req.classGroupId()));
        }

        if (req.startDate() != null && req.endDate() != null && req.endDate().isBefore(req.startDate())) {
            throw new ValidationException("Exam end date cannot be before start date");
        }

        Exam exam = new Exam();
        exam.setTenantId(tenantId);
        exam.setAcademicYearId(req.academicYearId());
        exam.setTermId(req.termId());
        exam.setName(req.name());
        exam.setExamType(req.examType());
        exam.setStatus(ExamStatus.DRAFT);
        exam.setGradingSchemeId(req.gradingSchemeId());
        exam.setClassGroupId(req.classGroupId());
        exam.setStartDate(req.startDate());
        exam.setEndDate(req.endDate());
        exam.setCreatedBy(userId);

        Exam saved = examRepository.save(exam);

        auditService.record(tenantId, userId, "exam:create", "Exam", saved.getId().toString(),
                Map.of("name", saved.getName(), "examType", saved.getExamType()));

        return ExamResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ExamResponse getExam(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Exam exam = examRepository.findWithSubjects(tenantId, id)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", id));
        return ExamResponse.from(exam);
    }

    @Transactional(readOnly = true)
    public Page<ExamResponse> listExams(Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return examRepository.findByTenantId(tenantId, pageable).map(ExamResponse::from);
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> listExamsByAcademicYear(UUID academicYearId) {
        UUID tenantId = TenantContext.getTenantId();
        return examRepository.findByTenantIdAndAcademicYearId(tenantId, academicYearId).stream()
                .map(ExamResponse::from)
                .toList();
    }

    @Transactional
    public ExamSubjectResponse addSubject(UUID examId, AddExamSubjectRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        Exam exam = examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        if (exam.getStatus() != ExamStatus.DRAFT && exam.getStatus() != ExamStatus.SCHEDULED) {
            throw new BusinessRuleException("EXAM_MODIFICATION_BLOCKED",
                    "Cannot add subject when exam is in status: " + exam.getStatus());
        }

        curriculumSubjectRepository.findById(req.curriculumSubjectId())
                .orElseThrow(() -> ResourceNotFoundException.of("CurriculumSubject", req.curriculumSubjectId()));

        Optional<ExamSubject> existingOpt = examSubjectRepository
                .findByExamIdAndCurriculumSubjectId(examId, req.curriculumSubjectId());

        ExamSubject es;
        if (existingOpt.isPresent()) {
            es = existingOpt.get();
            if (es.getStatus() == ExamSubjectStatus.ACTIVE) {
                throw new ValidationException("Curriculum subject already added to this exam");
            }
            es.setStatus(ExamSubjectStatus.ACTIVE);
        } else {
            es = new ExamSubject();
            es.setExam(exam);
            es.setCurriculumSubjectId(req.curriculumSubjectId());
            es.setStatus(ExamSubjectStatus.ACTIVE);
            exam.getExamSubjects().add(es);
        }

        ExamSubject saved = examSubjectRepository.save(es);

        auditService.record(tenantId, userId, "exam_subject:add", "ExamSubject", saved.getId().toString(),
                Map.of("examId", examId, "curriculumSubjectId", req.curriculumSubjectId()));

        return ExamSubjectResponse.from(saved);
    }

    @Transactional
    public void removeSubject(UUID examId, UUID examSubjectId) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        Exam exam = examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        if (exam.getStatus() != ExamStatus.DRAFT && exam.getStatus() != ExamStatus.SCHEDULED) {
            throw new BusinessRuleException("EXAM_MODIFICATION_BLOCKED",
                    "Cannot remove subject when exam is in status: " + exam.getStatus());
        }

        ExamSubject es = examSubjectRepository.findById(examSubjectId)
                .orElseThrow(() -> ResourceNotFoundException.of("ExamSubject", examSubjectId));

        if (!es.getExam().getId().equals(examId)) {
            throw new ValidationException("ExamSubject does not belong to specified exam");
        }

        List<MarkEntry> marks = markEntryRepository.findByTenantIdAndExamSubjectId(tenantId, examSubjectId);
        if (!marks.isEmpty()) {
            es.setStatus(ExamSubjectStatus.REMOVED);
            examSubjectRepository.save(es);
        } else {
            exam.getExamSubjects().remove(es);
            examSubjectRepository.delete(es);
        }

        auditService.record(tenantId, userId, "exam_subject:remove", "ExamSubject", examSubjectId.toString(),
                Map.of("examId", examId));
    }

    @Transactional
    public ExamResponse updateWorkflowStatus(UUID examId, ExamWorkflowRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        Exam exam = examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        ExamStatus current = exam.getStatus();
        ExamStatus target = req.targetStatus();

        if (current == target) {
            return ExamResponse.from(exam);
        }

        validateTransition(current, target);

        if ((target == ExamStatus.SCHEDULED || target == ExamStatus.MARK_ENTRY)
                && exam.getExamSubjects().isEmpty()) {
            throw new ValidationException("Exam must have at least one subject before scheduling or opening mark entry");
        }

        exam.setStatus(target);
        Exam updated = examRepository.save(exam);

        auditService.record(tenantId, userId, "exam:workflow_transition", "Exam", examId.toString(),
                Map.of("from", current, "to", target, "remarks", req.remarks() != null ? req.remarks() : ""));

        return ExamResponse.from(updated);
    }

    @Transactional
    public List<MarkEntryResponse> enterMarksBatch(UUID examId, BatchMarkEntryRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        Exam exam = examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        if (exam.getStatus() != ExamStatus.MARK_ENTRY && exam.getStatus() != ExamStatus.DRAFT
                && exam.getStatus() != ExamStatus.SCHEDULED) {
            throw new BusinessRuleException("EXAM_NOT_OPEN_FOR_MARKS",
                    "Exam status " + exam.getStatus() + " does not allow mark entry");
        }

        ExamSubject examSubject = examSubjectRepository.findById(req.examSubjectId())
                .orElseThrow(() -> ResourceNotFoundException.of("ExamSubject", req.examSubjectId()));

        if (!examSubject.getExam().getId().equals(examId)) {
            throw new ValidationException("Exam subject does not belong to this exam");
        }
        if (examSubject.getStatus() != ExamSubjectStatus.ACTIVE) {
            throw new ValidationException("Exam subject is not active");
        }

        List<SubjectComponent> components = subjectComponentRepository
                .findByCurriculumSubjectIdOrderBySequenceAsc(examSubject.getCurriculumSubjectId());
        Map<UUID, SubjectComponent> componentMap = components.stream()
                .collect(Collectors.toMap(SubjectComponent::getId, c -> c));

        List<MarkEntry> entriesToSave = new ArrayList<>();

        for (MarkEntryDto entry : req.entries()) {
            SubjectComponent comp = componentMap.get(entry.componentId());
            if (comp == null) {
                throw new ValidationException("Component " + entry.componentId()
                        + " does not belong to curriculum subject");
            }

            if (entry.rawMarks() != null) {
                if (entry.rawMarks().compareTo(BigDecimal.ZERO) < 0
                        || entry.rawMarks().compareTo(comp.getFullMarks()) > 0) {
                    throw new ValidationException("Raw marks must be between 0 and "
                            + comp.getFullMarks() + " for component " + comp.getName());
                }
            }

            studentRepository.findByTenantIdAndId(tenantId, entry.studentId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Student", entry.studentId()));

            Optional<MarkEntry> existingOpt = markEntryRepository
                    .findByTenantIdAndExamSubjectIdAndStudentIdAndComponentId(
                            tenantId, req.examSubjectId(), entry.studentId(), entry.componentId());

            MarkEntry me;
            if (existingOpt.isPresent()) {
                me = existingOpt.get();
                if (entry.version() != null && !entry.version().equals(me.getVersion())) {
                    throw new OptimisticLockConflictException(
                            "Concurrent modification detected for student " + entry.studentId()
                                    + " component " + entry.componentId());
                }
                if (me.getStatus() == MarkStatus.VERIFIED) {
                    throw new BusinessRuleException("MARK_ALREADY_VERIFIED",
                            "Cannot modify verified mark entry");
                }
                me.setRawMarks(entry.rawMarks());
                if (entry.status() != null) {
                    me.setStatus(entry.status());
                }
                me.setRemarks(entry.remarks());
                me.setEnteredBy(userId);
            } else {
                me = new MarkEntry();
                me.setTenantId(tenantId);
                me.setExamSubjectId(req.examSubjectId());
                me.setStudentId(entry.studentId());
                me.setComponentId(entry.componentId());
                me.setRawMarks(entry.rawMarks());
                me.setStatus(entry.status() != null ? entry.status() : MarkStatus.DRAFT);
                me.setRemarks(entry.remarks());
                me.setEnteredBy(userId);
            }

            entriesToSave.add(me);
        }

        List<MarkEntry> saved = markEntryRepository.saveAll(entriesToSave);

        auditService.record(tenantId, userId, "marks:enter_batch", "ExamSubject", req.examSubjectId().toString(),
                Map.of("examId", examId, "count", saved.size()));

        return saved.stream().map(MarkEntryResponse::from).toList();
    }

    @Transactional
    public List<MarkEntryResponse> submitMarks(UUID examId, UUID examSubjectId) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        ExamSubject examSubject = examSubjectRepository.findById(examSubjectId)
                .orElseThrow(() -> ResourceNotFoundException.of("ExamSubject", examSubjectId));
        if (!examSubject.getExam().getId().equals(examId)) {
            throw new ValidationException("Exam subject does not belong to this exam");
        }

        List<MarkEntry> entries = markEntryRepository.findByTenantIdAndExamSubjectId(tenantId, examSubjectId);
        if (entries.isEmpty()) {
            throw new ValidationException("No marks found to submit for this exam subject");
        }

        OffsetDateTime now = OffsetDateTime.now();
        for (MarkEntry me : entries) {
            if (me.getStatus() == MarkStatus.DRAFT || me.getStatus() == MarkStatus.REJECTED) {
                me.setStatus(MarkStatus.SUBMITTED);
                me.setSubmittedBy(userId);
                me.setSubmittedAt(now);
            }
        }

        List<MarkEntry> saved = markEntryRepository.saveAll(entries);
        auditService.record(tenantId, userId, "marks:submit", "ExamSubject", examSubjectId.toString(),
                Map.of("examId", examId, "count", saved.size()));

        return saved.stream().map(MarkEntryResponse::from).toList();
    }

    @Transactional
    public List<MarkEntryResponse> verifyMarks(UUID examId, UUID examSubjectId) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();

        examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        ExamSubject examSubject = examSubjectRepository.findById(examSubjectId)
                .orElseThrow(() -> ResourceNotFoundException.of("ExamSubject", examSubjectId));
        if (!examSubject.getExam().getId().equals(examId)) {
            throw new ValidationException("Exam subject does not belong to this exam");
        }

        List<MarkEntry> entries = markEntryRepository.findByTenantIdAndExamSubjectId(tenantId, examSubjectId);
        if (entries.isEmpty()) {
            throw new ValidationException("No marks found to verify for this exam subject");
        }

        OffsetDateTime now = OffsetDateTime.now();
        for (MarkEntry me : entries) {
            if (me.getStatus() == MarkStatus.SUBMITTED) {
                me.setStatus(MarkStatus.VERIFIED);
                me.setVerifiedBy(userId);
                me.setVerifiedAt(now);
            }
        }

        List<MarkEntry> saved = markEntryRepository.saveAll(entries);
        auditService.record(tenantId, userId, "marks:verify", "ExamSubject", examSubjectId.toString(),
                Map.of("examId", examId, "count", saved.size()));

        return saved.stream().map(MarkEntryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ExamMarksGridDto getMarksGrid(UUID examId, UUID examSubjectId) {
        UUID tenantId = TenantContext.getTenantId();
        Exam exam = examRepository.findByTenantIdAndId(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        ExamSubject examSubject = examSubjectRepository.findById(examSubjectId)
                .orElseThrow(() -> ResourceNotFoundException.of("ExamSubject", examSubjectId));
        if (!examSubject.getExam().getId().equals(examId)) {
            throw new ValidationException("Exam subject does not belong to this exam");
        }

        List<SubjectComponent> components = subjectComponentRepository
                .findByCurriculumSubjectIdOrderBySequenceAsc(examSubject.getCurriculumSubjectId());

        List<ExamMarksGridDto.ComponentHeaderDto> componentHeaders = components.stream()
                .map(c -> new ExamMarksGridDto.ComponentHeaderDto(
                        c.getId(), c.getCode(), c.getName(), c.getFullMarks(), c.getPassMarks()))
                .toList();

        List<Enrollment> enrollments = Collections.emptyList();
        if (exam.getClassGroupId() != null) {
            enrollments = enrollmentRepository.findByTenantIdAndClassGroupIdAndAcademicYearId(
                    tenantId, exam.getClassGroupId(), exam.getAcademicYearId());
        }

        List<MarkEntry> existingMarks = markEntryRepository.findByTenantIdAndExamSubjectId(tenantId, examSubjectId);
        Map<UUID, Map<UUID, MarkEntry>> marksByStudentAndComp = new HashMap<>();
        for (MarkEntry me : existingMarks) {
            marksByStudentAndComp
                    .computeIfAbsent(me.getStudentId(), k -> new HashMap<>())
                    .put(me.getComponentId(), me);
        }

        Set<UUID> studentIds = new LinkedHashSet<>();
        for (Enrollment en : enrollments) {
            studentIds.add(en.getStudentId());
        }
        studentIds.addAll(marksByStudentAndComp.keySet());

        List<ExamMarksGridDto.StudentMarksRowDto> rows = new ArrayList<>();
        for (UUID studentId : studentIds) {
            Student student = studentRepository.findByTenantIdAndId(tenantId, studentId).orElse(null);
            String admissionNo = student != null ? student.getAdmissionNo() : "";
            String studentName = student != null
                    ? (student.getFirstName() + (student.getMiddleName() != null ? " " + student.getMiddleName() : "") + " " + student.getLastName())
                    : "Unknown";

            Map<UUID, MarkEntry> compMap = marksByStudentAndComp.getOrDefault(studentId, Collections.emptyMap());
            List<MarkEntryResponse> compMarks = components.stream()
                    .map(c -> {
                        MarkEntry me = compMap.get(c.getId());
                        return me != null ? MarkEntryResponse.from(me) : null;
                    })
                    .filter(Objects::nonNull)
                    .toList();

            rows.add(new ExamMarksGridDto.StudentMarksRowDto(studentId, admissionNo, studentName, compMarks));
        }

        return new ExamMarksGridDto(
                examId,
                examSubjectId,
                examSubject.getCurriculumSubjectId(),
                componentHeaders,
                rows
        );
    }

    @Transactional(readOnly = true)
    public List<MarkEntryResponse> getStudentMarks(UUID examId, UUID studentId) {
        UUID tenantId = TenantContext.getTenantId();
        Exam exam = examRepository.findWithSubjects(tenantId, examId)
                .orElseThrow(() -> ResourceNotFoundException.of("Exam", examId));

        List<UUID> examSubjectIds = exam.getExamSubjects().stream().map(ExamSubject::getId).toList();
        List<MarkEntry> marks = markEntryRepository.findByTenantIdAndStudentId(tenantId, studentId);

        return marks.stream()
                .filter(m -> examSubjectIds.contains(m.getExamSubjectId()))
                .map(MarkEntryResponse::from)
                .toList();
    }

    private void validateTransition(ExamStatus current, ExamStatus target) {
        boolean valid = switch (current) {
            case DRAFT -> target == ExamStatus.SCHEDULED || target == ExamStatus.MARK_ENTRY;
            case SCHEDULED -> target == ExamStatus.MARK_ENTRY || target == ExamStatus.DRAFT;
            case MARK_ENTRY -> target == ExamStatus.SUBMITTED || target == ExamStatus.SCHEDULED;
            case SUBMITTED -> target == ExamStatus.VERIFIED || target == ExamStatus.MARK_ENTRY;
            case VERIFIED -> target == ExamStatus.APPROVED || target == ExamStatus.MARK_ENTRY;
            case APPROVED -> target == ExamStatus.PUBLISHED || target == ExamStatus.VERIFIED;
            case PUBLISHED -> target == ExamStatus.LOCKED;
            case LOCKED -> false;
        };
        if (!valid) {
            throw new BusinessRuleException("INVALID_EXAM_TRANSITION",
                    "Cannot transition exam from " + current + " to " + target);
        }
    }
}

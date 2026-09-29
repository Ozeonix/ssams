package com.artms.enrollment.application;

import com.artms.academic.domain.*;
import com.artms.enrollment.domain.*;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import com.artms.student.domain.Student;
import com.artms.student.domain.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ClassGroupRepository classGroupRepository;
    private final AcademicYearRepository academicYearRepository;
    private final CurriculumRepository curriculumRepository;

    @Transactional
    public EnrollmentResponse enrollStudent(EnrollStudentRequest request) {
        return enrollStudent(TenantContext.getTenantId(), request);
    }

    @Transactional
    public List<EnrollmentResponse> batchEnrollClass(BatchEnrollClassRequest request) {
        return batchEnrollClass(TenantContext.getTenantId(), request);
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(UUID enrollmentId) {
        return getEnrollmentById(TenantContext.getTenantId(), enrollmentId);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getEnrollmentsByClass(UUID classGroupId, UUID academicYearId, Pageable pageable) {
        return getEnrollmentsByClass(TenantContext.getTenantId(), classGroupId, academicYearId, pageable);
    }

    @Transactional
    public EnrollmentResponse updateStatus(UUID enrollmentId, EnrollmentStatus newStatus) {
        return updateStatus(TenantContext.getTenantId(), enrollmentId, newStatus);
    }

    @Transactional
    public EnrollmentResponse enrollStudent(UUID tenantId, EnrollStudentRequest request) {
        log.info("Enrolling student {} into class group {} for academic year {}",
                request.studentId(), request.classGroupId(), request.academicYearId());

        Student student = studentRepository.findByTenantIdAndId(tenantId, request.studentId())
                .orElseThrow(() -> ResourceNotFoundException.of("Student", request.studentId()));

        ClassGroup classGroup = classGroupRepository.findByTenantIdAndId(tenantId, request.classGroupId())
                .orElseThrow(() -> ResourceNotFoundException.of("ClassGroup", request.classGroupId()));

        AcademicYear academicYear = academicYearRepository.findByTenantIdAndId(tenantId, request.academicYearId())
                .orElseThrow(() -> ResourceNotFoundException.of("AcademicYear", request.academicYearId()));

        if (!classGroup.getAcademicYearId().equals(request.academicYearId())) {
            throw new BusinessRuleException("CLASS_GROUP_YEAR_MISMATCH",
                    "Class group does not belong to the specified academic year");
        }

        if (enrollmentRepository.existsByTenantIdAndStudentIdAndAcademicYearId(
                tenantId, request.studentId(), request.academicYearId())) {
            throw new BusinessRuleException("STUDENT_ALREADY_ENROLLED",
                    "Student is already enrolled for academic year: " + request.academicYearId());
        }

        if (classGroup.getCapacity() != null) {
            int currentCount = enrollmentRepository.countByTenantIdAndClassGroupIdAndAcademicYearId(
                    tenantId, request.classGroupId(), request.academicYearId());
            if (currentCount >= classGroup.getCapacity()) {
                throw new BusinessRuleException("CLASS_GROUP_CAPACITY_EXCEEDED",
                        "Class group capacity reached: " + classGroup.getCapacity());
            }
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setTenantId(tenantId);
        enrollment.setStudentId(student.getId());
        enrollment.setClassGroupId(classGroup.getId());
        enrollment.setAcademicYearId(academicYear.getId());
        enrollment.setRollNo(request.rollNo());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setEnrolledAt(request.enrolledAt() != null ? request.enrolledAt() : LocalDate.now());

        registerCurriculumSubjects(enrollment, tenantId, classGroup);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return EnrollmentResponse.from(saved);
    }

    @Transactional
    public List<EnrollmentResponse> batchEnrollClass(UUID tenantId, BatchEnrollClassRequest request) {
        log.info("Batch enrolling {} students into class group {} for academic year {}",
                request.students().size(), request.classGroupId(), request.academicYearId());

        ClassGroup classGroup = classGroupRepository.findByTenantIdAndId(tenantId, request.classGroupId())
                .orElseThrow(() -> ResourceNotFoundException.of("ClassGroup", request.classGroupId()));

        AcademicYear academicYear = academicYearRepository.findByTenantIdAndId(tenantId, request.academicYearId())
                .orElseThrow(() -> ResourceNotFoundException.of("AcademicYear", request.academicYearId()));

        if (!classGroup.getAcademicYearId().equals(request.academicYearId())) {
            throw new BusinessRuleException("CLASS_GROUP_YEAR_MISMATCH",
                    "Class group does not belong to the specified academic year");
        }

        if (classGroup.getCapacity() != null) {
            int currentCount = enrollmentRepository.countByTenantIdAndClassGroupIdAndAcademicYearId(
                    tenantId, request.classGroupId(), request.academicYearId());
            if (currentCount + request.students().size() > classGroup.getCapacity()) {
                throw new BusinessRuleException("CLASS_GROUP_CAPACITY_EXCEEDED",
                        "Class group capacity exceeded. Available: " + (classGroup.getCapacity() - currentCount)
                                + ", Requested: " + request.students().size());
            }
        }

        Optional<Curriculum> curriculumOpt = curriculumRepository
                .findFirstByTenantIdAndProgramIdAndAcademicYearIdAndStatusOrderByVersionDesc(
                        tenantId, classGroup.getProgramId(), request.academicYearId(), CurriculumStatus.PUBLISHED);

        List<Enrollment> enrollmentsToSave = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (StudentEnrollmentDto studentDto : request.students()) {
            Student student = studentRepository.findByTenantIdAndId(tenantId, studentDto.studentId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Student", studentDto.studentId()));

            if (enrollmentRepository.existsByTenantIdAndStudentIdAndAcademicYearId(
                    tenantId, student.getId(), request.academicYearId())) {
                throw new BusinessRuleException("STUDENT_ALREADY_ENROLLED",
                        "Student " + student.getAdmissionNo() + " is already enrolled for academic year: " + request.academicYearId());
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setTenantId(tenantId);
            enrollment.setStudentId(student.getId());
            enrollment.setClassGroupId(classGroup.getId());
            enrollment.setAcademicYearId(academicYear.getId());
            enrollment.setRollNo(studentDto.rollNo());
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
            enrollment.setEnrolledAt(today);

            if (curriculumOpt.isPresent()) {
                attachCurriculumSubjects(enrollment, curriculumOpt.get(), classGroup.getGradeLevel());
            }

            enrollmentsToSave.add(enrollment);
        }

        List<Enrollment> saved = enrollmentRepository.saveAll(enrollmentsToSave);
        return saved.stream().map(EnrollmentResponse::from).toList();
    }

    private void registerCurriculumSubjects(Enrollment enrollment, UUID tenantId, ClassGroup classGroup) {
        Optional<Curriculum> curriculumOpt = curriculumRepository
                .findFirstByTenantIdAndProgramIdAndAcademicYearIdAndStatusOrderByVersionDesc(
                        tenantId, classGroup.getProgramId(), enrollment.getAcademicYearId(), CurriculumStatus.PUBLISHED);

        curriculumOpt.ifPresent(curriculum -> attachCurriculumSubjects(enrollment, curriculum, classGroup.getGradeLevel()));
    }

    private void attachCurriculumSubjects(Enrollment enrollment, Curriculum curriculum, String gradeLevel) {
        if (curriculum.getSubjects() == null) return;
        for (CurriculumSubject cs : curriculum.getSubjects()) {
            boolean gradeMatches = cs.getGradeLevel() == null || cs.getGradeLevel().isBlank()
                    || cs.getGradeLevel().equalsIgnoreCase(gradeLevel);
            if (cs.isMandatory() && gradeMatches) {
                SubjectEnrollment se = new SubjectEnrollment();
                se.setEnrollment(enrollment);
                se.setCurriculumSubjectId(cs.getId());
                se.setStatus(SubjectEnrollmentStatus.ACTIVE);
                enrollment.getSubjectEnrollments().add(se);
            }
        }
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(UUID tenantId, UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findWithSubjects(tenantId, enrollmentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Enrollment", enrollmentId));
        return EnrollmentResponse.from(enrollment);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getEnrollmentsByClass(
            UUID tenantId, UUID classGroupId, UUID academicYearId, Pageable pageable) {
        return enrollmentRepository.findByTenantIdAndClassGroupIdAndAcademicYearId(
                tenantId, classGroupId, academicYearId, pageable)
                .map(EnrollmentResponse::from);
    }

    @Transactional
    public EnrollmentResponse updateStatus(UUID tenantId, UUID enrollmentId, EnrollmentStatus newStatus) {
        Enrollment enrollment = enrollmentRepository.findByTenantIdAndId(tenantId, enrollmentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Enrollment", enrollmentId));
        enrollment.setStatus(newStatus);
        return EnrollmentResponse.from(enrollment);
    }
}

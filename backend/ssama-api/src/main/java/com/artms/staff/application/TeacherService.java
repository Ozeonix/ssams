package com.artms.staff.application;

import com.artms.academic.domain.AcademicYearRepository;
import com.artms.academic.domain.CurriculumSubjectRepository;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import com.artms.staff.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherSubjectRepository teacherSubjectRepository;
    private final CurriculumSubjectRepository curriculumSubjectRepository;
    private final AcademicYearRepository academicYearRepository;

    @Transactional
    public Teacher createTeacher(CreateTeacherRequest req) {
        UUID tenantId = TenantContext.getTenantId();

        if (teacherRepository.findByTenantIdAndUserId(tenantId, req.userId()).isPresent()) {
            throw new BusinessRuleException("DUPLICATE_TEACHER_USER", "A teacher profile already exists for this user");
        }

        if (req.employeeCode() != null && !req.employeeCode().isBlank()) {
            if (teacherRepository.existsByTenantIdAndEmployeeCode(tenantId, req.employeeCode().trim())) {
                throw new BusinessRuleException("DUPLICATE_EMPLOYEE_CODE", "Employee code already exists: " + req.employeeCode());
            }
        }

        Teacher teacher = new Teacher();
        teacher.setTenantId(tenantId);
        teacher.setUserId(req.userId());
        teacher.setEmployeeCode(req.employeeCode() != null ? req.employeeCode().trim() : null);
        teacher.setFirstName(req.firstName().trim());
        teacher.setLastName(req.lastName().trim());
        teacher.setDepartmentId(req.departmentId());
        teacher.setDesignation(req.designation());
        teacher.setPhone(req.phone());
        teacher.setEmail(req.email());
        teacher.setStatus(TeacherStatus.ACTIVE);

        return teacherRepository.save(teacher);
    }

    @Transactional
    public TeacherSubject assignSubject(UUID teacherId, AssignTeacherSubjectRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        Teacher teacher = getTeacher(teacherId);

        academicYearRepository.findById(req.academicYearId())
            .filter(y -> y.getTenantId().equals(tenantId))
            .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", req.academicYearId()));

        curriculumSubjectRepository.findById(req.curriculumSubjectId())
            .orElseThrow(() -> new ResourceNotFoundException("CurriculumSubject", req.curriculumSubjectId()));

        TeacherSubject ts = new TeacherSubject();
        ts.setTeacherId(teacher.getId());
        ts.setCurriculumSubjectId(req.curriculumSubjectId());
        ts.setAcademicYearId(req.academicYearId());

        return teacherSubjectRepository.save(ts);
    }

    @Transactional(readOnly = true)
    public List<TeacherSubject> getTeacherSubjects(UUID teacherId, UUID academicYearId) {
        getTeacher(teacherId);
        return teacherSubjectRepository.findByTeacherIdAndAcademicYearId(teacherId, academicYearId);
    }

    @Transactional(readOnly = true)
    public Teacher getTeacher(UUID id) {
        return teacherRepository.findById(id)
            .filter(t -> t.getTenantId().equals(TenantContext.getTenantId()))
            .orElseThrow(() -> new ResourceNotFoundException("Teacher", id));
    }

    @Transactional(readOnly = true)
    public Page<Teacher> listTeachers(Pageable pageable) {
        return teacherRepository.findByTenantId(TenantContext.getTenantId(), pageable);
    }
}

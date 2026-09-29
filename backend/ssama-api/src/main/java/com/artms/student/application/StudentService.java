package com.artms.student.application;

import com.artms.audit.application.AuditService;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import com.artms.student.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final AuditService auditService;

    @Transactional
    @PreAuthorize("hasAuthority('PERM_student:create')")
    public BatchImportResult importStudentsBatch(List<CreateStudentRequest> requests) {
        UUID tenantId = TenantContext.getTenantId();
        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            CreateStudentRequest req = requests.get(i);
            try {
                if (studentRepository.existsByTenantIdAndAdmissionNo(tenantId, req.admissionNo())) {
                    skipped++;
                    errors.add("Row " + (i + 1) + ": Admission number '" + req.admissionNo() + "' already exists");
                    continue;
                }

                Student student = new Student();
                student.setTenantId(tenantId);
                student.setAdmissionNo(req.admissionNo());
                student.setFirstName(req.firstName());
                student.setMiddleName(req.middleName());
                student.setLastName(req.lastName());
                student.setDateOfBirth(req.dateOfBirth());
                student.setGender(req.gender());
                student.setPhone(req.phone());
                student.setEmail(req.email());
                student.setAddress(req.address());
                student.setRegistrationNo(req.registrationNo());
                student.setSymbolNo(req.symbolNo());

                studentRepository.save(student);
                imported++;
            } catch (Exception ex) {
                skipped++;
                errors.add("Row " + (i + 1) + " (" + req.admissionNo() + "): " + ex.getMessage());
            }
        }

        auditService.record(tenantId, TenantContext.getUserId(),
                "student.batch_imported", "Student", "BATCH",
                Map.of("total", requests.size(), "imported", imported, "skipped", skipped));

        return new BatchImportResult(requests.size(), imported, skipped, errors);
    }

    @Transactional
    @PreAuthorize("hasAuthority('PERM_student:create')")
    public Student create(CreateStudentRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        if (studentRepository.existsByTenantIdAndAdmissionNo(tenantId, request.admissionNo())) {
            throw new BusinessRuleException("ADMISSION_NO_EXISTS",
                    "A student with admission number '" + request.admissionNo() + "' already exists");
        }

        Student student = new Student();
        student.setTenantId(tenantId);
        student.setAdmissionNo(request.admissionNo());
        student.setFirstName(request.firstName());
        student.setMiddleName(request.middleName());
        student.setLastName(request.lastName());
        student.setDateOfBirth(request.dateOfBirth());
        student.setGender(request.gender());
        student.setPhone(request.phone());
        student.setEmail(request.email());
        student.setAddress(request.address());
        student.setRegistrationNo(request.registrationNo());
        student.setSymbolNo(request.symbolNo());

        Student saved = studentRepository.save(student);

        auditService.record(tenantId, TenantContext.getUserId(),
                "student.created", "Student", saved.getId().toString(),
                Map.of("admissionNo", saved.getAdmissionNo(), "name", saved.getFullName()));

        return saved;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PERM_student:read')")
    public Page<Student> list(StudentStatus status, String search, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return studentRepository.search(tenantId, status, search, pageable);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PERM_student:read')")
    public Student getById(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        return studentRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> ResourceNotFoundException.of("Student", id));
    }

    @Transactional
    @PreAuthorize("hasAuthority('PERM_student:update')")
    public Student update(UUID id, UpdateStudentRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        Student student = getById(id);

        Map<String, Object> before = Map.of("name", student.getFullName(), "status", student.getStatus());

        if (request.firstName() != null) student.setFirstName(request.firstName());
        if (request.middleName() != null) student.setMiddleName(request.middleName());
        if (request.lastName() != null) student.setLastName(request.lastName());
        if (request.phone() != null) student.setPhone(request.phone());
        if (request.email() != null) student.setEmail(request.email());
        if (request.address() != null) student.setAddress(request.address());

        Student saved = studentRepository.save(student);

        auditService.record(new com.artms.audit.application.AuditEvent(
                tenantId, TenantContext.getUserId(),
                "student.updated", "Student", id.toString(),
                before, Map.of("name", saved.getFullName()),
                null, null, null));

        return saved;
    }

    private final StudentGuardianRepository guardianRepository;

    @Transactional
    public StudentGuardian addGuardian(UUID studentId, AddGuardianRequest req) {
        Student student = getById(studentId);

        StudentGuardian guardian = new StudentGuardian();
        guardian.setStudent(student);
        guardian.setName(req.name().trim());
        guardian.setRelationship(req.relationship());
        guardian.setPhone(req.phone());
        guardian.setEmail(req.email());
        guardian.setAddress(req.address());
        guardian.setPrimary(req.isPrimary());

        return guardianRepository.save(guardian);
    }

    @Transactional(readOnly = true)
    public List<StudentGuardian> getGuardians(UUID studentId) {
        getById(studentId); // Verify student exists & belongs to tenant
        return guardianRepository.findByStudentId(studentId);
    }
}

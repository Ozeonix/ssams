package com.artms.academic.application;

import com.artms.academic.domain.*;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassGroupService {

    private final ClassGroupRepository classGroupRepository;
    private final AcademicYearRepository academicYearRepository;
    private final ProgramRepository programRepository;

    @Transactional
    public ClassGroup createClassGroup(CreateClassGroupRequest req) {
        UUID tenantId = TenantContext.getTenantId();

        AcademicYear year = academicYearRepository.findById(req.academicYearId())
            .filter(y -> y.getTenantId().equals(tenantId))
            .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", req.academicYearId()));

        Program program = programRepository.findById(req.programId())
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new ResourceNotFoundException("Program", req.programId()));

        ClassGroup group = new ClassGroup();
        group.setTenantId(tenantId);
        group.setAcademicYear(year);
        group.setProgram(program);
        group.setName(req.name().trim());
        group.setSection(req.section().trim());
        group.setGradeLevel(req.gradeLevel().trim());
        group.setCapacity(req.capacity() != null ? req.capacity() : 40);
        return classGroupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public Page<ClassGroup> listClassGroups(Pageable pageable) {
        return classGroupRepository.findByTenantId(TenantContext.getTenantId(), pageable);
    }

    @Transactional(readOnly = true)
    public ClassGroup getClassGroup(UUID id) {
        return classGroupRepository.findById(id)
            .filter(c -> c.getTenantId().equals(TenantContext.getTenantId()))
            .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", id));
    }
}

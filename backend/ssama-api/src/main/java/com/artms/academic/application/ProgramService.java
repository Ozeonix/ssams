package com.artms.academic.application;

import com.artms.academic.domain.Department;
import com.artms.academic.domain.Program;
import com.artms.academic.domain.ProgramRepository;
import com.artms.shared.exception.BusinessRuleException;
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
public class ProgramService {

    private final ProgramRepository programRepository;
    private final DepartmentService departmentService;

    @Transactional
    public Program createProgram(CreateProgramRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        if (programRepository.existsByTenantIdAndCode(tenantId, req.code())) {
            throw new BusinessRuleException("DUPLICATE_CODE", "Program code already exists: " + req.code());
        }

        UUID deptId = null;
        if (req.departmentId() != null) {
            Department dept = departmentService.getDepartment(req.departmentId());
            deptId = dept.getId();
        }

        Program program = new Program();
        program.setTenantId(tenantId);
        program.setCode(req.code().toUpperCase().trim());
        program.setName(req.name().trim());
        program.setDepartmentId(deptId);
        program.setLevel(req.level());
        program.setDurationYears(req.durationYears());
        program.setStatus("ACTIVE");
        return programRepository.save(program);
    }

    @Transactional(readOnly = true)
    public Page<Program> listPrograms(Pageable pageable) {
        return programRepository.findByTenantId(TenantContext.getTenantId(), pageable);
    }

    @Transactional(readOnly = true)
    public Program getProgram(UUID id) {
        return programRepository.findById(id)
            .filter(p -> p.getTenantId().equals(TenantContext.getTenantId()))
            .orElseThrow(() -> new ResourceNotFoundException("Program", id));
    }
}

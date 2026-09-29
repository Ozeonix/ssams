package com.artms.academic.application;

import com.artms.academic.domain.Department;
import com.artms.academic.domain.DepartmentRepository;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional
    public Department createDepartment(CreateDepartmentRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        if (departmentRepository.findByTenantIdAndCode(tenantId, req.code()).isPresent()) {
            throw new BusinessRuleException("DUPLICATE_CODE", "Department code already exists: " + req.code());
        }

        Department dept = new Department();
        dept.setTenantId(tenantId);
        dept.setCode(req.code().toUpperCase().trim());
        dept.setName(req.name().trim());
        return departmentRepository.save(dept);
    }

    @Transactional(readOnly = true)
    public List<Department> listDepartments() {
        return departmentRepository.findByTenantId(TenantContext.getTenantId());
    }

    @Transactional(readOnly = true)
    public Department getDepartment(UUID id) {
        return departmentRepository.findById(id)
            .filter(d -> d.getTenantId().equals(TenantContext.getTenantId()))
            .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }
}

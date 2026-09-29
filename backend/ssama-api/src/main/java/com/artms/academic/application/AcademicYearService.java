package com.artms.academic.application;

import com.artms.academic.domain.*;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    @Transactional
    @PreAuthorize("hasAuthority('PERM_academic:manage')")
    public AcademicYear create(CreateAcademicYearRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        if (academicYearRepository.existsByTenantIdAndName(tenantId, request.name())) {
            throw new BusinessRuleException("ACADEMIC_YEAR_EXISTS",
                    "Academic year '" + request.name() + "' already exists for this institution");
        }

        if (!request.endDate().isAfter(request.startDate())) {
            throw new BusinessRuleException("INVALID_DATE_RANGE",
                    "End date must be after start date");
        }

        AcademicYear year = new AcademicYear();
        year.setTenantId(tenantId);
        year.setName(request.name());
        year.setStartDate(request.startDate());
        year.setEndDate(request.endDate());
        return academicYearRepository.save(year);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PERM_academic:read')")
    public Page<AcademicYear> list(Pageable pageable) {
        return academicYearRepository.findByTenantIdOrderByStartDateDesc(
                TenantContext.getTenantId(), pageable);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PERM_academic:read')")
    public AcademicYear getById(UUID id) {
        return academicYearRepository.findByTenantIdAndId(TenantContext.getTenantId(), id)
                .orElseThrow(() -> ResourceNotFoundException.of("AcademicYear", id));
    }

    @Transactional
    @PreAuthorize("hasAuthority('PERM_academic:manage')")
    public AcademicYear activate(UUID id) {
        AcademicYear year = getById(id);
        if (year.getStatus() == AcademicYearStatus.COMPLETED ||
                year.getStatus() == AcademicYearStatus.ARCHIVED) {
            throw new BusinessRuleException("INVALID_STATUS_TRANSITION",
                    "Cannot activate a completed or archived academic year");
        }
        year.setStatus(AcademicYearStatus.ACTIVE);
        return academicYearRepository.save(year);
    }
}

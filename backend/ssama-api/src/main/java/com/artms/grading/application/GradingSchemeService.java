package com.artms.grading.application;

import com.artms.grading.domain.*;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradingSchemeService {

    private final GradingSchemeRepository gradingSchemeRepository;

    @Transactional
    public GradingScheme createGradingScheme(CreateGradingSchemeRequest req) {
        UUID tenantId = TenantContext.getTenantId();

        GradingScheme scheme = new GradingScheme();
        scheme.setTenantId(tenantId);
        scheme.setName(req.name().trim());
        scheme.setVersion(1);
        scheme.setEffectiveFrom(req.effectiveFrom());
        scheme.setStatus(GradingSchemeStatus.DRAFT);
        scheme.setGpaPrecision(req.gpaPrecision() != null ? req.gpaPrecision() : 2);
        scheme.setGpaRounding(req.gpaRounding() != null ? req.gpaRounding() : RoundingMode.HALF_UP);

        for (CreateGradingSchemeRequest.GradeBandDto bDto : req.bands()) {
            GradeBand band = new GradeBand();
            band.setGradingScheme(scheme);
            band.setMinPercentage(bDto.minPercentage());
            band.setMaxPercentage(bDto.maxPercentage());
            band.setLetterGrade(bDto.letterGrade().trim());
            band.setGradePoint(bDto.gradePoint());
            band.setPassFlag(bDto.passFlag());
            band.setRemarks(bDto.remarks());
            scheme.getBands().add(band);
        }

        // Validate scheme and bands integrity
        GradingSchemeValidator.validate(scheme);

        return gradingSchemeRepository.save(scheme);
    }

    @Transactional
    public GradingScheme activateScheme(UUID schemeId) {
        UUID tenantId = TenantContext.getTenantId();
        GradingScheme scheme = gradingSchemeRepository.findWithBands(tenantId, schemeId)
            .orElseThrow(() -> new ResourceNotFoundException("GradingScheme", schemeId));

        if (scheme.isActive()) {
            return scheme;
        }

        // Supersede any currently active scheme
        gradingSchemeRepository.findByTenantIdAndStatus(tenantId, GradingSchemeStatus.ACTIVE)
            .ifPresent(active -> {
                active.setStatus(GradingSchemeStatus.SUPERSEDED);
                gradingSchemeRepository.save(active);
            });

        scheme.setStatus(GradingSchemeStatus.ACTIVE);
        return gradingSchemeRepository.save(scheme);
    }

    @Transactional(readOnly = true)
    public GradingScheme getActiveScheme() {
        UUID tenantId = TenantContext.getTenantId();
        return gradingSchemeRepository.findByTenantIdAndStatus(tenantId, GradingSchemeStatus.ACTIVE)
            .flatMap(s -> gradingSchemeRepository.findWithBands(tenantId, s.getId()))
            .orElseThrow(() -> new BusinessRuleException("NO_ACTIVE_SCHEME", "No active grading scheme found for tenant"));
    }

    @Transactional(readOnly = true)
    public GradingScheme getScheme(UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        return gradingSchemeRepository.findWithBands(tenantId, id)
            .orElseThrow(() -> new ResourceNotFoundException("GradingScheme", id));
    }

    @Transactional(readOnly = true)
    public List<GradingScheme> listSchemes() {
        return gradingSchemeRepository.findByTenantIdOrderByVersionDesc(TenantContext.getTenantId());
    }
}

package com.artms.tenant.application;

import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.tenant.domain.Tenant;
import com.artms.tenant.domain.TenantRepository;
import com.artms.tenant.domain.TenantStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional
    public Tenant createTenant(CreateTenantRequest request) {
        if (tenantRepository.existsByCode(request.code())) {
            throw new BusinessRuleException("TENANT_CODE_EXISTS",
                    "A tenant with code '" + request.code() + "' already exists");
        }

        Tenant tenant = new Tenant();
        tenant.setCode(request.code().toLowerCase().strip());
        tenant.setName(request.name());
        tenant.setTimezone(request.timezone() != null ? request.timezone() : "Asia/Kathmandu");
        tenant.setLocale(request.locale() != null ? request.locale() : "en");
        return tenantRepository.save(tenant);
    }

    @Transactional(readOnly = true)
    public Tenant getById(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Tenant", id));
    }

    @Transactional(readOnly = true)
    public Tenant getByCode(String code) {
        return tenantRepository.findByCode(code)
                .orElseThrow(() -> ResourceNotFoundException.of("Tenant", code));
    }

    @Transactional
    public Tenant updateSettings(UUID tenantId, UpdateTenantRequest request) {
        Tenant tenant = getById(tenantId);
        if (request.name() != null) tenant.setName(request.name());
        if (request.timezone() != null) tenant.setTimezone(request.timezone());
        if (request.locale() != null) tenant.setLocale(request.locale());
        if (request.branding() != null) tenant.setBranding(request.branding());
        if (request.settings() != null) tenant.setSettings(request.settings());
        return tenantRepository.save(tenant);
    }

    @Transactional
    public void suspend(UUID tenantId) {
        Tenant tenant = getById(tenantId);
        if (tenant.getStatus() == TenantStatus.INACTIVE) {
            throw new BusinessRuleException("TENANT_INACTIVE", "Tenant is already inactive");
        }
        tenant.setStatus(TenantStatus.SUSPENDED);
        tenantRepository.save(tenant);
    }

    @Transactional
    public void reactivate(UUID tenantId) {
        Tenant tenant = getById(tenantId);
        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);
    }
}

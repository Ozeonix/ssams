package com.artms.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeeCategoryRepository extends JpaRepository<FeeCategory, UUID> {
    List<FeeCategory> findByTenantIdAndActiveTrue(UUID tenantId);
    Optional<FeeCategory> findByTenantIdAndCode(UUID tenantId, String code);
}

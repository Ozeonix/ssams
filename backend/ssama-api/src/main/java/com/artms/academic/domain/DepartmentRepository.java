package com.artms.academic.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByTenantId(UUID tenantId);
    List<Department> findByTenantIdAndStatusOrderByNameAsc(UUID tenantId, String status);
    Optional<Department> findByTenantIdAndId(UUID tenantId, UUID id);
    boolean existsByTenantIdAndCode(UUID tenantId, String code);
}

package com.artms.staff.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

    Page<Teacher> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<Teacher> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<Teacher> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    boolean existsByTenantIdAndEmployeeCode(UUID tenantId, String employeeCode);
}

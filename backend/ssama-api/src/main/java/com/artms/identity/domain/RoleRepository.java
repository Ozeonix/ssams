package com.artms.identity.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findByTenantIdOrTenantIdIsNull(UUID tenantId);

    List<Role> findByTenantId(UUID tenantId);

    Optional<Role> findByTenantIdAndCode(UUID tenantId, String code);

    Optional<Role> findByCodeAndTenantIdIsNull(String code);

    @Query("""
        SELECT r FROM Role r
        LEFT JOIN FETCH r.permissions
        WHERE r.id = :id AND (r.tenantId = :tenantId OR r.tenantId IS NULL)
        """)
    Optional<Role> findWithPermissions(@Param("tenantId") UUID tenantId, @Param("id") UUID id);
}

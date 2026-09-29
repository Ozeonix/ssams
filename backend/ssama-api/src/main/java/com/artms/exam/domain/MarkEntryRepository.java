package com.artms.exam.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarkEntryRepository extends JpaRepository<MarkEntry, UUID> {

    List<MarkEntry> findByTenantIdAndExamSubjectId(UUID tenantId, UUID examSubjectId);

    List<MarkEntry> findByTenantIdAndStudentId(UUID tenantId, UUID studentId);

    Optional<MarkEntry> findByTenantIdAndExamSubjectIdAndStudentIdAndComponentId(
            UUID tenantId, UUID examSubjectId, UUID studentId, UUID componentId);

    List<MarkEntry> findByTenantIdAndExamSubjectIdAndStudentId(
            UUID tenantId, UUID examSubjectId, UUID studentId);
}

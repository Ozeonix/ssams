package com.artms.academic.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TermRepository extends JpaRepository<Term, UUID> {
    List<Term> findByTenantIdAndAcademicYearIdOrderBySequenceAsc(UUID tenantId, UUID academicYearId);
    Optional<Term> findByTenantIdAndId(UUID tenantId, UUID id);
    boolean existsByAcademicYearIdAndSequence(UUID academicYearId, int sequence);
}

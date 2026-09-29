package com.artms.academic.application;

import com.artms.academic.domain.Subject;
import com.artms.academic.domain.SubjectRepository;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional
    public Subject createSubject(CreateSubjectRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        if (subjectRepository.existsByTenantIdAndCode(tenantId, req.code())) {
            throw new BusinessRuleException("DUPLICATE_CODE", "Subject code already exists: " + req.code());
        }

        Subject subject = new Subject();
        subject.setTenantId(tenantId);
        subject.setCode(req.code().toUpperCase().trim());
        subject.setName(req.name().trim());
        subject.setSubjectType(req.subjectType());
        subject.setActive(true);
        return subjectRepository.save(subject);
    }

    @Transactional(readOnly = true)
    public Page<Subject> listSubjects(Pageable pageable) {
        return subjectRepository.findByTenantId(TenantContext.getTenantId(), pageable);
    }

    @Transactional(readOnly = true)
    public Subject getSubject(UUID id) {
        return subjectRepository.findById(id)
            .filter(s -> s.getTenantId().equals(TenantContext.getTenantId()))
            .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
    }
}

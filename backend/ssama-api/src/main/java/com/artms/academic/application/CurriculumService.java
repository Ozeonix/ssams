package com.artms.academic.application;

import com.artms.academic.domain.*;
import com.artms.shared.exception.BusinessRuleException;
import com.artms.shared.exception.ResourceNotFoundException;
import com.artms.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final CurriculumSubjectRepository curriculumSubjectRepository;
    private final SubjectRepository subjectRepository;
    private final ProgramRepository programRepository;
    private final AcademicYearRepository academicYearRepository;

    @Transactional
    public Curriculum createCurriculum(CreateCurriculumRequest req) {
        UUID tenantId = TenantContext.getTenantId();

        programRepository.findById(req.programId())
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new ResourceNotFoundException("Program", req.programId()));

        academicYearRepository.findById(req.academicYearId())
            .filter(y -> y.getTenantId().equals(tenantId))
            .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", req.academicYearId()));

        int version = req.version() != null ? req.version() : 1;
        if (curriculumRepository.findByTenantIdAndProgramIdAndAcademicYearIdAndVersion(
                tenantId, req.programId(), req.academicYearId(), version).isPresent()) {
            throw new BusinessRuleException("DUPLICATE_VERSION", "Curriculum version already exists for this program and academic year");
        }

        Curriculum curriculum = new Curriculum();
        curriculum.setTenantId(tenantId);
        curriculum.setProgramId(req.programId());
        curriculum.setAcademicYearId(req.academicYearId());
        curriculum.setVersion(version);
        curriculum.setStatus(CurriculumStatus.DRAFT);
        return curriculumRepository.save(curriculum);
    }

    @Transactional
    public CurriculumSubject addSubject(UUID curriculumId, AddCurriculumSubjectRequest req) {
        UUID tenantId = TenantContext.getTenantId();
        Curriculum curriculum = getCurriculum(curriculumId);

        if (!curriculum.isDraft()) {
            throw new BusinessRuleException("CURRICULUM_NOT_DRAFT", "Cannot add subjects to a non-draft curriculum");
        }

        subjectRepository.findById(req.subjectId())
            .filter(s -> s.getTenantId().equals(tenantId))
            .orElseThrow(() -> new ResourceNotFoundException("Subject", req.subjectId()));

        CurriculumSubject cs = new CurriculumSubject();
        cs.setCurriculum(curriculum);
        cs.setSubjectId(req.subjectId());
        cs.setGradeLevel(req.gradeLevel());
        cs.setSequence(req.sequence());
        cs.setMandatory(req.mandatory());
        cs.setCreditHours(req.creditHours());
        cs.setGradingSchemeId(req.gradingSchemeId());

        if (req.components() != null && !req.components().isEmpty()) {
            BigDecimal totalWeight = BigDecimal.ZERO;
            for (AddCurriculumSubjectRequest.ComponentDto compDto : req.components()) {
                if (compDto.passMarks().compareTo(compDto.fullMarks()) > 0) {
                    throw new BusinessRuleException("INVALID_PASS_MARKS",
                        String.format("Component pass marks (%s) cannot exceed full marks (%s)",
                            compDto.passMarks(), compDto.fullMarks()));
                }
                SubjectComponent sc = new SubjectComponent();
                sc.setCurriculumSubject(cs);
                sc.setCode(compDto.code());
                sc.setName(compDto.name());
                sc.setAssessmentType(compDto.assessmentType());
                sc.setFullMarks(compDto.fullMarks());
                sc.setPassMarks(compDto.passMarks());
                sc.setWeight(compDto.weight());
                sc.setCreditHours(compDto.creditHours() != null ? compDto.creditHours() : BigDecimal.ZERO);
                sc.setSequence(compDto.sequence());
                cs.getComponents().add(sc);
                totalWeight = totalWeight.add(compDto.weight());
            }
        }

        curriculum.getSubjects().add(cs);
        return curriculumSubjectRepository.save(cs);
    }

    @Transactional
    public Curriculum publishCurriculum(UUID curriculumId) {
        Curriculum curriculum = getCurriculum(curriculumId);
        if (!curriculum.isDraft()) {
            throw new BusinessRuleException("INVALID_STATUS", "Only DRAFT curriculum can be published");
        }
        if (curriculum.getSubjects().isEmpty()) {
            throw new BusinessRuleException("EMPTY_CURRICULUM", "Cannot publish a curriculum with no subjects");
        }
        curriculum.setStatus(CurriculumStatus.PUBLISHED);
        return curriculumRepository.save(curriculum);
    }

    @Transactional(readOnly = true)
    public Curriculum getCurriculum(UUID id) {
        return curriculumRepository.findById(id)
            .filter(c -> c.getTenantId().equals(TenantContext.getTenantId()))
            .orElseThrow(() -> new ResourceNotFoundException("Curriculum", id));
    }

    @Transactional(readOnly = true)
    public Page<Curriculum> listCurricula(Pageable pageable) {
        return curriculumRepository.findByTenantId(TenantContext.getTenantId(), pageable);
    }
}

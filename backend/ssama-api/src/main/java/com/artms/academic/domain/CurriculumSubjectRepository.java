package com.artms.academic.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CurriculumSubjectRepository extends JpaRepository<CurriculumSubject, UUID> {

    List<CurriculumSubject> findByCurriculumId(UUID curriculumId);
}

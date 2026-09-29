package com.artms.exam.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamSubjectRepository extends JpaRepository<ExamSubject, UUID> {

    List<ExamSubject> findByExamIdAndStatus(UUID examId, ExamSubjectStatus status);

    Optional<ExamSubject> findByExamIdAndCurriculumSubjectId(UUID examId, UUID curriculumSubjectId);
}

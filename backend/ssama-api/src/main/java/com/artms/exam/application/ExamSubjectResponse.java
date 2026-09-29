package com.artms.exam.application;

import com.artms.exam.domain.ExamSubject;
import com.artms.exam.domain.ExamSubjectStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ExamSubjectResponse(
    UUID id,
    UUID examId,
    UUID curriculumSubjectId,
    ExamSubjectStatus status,
    OffsetDateTime createdAt
) {
    public static ExamSubjectResponse from(ExamSubject es) {
        return new ExamSubjectResponse(
            es.getId(),
            es.getExam() != null ? es.getExam().getId() : null,
            es.getCurriculumSubjectId(),
            es.getStatus(),
            es.getCreatedAt()
        );
    }
}

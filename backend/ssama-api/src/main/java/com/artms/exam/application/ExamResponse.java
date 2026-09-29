package com.artms.exam.application;

import com.artms.exam.domain.Exam;
import com.artms.exam.domain.ExamStatus;
import com.artms.exam.domain.ExamType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ExamResponse(
    UUID id,
    UUID tenantId,
    UUID academicYearId,
    UUID termId,
    String name,
    ExamType examType,
    ExamStatus status,
    UUID gradingSchemeId,
    UUID classGroupId,
    LocalDate startDate,
    LocalDate endDate,
    UUID createdBy,
    int version,
    List<ExamSubjectResponse> subjects,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static ExamResponse from(Exam exam) {
        List<ExamSubjectResponse> subjects = exam.getExamSubjects() == null ? List.of() :
            exam.getExamSubjects().stream().map(ExamSubjectResponse::from).toList();

        return new ExamResponse(
            exam.getId(),
            exam.getTenantId(),
            exam.getAcademicYearId(),
            exam.getTermId(),
            exam.getName(),
            exam.getExamType(),
            exam.getStatus(),
            exam.getGradingSchemeId(),
            exam.getClassGroupId(),
            exam.getStartDate(),
            exam.getEndDate(),
            exam.getCreatedBy(),
            exam.getVersion(),
            subjects,
            exam.getCreatedAt(),
            exam.getUpdatedAt()
        );
    }
}

package com.artms.academic.application;

import com.artms.academic.domain.AssessmentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AddCurriculumSubjectRequest(
    @NotNull UUID subjectId,
    String gradeLevel,
    int sequence,
    boolean mandatory,
    @NotNull @DecimalMin("0.0") BigDecimal creditHours,
    UUID gradingSchemeId,
    List<ComponentDto> components
) {
    public record ComponentDto(
        @NotBlank String code,
        @NotBlank String name,
        @NotNull AssessmentType assessmentType,
        @NotNull @DecimalMin("0.01") BigDecimal fullMarks,
        @NotNull @DecimalMin("0.00") BigDecimal passMarks,
        @NotNull @DecimalMin("0.01") BigDecimal weight,
        BigDecimal creditHours,
        int sequence
    ) {}
}

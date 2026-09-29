package com.artms.exam.application;

import com.artms.exam.domain.ExamType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateExamRequest(
    @NotNull UUID academicYearId,
    UUID termId,
    @NotBlank String name,
    @NotNull ExamType examType,
    @NotNull UUID gradingSchemeId,
    UUID classGroupId,
    LocalDate startDate,
    LocalDate endDate
) {}

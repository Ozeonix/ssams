package com.artms.staff.application;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignTeacherSubjectRequest(
    @NotNull UUID curriculumSubjectId,
    @NotNull UUID academicYearId
) {}

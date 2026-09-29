package com.artms.exam.application;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddExamSubjectRequest(
    @NotNull UUID curriculumSubjectId
) {}

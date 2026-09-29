package com.artms.exam.application;

import com.artms.exam.domain.ExamStatus;
import jakarta.validation.constraints.NotNull;

public record ExamWorkflowRequest(
    @NotNull ExamStatus targetStatus,
    String remarks
) {}

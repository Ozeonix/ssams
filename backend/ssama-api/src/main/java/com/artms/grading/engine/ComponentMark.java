package com.artms.grading.engine;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Input record for a single component mark in the grading engine.
 */
public record ComponentMark(
    UUID componentId,
    UUID subjectId,
    BigDecimal rawMarks,   // null if status is not PRESENT
    BigDecimal fullMarks,
    BigDecimal passMarks,
    BigDecimal weight,     // percentage weight within subject
    BigDecimal creditHours,
    String assessmentType,
    MarkStatus status
) {}

package com.artms.grading.engine;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Result for a single subject from the grading engine.
 */
public record SubjectResult(
    UUID subjectId,
    BigDecimal totalFullMarks,
    BigDecimal totalObtained,
    BigDecimal percentage,        // null if not gradeable
    String letterGrade,           // null if status overrides
    BigDecimal gradePoint,
    BigDecimal creditHours,
    boolean passed,
    SubjectResultStatus status,   // GRADED, ABSENT, WITHHELD, EXPELLED, NOT_GRADED
    List<ComponentResult> componentResults
) {}

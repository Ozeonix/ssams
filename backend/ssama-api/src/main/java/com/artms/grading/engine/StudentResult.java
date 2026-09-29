package com.artms.grading.engine;

import java.math.BigDecimal;
import java.util.List;

/**
 * Final result output from the grading engine for one student in one exam.
 */
public record StudentResult(
    BigDecimal totalCreditHours,
    BigDecimal earnedPoints,
    BigDecimal gpa,
    OverallResultStatus overallStatus,
    List<SubjectResult> subjectResults
) {}

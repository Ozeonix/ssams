package com.artms.grading.engine;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Per-component calculation result.
 */
public record ComponentResult(
    UUID componentId,
    BigDecimal rawMarks,
    BigDecimal fullMarks,
    BigDecimal percentage,
    boolean componentPassed,
    MarkStatus status
) {}

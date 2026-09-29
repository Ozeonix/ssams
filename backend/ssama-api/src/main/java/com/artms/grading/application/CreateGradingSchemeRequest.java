package com.artms.grading.application;

import com.artms.grading.domain.RoundingMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateGradingSchemeRequest(
    @NotBlank String name,
    LocalDate effectiveFrom,
    Integer gpaPrecision,
    RoundingMode gpaRounding,
    @NotEmpty List<GradeBandDto> bands
) {
    public record GradeBandDto(
        @NotNull @DecimalMin("0.0") BigDecimal minPercentage,
        @NotNull @DecimalMin("0.0") BigDecimal maxPercentage,
        @NotBlank String letterGrade,
        @NotNull @DecimalMin("0.0") BigDecimal gradePoint,
        boolean passFlag,
        String remarks
    ) {}
}

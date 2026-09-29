package com.artms.grading.domain;

import com.artms.shared.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Validates grading scheme configurations.
 * Enforces:
 *   - No overlapping percentage bands
 *   - minPercentage <= maxPercentage for each band
 *   - Range is within 0.000 to 100.000
 *   - Letter grade and grade point are present
 */
public final class GradingSchemeValidator {

    private GradingSchemeValidator() {}

    public static void validate(GradingScheme scheme) {
        if (scheme == null) {
            throw new ValidationException("Grading scheme cannot be null");
        }
        List<GradeBand> bands = scheme.getBands();
        if (bands == null || bands.isEmpty()) {
            throw new ValidationException("Grading scheme must contain at least one grade band");
        }

        // Sort bands by minPercentage ascending
        List<GradeBand> sortedBands = bands.stream()
            .sorted(Comparator.comparing(GradeBand::getMinPercentage))
            .toList();

        GradeBand previous = null;
        for (GradeBand band : sortedBands) {
            validateSingleBand(band);

            if (previous != null) {
                // Ensure no overlap between adjacent bands
                if (band.getMinPercentage().compareTo(previous.getMaxPercentage()) < 0) {
                    throw new ValidationException(String.format(
                        "Overlapping grade bands: [%s - %s] and [%s - %s]",
                        previous.getMinPercentage(), previous.getMaxPercentage(),
                        band.getMinPercentage(), band.getMaxPercentage()
                    ));
                }
            }
            previous = band;
        }
    }

    private static void validateSingleBand(GradeBand band) {
        if (band.getMinPercentage() == null || band.getMaxPercentage() == null) {
            throw new ValidationException("Band percentages cannot be null");
        }
        if (band.getMinPercentage().compareTo(BigDecimal.ZERO) < 0 ||
            band.getMaxPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ValidationException("Band percentages must be between 0 and 100");
        }
        if (band.getMinPercentage().compareTo(band.getMaxPercentage()) > 0) {
            throw new ValidationException(String.format(
                "minPercentage (%s) cannot exceed maxPercentage (%s)",
                band.getMinPercentage(), band.getMaxPercentage()
            ));
        }
        if (band.getLetterGrade() == null || band.getLetterGrade().isBlank()) {
            throw new ValidationException("Band must have a letter grade");
        }
        if (band.getGradePoint() == null || band.getGradePoint().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Band grade point cannot be null or negative");
        }
    }
}

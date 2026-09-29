package com.artms.grading.engine;

import com.artms.grading.domain.GradeBand;
import com.artms.grading.domain.GradingScheme;
import com.artms.grading.domain.RoundingMode;
import com.artms.shared.exception.ValidationException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

/**
 * Pure, deterministic calculation engine for academic results.
 * Implements specifications in docs/GRADING_ENGINE.md.
 */
public final class GradingEngine {

    private GradingEngine() {}

    /**
     * Calculates the student's result snapshot given component marks and a grading scheme.
     */
    public static StudentResult calculate(List<ComponentMark> marks, GradingScheme scheme) {
        if (scheme == null) {
            throw new ValidationException("Grading scheme cannot be null for calculation");
        }
        if (marks == null || marks.isEmpty()) {
            return new StudentResult(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                OverallResultStatus.INCOMPLETE,
                Collections.emptyList()
            );
        }

        // Group component marks by subject ID
        Map<UUID, List<ComponentMark>> bySubject = new LinkedHashMap<>();
        for (ComponentMark mark : marks) {
            bySubject.computeIfAbsent(mark.subjectId(), k -> new ArrayList<>()).add(mark);
        }

        List<SubjectResult> subjectResults = new ArrayList<>();
        BigDecimal totalCreditHours = BigDecimal.ZERO;
        BigDecimal totalEarnedPoints = BigDecimal.ZERO;
        boolean hasExpelled = false;
        boolean hasWithheld = false;
        boolean hasAbsent = false;
        boolean hasNotGraded = false;
        boolean allPassed = true;

        for (Map.Entry<UUID, List<ComponentMark>> entry : bySubject.entrySet()) {
            UUID subjectId = entry.getKey();
            List<ComponentMark> subjectMarks = entry.getValue();

            SubjectResult subResult = calculateSubject(subjectId, subjectMarks, scheme);
            subjectResults.add(subResult);

            if (subResult.status() == SubjectResultStatus.EXPELLED) {
                hasExpelled = true;
                allPassed = false;
            } else if (subResult.status() == SubjectResultStatus.WITHHELD) {
                hasWithheld = true;
                allPassed = false;
            } else if (subResult.status() == SubjectResultStatus.ABSENT) {
                hasAbsent = true;
                allPassed = false;
            } else if (subResult.status() == SubjectResultStatus.NOT_GRADED) {
                hasNotGraded = true;
                allPassed = false;
            } else if (!subResult.passed()) {
                allPassed = false;
            }

            BigDecimal credit = subResult.creditHours();
            if (credit != null && credit.compareTo(BigDecimal.ZERO) > 0 && subResult.gradePoint() != null) {
                totalCreditHours = totalCreditHours.add(credit);
                totalEarnedPoints = totalEarnedPoints.add(credit.multiply(subResult.gradePoint()));
            }
        }

        // Calculate GPA
        BigDecimal gpa = BigDecimal.ZERO;
        if (totalCreditHours.compareTo(BigDecimal.ZERO) > 0) {
            java.math.RoundingMode javaMode = toJavaRoundingMode(scheme.getGpaRounding());
            gpa = totalEarnedPoints.divide(totalCreditHours, scheme.getGpaPrecision(), javaMode);
        }

        // Determine overall status
        OverallResultStatus overallStatus;
        if (hasExpelled) {
            overallStatus = OverallResultStatus.EXPELLED;
        } else if (hasWithheld) {
            overallStatus = OverallResultStatus.WITHHELD;
        } else if (hasAbsent) {
            overallStatus = OverallResultStatus.ABSENT;
        } else if (hasNotGraded) {
            overallStatus = OverallResultStatus.INCOMPLETE;
        } else if (allPassed) {
            overallStatus = OverallResultStatus.PASS;
        } else {
            overallStatus = OverallResultStatus.FAIL;
        }

        return new StudentResult(
            totalCreditHours,
            totalEarnedPoints.setScale(scheme.getGpaPrecision(), toJavaRoundingMode(scheme.getGpaRounding())),
            gpa,
            overallStatus,
            subjectResults
        );
    }

    private static SubjectResult calculateSubject(UUID subjectId, List<ComponentMark> marks, GradingScheme scheme) {
        BigDecimal subjectCredit = BigDecimal.ZERO;
        for (ComponentMark m : marks) {
            if (m.creditHours() != null && m.creditHours().compareTo(subjectCredit) > 0) {
                subjectCredit = m.creditHours();
            }
        }

        // Check for special non-present statuses
        for (ComponentMark m : marks) {
            if (m.status() == MarkStatus.EXPELLED) {
                return buildSpecialSubjectResult(subjectId, marks, subjectCredit, SubjectResultStatus.EXPELLED);
            }
            if (m.status() == MarkStatus.WITHHELD) {
                return buildSpecialSubjectResult(subjectId, marks, subjectCredit, SubjectResultStatus.WITHHELD);
            }
            if (m.status() == MarkStatus.ABSENT) {
                return buildSpecialSubjectResult(subjectId, marks, subjectCredit, SubjectResultStatus.ABSENT);
            }
            if (m.status() == MarkStatus.MISSING) {
                return buildSpecialSubjectResult(subjectId, marks, subjectCredit, SubjectResultStatus.NOT_GRADED);
            }
        }

        // Process PRESENT components
        List<ComponentResult> compResults = new ArrayList<>();
        BigDecimal totalFullMarks = BigDecimal.ZERO;
        BigDecimal totalObtained = BigDecimal.ZERO;
        boolean componentsPassed = true;

        for (ComponentMark m : marks) {
            if (m.status() == MarkStatus.NOT_APPLICABLE) {
                continue;
            }
            BigDecimal raw = m.rawMarks() != null ? m.rawMarks() : BigDecimal.ZERO;
            BigDecimal full = m.fullMarks() != null ? m.fullMarks() : BigDecimal.ZERO;
            BigDecimal pass = m.passMarks() != null ? m.passMarks() : BigDecimal.ZERO;

            boolean compPass = raw.compareTo(pass) >= 0;
            if (!compPass) {
                componentsPassed = false;
            }

            BigDecimal compPct = BigDecimal.ZERO;
            if (full.compareTo(BigDecimal.ZERO) > 0) {
                compPct = raw.multiply(BigDecimal.valueOf(100)).divide(full, 3, java.math.RoundingMode.HALF_UP);
            }

            compResults.add(new ComponentResult(
                m.componentId(),
                raw,
                full,
                compPct,
                compPass,
                m.status()
            ));

            totalFullMarks = totalFullMarks.add(full);
            totalObtained = totalObtained.add(raw);
        }

        BigDecimal subjectPercentage = BigDecimal.ZERO;
        if (totalFullMarks.compareTo(BigDecimal.ZERO) > 0) {
            subjectPercentage = totalObtained.multiply(BigDecimal.valueOf(100))
                .divide(totalFullMarks, 3, java.math.RoundingMode.HALF_UP);
        }

        // Match grade band
        GradeBand matchedBand = findMatchingBand(scheme.getBands(), subjectPercentage);

        String letterGrade = matchedBand != null ? matchedBand.getLetterGrade() : "F";
        BigDecimal gradePoint = matchedBand != null ? matchedBand.getGradePoint() : BigDecimal.ZERO;
        boolean bandPassed = matchedBand != null && matchedBand.isPassFlag();

        boolean finalPassed = componentsPassed && bandPassed;

        return new SubjectResult(
            subjectId,
            totalFullMarks,
            totalObtained,
            subjectPercentage,
            letterGrade,
            gradePoint,
            subjectCredit,
            finalPassed,
            SubjectResultStatus.GRADED,
            compResults
        );
    }

    private static GradeBand findMatchingBand(List<GradeBand> bands, BigDecimal percentage) {
        if (bands == null) return null;
        for (GradeBand band : bands) {
            if (band.contains(percentage)) {
                return band;
            }
        }
        return null;
    }

    private static SubjectResult buildSpecialSubjectResult(
        UUID subjectId,
        List<ComponentMark> marks,
        BigDecimal credit,
        SubjectResultStatus status
    ) {
        BigDecimal totalFull = BigDecimal.ZERO;
        List<ComponentResult> compResults = new ArrayList<>();
        for (ComponentMark m : marks) {
            BigDecimal full = m.fullMarks() != null ? m.fullMarks() : BigDecimal.ZERO;
            totalFull = totalFull.add(full);
            compResults.add(new ComponentResult(
                m.componentId(),
                BigDecimal.ZERO,
                full,
                BigDecimal.ZERO,
                false,
                m.status()
            ));
        }

        return new SubjectResult(
            subjectId,
            totalFull,
            BigDecimal.ZERO,
            null,
            null,
            BigDecimal.ZERO,
            credit,
            false,
            status,
            compResults
        );
    }

    private static java.math.RoundingMode toJavaRoundingMode(RoundingMode mode) {
        if (mode == null) return java.math.RoundingMode.HALF_UP;
        return switch (mode) {
            case HALF_UP -> java.math.RoundingMode.HALF_UP;
            case FLOOR -> java.math.RoundingMode.FLOOR;
            case CEILING -> java.math.RoundingMode.CEILING;
        };
    }
}

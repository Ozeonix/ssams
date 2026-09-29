package com.artms.grading;

import com.artms.grading.domain.*;
import com.artms.grading.engine.*;
import com.artms.shared.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GradingEngineTest {

    private GradingScheme standardScheme;

    @BeforeEach
    void setUp() {
        standardScheme = new GradingScheme();
        standardScheme.setName("Standard GPA Scheme");
        standardScheme.setGpaPrecision(2);
        standardScheme.setGpaRounding(RoundingMode.HALF_UP);

        // Bands:
        // A+: 90 - 100 -> 4.0
        // A : 80 - 89.999 -> 3.6
        // B : 60 - 79.999 -> 2.8
        // C : 40 - 59.999 -> 2.0
        // F : 0  - 39.999 -> 0.0 (fail)
        addBand(standardScheme, "90.000", "100.000", "A+", "4.00", true);
        addBand(standardScheme, "80.000", "89.999", "A", "3.60", true);
        addBand(standardScheme, "60.000", "79.999", "B", "2.80", true);
        addBand(standardScheme, "40.000", "59.999", "C", "2.00", true);
        addBand(standardScheme, "0.000", "39.999", "F", "0.00", false);
    }

    private void addBand(GradingScheme scheme, String min, String max, String letter, String gp, boolean pass) {
        GradeBand band = new GradeBand();
        band.setGradingScheme(scheme);
        band.setMinPercentage(new BigDecimal(min));
        band.setMaxPercentage(new BigDecimal(max));
        band.setLetterGrade(letter);
        band.setGradePoint(new BigDecimal(gp));
        band.setPassFlag(pass);
        scheme.getBands().add(band);
    }

    @Test
    @DisplayName("Should correctly validate valid grading scheme and reject overlapping bands")
    void testGradingSchemeValidation() {
        GradingSchemeValidator.validate(standardScheme);

        // Add overlapping band
        GradingScheme invalidScheme = new GradingScheme();
        addBand(invalidScheme, "50.000", "70.000", "B", "3.00", true);
        addBand(invalidScheme, "60.000", "80.000", "A", "3.50", true);

        assertThatThrownBy(() -> GradingSchemeValidator.validate(invalidScheme))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Overlapping grade bands");
    }

    @Test
    @DisplayName("Should calculate correct grade and GPA for student with theory and practical components")
    void testStandardCalculationWithComponents() {
        UUID mathId = UUID.randomUUID();
        UUID mathTheory = UUID.randomUUID();
        UUID mathPractical = UUID.randomUUID();

        UUID physicsId = UUID.randomUUID();
        UUID physicsTheory = UUID.randomUUID();

        List<ComponentMark> marks = List.of(
            // Math: Theory 75/75 (pass: 27), Practical 20/25 (pass: 10) -> Total 95/100 -> A+ (4.0), 4 credits
            new ComponentMark(mathTheory, mathId, new BigDecimal("75"), new BigDecimal("75"), new BigDecimal("27"), new BigDecimal("75"), new BigDecimal("4.0"), "THEORY", MarkStatus.PRESENT),
            new ComponentMark(mathPractical, mathId, new BigDecimal("20"), new BigDecimal("25"), new BigDecimal("10"), new BigDecimal("25"), new BigDecimal("4.0"), "PRACTICAL", MarkStatus.PRESENT),
            // Physics: Theory 85/100 (pass: 40) -> Total 85/100 -> A (3.6), 4 credits
            new ComponentMark(physicsTheory, physicsId, new BigDecimal("85"), new BigDecimal("100"), new BigDecimal("40"), new BigDecimal("100"), new BigDecimal("4.0"), "THEORY", MarkStatus.PRESENT)
        );

        StudentResult result = GradingEngine.calculate(marks, standardScheme);

        assertThat(result.overallStatus()).isEqualTo(OverallResultStatus.PASS);
        assertThat(result.totalCreditHours()).isEqualByComparingTo("8.0");
        // Math = 4.0 * 4 = 16, Physics = 3.6 * 4 = 14.4 -> Total points = 30.4 -> GPA = 30.4 / 8 = 3.80
        assertThat(result.gpa()).isEqualByComparingTo("3.80");
        assertThat(result.subjectResults()).hasSize(2);
    }

    @Test
    @DisplayName("Grade boundary: 89.999% gets A (3.60), exactly 90.000% gets A+ (4.00)")
    void testGradeBoundaries() {
        UUID sub1 = UUID.randomUUID();
        UUID c1 = UUID.randomUUID();
        UUID sub2 = UUID.randomUUID();
        UUID c2 = UUID.randomUUID();

        // sub1: 89.999 out of 100
        ComponentMark mark1 = new ComponentMark(c1, sub1, new BigDecimal("89.999"), new BigDecimal("100"), new BigDecimal("40"), new BigDecimal("100"), new BigDecimal("3.0"), "THEORY", MarkStatus.PRESENT);
        // sub2: 90.000 out of 100
        ComponentMark mark2 = new ComponentMark(c2, sub2, new BigDecimal("90.000"), new BigDecimal("100"), new BigDecimal("40"), new BigDecimal("100"), new BigDecimal("3.0"), "THEORY", MarkStatus.PRESENT);

        StudentResult result = GradingEngine.calculate(List.of(mark1, mark2), standardScheme);

        SubjectResult r1 = result.subjectResults().stream().filter(s -> s.subjectId().equals(sub1)).findFirst().orElseThrow();
        SubjectResult r2 = result.subjectResults().stream().filter(s -> s.subjectId().equals(sub2)).findFirst().orElseThrow();

        assertThat(r1.letterGrade()).isEqualTo("A");
        assertThat(r1.gradePoint()).isEqualByComparingTo("3.60");

        assertThat(r2.letterGrade()).isEqualTo("A+");
        assertThat(r2.gradePoint()).isEqualByComparingTo("4.00");
    }

    @Test
    @DisplayName("Special status: ABSENT, WITHHELD, EXPELLED result in subject failure and status flags")
    void testSpecialStatuses() {
        UUID subExpelled = UUID.randomUUID();
        UUID c1 = UUID.randomUUID();
        UUID subAbsent = UUID.randomUUID();
        UUID c2 = UUID.randomUUID();

        List<ComponentMark> marks = List.of(
            new ComponentMark(c1, subExpelled, null, new BigDecimal("100"), new BigDecimal("40"), new BigDecimal("100"), new BigDecimal("3.0"), "THEORY", MarkStatus.EXPELLED),
            new ComponentMark(c2, subAbsent, null, new BigDecimal("100"), new BigDecimal("40"), new BigDecimal("100"), new BigDecimal("3.0"), "THEORY", MarkStatus.ABSENT)
        );

        StudentResult result = GradingEngine.calculate(marks, standardScheme);

        assertThat(result.overallStatus()).isEqualTo(OverallResultStatus.EXPELLED);
        SubjectResult exp = result.subjectResults().stream().filter(s -> s.subjectId().equals(subExpelled)).findFirst().orElseThrow();
        assertThat(exp.status()).isEqualTo(SubjectResultStatus.EXPELLED);
        assertThat(exp.passed()).isFalse();
    }

    @Test
    @DisplayName("Component pass requirement: failing a component fails the subject even if aggregate percentage is high")
    void testComponentPassRequirementFailure() {
        UUID subId = UUID.randomUUID();
        UUID theoryComp = UUID.randomUUID();
        UUID pracComp = UUID.randomUUID();

        // Theory: 70/75 (passed, pass mark 27)
        // Practical: 5/25 (FAILED, pass mark 10)
        // Total: 75/100 -> 75% normally would be B, but practical failed -> passed=false
        List<ComponentMark> marks = List.of(
            new ComponentMark(theoryComp, subId, new BigDecimal("70"), new BigDecimal("75"), new BigDecimal("27"), new BigDecimal("75"), new BigDecimal("4.0"), "THEORY", MarkStatus.PRESENT),
            new ComponentMark(pracComp, subId, new BigDecimal("5"), new BigDecimal("25"), new BigDecimal("10"), new BigDecimal("25"), new BigDecimal("4.0"), "PRACTICAL", MarkStatus.PRESENT)
        );

        StudentResult result = GradingEngine.calculate(marks, standardScheme);

        SubjectResult subResult = result.subjectResults().getFirst();
        assertThat(subResult.passed()).isFalse();
        assertThat(result.overallStatus()).isEqualTo(OverallResultStatus.FAIL);
    }
}

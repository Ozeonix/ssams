package com.artms.grading.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * GradeBand — defines a percentage range mapped to a letter and grade point.
 * Invariants enforced by GradingSchemeValidator:
 *   - no overlaps between bands in the same scheme
 *   - min <= max
 *   - percentage 0-100
 */
@Entity
@Table(name = "grade_band")
@Getter
@Setter
@NoArgsConstructor
public class GradeBand {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grading_scheme_id", nullable = false)
    private GradingScheme gradingScheme;

    @Column(name = "min_percentage", nullable = false, precision = 6, scale = 3)
    private BigDecimal minPercentage;

    @Column(name = "max_percentage", nullable = false, precision = 6, scale = 3)
    private BigDecimal maxPercentage;

    @Column(name = "letter_grade", nullable = false, length = 8)
    private String letterGrade;

    @Column(name = "grade_point", nullable = false, precision = 5, scale = 2)
    private BigDecimal gradePoint;

    @Column(name = "pass_flag", nullable = false)
    private boolean passFlag = true;

    @Column(name = "remarks", length = 64)
    private String remarks;

    /**
     * Returns true if the given percentage falls within this band (inclusive).
     */
    public boolean contains(BigDecimal percentage) {
        return percentage.compareTo(minPercentage) >= 0
            && percentage.compareTo(maxPercentage) <= 0;
    }
}

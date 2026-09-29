package com.artms.academic.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * SubjectComponent — a distinct assessable part of a subject.
 * e.g. Theory (TH), Practical (PR), Internal (INT), Project (PROJ).
 *
 * INVARIANT: pass_marks <= full_marks, weight 0 < w <= 100.
 */
@Entity
@Table(name = "subject_component")
@Getter
@Setter
@NoArgsConstructor
public class SubjectComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_subject_id", nullable = false)
    private CurriculumSubject curriculumSubject;

    @Column(name = "code", nullable = false, length = 32)
    private String code;   // e.g. TH, PR, INT

    @Column(name = "name", nullable = false, length = 128)
    private String name;   // e.g. Theory, Practical

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type", nullable = false, length = 32)
    private AssessmentType assessmentType;

    @Column(name = "full_marks", nullable = false, precision = 6, scale = 2)
    private BigDecimal fullMarks;

    @Column(name = "pass_marks", nullable = false, precision = 6, scale = 2)
    private BigDecimal passMarks;

    /** Percentage weight this component contributes to subject total */
    @Column(name = "weight", nullable = false, precision = 5, scale = 2)
    private BigDecimal weight = new BigDecimal("100");

    @Column(name = "credit_hours", nullable = false, precision = 4, scale = 1)
    private BigDecimal creditHours = BigDecimal.ZERO;

    @Column(name = "sequence", nullable = false)
    private int sequence = 1;
}

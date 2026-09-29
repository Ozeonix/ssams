package com.artms.academic.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CurriculumSubject — a subject as it appears in a versioned curriculum.
 * Carries credit hours, mandatory flag, and grade-level targeting.
 */
@Entity
@Table(name = "curriculum_subject",
    uniqueConstraints = @UniqueConstraint(columnNames = {"curriculum_id", "subject_id", "grade_level"}))
@Getter
@Setter
@NoArgsConstructor
public class CurriculumSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false)
    private Curriculum curriculum;

    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;

    @Column(name = "grade_level", length = 64)
    private String gradeLevel;

    @Column(name = "sequence", nullable = false)
    private int sequence = 1;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory = true;

    @Column(name = "credit_hours", nullable = false, precision = 4, scale = 1)
    private BigDecimal creditHours = BigDecimal.ZERO;

    @Column(name = "grading_scheme_id")
    private UUID gradingSchemeId;

    @OneToMany(mappedBy = "curriculumSubject", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sequence ASC")
    private List<SubjectComponent> components = new ArrayList<>();
}

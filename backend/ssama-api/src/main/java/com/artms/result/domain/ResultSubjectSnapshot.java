package com.artms.result.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "result_subject_snapshot",
    uniqueConstraints = @UniqueConstraint(columnNames = {"result_snapshot_id", "subject_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ResultSubjectSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_snapshot_id", nullable = false)
    private ResultSnapshot resultSnapshot;

    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;

    @Column(name = "subject_name_snapshot", nullable = false)
    private String subjectNameSnapshot;

    @Column(name = "credit_hours", nullable = false, precision = 4, scale = 1)
    private BigDecimal creditHours = BigDecimal.ZERO;

    @Column(name = "theory_full_marks", precision = 6, scale = 2)
    private BigDecimal theoryFullMarks;

    @Column(name = "theory_obtained", precision = 6, scale = 2)
    private BigDecimal theoryObtained;

    @Column(name = "practical_full_marks", precision = 6, scale = 2)
    private BigDecimal practicalFullMarks;

    @Column(name = "practical_obtained", precision = 6, scale = 2)
    private BigDecimal practicalObtained;

    @Column(name = "total_full_marks", nullable = false, precision = 6, scale = 2)
    private BigDecimal totalFullMarks;

    @Column(name = "total_obtained", nullable = false, precision = 6, scale = 2)
    private BigDecimal totalObtained;

    @Column(name = "percentage", precision = 6, scale = 3)
    private BigDecimal percentage;

    @Column(name = "letter_grade", length = 8)
    private String letterGrade;

    @Column(name = "grade_point", precision = 5, scale = 2)
    private BigDecimal gradePoint;

    @Column(name = "final_grade", length = 32)
    private String finalGrade;

    @Column(name = "remarks", length = 256)
    private String remarks;
}

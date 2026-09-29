package com.artms.result.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "result_snapshot",
    uniqueConstraints = @UniqueConstraint(columnNames = {"exam_id", "student_id", "result_version"}))
@Getter
@Setter
@NoArgsConstructor
public class ResultSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "exam_id", nullable = false)
    private UUID examId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "result_version", nullable = false)
    private int resultVersion = 1;

    @Column(name = "total_credits", nullable = false, precision = 6, scale = 2)
    private BigDecimal totalCredits = BigDecimal.ZERO;

    @Column(name = "earned_points", nullable = false, precision = 8, scale = 4)
    private BigDecimal earnedPoints = BigDecimal.ZERO;

    @Column(name = "gpa", precision = 5, scale = 4)
    private BigDecimal gpa;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_status", nullable = false, length = 32)
    private ResultStatus resultStatus;

    @Column(name = "grading_scheme_id", nullable = false)
    private UUID gradingSchemeId;

    @Column(name = "grading_scheme_version", nullable = false)
    private int gradingSchemeVersion;

    @Column(name = "published_by")
    private UUID publishedBy;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "immutable_hash", length = 64)
    private String immutableHash;

    @Column(name = "correction_reason", columnDefinition = "text")
    private String correctionReason;

    @Column(name = "previous_version_id")
    private UUID previousVersionId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "resultSnapshot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ResultSubjectSnapshot> subjectSnapshots = new ArrayList<>();
}

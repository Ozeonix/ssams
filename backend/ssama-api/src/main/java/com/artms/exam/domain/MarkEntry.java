package com.artms.exam.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "mark_entry",
    uniqueConstraints = @UniqueConstraint(columnNames = {"exam_subject_id", "student_id", "component_id"}))
@Getter
@Setter
@NoArgsConstructor
public class MarkEntry extends TenantBaseEntity {

    @Column(name = "exam_subject_id", nullable = false)
    private UUID examSubjectId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "component_id", nullable = false)
    private UUID componentId;

    @Column(name = "raw_marks", precision = 6, scale = 2)
    private BigDecimal rawMarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private MarkStatus status = MarkStatus.DRAFT;

    @Column(name = "entered_by")
    private UUID enteredBy;

    @Column(name = "submitted_by")
    private UUID submittedBy;

    @Column(name = "verified_by")
    private UUID verifiedBy;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "verified_at")
    private OffsetDateTime verifiedAt;

    @Column(name = "remarks", columnDefinition = "text")
    private String remarks;

    @Version
    @Column(name = "version", nullable = false)
    private int version = 0;
}

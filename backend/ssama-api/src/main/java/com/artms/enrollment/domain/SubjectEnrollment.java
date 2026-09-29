package com.artms.enrollment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "subject_enrollment",
    uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "curriculum_subject_id"}))
@Getter
@Setter
@NoArgsConstructor
public class SubjectEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(name = "curriculum_subject_id", nullable = false)
    private UUID curriculumSubjectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private SubjectEnrollmentStatus status = SubjectEnrollmentStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}

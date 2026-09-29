package com.artms.enrollment.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "enrollment",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "student_id", "academic_year_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Enrollment extends TenantBaseEntity {

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "class_group_id", nullable = false)
    private UUID classGroupId;

    @Column(name = "academic_year_id", nullable = false)
    private UUID academicYearId;

    @Column(name = "roll_no", length = 32)
    private String rollNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDate enrolledAt = LocalDate.now();

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubjectEnrollment> subjectEnrollments = new ArrayList<>();
}

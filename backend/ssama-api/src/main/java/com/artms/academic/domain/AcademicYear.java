package com.artms.academic.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Academic Year — the top-level time boundary for academic activities.
 * Status transitions: PLANNING -> ACTIVE -> COMPLETED -> ARCHIVED
 */
@Entity
@Table(name = "academic_year",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
public class AcademicYear extends TenantBaseEntity {

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AcademicYearStatus status = AcademicYearStatus.PLANNING;

    public boolean isActive() {
        return AcademicYearStatus.ACTIVE == status;
    }
}

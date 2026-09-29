package com.artms.grading.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * GradingScheme — versioned configuration for grade calculation.
 * When a scheme is changed a new version is created;
 * published results always reference the exact version used at time of calculation.
 *
 * INVARIANT: only one ACTIVE scheme per tenant at a time.
 */
@Entity
@Table(name = "grading_scheme",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "name", "version"}))
@Getter
@Setter
@NoArgsConstructor
public class GradingScheme extends TenantBaseEntity {

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "version", nullable = false)
    private int version = 1;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private GradingSchemeStatus status = GradingSchemeStatus.DRAFT;

    @Column(name = "gpa_precision", nullable = false)
    private int gpaPrecision = 2;

    @Enumerated(EnumType.STRING)
    @Column(name = "gpa_rounding", nullable = false, length = 32)
    private RoundingMode gpaRounding = RoundingMode.HALF_UP;

    @OneToMany(mappedBy = "gradingScheme", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("minPercentage ASC")
    private List<GradeBand> bands = new ArrayList<>();

    public boolean isActive() {
        return GradingSchemeStatus.ACTIVE == status;
    }

    public boolean isDraft() {
        return GradingSchemeStatus.DRAFT == status;
    }
}

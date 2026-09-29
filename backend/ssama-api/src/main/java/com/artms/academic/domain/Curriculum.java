package com.artms.academic.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Curriculum — versioned configuration of subjects for a program in an academic year.
 * Status: DRAFT -> PUBLISHED -> ARCHIVED.
 * Only PUBLISHED curricula can be used for enrollment.
 */
@Entity
@Table(name = "curriculum",
    uniqueConstraints = @UniqueConstraint(columnNames = {"program_id", "academic_year_id", "version"}))
@Getter
@Setter
@NoArgsConstructor
public class Curriculum extends TenantBaseEntity {

    @Column(name = "program_id", nullable = false)
    private UUID programId;

    @Column(name = "academic_year_id", nullable = false)
    private UUID academicYearId;

    @Column(name = "version", nullable = false)
    private int version = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private CurriculumStatus status = CurriculumStatus.DRAFT;

    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sequence ASC")
    private List<CurriculumSubject> subjects = new ArrayList<>();

    public boolean isPublished() {
        return CurriculumStatus.PUBLISHED == status;
    }

    public boolean isDraft() {
        return CurriculumStatus.DRAFT == status;
    }
}

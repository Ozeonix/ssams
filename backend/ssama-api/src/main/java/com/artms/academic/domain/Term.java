package com.artms.academic.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "term",
    uniqueConstraints = @UniqueConstraint(columnNames = {"academic_year_id", "sequence"}))
@Getter
@Setter
@NoArgsConstructor
public class Term extends TenantBaseEntity {

    @Column(name = "academic_year_id", nullable = false)
    private UUID academicYearId;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}

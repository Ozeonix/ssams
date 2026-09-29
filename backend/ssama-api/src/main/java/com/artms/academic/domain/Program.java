package com.artms.academic.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "program",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "code"}))
@Getter
@Setter
@NoArgsConstructor
public class Program extends TenantBaseEntity {

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "level", length = 64)
    private String level; // GRADE_9_10, PLUS_TWO, DIPLOMA, BACHELOR

    @Column(name = "duration_years")
    private Integer durationYears;

    @Column(name = "status", nullable = false, length = 32)
    private String status = "ACTIVE";
}

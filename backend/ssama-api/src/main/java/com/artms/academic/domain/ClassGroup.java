package com.artms.academic.domain;

import com.artms.shared.domain.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "class_group")
@Getter
@Setter
@NoArgsConstructor
public class ClassGroup extends TenantBaseEntity {

    @Column(name = "program_id", nullable = false)
    private UUID programId;

    @Column(name = "academic_year_id", nullable = false)
    private UUID academicYearId;

    @Column(name = "grade_level", nullable = false, length = 64)
    private String gradeLevel;

    @Column(name = "section", length = 32)
    private String section;

    @Column(name = "stream", length = 64)
    private String stream;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "room_no", length = 32)
    private String roomNo;

    @Column(name = "class_teacher_id")
    private UUID classTeacherId;

    @Column(name = "status", nullable = false, length = 32)
    private String status = "ACTIVE";
}

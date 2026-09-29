package com.artms.staff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "teacher_subject",
    uniqueConstraints = @UniqueConstraint(columnNames = {"teacher_id", "curriculum_subject_id", "academic_year_id"}))
@Getter
@Setter
@NoArgsConstructor
public class TeacherSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "teacher_id", nullable = false)
    private UUID teacherId;

    @Column(name = "curriculum_subject_id", nullable = false)
    private UUID curriculumSubjectId;

    @Column(name = "academic_year_id", nullable = false)
    private UUID academicYearId;
}

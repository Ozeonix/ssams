package com.artms.staff.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, UUID> {

    List<TeacherSubject> findByTeacherIdAndAcademicYearId(UUID teacherId, UUID academicYearId);

    List<TeacherSubject> findByCurriculumSubjectIdAndAcademicYearId(UUID curriculumSubjectId, UUID academicYearId);
}

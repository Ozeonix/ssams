package com.artms.student.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentGuardianRepository extends JpaRepository<StudentGuardian, UUID> {

    List<StudentGuardian> findByStudentId(UUID studentId);
}

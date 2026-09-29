package com.artms.enrollment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectEnrollmentRepository extends JpaRepository<SubjectEnrollment, UUID> {

    List<SubjectEnrollment> findByEnrollmentId(UUID enrollmentId);
}

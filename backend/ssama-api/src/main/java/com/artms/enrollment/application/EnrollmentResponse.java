package com.artms.enrollment.application;

import com.artms.enrollment.domain.Enrollment;
import com.artms.enrollment.domain.EnrollmentStatus;
import com.artms.enrollment.domain.SubjectEnrollment;
import com.artms.enrollment.domain.SubjectEnrollmentStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EnrollmentResponse(
    UUID id,
    UUID tenantId,
    UUID studentId,
    UUID classGroupId,
    UUID academicYearId,
    String rollNo,
    EnrollmentStatus status,
    LocalDate enrolledAt,
    List<SubjectEnrollmentResponse> subjectEnrollments,
    OffsetDateTime createdAt
) {
    public static EnrollmentResponse from(Enrollment e) {
        List<SubjectEnrollmentResponse> subjects = e.getSubjectEnrollments() == null ? List.of() :
            e.getSubjectEnrollments().stream()
                .map(SubjectEnrollmentResponse::from)
                .toList();

        return new EnrollmentResponse(
            e.getId(),
            e.getTenantId(),
            e.getStudentId(),
            e.getClassGroupId(),
            e.getAcademicYearId(),
            e.getRollNo(),
            e.getStatus(),
            e.getEnrolledAt(),
            subjects,
            e.getCreatedAt()
        );
    }

    public record SubjectEnrollmentResponse(
        UUID id,
        UUID curriculumSubjectId,
        SubjectEnrollmentStatus status,
        OffsetDateTime createdAt
    ) {
        public static SubjectEnrollmentResponse from(SubjectEnrollment se) {
            return new SubjectEnrollmentResponse(
                se.getId(),
                se.getCurriculumSubjectId(),
                se.getStatus(),
                se.getCreatedAt()
            );
        }
    }
}

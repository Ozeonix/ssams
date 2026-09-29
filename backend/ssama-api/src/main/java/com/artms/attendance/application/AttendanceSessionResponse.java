package com.artms.attendance.application;

import com.artms.attendance.domain.AttendanceSession;
import com.artms.attendance.domain.AttendanceSessionStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AttendanceSessionResponse(
    UUID id,
    UUID tenantId,
    UUID classGroupId,
    UUID subjectId,
    LocalDate sessionDate,
    String period,
    UUID teacherId,
    AttendanceSessionStatus status,
    OffsetDateTime submittedAt,
    List<AttendanceRecordResponse> records,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static AttendanceSessionResponse from(AttendanceSession s) {
        List<AttendanceRecordResponse> records = s.getRecords() == null ? List.of() :
            s.getRecords().stream().map(AttendanceRecordResponse::from).toList();

        return new AttendanceSessionResponse(
            s.getId(),
            s.getTenantId(),
            s.getClassGroupId(),
            s.getSubjectId(),
            s.getSessionDate(),
            s.getPeriod(),
            s.getTeacherId(),
            s.getStatus(),
            s.getSubmittedAt(),
            records,
            s.getCreatedAt(),
            s.getUpdatedAt()
        );
    }
}

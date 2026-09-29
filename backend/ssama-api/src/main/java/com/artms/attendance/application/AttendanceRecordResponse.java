package com.artms.attendance.application;

import com.artms.attendance.domain.AttendanceRecord;
import com.artms.attendance.domain.AttendanceStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AttendanceRecordResponse(
    UUID id,
    UUID sessionId,
    UUID studentId,
    AttendanceStatus status,
    String remarks,
    boolean corrected,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static AttendanceRecordResponse from(AttendanceRecord r) {
        return new AttendanceRecordResponse(
            r.getId(),
            r.getSession() != null ? r.getSession().getId() : null,
            r.getStudentId(),
            r.getStatus(),
            r.getRemarks(),
            r.isCorrected(),
            r.getCreatedAt(),
            r.getUpdatedAt()
        );
    }
}

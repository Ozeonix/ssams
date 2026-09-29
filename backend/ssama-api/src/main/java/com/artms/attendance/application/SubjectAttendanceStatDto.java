package com.artms.attendance.application;

import java.math.BigDecimal;
import java.util.UUID;

public record SubjectAttendanceStatDto(
    UUID subjectId,
    int totalSessions,
    int presentCount,
    int absentCount,
    BigDecimal attendancePercentage
) {}

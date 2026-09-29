package com.artms.attendance.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record StudentAttendanceSummaryDto(
    UUID studentId,
    int totalSessions,
    int presentCount,
    int absentCount,
    int lateCount,
    int excusedCount,
    BigDecimal attendancePercentage,
    boolean lowAttendanceWarning,
    List<SubjectAttendanceStatDto> subjectStats,
    List<AttendanceRecordResponse> recentAbsences
) {}

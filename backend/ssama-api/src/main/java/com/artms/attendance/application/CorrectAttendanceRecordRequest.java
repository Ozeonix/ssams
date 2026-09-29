package com.artms.attendance.application;

import com.artms.attendance.domain.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CorrectAttendanceRecordRequest(
    @NotNull AttendanceStatus newStatus,
    @NotBlank String reason,
    String remarks
) {}

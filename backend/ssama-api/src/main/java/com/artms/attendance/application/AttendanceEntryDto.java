package com.artms.attendance.application;

import com.artms.attendance.domain.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AttendanceEntryDto(
    @NotNull UUID studentId,
    @NotNull AttendanceStatus status,
    String remarks
) {}

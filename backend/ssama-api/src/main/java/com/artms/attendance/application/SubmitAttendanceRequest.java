package com.artms.attendance.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SubmitAttendanceRequest(
    @NotNull UUID classGroupId,
    UUID subjectId,
    @NotNull LocalDate sessionDate,
    String period,
    UUID teacherId,
    @NotEmpty @Valid List<AttendanceEntryDto> records
) {}

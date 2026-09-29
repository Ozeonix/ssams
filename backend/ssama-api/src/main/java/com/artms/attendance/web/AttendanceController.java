package com.artms.attendance.web;

import com.artms.attendance.application.*;
import com.artms.shared.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<AttendanceSessionResponse>> submitAttendance(
            @Valid @RequestBody SubmitAttendanceRequest request) {
        AttendanceSessionResponse response = attendanceService.submitAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<ApiResponse<AttendanceSessionResponse>> getSession(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(attendanceService.getSession(id)));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<Page<AttendanceSessionResponse>>> listSessionsByClass(
            @RequestParam UUID classGroupId,
            @PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(attendanceService.listSessionsByClass(classGroupId, pageable)));
    }

    @PatchMapping("/records/{id}/correct")
    public ResponseEntity<ApiResponse<AttendanceRecordResponse>> correctRecord(
            @PathVariable UUID id,
            @Valid @RequestBody CorrectAttendanceRecordRequest request) {
        AttendanceRecordResponse response = attendanceService.correctRecord(id, request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/students/{studentId}/summary")
    public ResponseEntity<ApiResponse<StudentAttendanceSummaryDto>> getStudentSummary(
            @PathVariable UUID studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.of(
                attendanceService.getStudentAttendanceSummary(studentId, startDate, endDate)));
    }
}

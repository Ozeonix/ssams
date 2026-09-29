package com.artms.enrollment.web;

import com.artms.enrollment.application.*;
import com.artms.enrollment.domain.EnrollmentStatus;
import com.artms.shared.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            @Valid @RequestBody EnrollStudentRequest request) {
        EnrollmentResponse response = enrollmentService.enrollStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> batchEnroll(
            @Valid @RequestBody BatchEnrollClassRequest request) {
        List<EnrollmentResponse> response = enrollmentService.batchEnrollClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(enrollmentService.getEnrollmentById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EnrollmentResponse>>> listByClass(
            @RequestParam UUID classGroupId,
            @RequestParam UUID academicYearId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(
                enrollmentService.getEnrollmentsByClass(classGroupId, academicYearId, pageable)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestParam EnrollmentStatus status) {
        return ResponseEntity.ok(ApiResponse.of(enrollmentService.updateStatus(id, status)));
    }
}

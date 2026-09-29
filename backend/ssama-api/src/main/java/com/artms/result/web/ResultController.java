package com.artms.result.web;

import com.artms.result.application.ResultCorrectionRequest;
import com.artms.result.application.ResultService;
import com.artms.result.application.ResultSnapshotResponse;
import com.artms.shared.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @PostMapping("/api/v1/exams/{id}/calculate-preview")
    @PreAuthorize("hasAuthority('PERM_result:preview') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<ResultSnapshotResponse>>> calculatePreview(
            @PathVariable UUID id) {
        List<ResultSnapshotResponse> preview = resultService.calculatePreview(id);
        return ResponseEntity.ok(ApiResponse.of(preview));
    }

    @PostMapping("/api/v1/exams/{id}/publish")
    @PreAuthorize("hasAuthority('PERM_result:publish') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<ResultSnapshotResponse>>> publishResults(
            @PathVariable UUID id) {
        List<ResultSnapshotResponse> results = resultService.publishResults(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(results));
    }

    @PostMapping("/api/v1/exams/{id}/corrections")
    @PreAuthorize("hasAuthority('PERM_result:correct') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<ResultSnapshotResponse>> correctResult(
            @PathVariable UUID id,
            @Valid @RequestBody ResultCorrectionRequest request) {
        ResultSnapshotResponse corrected = resultService.correctResult(id, request);
        return ResponseEntity.ok(ApiResponse.of(corrected));
    }

    @GetMapping("/api/v1/exams/{id}/results")
    @PreAuthorize("hasAuthority('PERM_result:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<ResultSnapshotResponse>>> getExamResults(
            @PathVariable UUID id) {
        List<ResultSnapshotResponse> results = resultService.getExamResults(id);
        return ResponseEntity.ok(ApiResponse.of(results));
    }

    @GetMapping("/api/v1/results/{id}")
    @PreAuthorize("hasAuthority('PERM_result:read') or hasAuthority('PERM_student:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<ResultSnapshotResponse>> getResultById(
            @PathVariable UUID id) {
        ResultSnapshotResponse result = resultService.getResultById(id);
        return ResponseEntity.ok(ApiResponse.of(result));
    }

    @GetMapping("/api/v1/students/{studentId}/results")
    @PreAuthorize("hasAuthority('PERM_result:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<ResultSnapshotResponse>>> getStudentResults(
            @PathVariable UUID studentId) {
        List<ResultSnapshotResponse> results = resultService.getStudentPublishedResults(studentId);
        return ResponseEntity.ok(ApiResponse.of(results));
    }

    @GetMapping("/api/v1/me/results")
    @PreAuthorize("hasAuthority('PERM_student:read') or hasAuthority('PERM_result:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<ResultSnapshotResponse>>> getMyResults() {
        List<ResultSnapshotResponse> results = resultService.getMyResults();
        return ResponseEntity.ok(ApiResponse.of(results));
    }
}

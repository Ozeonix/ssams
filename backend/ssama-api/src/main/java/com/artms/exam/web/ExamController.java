package com.artms.exam.web;

import com.artms.exam.application.*;
import com.artms.shared.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_academic:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(
            @Valid @RequestBody CreateExamRequest request) {
        ExamResponse response = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_academic:read') or hasAuthority('PERM_marks:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<ExamResponse>> getExam(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(examService.getExam(id)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_academic:read') or hasAuthority('PERM_marks:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<?>> listExams(
            @RequestParam(required = false) UUID academicYearId,
            @PageableDefault(size = 20) Pageable pageable) {
        if (academicYearId != null) {
            return ResponseEntity.ok(ApiResponse.of(examService.listExamsByAcademicYear(academicYearId)));
        }
        return ResponseEntity.ok(ApiResponse.of(examService.listExams(pageable)));
    }

    @PostMapping("/{id}/subjects")
    @PreAuthorize("hasAuthority('PERM_academic:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<ExamSubjectResponse>> addSubject(
            @PathVariable UUID id,
            @Valid @RequestBody AddExamSubjectRequest request) {
        ExamSubjectResponse response = examService.addSubject(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @DeleteMapping("/{id}/subjects/{examSubjectId}")
    @PreAuthorize("hasAuthority('PERM_academic:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<Void> removeSubject(
            @PathVariable UUID id,
            @PathVariable UUID examSubjectId) {
        examService.removeSubject(id, examSubjectId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/workflow")
    @PreAuthorize("hasAuthority('PERM_academic:manage') or hasAuthority('PERM_marks:submit') or hasAuthority('PERM_marks:verify') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<ExamResponse>> updateWorkflow(
            @PathVariable UUID id,
            @Valid @RequestBody ExamWorkflowRequest request) {
        ExamResponse response = examService.updateWorkflowStatus(id, request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping("/{id}/marks/batch")
    @PreAuthorize("hasAuthority('PERM_marks:write') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<MarkEntryResponse>>> enterMarksBatch(
            @PathVariable UUID id,
            @Valid @RequestBody BatchMarkEntryRequest request) {
        List<MarkEntryResponse> response = examService.enterMarksBatch(id, request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping("/{id}/marks/subjects/{examSubjectId}/submit")
    @PreAuthorize("hasAuthority('PERM_marks:submit') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<MarkEntryResponse>>> submitMarks(
            @PathVariable UUID id,
            @PathVariable UUID examSubjectId) {
        List<MarkEntryResponse> response = examService.submitMarks(id, examSubjectId);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping("/{id}/marks/subjects/{examSubjectId}/verify")
    @PreAuthorize("hasAuthority('PERM_marks:verify') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<MarkEntryResponse>>> verifyMarks(
            @PathVariable UUID id,
            @PathVariable UUID examSubjectId) {
        List<MarkEntryResponse> response = examService.verifyMarks(id, examSubjectId);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/{id}/marks/subjects/{examSubjectId}/grid")
    @PreAuthorize("hasAuthority('PERM_marks:read') or hasAuthority('PERM_marks:write') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<ExamMarksGridDto>> getMarksGrid(
            @PathVariable UUID id,
            @PathVariable UUID examSubjectId) {
        ExamMarksGridDto grid = examService.getMarksGrid(id, examSubjectId);
        return ResponseEntity.ok(ApiResponse.of(grid));
    }

    @GetMapping("/{id}/students/{studentId}/marks")
    @PreAuthorize("hasAuthority('PERM_marks:read') or hasAuthority('PERM_student:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<List<MarkEntryResponse>>> getStudentMarks(
            @PathVariable UUID id,
            @PathVariable UUID studentId) {
        List<MarkEntryResponse> marks = examService.getStudentMarks(id, studentId);
        return ResponseEntity.ok(ApiResponse.of(marks));
    }
}

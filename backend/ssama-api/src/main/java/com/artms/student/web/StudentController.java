package com.artms.student.web;

import com.artms.shared.web.ApiResponse;
import com.artms.student.application.*;
import com.artms.student.domain.Student;
import com.artms.student.domain.StudentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Student REST controller.
 * GET    /api/v1/students           — paginated list with search/filter
 * POST   /api/v1/students           — create student
 * GET    /api/v1/students/{id}      — get student
 * PATCH  /api/v1/students/{id}      — update student
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Student>>> list(
            @RequestParam(required = false) StudentStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(studentService.list(status, search, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Student>> create(@Valid @RequestBody CreateStudentRequest request) {
        Student student = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(student));
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<BatchImportResult>> importStudents(
            @Valid @RequestBody java.util.List<CreateStudentRequest> requests) {
        BatchImportResult result = studentService.importStudentsBatch(requests);
        return ResponseEntity.ok(ApiResponse.of(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(studentService.getById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> update(
            @PathVariable UUID id,
            @RequestBody UpdateStudentRequest request) {
        return ResponseEntity.ok(ApiResponse.of(studentService.update(id, request)));
    }

    @PostMapping("/{id}/guardians")
    public ResponseEntity<ApiResponse<com.artms.student.domain.StudentGuardian>> addGuardian(
            @PathVariable UUID id,
            @Valid @RequestBody AddGuardianRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(studentService.addGuardian(id, request)));
    }

    @GetMapping("/{id}/guardians")
    public ResponseEntity<ApiResponse<java.util.List<com.artms.student.domain.StudentGuardian>>> getGuardians(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(studentService.getGuardians(id)));
    }
}

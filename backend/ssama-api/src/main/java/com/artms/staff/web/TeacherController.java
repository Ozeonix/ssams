package com.artms.staff.web;

import com.artms.shared.web.ApiResponse;
import com.artms.staff.application.*;
import com.artms.staff.domain.Teacher;
import com.artms.staff.domain.TeacherSubject;
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
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    public ResponseEntity<ApiResponse<Teacher>> create(@Valid @RequestBody CreateTeacherRequest request) {
        Teacher teacher = teacherService.createTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(teacher));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Teacher>>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(teacherService.listTeachers(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Teacher>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(teacherService.getTeacher(id)));
    }

    @PostMapping("/{id}/subjects")
    public ResponseEntity<ApiResponse<TeacherSubject>> assignSubject(
            @PathVariable UUID id,
            @Valid @RequestBody AssignTeacherSubjectRequest request) {
        TeacherSubject ts = teacherService.assignSubject(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(ts));
    }

    @GetMapping("/{id}/subjects")
    public ResponseEntity<ApiResponse<List<TeacherSubject>>> getTeacherSubjects(
            @PathVariable UUID id,
            @RequestParam UUID academicYearId) {
        return ResponseEntity.ok(ApiResponse.of(teacherService.getTeacherSubjects(id, academicYearId)));
    }
}

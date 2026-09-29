package com.artms.academic.web;

import com.artms.academic.application.CreateSubjectRequest;
import com.artms.academic.application.SubjectService;
import com.artms.academic.domain.Subject;
import com.artms.shared.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<ApiResponse<Subject>> create(@Valid @RequestBody CreateSubjectRequest request) {
        Subject subject = subjectService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(subject));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Subject>>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(subjectService.listSubjects(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Subject>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(subjectService.getSubject(id)));
    }
}

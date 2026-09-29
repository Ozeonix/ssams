package com.artms.academic.web;

import com.artms.academic.application.AddCurriculumSubjectRequest;
import com.artms.academic.application.CreateCurriculumRequest;
import com.artms.academic.application.CurriculumService;
import com.artms.academic.domain.Curriculum;
import com.artms.academic.domain.CurriculumSubject;
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
@RequestMapping("/api/v1/curricula")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    @PostMapping
    public ResponseEntity<ApiResponse<Curriculum>> create(@Valid @RequestBody CreateCurriculumRequest request) {
        Curriculum curriculum = curriculumService.createCurriculum(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(curriculum));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Curriculum>>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(curriculumService.listCurricula(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Curriculum>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(curriculumService.getCurriculum(id)));
    }

    @PostMapping("/{id}/subjects")
    public ResponseEntity<ApiResponse<CurriculumSubject>> addSubject(
            @PathVariable UUID id,
            @Valid @RequestBody AddCurriculumSubjectRequest request) {
        CurriculumSubject subject = curriculumService.addSubject(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(subject));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<Curriculum>> publish(@PathVariable UUID id) {
        Curriculum published = curriculumService.publishCurriculum(id);
        return ResponseEntity.ok(ApiResponse.of(published));
    }
}

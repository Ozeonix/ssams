package com.artms.academic.web;

import com.artms.academic.application.*;
import com.artms.academic.domain.AcademicYear;
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

/**
 * Academic year REST controller.
 * GET  /api/v1/academic-years           — paginated list (requires academic:read)
 * POST /api/v1/academic-years           — create (requires academic:manage)
 * GET  /api/v1/academic-years/{id}      — get by id (requires academic:read)
 * POST /api/v1/academic-years/{id}/activate — activate (requires academic:manage)
 */
@RestController
@RequestMapping("/api/v1/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AcademicYear>>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(academicYearService.list(pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AcademicYear>> create(
            @Valid @RequestBody CreateAcademicYearRequest request) {
        AcademicYear year = academicYearService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(year));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicYear>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(academicYearService.getById(id)));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<AcademicYear>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(academicYearService.activate(id)));
    }
}

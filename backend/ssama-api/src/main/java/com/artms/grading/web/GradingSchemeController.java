package com.artms.grading.web;

import com.artms.grading.application.CreateGradingSchemeRequest;
import com.artms.grading.application.GradingSchemeService;
import com.artms.grading.domain.GradingScheme;
import com.artms.shared.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/grading-schemes")
@RequiredArgsConstructor
public class GradingSchemeController {

    private final GradingSchemeService gradingSchemeService;

    @PostMapping
    public ResponseEntity<ApiResponse<GradingScheme>> create(@Valid @RequestBody CreateGradingSchemeRequest request) {
        GradingScheme scheme = gradingSchemeService.createGradingScheme(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(scheme));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GradingScheme>>> list() {
        return ResponseEntity.ok(ApiResponse.of(gradingSchemeService.listSchemes()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<GradingScheme>> getActive() {
        return ResponseEntity.ok(ApiResponse.of(gradingSchemeService.getActiveScheme()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GradingScheme>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(gradingSchemeService.getScheme(id)));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<GradingScheme>> activate(@PathVariable UUID id) {
        GradingScheme activated = gradingSchemeService.activateScheme(id);
        return ResponseEntity.ok(ApiResponse.of(activated));
    }
}

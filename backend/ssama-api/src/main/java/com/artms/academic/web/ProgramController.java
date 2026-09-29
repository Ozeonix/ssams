package com.artms.academic.web;

import com.artms.academic.application.CreateProgramRequest;
import com.artms.academic.application.ProgramService;
import com.artms.academic.domain.Program;
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
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    public ResponseEntity<ApiResponse<Program>> create(@Valid @RequestBody CreateProgramRequest request) {
        Program program = programService.createProgram(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(program));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Program>>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(programService.listPrograms(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Program>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(programService.getProgram(id)));
    }
}

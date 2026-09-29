package com.artms.academic.web;

import com.artms.academic.application.ClassGroupService;
import com.artms.academic.application.CreateClassGroupRequest;
import com.artms.academic.domain.ClassGroup;
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
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class ClassGroupController {

    private final ClassGroupService classGroupService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClassGroup>> create(@Valid @RequestBody CreateClassGroupRequest request) {
        ClassGroup group = classGroupService.createClassGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(group));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ClassGroup>>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(classGroupService.listClassGroups(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassGroup>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(classGroupService.getClassGroup(id)));
    }
}

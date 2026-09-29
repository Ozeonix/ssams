package com.artms.academic.web;

import com.artms.academic.application.CreateDepartmentRequest;
import com.artms.academic.application.DepartmentService;
import com.artms.academic.domain.Department;
import com.artms.shared.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Department>> create(@Valid @RequestBody CreateDepartmentRequest request) {
        Department dept = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(dept));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Department>>> list() {
        return ResponseEntity.ok(ApiResponse.of(departmentService.listDepartments()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(departmentService.getDepartment(id)));
    }
}

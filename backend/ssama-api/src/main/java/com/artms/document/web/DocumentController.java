package com.artms.document.web;

import com.artms.document.application.DocumentService;
import com.artms.document.application.DocumentVerificationResponse;
import com.artms.document.application.GenerateDocumentRequest;
import com.artms.document.application.GeneratedDocumentResponse;
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

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/api/v1/documents/generate")
    @PreAuthorize("hasAuthority('PERM_document:generate') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<GeneratedDocumentResponse>> generateDocument(
            @Valid @RequestBody GenerateDocumentRequest request) {
        GeneratedDocumentResponse response = documentService.generateDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/api/v1/documents/{id}")
    @PreAuthorize("hasAuthority('PERM_document:read') or hasAuthority('PERM_student:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<GeneratedDocumentResponse>> getDocument(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(documentService.getDocument(id)));
    }

    @GetMapping("/api/v1/documents")
    @PreAuthorize("hasAuthority('PERM_document:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<Page<GeneratedDocumentResponse>>> listDocuments(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(documentService.listDocuments(pageable)));
    }

    @GetMapping("/api/v1/students/{studentId}/documents")
    @PreAuthorize("hasAuthority('PERM_document:read') or hasAuthority('PERM_student:read') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<Page<GeneratedDocumentResponse>>> listStudentDocuments(
            @PathVariable UUID studentId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(documentService.listStudentDocuments(studentId, pageable)));
    }

    @PostMapping("/api/v1/documents/{id}/revoke")
    @PreAuthorize("hasAuthority('PERM_document:revoke') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<GeneratedDocumentResponse>> revokeDocument(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        GeneratedDocumentResponse response = documentService.revokeDocument(id, reason);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/api/v1/documents/verify/{id}")
    public ResponseEntity<ApiResponse<DocumentVerificationResponse>> verifyDocument(@PathVariable UUID id) {
        DocumentVerificationResponse response = documentService.verifyDocument(id);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}

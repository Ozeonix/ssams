package com.artms.document.web;

import com.artms.document.application.CreateDocumentTemplateRequest;
import com.artms.document.application.DocumentService;
import com.artms.document.application.DocumentTemplateResponse;
import com.artms.document.domain.DocumentType;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/document-templates")
@RequiredArgsConstructor
public class DocumentTemplateController {

    private final DocumentService documentService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_settings:manage') or hasAuthority('PERM_academic:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<DocumentTemplateResponse>> createTemplate(
            @Valid @RequestBody CreateDocumentTemplateRequest request) {
        DocumentTemplateResponse response = documentService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_document:read') or hasAuthority('PERM_settings:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<DocumentTemplateResponse>> getTemplate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.of(documentService.getTemplate(id)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_document:read') or hasAuthority('PERM_settings:manage') or hasAuthority('PERM_platform:admin')")
    public ResponseEntity<ApiResponse<?>> listTemplates(
            @RequestParam(required = false) DocumentType type,
            @PageableDefault(size = 20) Pageable pageable) {
        if (type != null) {
            return ResponseEntity.ok(ApiResponse.of(documentService.listTemplatesByType(type)));
        }
        return ResponseEntity.ok(ApiResponse.of(documentService.listTemplates(pageable)));
    }
}

package com.artms.shared.web;

import com.artms.shared.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Centralised exception handler.
 * All exceptions must surface through ApiError — no raw exception info leaks to clients.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> ApiError.FieldError.builder()
                        .field(fe.getField())
                        .code(fe.getCode())
                        .message(fe.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ApiError error = ApiError.builder()
                .requestId(UUID.randomUUID())
                .timestamp(OffsetDateTime.now())
                .code("VALIDATION_ERROR")
                .message("Request validation failed")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        List<ApiError.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(cv -> ApiError.FieldError.builder()
                        .field(cv.getPropertyPath().toString())
                        .code("CONSTRAINT_VIOLATION")
                        .message(cv.getMessage())
                        .build())
                .collect(Collectors.toList());

        ApiError error = ApiError.builder()
                .requestId(UUID.randomUUID())
                .timestamp(OffsetDateTime.now())
                .code("VALIDATION_ERROR")
                .message("Constraint violation")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        ApiError error = ApiError.builder()
                .requestId(UUID.randomUUID())
                .timestamp(OffsetDateTime.now())
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException ex) {
        ApiError error = ApiError.builder()
                .requestId(UUID.randomUUID())
                .timestamp(OffsetDateTime.now())
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(OptimisticLockConflictException.class)
    public ResponseEntity<ApiError> handleOptimisticLock(OptimisticLockConflictException ex) {
        ApiError error = ApiError.builder()
                .requestId(UUID.randomUUID())
                .timestamp(OffsetDateTime.now())
                .code("OPTIMISTIC_LOCK_CONFLICT")
                .message("The resource has been modified by another request. Please reload and retry.")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TenantAccessException.class)
    public ResponseEntity<ApiError> handleTenantAccess(TenantAccessException ex) {
        log.warn("Tenant access violation: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .requestId(UUID.randomUUID())
                .timestamp(OffsetDateTime.now())
                .code("FORBIDDEN")
                .message("Access denied")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        ApiError error = ApiError.builder()
                .requestId(UUID.randomUUID())
                .timestamp(OffsetDateTime.now())
                .code("FORBIDDEN")
                .message("Access denied")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiError> handleUnauthenticated(AuthenticationCredentialsNotFoundException ex) {
        ApiError error = ApiError.builder()
                .requestId(UUID.randomUUID())
                .timestamp(OffsetDateTime.now())
                .code("UNAUTHORIZED")
                .message("Authentication required")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        // Log the full exception but do NOT surface internal details to client
        log.error("Unhandled exception", ex);
        ApiError error = ApiError.builder()
                .requestId(UUID.randomUUID())
                .timestamp(OffsetDateTime.now())
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred. Please contact support if the problem persists.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

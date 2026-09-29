package com.artms.shared.exception;

import lombok.Getter;

/**
 * Thrown when a business rule is violated.
 * Maps to HTTP 409 CONFLICT with a machine-readable error code.
 */
@Getter
public class BusinessRuleException extends RuntimeException {

    private final String errorCode;

    public BusinessRuleException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

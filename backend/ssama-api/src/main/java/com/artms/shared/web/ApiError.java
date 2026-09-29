package com.artms.shared.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Standard API error envelope returned on all error responses.
 * Clients must parse this structure.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final UUID requestId;
    private final OffsetDateTime timestamp;
    private final String code;
    private final String message;
    private final List<FieldError> fieldErrors;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {
        private final String field;
        private final String code;
        private final String message;
    }
}

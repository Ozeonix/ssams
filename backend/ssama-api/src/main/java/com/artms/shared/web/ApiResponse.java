package com.artms.shared.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Wraps successful paginated or singular API responses with request metadata.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final UUID requestId;
    private final OffsetDateTime timestamp;
    private final T data;

    public static <T> ApiResponse<T> of(T data) {
        return ApiResponse.<T>builder()
                .requestId(UUID.randomUUID())
                .timestamp(OffsetDateTime.now())
                .data(data)
                .build();
    }
}

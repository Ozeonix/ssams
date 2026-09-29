package com.artms.exam.application;

import com.artms.exam.domain.MarkEntry;
import com.artms.exam.domain.MarkStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MarkEntryResponse(
    UUID id,
    UUID tenantId,
    UUID examSubjectId,
    UUID studentId,
    UUID componentId,
    BigDecimal rawMarks,
    MarkStatus status,
    UUID enteredBy,
    UUID submittedBy,
    UUID verifiedBy,
    OffsetDateTime submittedAt,
    OffsetDateTime verifiedAt,
    String remarks,
    int version,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static MarkEntryResponse from(MarkEntry me) {
        return new MarkEntryResponse(
            me.getId(),
            me.getTenantId(),
            me.getExamSubjectId(),
            me.getStudentId(),
            me.getComponentId(),
            me.getRawMarks(),
            me.getStatus(),
            me.getEnteredBy(),
            me.getSubmittedBy(),
            me.getVerifiedBy(),
            me.getSubmittedAt(),
            me.getVerifiedAt(),
            me.getRemarks(),
            me.getVersion(),
            me.getCreatedAt(),
            me.getUpdatedAt()
        );
    }
}

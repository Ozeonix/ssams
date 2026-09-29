package com.artms.result.application;

import com.artms.result.domain.ResultSnapshot;
import com.artms.result.domain.ResultStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ResultSnapshotResponse(
    UUID id,
    UUID tenantId,
    UUID examId,
    UUID studentId,
    int resultVersion,
    BigDecimal totalCredits,
    BigDecimal earnedPoints,
    BigDecimal gpa,
    ResultStatus resultStatus,
    UUID gradingSchemeId,
    int gradingSchemeVersion,
    UUID publishedBy,
    OffsetDateTime publishedAt,
    String immutableHash,
    String correctionReason,
    UUID previousVersionId,
    List<ResultSubjectDto> subjects,
    OffsetDateTime createdAt
) {
    public static ResultSnapshotResponse from(ResultSnapshot snapshot) {
        List<ResultSubjectDto> subjects = snapshot.getSubjectSnapshots() == null ? List.of() :
            snapshot.getSubjectSnapshots().stream().map(ResultSubjectDto::from).toList();

        return new ResultSnapshotResponse(
            snapshot.getId(),
            snapshot.getTenantId(),
            snapshot.getExamId(),
            snapshot.getStudentId(),
            snapshot.getResultVersion(),
            snapshot.getTotalCredits(),
            snapshot.getEarnedPoints(),
            snapshot.getGpa(),
            snapshot.getResultStatus(),
            snapshot.getGradingSchemeId(),
            snapshot.getGradingSchemeVersion(),
            snapshot.getPublishedBy(),
            snapshot.getPublishedAt(),
            snapshot.getImmutableHash(),
            snapshot.getCorrectionReason(),
            snapshot.getPreviousVersionId(),
            subjects,
            snapshot.getCreatedAt()
        );
    }
}

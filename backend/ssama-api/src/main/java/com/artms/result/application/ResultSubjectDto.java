package com.artms.result.application;

import com.artms.result.domain.ResultSubjectSnapshot;

import java.math.BigDecimal;
import java.util.UUID;

public record ResultSubjectDto(
    UUID id,
    UUID subjectId,
    String subjectName,
    BigDecimal creditHours,
    BigDecimal theoryFullMarks,
    BigDecimal theoryObtained,
    BigDecimal practicalFullMarks,
    BigDecimal practicalObtained,
    BigDecimal totalFullMarks,
    BigDecimal totalObtained,
    BigDecimal percentage,
    String letterGrade,
    BigDecimal gradePoint,
    String finalGrade,
    String remarks
) {
    public static ResultSubjectDto from(ResultSubjectSnapshot s) {
        return new ResultSubjectDto(
            s.getId(),
            s.getSubjectId(),
            s.getSubjectNameSnapshot(),
            s.getCreditHours(),
            s.getTheoryFullMarks(),
            s.getTheoryObtained(),
            s.getPracticalFullMarks(),
            s.getPracticalObtained(),
            s.getTotalFullMarks(),
            s.getTotalObtained(),
            s.getPercentage(),
            s.getLetterGrade(),
            s.getGradePoint(),
            s.getFinalGrade(),
            s.getRemarks()
        );
    }
}

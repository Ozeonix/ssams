package com.artms.exam.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ExamMarksGridDto(
    UUID examId,
    UUID examSubjectId,
    UUID curriculumSubjectId,
    List<ComponentHeaderDto> components,
    List<StudentMarksRowDto> rows
) {
    public record ComponentHeaderDto(
        UUID componentId,
        String code,
        String name,
        BigDecimal fullMarks,
        BigDecimal passMarks
    ) {}

    public record StudentMarksRowDto(
        UUID studentId,
        String admissionNo,
        String studentName,
        List<MarkEntryResponse> componentMarks
    ) {}
}

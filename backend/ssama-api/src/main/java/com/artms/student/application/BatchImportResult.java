package com.artms.student.application;

import java.util.List;

public record BatchImportResult(
        int total,
        int imported,
        int skipped,
        List<String> errors
) {}

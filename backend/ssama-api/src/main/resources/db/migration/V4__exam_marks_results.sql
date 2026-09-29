-- V4: Examination and Marks Schema

-- ============================================================
-- EXAM
-- ============================================================
CREATE TABLE exam (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id         UUID         NOT NULL REFERENCES tenant(id),
    academic_year_id  UUID         NOT NULL REFERENCES academic_year(id),
    term_id           UUID         REFERENCES term(id),
    name              VARCHAR(255) NOT NULL,
    exam_type         VARCHAR(64)  NOT NULL,
    -- ANNUAL, TERMINAL, UNIT, MID_TERM, PRE_BOARD, PRACTICAL, INTERNAL, SUPPLEMENTARY
    status            VARCHAR(32)  NOT NULL DEFAULT 'DRAFT',
    -- DRAFT, SCHEDULED, MARK_ENTRY, SUBMITTED, VERIFIED, APPROVED, PUBLISHED, LOCKED
    grading_scheme_id UUID         NOT NULL REFERENCES grading_scheme(id),
    class_group_id    UUID         REFERENCES class_group(id),
    start_date        DATE,
    end_date          DATE,
    created_by        UUID         REFERENCES user_account(id),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version           INTEGER      NOT NULL DEFAULT 0,
    CONSTRAINT exam_type_chk CHECK (exam_type IN ('ANNUAL','TERMINAL','UNIT','MID_TERM','PRE_BOARD','PRACTICAL','INTERNAL','SUPPLEMENTARY','BOARD_IMPORTED')),
    CONSTRAINT exam_status_chk CHECK (status IN ('DRAFT','SCHEDULED','MARK_ENTRY','SUBMITTED','VERIFIED','APPROVED','PUBLISHED','LOCKED'))
);

CREATE INDEX idx_exam_tenant_year ON exam (tenant_id, academic_year_id);
CREATE INDEX idx_exam_tenant_status ON exam (tenant_id, status);

-- ============================================================
-- EXAM SUBJECT (which subjects are included in an exam)
-- ============================================================
CREATE TABLE exam_subject (
    id                    UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_id               UUID         NOT NULL REFERENCES exam(id),
    curriculum_subject_id UUID         NOT NULL REFERENCES curriculum_subject(id),
    status                VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (exam_id, curriculum_subject_id),
    CONSTRAINT exam_subject_status_chk CHECK (status IN ('ACTIVE','REMOVED'))
);

CREATE INDEX idx_exam_subject_exam ON exam_subject (exam_id);

-- ============================================================
-- MARK ENTRY
-- ============================================================
CREATE TABLE mark_entry (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    exam_subject_id UUID         NOT NULL REFERENCES exam_subject(id),
    student_id      UUID         NOT NULL REFERENCES student(id),
    component_id    UUID         NOT NULL REFERENCES subject_component(id),
    raw_marks       NUMERIC(6,2),
    status          VARCHAR(32)  NOT NULL DEFAULT 'DRAFT',
    -- DRAFT, SUBMITTED, VERIFIED, REJECTED, ABSENT, WITHHELD, EXPELLED, NOT_APPLICABLE
    entered_by      UUID         REFERENCES user_account(id),
    submitted_by    UUID         REFERENCES user_account(id),
    verified_by     UUID         REFERENCES user_account(id),
    submitted_at    TIMESTAMPTZ,
    verified_at     TIMESTAMPTZ,
    remarks         TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version         INTEGER      NOT NULL DEFAULT 0,
    UNIQUE (exam_subject_id, student_id, component_id),
    CONSTRAINT mark_status_chk CHECK (status IN ('DRAFT','SUBMITTED','VERIFIED','REJECTED','ABSENT','WITHHELD','EXPELLED','NOT_APPLICABLE'))
);

CREATE INDEX idx_mark_entry_exam_subject ON mark_entry (exam_subject_id);
CREATE INDEX idx_mark_entry_student ON mark_entry (student_id);
CREATE INDEX idx_mark_entry_tenant ON mark_entry (tenant_id);

-- ============================================================
-- RESULT SNAPSHOT (immutable published result)
-- ============================================================
CREATE TABLE result_snapshot (
    id                      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id               UUID         NOT NULL REFERENCES tenant(id),
    exam_id                 UUID         NOT NULL REFERENCES exam(id),
    student_id              UUID         NOT NULL REFERENCES student(id),
    result_version          INTEGER      NOT NULL DEFAULT 1,
    total_credits           NUMERIC(6,2) NOT NULL DEFAULT 0,
    earned_points           NUMERIC(8,4) NOT NULL DEFAULT 0,
    gpa                     NUMERIC(5,4),
    result_status           VARCHAR(32)  NOT NULL,
    -- PASS, FAIL, CONDITIONAL_PASS, INCOMPLETE, WITHHELD, EXPELLED, ABSENT
    grading_scheme_id       UUID         NOT NULL REFERENCES grading_scheme(id),
    grading_scheme_version  INTEGER      NOT NULL,
    published_by            UUID         REFERENCES user_account(id),
    published_at            TIMESTAMPTZ,
    immutable_hash          VARCHAR(64), -- SHA-256 of snapshot content
    correction_reason       TEXT,
    previous_version_id     UUID         REFERENCES result_snapshot(id),
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (exam_id, student_id, result_version),
    CONSTRAINT result_status_chk CHECK (result_status IN ('PASS','FAIL','CONDITIONAL_PASS','INCOMPLETE','WITHHELD','EXPELLED','ABSENT'))
);

CREATE INDEX idx_result_snapshot_tenant_exam ON result_snapshot (tenant_id, exam_id);
CREATE INDEX idx_result_snapshot_student ON result_snapshot (student_id);

-- ============================================================
-- RESULT SUBJECT SNAPSHOT (per-subject line inside result)
-- ============================================================
CREATE TABLE result_subject_snapshot (
    id                   UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    result_snapshot_id   UUID         NOT NULL REFERENCES result_snapshot(id),
    subject_id           UUID         NOT NULL REFERENCES subject(id),
    subject_name_snapshot VARCHAR(255) NOT NULL,  -- snapshot name at time of publish
    credit_hours         NUMERIC(4,1) NOT NULL,
    theory_full_marks    NUMERIC(6,2),
    theory_obtained      NUMERIC(6,2),
    practical_full_marks NUMERIC(6,2),
    practical_obtained   NUMERIC(6,2),
    total_full_marks     NUMERIC(6,2) NOT NULL,
    total_obtained       NUMERIC(6,2) NOT NULL,
    percentage           NUMERIC(6,3),
    letter_grade         VARCHAR(8),
    grade_point          NUMERIC(5,2),
    final_grade          VARCHAR(32),  -- PASS, FAIL, ABSENT, WITHHELD, NOT_GRADED
    remarks              VARCHAR(256),
    UNIQUE (result_snapshot_id, subject_id)
);

CREATE INDEX idx_result_subject_snapshot_result ON result_subject_snapshot (result_snapshot_id);

-- V2: Academic Core Schema
-- Migration: V2__academic_core.sql

-- ============================================================
-- ACADEMIC YEAR
-- ============================================================
CREATE TABLE academic_year (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL REFERENCES tenant(id),
    name        VARCHAR(64)  NOT NULL,
    start_date  DATE         NOT NULL,
    end_date    DATE         NOT NULL,
    status      VARCHAR(32)  NOT NULL DEFAULT 'PLANNING', -- PLANNING, ACTIVE, COMPLETED, ARCHIVED
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, name),
    CONSTRAINT academic_year_dates_chk CHECK (end_date > start_date),
    CONSTRAINT academic_year_status_chk CHECK (status IN ('PLANNING','ACTIVE','COMPLETED','ARCHIVED'))
);

CREATE INDEX idx_academic_year_tenant ON academic_year (tenant_id, status);

-- ============================================================
-- TERM / SEMESTER
-- ============================================================
CREATE TABLE term (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL REFERENCES tenant(id),
    academic_year_id UUID         NOT NULL REFERENCES academic_year(id),
    name             VARCHAR(64)  NOT NULL,
    sequence         INTEGER      NOT NULL DEFAULT 1,
    start_date       DATE,
    end_date         DATE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (academic_year_id, sequence)
);

CREATE INDEX idx_term_academic_year ON term (tenant_id, academic_year_id);

-- ============================================================
-- DEPARTMENT
-- ============================================================
CREATE TABLE department (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL REFERENCES tenant(id),
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(255) NOT NULL,
    status      VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, code)
);

CREATE INDEX idx_department_tenant ON department (tenant_id, status);

-- ============================================================
-- PROGRAM
-- ============================================================
CREATE TABLE program (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID         NOT NULL REFERENCES tenant(id),
    department_id UUID         REFERENCES department(id),
    code          VARCHAR(64)  NOT NULL,
    name          VARCHAR(255) NOT NULL,
    level         VARCHAR(64),  -- e.g. GRADE_9_10, PLUS_TWO, DIPLOMA, BACHELOR
    duration_years INTEGER,
    status        VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, code)
);

CREATE INDEX idx_program_tenant ON program (tenant_id, status);

-- ============================================================
-- CLASS GROUP (a specific class/section in an academic year)
-- ============================================================
CREATE TABLE class_group (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL REFERENCES tenant(id),
    program_id       UUID         NOT NULL REFERENCES program(id),
    academic_year_id UUID         NOT NULL REFERENCES academic_year(id),
    grade_level      VARCHAR(64)  NOT NULL, -- e.g. Grade 9, Grade 11, Year 1
    section          VARCHAR(32),            -- e.g. A, B, Science, Management
    stream           VARCHAR(64),            -- e.g. Science, Humanities, Computer
    capacity         INTEGER,
    room_no          VARCHAR(32),
    class_teacher_id UUID,                   -- FK set later
    status           VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT class_status_chk CHECK (status IN ('ACTIVE','INACTIVE','ARCHIVED'))
);

CREATE INDEX idx_class_group_tenant_year ON class_group (tenant_id, academic_year_id);

-- ============================================================
-- SUBJECT
-- ============================================================
CREATE TABLE subject (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id    UUID         NOT NULL REFERENCES tenant(id),
    code         VARCHAR(64)  NOT NULL,
    name         VARCHAR(255) NOT NULL,
    subject_type VARCHAR(64)  NOT NULL DEFAULT 'THEORY', -- THEORY, PRACTICAL, MIXED, VOCATIONAL
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, code)
);

CREATE INDEX idx_subject_tenant ON subject (tenant_id, active);

-- ============================================================
-- GRADING SCHEME (versioned)
-- ============================================================
CREATE TABLE grading_scheme (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id      UUID         NOT NULL REFERENCES tenant(id),
    name           VARCHAR(128) NOT NULL,
    version        INTEGER      NOT NULL DEFAULT 1,
    effective_from DATE,
    status         VARCHAR(32)  NOT NULL DEFAULT 'DRAFT', -- DRAFT, ACTIVE, SUPERSEDED
    gpa_precision  INTEGER      NOT NULL DEFAULT 2,
    gpa_rounding   VARCHAR(32)  NOT NULL DEFAULT 'HALF_UP', -- HALF_UP, FLOOR, CEILING
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, name, version),
    CONSTRAINT grading_scheme_status_chk CHECK (status IN ('DRAFT','ACTIVE','SUPERSEDED'))
);

CREATE INDEX idx_grading_scheme_tenant ON grading_scheme (tenant_id, status);

-- ============================================================
-- GRADE BAND
-- ============================================================
CREATE TABLE grade_band (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    grading_scheme_id UUID         NOT NULL REFERENCES grading_scheme(id),
    min_percentage    NUMERIC(6,3) NOT NULL,
    max_percentage    NUMERIC(6,3) NOT NULL,
    letter_grade      VARCHAR(8)   NOT NULL,
    grade_point       NUMERIC(5,2) NOT NULL,
    pass_flag         BOOLEAN      NOT NULL DEFAULT TRUE,
    remarks           VARCHAR(64),
    CONSTRAINT grade_band_range_chk CHECK (max_percentage >= min_percentage),
    CONSTRAINT grade_band_pct_chk CHECK (min_percentage >= 0 AND max_percentage <= 100)
);

CREATE INDEX idx_grade_band_scheme ON grade_band (grading_scheme_id);

-- ============================================================
-- CURRICULUM (versioned per program/year)
-- ============================================================
CREATE TABLE curriculum (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL REFERENCES tenant(id),
    program_id       UUID         NOT NULL REFERENCES program(id),
    academic_year_id UUID         NOT NULL REFERENCES academic_year(id),
    version          INTEGER      NOT NULL DEFAULT 1,
    status           VARCHAR(32)  NOT NULL DEFAULT 'DRAFT', -- DRAFT, PUBLISHED, ARCHIVED
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (program_id, academic_year_id, version),
    CONSTRAINT curriculum_status_chk CHECK (status IN ('DRAFT','PUBLISHED','ARCHIVED'))
);

CREATE INDEX idx_curriculum_tenant_program ON curriculum (tenant_id, program_id, academic_year_id);

-- ============================================================
-- CURRICULUM SUBJECT
-- ============================================================
CREATE TABLE curriculum_subject (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    curriculum_id   UUID         NOT NULL REFERENCES curriculum(id),
    subject_id      UUID         NOT NULL REFERENCES subject(id),
    grade_level     VARCHAR(64),              -- target grade if curriculum spans multiple
    sequence        INTEGER      NOT NULL DEFAULT 1,
    mandatory       BOOLEAN      NOT NULL DEFAULT TRUE,
    credit_hours    NUMERIC(4,1) NOT NULL DEFAULT 0,
    grading_scheme_id UUID       REFERENCES grading_scheme(id),
    UNIQUE (curriculum_id, subject_id, grade_level)
);

CREATE INDEX idx_curriculum_subject_curriculum ON curriculum_subject (curriculum_id);

-- ============================================================
-- SUBJECT COMPONENT (theory / practical / internal / project)
-- ============================================================
CREATE TABLE subject_component (
    id                   UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    curriculum_subject_id UUID        NOT NULL REFERENCES curriculum_subject(id),
    code                 VARCHAR(32)  NOT NULL, -- e.g. TH, PR, INT
    name                 VARCHAR(128) NOT NULL, -- e.g. Theory, Practical, Internal
    assessment_type      VARCHAR(32)  NOT NULL, -- THEORY, PRACTICAL, INTERNAL, PROJECT
    full_marks           NUMERIC(6,2) NOT NULL,
    pass_marks           NUMERIC(6,2) NOT NULL,
    weight               NUMERIC(5,2) NOT NULL DEFAULT 100, -- percentage weight in subject
    credit_hours         NUMERIC(4,1) NOT NULL DEFAULT 0,
    sequence             INTEGER      NOT NULL DEFAULT 1,
    CONSTRAINT component_marks_chk CHECK (pass_marks <= full_marks AND full_marks > 0),
    CONSTRAINT component_weight_chk CHECK (weight > 0 AND weight <= 100),
    CONSTRAINT component_assessment_type_chk CHECK (assessment_type IN ('THEORY','PRACTICAL','INTERNAL','PROJECT','ATTENDANCE'))
);

CREATE INDEX idx_subject_component_curriculum_subject ON subject_component (curriculum_subject_id);

-- V3: Student and Enrollment Schema

-- ============================================================
-- STUDENT
-- ============================================================
CREATE TABLE student (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    admission_no    VARCHAR(64)  NOT NULL,
    registration_no VARCHAR(64),
    symbol_no       VARCHAR(64),
    first_name      VARCHAR(128) NOT NULL,
    middle_name     VARCHAR(128),
    last_name       VARCHAR(128) NOT NULL,
    date_of_birth   DATE,
    gender          VARCHAR(16),  -- MALE, FEMALE, OTHER, NOT_SPECIFIED
    phone           VARCHAR(32),
    email           VARCHAR(255),
    address         TEXT,
    photo_url       VARCHAR(512),
    status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    -- APPLICANT, ACTIVE, PROMOTED, REPEATED, GRADUATED, COMPLETED, TRANSFERRED, ALUMNI, INACTIVE
    user_id         UUID         REFERENCES user_account(id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version         INTEGER      NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, admission_no),
    CONSTRAINT student_gender_chk CHECK (gender IN ('MALE','FEMALE','OTHER','NOT_SPECIFIED') OR gender IS NULL),
    CONSTRAINT student_status_chk CHECK (status IN ('APPLICANT','ACTIVE','PROMOTED','REPEATED','GRADUATED','COMPLETED','TRANSFERRED','ALUMNI','INACTIVE'))
);

CREATE INDEX idx_student_tenant_status ON student (tenant_id, status);
CREATE INDEX idx_student_tenant_registration ON student (tenant_id, registration_no) WHERE registration_no IS NOT NULL;
CREATE INDEX idx_student_tenant_admission ON student (tenant_id, admission_no);
CREATE INDEX idx_student_user ON student (user_id) WHERE user_id IS NOT NULL;

-- ============================================================
-- STUDENT GUARDIAN
-- ============================================================
CREATE TABLE student_guardian (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id   UUID         NOT NULL REFERENCES student(id),
    name         VARCHAR(255) NOT NULL,
    relationship VARCHAR(64),  -- FATHER, MOTHER, GUARDIAN, SIBLING, OTHER
    phone        VARCHAR(32),
    email        VARCHAR(255),
    address      TEXT,
    is_primary   BOOLEAN      NOT NULL DEFAULT FALSE,
    user_id      UUID         REFERENCES user_account(id),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_guardian_student ON student_guardian (student_id);

-- ============================================================
-- TEACHER / STAFF
-- ============================================================
CREATE TABLE teacher (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID         NOT NULL REFERENCES tenant(id),
    user_id       UUID         NOT NULL REFERENCES user_account(id),
    employee_code VARCHAR(64),
    first_name    VARCHAR(128) NOT NULL,
    last_name     VARCHAR(128) NOT NULL,
    department_id UUID         REFERENCES department(id),
    designation   VARCHAR(128),
    phone         VARCHAR(32),
    email         VARCHAR(255),
    status        VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, user_id),
    CONSTRAINT teacher_status_chk CHECK (status IN ('ACTIVE','INACTIVE','RESIGNED','SUSPENDED'))
);

CREATE INDEX idx_teacher_tenant ON teacher (tenant_id, status);

-- Add FK for class teacher after teacher table exists
ALTER TABLE class_group ADD CONSTRAINT fk_class_teacher
    FOREIGN KEY (class_teacher_id) REFERENCES teacher(id);

-- ============================================================
-- TEACHER SUBJECT ASSIGNMENT
-- ============================================================
CREATE TABLE teacher_subject (
    id                    UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id            UUID         NOT NULL REFERENCES teacher(id),
    curriculum_subject_id UUID         NOT NULL REFERENCES curriculum_subject(id),
    academic_year_id      UUID         NOT NULL REFERENCES academic_year(id),
    UNIQUE (teacher_id, curriculum_subject_id, academic_year_id)
);

CREATE INDEX idx_teacher_subject_teacher ON teacher_subject (teacher_id);

-- ============================================================
-- ENROLLMENT (student in a class for an academic year)
-- ============================================================
CREATE TABLE enrollment (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL REFERENCES tenant(id),
    student_id       UUID         NOT NULL REFERENCES student(id),
    class_group_id   UUID         NOT NULL REFERENCES class_group(id),
    academic_year_id UUID         NOT NULL REFERENCES academic_year(id),
    roll_no          VARCHAR(32),
    status           VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    -- ACTIVE, WITHDRAWN, TRANSFERRED, COMPLETED
    enrolled_at      DATE         NOT NULL DEFAULT CURRENT_DATE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, student_id, academic_year_id),
    CONSTRAINT enrollment_status_chk CHECK (status IN ('ACTIVE','WITHDRAWN','TRANSFERRED','COMPLETED','REPEATING'))
);

CREATE INDEX idx_enrollment_tenant_year ON enrollment (tenant_id, academic_year_id);
CREATE INDEX idx_enrollment_student ON enrollment (student_id);
CREATE INDEX idx_enrollment_class ON enrollment (class_group_id, academic_year_id);

-- ============================================================
-- SUBJECT ENROLLMENT
-- ============================================================
CREATE TABLE subject_enrollment (
    id                    UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    enrollment_id         UUID         NOT NULL REFERENCES enrollment(id),
    curriculum_subject_id UUID         NOT NULL REFERENCES curriculum_subject(id),
    status                VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (enrollment_id, curriculum_subject_id),
    CONSTRAINT subject_enrollment_status_chk CHECK (status IN ('ACTIVE','DROPPED','WITHDRAWN'))
);

CREATE INDEX idx_subject_enrollment_enrollment ON subject_enrollment (enrollment_id);

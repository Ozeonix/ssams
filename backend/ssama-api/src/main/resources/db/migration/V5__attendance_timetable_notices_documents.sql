-- V5: Attendance, Timetable, Notices, Notifications, Documents

-- ============================================================
-- ATTENDANCE SESSION
-- ============================================================
CREATE TABLE attendance_session (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id      UUID         NOT NULL REFERENCES tenant(id),
    class_group_id UUID         NOT NULL REFERENCES class_group(id),
    subject_id     UUID         REFERENCES subject(id),
    session_date   DATE         NOT NULL,
    period         VARCHAR(32),
    teacher_id     UUID         REFERENCES teacher(id),
    status         VARCHAR(32)  NOT NULL DEFAULT 'DRAFT', -- DRAFT, SUBMITTED, CORRECTED
    submitted_at   TIMESTAMPTZ,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (class_group_id, subject_id, session_date, period),
    CONSTRAINT attendance_session_status_chk CHECK (status IN ('DRAFT','SUBMITTED','CORRECTED'))
);

CREATE INDEX idx_attendance_session_class ON attendance_session (tenant_id, class_group_id, session_date);
CREATE INDEX idx_attendance_session_teacher ON attendance_session (teacher_id, session_date);

-- ============================================================
-- ATTENDANCE RECORD
-- ============================================================
CREATE TABLE attendance_record (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id  UUID         NOT NULL REFERENCES attendance_session(id),
    student_id  UUID         NOT NULL REFERENCES student(id),
    status      VARCHAR(32)  NOT NULL DEFAULT 'PRESENT', -- PRESENT, ABSENT, LATE, EXCUSED
    remarks     TEXT,
    corrected   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (session_id, student_id),
    CONSTRAINT attendance_record_status_chk CHECK (status IN ('PRESENT','ABSENT','LATE','EXCUSED'))
);

CREATE INDEX idx_attendance_record_session ON attendance_record (session_id);
CREATE INDEX idx_attendance_record_student ON attendance_record (student_id);

-- ============================================================
-- TIMETABLE ENTRY
-- ============================================================
CREATE TABLE timetable_entry (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id      UUID         NOT NULL REFERENCES tenant(id),
    class_group_id UUID         NOT NULL REFERENCES class_group(id),
    subject_id     UUID         NOT NULL REFERENCES subject(id),
    teacher_id     UUID         REFERENCES teacher(id),
    room           VARCHAR(32),
    weekday        SMALLINT     NOT NULL, -- 1=Mon ... 7=Sun
    start_time     TIME         NOT NULL,
    end_time       TIME         NOT NULL,
    effective_from DATE         NOT NULL,
    effective_to   DATE,
    published      BOOLEAN      NOT NULL DEFAULT FALSE,
    version        INTEGER      NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT timetable_time_chk CHECK (end_time > start_time),
    CONSTRAINT timetable_weekday_chk CHECK (weekday BETWEEN 1 AND 7)
);

CREATE INDEX idx_timetable_class ON timetable_entry (class_group_id, weekday);
CREATE INDEX idx_timetable_teacher ON timetable_entry (teacher_id, weekday);

-- ============================================================
-- NOTICE
-- ============================================================
CREATE TABLE notice (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL REFERENCES tenant(id),
    title       VARCHAR(512) NOT NULL,
    body        TEXT         NOT NULL,
    priority    VARCHAR(32)  NOT NULL DEFAULT 'NORMAL', -- LOW, NORMAL, HIGH, URGENT
    status      VARCHAR(32)  NOT NULL DEFAULT 'DRAFT',  -- DRAFT, PUBLISHED, EXPIRED, WITHDRAWN
    publish_at  TIMESTAMPTZ,
    expires_at  TIMESTAMPTZ,
    attachment_url VARCHAR(512),
    created_by  UUID         REFERENCES user_account(id),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT notice_priority_chk CHECK (priority IN ('LOW','NORMAL','HIGH','URGENT')),
    CONSTRAINT notice_status_chk CHECK (status IN ('DRAFT','PUBLISHED','EXPIRED','WITHDRAWN'))
);

CREATE INDEX idx_notice_tenant_status ON notice (tenant_id, status, publish_at);

-- ============================================================
-- NOTICE TARGET
-- ============================================================
CREATE TABLE notice_target (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    notice_id   UUID         NOT NULL REFERENCES notice(id),
    target_type VARCHAR(64)  NOT NULL, -- ALL, CLASS, PROGRAM, STUDENT, TEACHER, ROLE
    target_id   UUID,                   -- NULL = ALL
    UNIQUE (notice_id, target_type, target_id)
);

CREATE INDEX idx_notice_target_notice ON notice_target (notice_id);

-- ============================================================
-- NOTIFICATION (in-app)
-- ============================================================
CREATE TABLE notification (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL REFERENCES tenant(id),
    user_id     UUID         NOT NULL REFERENCES user_account(id),
    type        VARCHAR(64)  NOT NULL,  -- RESULT_PUBLISHED, NOTICE_PUBLISHED, etc.
    title       VARCHAR(512) NOT NULL,
    body        TEXT,
    data        JSONB,
    read_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_notification_user_read ON notification (user_id, read_at, created_at DESC);
CREATE INDEX idx_notification_tenant ON notification (tenant_id, created_at DESC);

-- ============================================================
-- DOCUMENT TEMPLATE
-- ============================================================
CREATE TABLE document_template (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID         NOT NULL REFERENCES tenant(id),
    type          VARCHAR(64)  NOT NULL, -- GRADE_SHEET, TRANSCRIPT, CERTIFICATE, PROGRESS_REPORT
    name          VARCHAR(255) NOT NULL,
    version       INTEGER      NOT NULL DEFAULT 1,
    template_body TEXT,                   -- Thymeleaf/Freemarker template content
    status        VARCHAR(32)  NOT NULL DEFAULT 'DRAFT', -- DRAFT, ACTIVE, ARCHIVED
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT doc_template_status_chk CHECK (status IN ('DRAFT','ACTIVE','ARCHIVED'))
);

CREATE INDEX idx_doc_template_tenant ON document_template (tenant_id, type, status);

-- ============================================================
-- GENERATED DOCUMENT
-- ============================================================
CREATE TABLE generated_document (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    student_id      UUID         REFERENCES student(id),
    document_type   VARCHAR(64)  NOT NULL,
    object_key      VARCHAR(512),
    checksum        VARCHAR(128),
    status          VARCHAR(32)  NOT NULL DEFAULT 'PENDING', -- PENDING, READY, FAILED, REVOKED
    generated_at    TIMESTAMPTZ,
    expires_at      TIMESTAMPTZ,
    request_data    JSONB,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT gen_doc_status_chk CHECK (status IN ('PENDING','READY','FAILED','REVOKED'))
);

CREATE INDEX idx_generated_document_student ON generated_document (student_id, document_type);
CREATE INDEX idx_generated_document_tenant ON generated_document (tenant_id, status);

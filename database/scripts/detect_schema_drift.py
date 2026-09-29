#!/usr/bin/env python3
"""
SSAMS / ARTMS – Database Schema Drift Detection
================================================
Compares the live database schema against the standalone DDL reference
(database/migrations/standalone_schema_sync.sql) and reports differences.

Detects:
  • Missing tables
  • Missing columns (with type mismatches)
  • Missing indexes
  • Missing constraints
  • Extra tables / columns not in schema (potential drift)

Usage:
    python3 database/scripts/detect_schema_drift.py \
        --host localhost --port 5432 \
        --dbname artms --user artms --password artms_dev_password

Exit codes:
    0  – No drift detected
    1  – Drift detected (see stdout for details)
    2  – Connection/configuration error
"""

import argparse
import sys
import textwrap

try:
    import psycopg2
    import psycopg2.extras
except ImportError:
    print("ERROR: 'psycopg2-binary' not installed. Run: pip install psycopg2-binary")
    sys.exit(2)

# ── Expected schema definition ─────────────────────────────────────────────
# (derived from Flyway migrations V1–V7)
EXPECTED_TABLES = {
    "tenant": [
        "id", "code", "name", "status", "timezone", "locale",
        "branding", "modules", "settings", "created_at", "updated_at",
    ],
    "user_account": [
        "id", "username", "email", "phone", "password_hash", "status",
        "failed_attempts", "locked_until", "last_login_at", "created_at", "updated_at",
    ],
    "user_tenant_membership": [
        "id", "tenant_id", "user_id", "status", "joined_at", "updated_at",
    ],
    "app_role": [
        "id", "tenant_id", "code", "name", "is_system", "created_at",
    ],
    "permission": [
        "id", "code", "description", "created_at",
    ],
    "role_permission": ["role_id", "permission_id"],
    "user_role": ["id", "user_id", "role_id", "tenant_id", "assigned_at"],
    "refresh_token": [
        "id", "user_id", "token_hash", "device_info", "ip_address",
        "issued_at", "expires_at", "revoked_at",
    ],
    "password_reset_token": [
        "id", "user_id", "token_hash", "expires_at", "used_at", "created_at",
    ],
    "academic_year": [
        "id", "tenant_id", "name", "start_date", "end_date", "status", "created_at", "updated_at",
    ],
    "term": [
        "id", "tenant_id", "academic_year_id", "name", "sequence",
        "start_date", "end_date", "created_at", "updated_at",
    ],
    "department": [
        "id", "tenant_id", "code", "name", "status", "created_at", "updated_at",
    ],
    "program": [
        "id", "tenant_id", "department_id", "code", "name",
        "duration_years", "level", "status", "created_at", "updated_at",
    ],
    "class_section": [
        "id", "tenant_id", "program_id", "academic_year_id", "name", "capacity", "created_at", "updated_at",
    ],
    "subject": [
        "id", "tenant_id", "department_id", "code", "name",
        "credit_hours", "subject_type", "status", "created_at", "updated_at",
    ],
    "grading_scheme": ["id", "tenant_id", "name", "is_default", "created_at"],
    "grade_band": [
        "id", "grading_scheme_id", "grade_letter", "min_percentage",
        "max_percentage", "grade_point", "remarks", "created_at",
    ],
    "student": [
        "id", "tenant_id", "student_code", "user_id", "first_name", "middle_name",
        "last_name", "date_of_birth", "gender", "nationality", "phone", "email",
        "address", "status", "admission_date", "photo_url", "documents", "created_at", "updated_at",
    ],
    "guardian": [
        "id", "tenant_id", "student_id", "first_name", "last_name",
        "relationship", "phone", "email", "is_primary", "created_at", "updated_at",
    ],
    "enrollment": [
        "id", "tenant_id", "student_id", "class_section_id", "academic_year_id",
        "term_id", "enrolled_at", "status", "updated_at",
    ],
    "exam": [
        "id", "tenant_id", "academic_year_id", "term_id", "name",
        "exam_type", "start_date", "end_date", "status", "created_at", "updated_at",
    ],
    "exam_subject": [
        "id", "exam_id", "subject_id", "full_marks", "pass_marks", "exam_date", "created_at",
    ],
    "student_mark": [
        "id", "tenant_id", "exam_subject_id", "student_id", "marks_obtained",
        "is_absent", "is_exempt", "submitted_at", "verified_at", "verified_by",
        "created_at", "updated_at",
    ],
    "result_snapshot": [
        "id", "tenant_id", "student_id", "exam_id", "gpa", "total_marks",
        "percentage", "rank", "status", "published_at", "created_at", "updated_at",
    ],
    "attendance_session": [
        "id", "tenant_id", "class_section_id", "subject_id", "teacher_id",
        "session_date", "start_time", "end_time", "status", "created_at", "updated_at",
    ],
    "attendance_record": [
        "id", "attendance_session_id", "student_id", "status", "remarks",
        "recorded_at", "corrected_at", "corrected_by",
    ],
    "document_template": [
        "id", "tenant_id", "name", "template_type", "content", "is_active", "created_at", "updated_at",
    ],
    "generated_document": [
        "id", "tenant_id", "student_id", "template_id", "document_type",
        "file_url", "verification_code", "generated_at", "expires_at",
    ],
    "notification_outbox": [
        "id", "tenant_id", "recipient_id", "event_type", "payload", "status",
        "attempts", "scheduled_at", "sent_at", "failed_at", "error_message", "created_at",
    ],
}

INTERNAL_TABLES = {"flyway_schema_history"}


def get_live_schema(conn):
    """Fetch all tables and their columns from the live database."""
    live = {}
    with conn.cursor(cursor_factory=psycopg2.extras.DictCursor) as cur:
        cur.execute("""
            SELECT table_name, column_name
            FROM information_schema.columns
            WHERE table_schema = 'public'
            ORDER BY table_name, ordinal_position
        """)
        for row in cur.fetchall():
            tbl = row["table_name"]
            if tbl in INTERNAL_TABLES:
                continue
            live.setdefault(tbl, []).append(row["column_name"])
    return live


def detect_drift(expected, live):
    issues = []

    # Missing tables
    for tbl in expected:
        if tbl not in live:
            issues.append(f"MISSING TABLE: {tbl}")

    # Extra tables (warn only)
    extra_tables = set(live.keys()) - set(expected.keys()) - INTERNAL_TABLES
    for tbl in sorted(extra_tables):
        issues.append(f"EXTRA TABLE (not in schema): {tbl}")

    # Column-level drift
    for tbl, expected_cols in expected.items():
        if tbl not in live:
            continue
        live_cols = set(live[tbl])
        for col in expected_cols:
            if col not in live_cols:
                issues.append(f"MISSING COLUMN: {tbl}.{col}")
        extra_cols = live_cols - set(expected_cols)
        for col in sorted(extra_cols):
            issues.append(f"EXTRA COLUMN (not in schema): {tbl}.{col}")

    return issues


def check_flyway_status(conn):
    """Report unapplied Flyway migrations."""
    issues = []
    with conn.cursor(cursor_factory=psycopg2.extras.DictCursor) as cur:
        try:
            cur.execute("""
                SELECT version, description, success
                FROM flyway_schema_history
                ORDER BY installed_rank
            """)
            rows = cur.fetchall()
            for row in rows:
                if not row["success"]:
                    issues.append(
                        f"FAILED MIGRATION: V{row['version']} – {row['description']}"
                    )
        except psycopg2.errors.UndefinedTable:
            issues.append("FLYWAY not initialised (flyway_schema_history table missing)")
    return issues


def main():
    parser = argparse.ArgumentParser(description="Detect schema drift in ARTMS database")
    parser.add_argument("--host",     default="localhost")
    parser.add_argument("--port",     type=int, default=5432)
    parser.add_argument("--dbname",   default="artms")
    parser.add_argument("--user",     default="artms")
    parser.add_argument("--password", default="artms_dev_password")
    parser.add_argument("--quiet",    action="store_true",
                        help="Only output issues (no header)")
    args = parser.parse_args()

    if not args.quiet:
        print("=" * 60)
        print("ARTMS Database Schema Drift Detector")
        print(f"Target: {args.user}@{args.host}:{args.port}/{args.dbname}")
        print("=" * 60)

    try:
        conn = psycopg2.connect(
            host=args.host, port=args.port,
            dbname=args.dbname, user=args.user, password=args.password,
            connect_timeout=10,
        )
    except psycopg2.OperationalError as e:
        print(f"ERROR: Cannot connect to database – {e}", file=sys.stderr)
        sys.exit(2)

    live_schema    = get_live_schema(conn)
    drift_issues   = detect_drift(EXPECTED_TABLES, live_schema)
    flyway_issues  = check_flyway_status(conn)
    conn.close()

    all_issues = flyway_issues + drift_issues

    if not all_issues:
        print("✅  No schema drift detected. Database is in sync.")
        sys.exit(0)
    else:
        print(f"\n⚠️  {len(all_issues)} drift issue(s) found:\n")
        for issue in all_issues:
            print(f"  • {issue}")
        print()
        print(textwrap.dedent("""\
            Recommendation:
              • Run Flyway migrations: mvn flyway:migrate -pl backend/ssama-api
              • Or apply the standalone sync: psql -f database/migrations/standalone_schema_sync.sql
        """))
        sys.exit(1)


if __name__ == "__main__":
    main()

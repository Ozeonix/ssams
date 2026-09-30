#!/usr/bin/env python3
"""
SSAMS / ARTMS – Batch Onboarding CSV Importer & Validator
==========================================================
Validates and converts batch onboarding CSVs (Students & Staff)
into idempotent, production-ready PostgreSQL SQL import batches.

Target Institution: Shree Susanskrit Secondary School (or any SSAMS tenant)

Usage:
    # 1. Validate & generate SQL for Students:
    python3 database/scripts/import_onboarding_batch.py \
        --type students \
        --input database/templates/students_onboarding_template.csv \
        --tenant-id e1000000-0000-0000-0000-000000000001 \
        --academic-year-id e1000000-0000-0000-0000-000000000002 \
        --output-sql database/data/imported_students_batch.sql \
        --rejected-csv database/data/rejected_students.csv

    # 2. Validate & generate SQL for Staff:
    python3 database/scripts/import_onboarding_batch.py \
        --type staff \
        --input database/templates/staff_onboarding_template.csv \
        --tenant-id e1000000-0000-0000-0000-000000000001 \
        --output-sql database/data/imported_staff_batch.sql \
        --rejected-csv database/data/rejected_staff.csv

    # 3. Dry-run validation only (no SQL output):
    python3 database/scripts/import_onboarding_batch.py \
        --type students \
        --input database/templates/students_onboarding_template.csv \
        --dry-run
"""

import argparse
import csv
import re
import sys
import uuid
from datetime import datetime
from pathlib import Path

# Defaults
DEFAULT_TENANT_ID = "e1000000-0000-0000-0000-000000000001"
DEFAULT_AY_ID = "e1000000-0000-0000-0000-000000000002"
DEFAULT_PASS_HASH = "$argon2id$v=19$m=65536,t=3,p=1$c29tZXNhbHQ$abcdefghijklmnopqrstuvwxyz1234567890"

# Allowed enums
VALID_GENDERS = {"MALE", "FEMALE", "OTHER"}
VALID_RELATIONS = {"FATHER", "MOTHER", "GUARDIAN", "SIBLING", "OTHER"}
VALID_ROLES = {"INSTITUTION_ADMIN", "ACCOUNTANT", "EXAM_CONTROLLER", "TEACHER", "STUDENT"}

# Regular expressions
PHONE_REGEX = re.compile(r"^\+977-[0-9]{10}$|^9[78][0-9]{8}$")
EMAIL_REGEX = re.compile(r"^[^@\s]+@[^@\s]+\.[^@\s]+$")
DATE_REGEX = re.compile(r"^\d{4}-\d{2}-\d{2}$")


def escape_sql(val: str | None) -> str:
    if val is None or val.strip() == "":
        return "NULL"
    cleaned = val.strip().replace("'", "''")
    return f"'{cleaned}'"


def normalize_phone(phone_str: str | None) -> str | None:
    if not phone_str:
        return None
    val = phone_str.strip().replace(" ", "").replace("-", "")
    if val.startswith("+977"):
        val = val[4:]
    if len(val) == 10 and (val.startswith("98") or val.startswith("97")):
        return f"+977-{val}"
    return phone_str.strip()


def validate_student_row(row: dict, seen_admissions: set) -> list[str]:
    errors = []
    adm = row.get("admission_no", "").strip()
    if not adm:
        errors.append("Missing required field: admission_no")
    elif adm in seen_admissions:
        errors.append(f"Duplicate admission_no in batch: {adm}")
    else:
        seen_admissions.add(adm)

    if not row.get("first_name", "").strip():
        errors.append("Missing required field: first_name")
    if not row.get("last_name", "").strip():
        errors.append("Missing required field: last_name")

    gender = row.get("gender", "").strip().upper()
    if not gender or gender not in VALID_GENDERS:
        errors.append(f"Invalid gender '{gender}'. Allowed: {', '.join(sorted(VALID_GENDERS))}")

    dob_ad = row.get("date_of_birth_ad", "").strip()
    if dob_ad and not DATE_REGEX.match(dob_ad):
        errors.append(f"Invalid Gregorian date_of_birth_ad '{dob_ad}'. Must be YYYY-MM-DD")

    dob_bs = row.get("date_of_birth_bs", "").strip()
    if dob_bs and not DATE_REGEX.match(dob_bs):
        errors.append(f"Invalid Bikram Sambat date_of_birth_bs '{dob_bs}'. Must be YYYY-MM-DD")

    if not dob_ad and not dob_bs:
        errors.append("At least one date of birth (date_of_birth_ad or date_of_birth_bs) is required")

    if not row.get("grade_level", "").strip():
        errors.append("Missing required field: grade_level")
    if not row.get("section", "").strip():
        errors.append("Missing required field: section")

    # Guardian
    if not row.get("guardian_name", "").strip():
        errors.append("Missing required field: guardian_name")
    rel = row.get("guardian_relation", "").strip().upper()
    if not rel or rel not in VALID_RELATIONS:
        errors.append(f"Invalid guardian_relation '{rel}'. Allowed: {', '.join(sorted(VALID_RELATIONS))}")

    g_phone = normalize_phone(row.get("guardian_phone", ""))
    if not g_phone or not PHONE_REGEX.match(g_phone):
        errors.append(f"Invalid guardian_phone '{row.get('guardian_phone')}'. Expected 10-digit Nepal mobile")

    email = row.get("student_email", "").strip()
    if email and not EMAIL_REGEX.match(email):
        errors.append(f"Invalid student_email format '{email}'")

    return errors


def validate_staff_row(row: dict, seen_codes: set, seen_emails: set) -> list[str]:
    errors = []
    code = row.get("employee_code", "").strip()
    if not code:
        errors.append("Missing required field: employee_code")
    elif code in seen_codes:
        errors.append(f"Duplicate employee_code in batch: {code}")
    else:
        seen_codes.add(code)

    if not row.get("first_name", "").strip():
        errors.append("Missing required field: first_name")
    if not row.get("last_name", "").strip():
        errors.append("Missing required field: last_name")
    if not row.get("designation", "").strip():
        errors.append("Missing required field: designation")

    role = row.get("role", "").strip().upper()
    if not role or role not in VALID_ROLES:
        errors.append(f"Invalid role '{role}'. Allowed: {', '.join(sorted(VALID_ROLES))}")

    phone = normalize_phone(row.get("phone", ""))
    if not phone or not PHONE_REGEX.match(phone):
        errors.append(f"Invalid phone '{row.get('phone')}'. Expected 10-digit Nepal mobile")

    email = row.get("email", "").strip().lower()
    if not email or not EMAIL_REGEX.match(email):
        errors.append(f"Invalid or missing email '{row.get('email')}'")
    elif email in seen_emails:
        errors.append(f"Duplicate email in batch: {email}")
    else:
        seen_emails.add(email)

    return errors


def process_students(
    input_path: Path,
    tenant_id: str,
    ay_id: str,
    output_sql_path: Path | None,
    rejected_path: Path | None,
    dry_run: bool,
):
    print(f"\n[SSAMS Batch Onboarding] Processing Students CSV: {input_path}")
    print(f"Tenant ID:        {tenant_id}")
    print(f"Academic Year ID: {ay_id}")

    valid_rows = []
    rejected_rows = []
    seen_admissions = set()

    with open(input_path, mode="r", encoding="utf-8-sig") as f:
        reader = csv.DictReader(f)
        for row_idx, row in enumerate(reader, start=2):
            errs = validate_student_row(row, seen_admissions)
            if errs:
                row_copy = dict(row)
                row_copy["_row_number"] = row_idx
                row_copy["_validation_errors"] = " | ".join(errs)
                rejected_rows.append(row_copy)
            else:
                row["_row_number"] = row_idx
                valid_rows.append(row)

    print(f"-> Total Rows Evaluated: {len(valid_rows) + len(rejected_rows)}")
    print(f"-> Valid Rows:           {len(valid_rows)}")
    print(f"-> Rejected Rows:        {len(rejected_rows)}")

    if rejected_rows and rejected_path:
        rejected_path.parent.mkdir(parents=True, exist_ok=True)
        fieldnames = ["_row_number", "_validation_errors"] + [
            k for k in rejected_rows[0].keys() if not k.startswith("_")
        ]
        with open(rejected_path, mode="w", encoding="utf-8", newline="") as rf:
            writer = csv.DictWriter(rf, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(rejected_rows)
        print(f"-> Rejected rows written to: {rejected_path}")

    if dry_run or not output_sql_path:
        return

    output_sql_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_sql_path, mode="w", encoding="utf-8") as sf:
        sf.write("-- ==============================================================================\n")
        sf.write(f"-- SSAMS Student Batch Import SQL - Generated on {datetime.now().isoformat()}\n")
        sf.write(f"-- Target Tenant: {tenant_id} | Academic Year: {ay_id}\n")
        sf.write("-- ==============================================================================\n\n")
        sf.write("BEGIN;\n\n")

        for r in valid_rows:
            s_id = str(uuid.uuid4())
            g_id = str(uuid.uuid4())
            e_id = str(uuid.uuid4())

            adm = r.get("admission_no", "").strip()
            reg = r.get("registration_no", "").strip() or None
            sym = r.get("symbol_no", "").strip() or None
            fn = r.get("first_name", "").strip()
            mn = r.get("middle_name", "").strip() or None
            ln = r.get("last_name", "").strip()
            gender = r.get("gender", "").strip().upper()
            dob = r.get("date_of_birth_ad", "").strip() or r.get("date_of_birth_bs", "").strip()
            phone = normalize_phone(r.get("student_phone"))
            email = r.get("student_email", "").strip() or None
            addr = r.get("address", "").strip() or None
            g_name = r.get("guardian_name", "").strip()
            g_rel = r.get("guardian_relation", "").strip().upper()
            g_phone = normalize_phone(r.get("guardian_phone"))
            g_email = r.get("guardian_email", "").strip() or None
            grade = r.get("grade_level", "").strip()
            sec = r.get("section", "").strip()
            roll = r.get("roll_no", "").strip() or None

            # Student INSERT
            sf.write(f"-- Student: {adm} ({fn} {ln})\n")
            sf.write(
                f"INSERT INTO student (id, tenant_id, admission_no, registration_no, symbol_no, "
                f"first_name, middle_name, last_name, date_of_birth, gender, phone, email, address, status)\n"
                f"VALUES ('{s_id}', '{tenant_id}', {escape_sql(adm)}, {escape_sql(reg)}, {escape_sql(sym)}, "
                f"{escape_sql(fn)}, {escape_sql(mn)}, {escape_sql(ln)}, {escape_sql(dob)}::date, '{gender}', "
                f"{escape_sql(phone)}, {escape_sql(email)}, {escape_sql(addr)}, 'ACTIVE')\n"
                f"ON CONFLICT (tenant_id, admission_no) DO UPDATE SET "
                f"first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name, updated_at = now();\n"
            )

            # Guardian INSERT
            sf.write(
                f"INSERT INTO student_guardian (id, student_id, name, relationship, phone, email, address, is_primary)\n"
                f"VALUES ('{g_id}', (SELECT id FROM student WHERE tenant_id = '{tenant_id}' AND admission_no = {escape_sql(adm)}), "
                f"{escape_sql(g_name)}, '{g_rel}', {escape_sql(g_phone)}, {escape_sql(g_email)}, {escape_sql(addr)}, true)\n"
                f"ON CONFLICT DO NOTHING;\n"
            )

            # Enrollment INSERT
            sf.write(
                f"INSERT INTO enrollment (id, tenant_id, student_id, class_group_id, academic_year_id, roll_no, status, enrolled_at)\n"
                f"VALUES ('{e_id}', '{tenant_id}', "
                f"(SELECT id FROM student WHERE tenant_id = '{tenant_id}' AND admission_no = {escape_sql(adm)}), "
                f"(SELECT id FROM class_group WHERE tenant_id = '{tenant_id}' AND grade_level = {escape_sql(grade)} AND section = {escape_sql(sec)} LIMIT 1), "
                f"'{ay_id}', {escape_sql(roll)}, 'ACTIVE', CURRENT_DATE)\n"
                f"ON CONFLICT (tenant_id, student_id, academic_year_id) DO NOTHING;\n\n"
            )

        sf.write("COMMIT;\n")
    print(f"-> Generated SQL written to: {output_sql_path}")


def process_staff(
    input_path: Path,
    tenant_id: str,
    output_sql_path: Path | None,
    rejected_path: Path | None,
    dry_run: bool,
):
    print(f"\n[SSAMS Batch Onboarding] Processing Staff CSV: {input_path}")
    print(f"Tenant ID: {tenant_id}")

    valid_rows = []
    rejected_rows = []
    seen_codes = set()
    seen_emails = set()

    with open(input_path, mode="r", encoding="utf-8-sig") as f:
        reader = csv.DictReader(f)
        for row_idx, row in enumerate(reader, start=2):
            errs = validate_staff_row(row, seen_codes, seen_emails)
            if errs:
                row_copy = dict(row)
                row_copy["_row_number"] = row_idx
                row_copy["_validation_errors"] = " | ".join(errs)
                rejected_rows.append(row_copy)
            else:
                row["_row_number"] = row_idx
                valid_rows.append(row)

    print(f"-> Total Rows Evaluated: {len(valid_rows) + len(rejected_rows)}")
    print(f"-> Valid Rows:           {len(valid_rows)}")
    print(f"-> Rejected Rows:        {len(rejected_rows)}")

    if rejected_rows and rejected_path:
        rejected_path.parent.mkdir(parents=True, exist_ok=True)
        fieldnames = ["_row_number", "_validation_errors"] + [
            k for k in rejected_rows[0].keys() if not k.startswith("_")
        ]
        with open(rejected_path, mode="w", encoding="utf-8", newline="") as rf:
            writer = csv.DictWriter(rf, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(rejected_rows)
        print(f"-> Rejected rows written to: {rejected_path}")

    if dry_run or not output_sql_path:
        return

    output_sql_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_sql_path, mode="w", encoding="utf-8") as sf:
        sf.write("-- ==============================================================================\n")
        sf.write(f"-- SSAMS Staff Batch Import SQL - Generated on {datetime.now().isoformat()}\n")
        sf.write(f"-- Target Tenant: {tenant_id}\n")
        sf.write("-- ==============================================================================\n\n")
        sf.write("BEGIN;\n\n")

        for r in valid_rows:
            u_id = str(uuid.uuid4())
            m_id = str(uuid.uuid4())
            t_id = str(uuid.uuid4())

            code = r.get("employee_code", "").strip()
            fn = r.get("first_name", "").strip()
            ln = r.get("last_name", "").strip()
            desig = r.get("designation", "").strip()
            dept = r.get("department_code", "").strip() or None
            role = r.get("role", "").strip().upper()
            phone = normalize_phone(r.get("phone"))
            email = r.get("email", "").strip().lower()

            username = email.split("@")[0]

            sf.write(f"-- Staff: {code} ({fn} {ln} - {desig})\n")
            # User account
            sf.write(
                f"INSERT INTO user_account (id, username, email, password_hash, status)\n"
                f"VALUES ('{u_id}', {escape_sql(username)}, {escape_sql(email)}, '{DEFAULT_PASS_HASH}', 'ACTIVE')\n"
                f"ON CONFLICT (username) DO NOTHING;\n"
            )
            # Tenant membership
            sf.write(
                f"INSERT INTO user_tenant_membership (id, tenant_id, user_id, status)\n"
                f"VALUES ('{m_id}', '{tenant_id}', (SELECT id FROM user_account WHERE username = {escape_sql(username)}), 'ACTIVE')\n"
                f"ON CONFLICT (tenant_id, user_id) DO NOTHING;\n"
            )
            # Role mapping
            sf.write(
                f"INSERT INTO user_role (membership_id, role_id)\n"
                f"VALUES (\n"
                f"    (SELECT id FROM user_tenant_membership WHERE tenant_id = '{tenant_id}' AND user_id = (SELECT id FROM user_account WHERE username = {escape_sql(username)})),\n"
                f"    (SELECT id FROM role WHERE (tenant_id = '{tenant_id}' OR tenant_id IS NULL) AND code = '{role}' LIMIT 1)\n"
                f") ON CONFLICT DO NOTHING;\n"
            )
            # Teacher profile
            sf.write(
                f"INSERT INTO teacher (id, tenant_id, user_id, employee_code, first_name, last_name, department_id, designation, phone, email, status)\n"
                f"VALUES ('{t_id}', '{tenant_id}', (SELECT id FROM user_account WHERE username = {escape_sql(username)}), "
                f"{escape_sql(code)}, {escape_sql(fn)}, {escape_sql(ln)}, "
                f"(SELECT id FROM department WHERE tenant_id = '{tenant_id}' AND code = {escape_sql(dept)} LIMIT 1), "
                f"{escape_sql(desig)}, {escape_sql(phone)}, {escape_sql(email)}, 'ACTIVE')\n"
                f"ON CONFLICT (tenant_id, user_id) DO NOTHING;\n\n"
            )

        sf.write("COMMIT;\n")
    print(f"-> Generated SQL written to: {output_sql_path}")


def main():
    parser = argparse.ArgumentParser(description="SSAMS Batch Onboarding Importer and Validator")
    parser.add_argument("--type", choices=["students", "staff"], required=True, help="Entity type to import")
    parser.add_argument("--input", type=Path, required=True, help="Input CSV path")
    parser.add_argument("--tenant-id", default=DEFAULT_TENANT_ID, help="Tenant UUID")
    parser.add_argument("--academic-year-id", default=DEFAULT_AY_ID, help="Academic Year UUID (for students)")
    parser.add_argument("--output-sql", type=Path, help="Output SQL script path")
    parser.add_argument("--rejected-csv", type=Path, help="Output rejected rows CSV path")
    parser.add_argument("--dry-run", action="store_true", help="Perform validation only")

    args = parser.parse_args()

    if not args.input.exists():
        print(f"ERROR: Input file does not exist: {args.input}", file=sys.stderr)
        sys.exit(1)

    if args.type == "students":
        process_students(
            input_path=args.input,
            tenant_id=args.tenant_id,
            ay_id=args.academic_year_id,
            output_sql_path=args.output_sql,
            rejected_path=args.rejected_csv,
            dry_run=args.dry_run,
        )
    elif args.type == "staff":
        process_staff(
            input_path=args.input,
            tenant_id=args.tenant_id,
            output_sql_path=args.output_sql,
            rejected_path=args.rejected_csv,
            dry_run=args.dry_run,
        )


if __name__ == "__main__":
    main()

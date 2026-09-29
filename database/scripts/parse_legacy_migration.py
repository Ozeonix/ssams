#!/usr/bin/env python3
"""
SSAMS / ARTMS – Legacy Marksheet Migration CSV Parser
======================================================
Reads the legacy student CSV (see database/data/legacy_marksheet_migration_template.csv)
and generates:
  1. Validated, cleaned data report (console/log)
  2. SQL INSERT file ready to apply against the ARTMS database
  3. Rejected rows file (CSV) with reason column

Usage:
    python3 database/scripts/parse_legacy_migration.py \
        --input  database/data/legacy_students.csv \
        --tenant-id 00000000-0000-0000-0000-000000000001 \
        --output database/data/legacy_migration_output.sql \
        --rejected database/data/legacy_migration_rejected.csv

Required columns in input CSV:
    student_code, first_name, last_name, date_of_birth, gender,
    phone, email, guardian_name, guardian_phone, guardian_relation,
    admission_date, program_code, class_section, academic_year

Requirements:
    pip install pandas python-dateutil
"""

import argparse
import csv
import sys
import uuid
from datetime import datetime
from pathlib import Path

try:
    import pandas as pd
    from dateutil import parser as dateparser
except ImportError:
    print("ERROR: Install required packages: pip install pandas python-dateutil")
    sys.exit(1)

REQUIRED_COLUMNS = [
    "student_code", "first_name", "last_name", "date_of_birth",
    "gender", "phone", "email", "guardian_name", "guardian_phone",
    "guardian_relation", "admission_date", "program_code",
    "class_section", "academic_year",
]

VALID_GENDERS = {"MALE", "FEMALE", "OTHER"}
VALID_RELATIONS = {"FATHER", "MOTHER", "GUARDIAN", "SIBLING", "SPOUSE", "OTHER"}


def pg_str(s: str) -> str:
    return str(s).replace("'", "''").strip()


def parse_date(val: str) -> str | None:
    try:
        return dateparser.parse(val.strip()).strftime("%Y-%m-%d")
    except Exception:
        return None


def validate_row(row: dict, line_num: int) -> list[str]:
    errors = []
    for col in REQUIRED_COLUMNS:
        if not str(row.get(col, "")).strip():
            errors.append(f"Missing required field: {col}")

    dob = parse_date(str(row.get("date_of_birth", "")))
    if not dob:
        errors.append(f"Invalid date_of_birth: {row.get('date_of_birth')}")

    adm = parse_date(str(row.get("admission_date", "")))
    if not adm:
        errors.append(f"Invalid admission_date: {row.get('admission_date')}")

    gender = str(row.get("gender", "")).upper()
    if gender not in VALID_GENDERS:
        errors.append(f"Invalid gender '{gender}' – must be one of {VALID_GENDERS}")

    relation = str(row.get("guardian_relation", "")).upper()
    if relation not in VALID_RELATIONS:
        errors.append(f"Invalid guardian_relation '{relation}'")

    phone = str(row.get("phone", "")).strip()
    if phone and len(phone) < 7:
        errors.append(f"Phone too short: {phone}")

    code = str(row.get("student_code", "")).strip()
    if not code:
        errors.append("student_code is required")

    return errors


def generate_sql(row: dict, tenant_id: str) -> list[str]:
    stmts = []
    sid = str(uuid.uuid4())
    gid = str(uuid.uuid4())

    dob = parse_date(str(row["date_of_birth"]))
    adm = parse_date(str(row["admission_date"]))
    gender = str(row["gender"]).upper()
    relation = str(row["guardian_relation"]).upper()

    # Student INSERT
    stmts.append(
        f"INSERT INTO student "
        f"(id, tenant_id, student_code, first_name, last_name, date_of_birth, "
        f"gender, phone, email, status, admission_date) "
        f"VALUES ("
        f"'{sid}', '{tenant_id}', '{pg_str(row['student_code'])}', "
        f"'{pg_str(row['first_name'])}', '{pg_str(row['last_name'])}', "
        f"'{dob}', '{gender}', "
        f"'{pg_str(row.get('phone',''))}', "
        f"'{pg_str(row.get('email',''))}', "
        f"'ACTIVE', '{adm}'"
        f") ON CONFLICT (tenant_id, student_code) DO NOTHING;"
    )

    # Guardian INSERT
    gname_parts = pg_str(row.get("guardian_name", "Guardian")).split(" ", 1)
    g_first = gname_parts[0]
    g_last  = gname_parts[1] if len(gname_parts) > 1 else "N/A"
    stmts.append(
        f"INSERT INTO guardian "
        f"(id, tenant_id, student_id, first_name, last_name, relationship, phone, is_primary) "
        f"VALUES ("
        f"'{gid}', '{tenant_id}', '{sid}', "
        f"'{g_first}', '{g_last}', "
        f"'{relation}', "
        f"'{pg_str(row.get('guardian_phone',''))}', "
        f"true"
        f") ON CONFLICT DO NOTHING;"
    )

    return stmts


def main():
    parser = argparse.ArgumentParser(description="Parse legacy student CSV for ARTMS migration")
    parser.add_argument("--input",       required=True,  help="Input CSV file path")
    parser.add_argument("--tenant-id",   required=True,  help="Target tenant UUID")
    parser.add_argument("--output",      required=True,  help="Output SQL file path")
    parser.add_argument("--rejected",    required=True,  help="Rejected rows CSV path")
    args = parser.parse_args()

    input_path    = Path(args.input)
    output_path   = Path(args.output)
    rejected_path = Path(args.rejected)

    if not input_path.exists():
        print(f"ERROR: Input file not found: {input_path}")
        sys.exit(1)

    # ── Read CSV ─────────────────────────────────────────────────────────────
    df = pd.read_csv(input_path, dtype=str, keep_default_na=False)
    df.columns = [c.strip().lower() for c in df.columns]

    # Check required columns
    missing_cols = [c for c in REQUIRED_COLUMNS if c not in df.columns]
    if missing_cols:
        print(f"ERROR: Missing columns in input CSV: {missing_cols}")
        sys.exit(1)

    total = len(df)
    print(f"Input: {total} rows from {input_path}")

    valid_rows    = []
    rejected_rows = []

    for i, row in df.iterrows():
        row_dict = row.to_dict()
        errors = validate_row(row_dict, i + 2)
        if errors:
            row_dict["__rejection_reasons"] = "; ".join(errors)
            rejected_rows.append(row_dict)
        else:
            valid_rows.append(row_dict)

    # ── Write rejected rows ───────────────────────────────────────────────────
    if rejected_rows:
        rejected_df = pd.DataFrame(rejected_rows)
        rejected_df.to_csv(rejected_path, index=False)
        print(f"Rejected: {len(rejected_rows)} rows → {rejected_path}")

    # ── Write SQL ─────────────────────────────────────────────────────────────
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8") as f:
        f.write("-- ============================================================\n")
        f.write("-- ARTMS Legacy Marksheet Migration SQL\n")
        f.write(f"-- Generated: {datetime.now().isoformat()}\n")
        f.write(f"-- Input: {input_path}\n")
        f.write(f"-- Valid rows: {len(valid_rows)} / Rejected: {len(rejected_rows)}\n")
        f.write("-- ============================================================\n\n")
        f.write("BEGIN;\n\n")

        for row in valid_rows:
            for stmt in generate_sql(row, args.tenant_id):
                f.write(stmt + "\n")
            f.write("\n")

        f.write("COMMIT;\n")
        f.write(f"\n-- Total students inserted: {len(valid_rows)} (ON CONFLICT DO NOTHING)\n")

    # ── Summary ───────────────────────────────────────────────────────────────
    print(f"\nMigration Summary")
    print(f"  Total rows   : {total}")
    print(f"  Valid        : {len(valid_rows)}")
    print(f"  Rejected     : {len(rejected_rows)}")
    print(f"  Output SQL   : {output_path}")
    print(f"  Rejected CSV : {rejected_path}")
    print(f"\nTo apply:")
    print(f"  psql -h <host> -U <user> -d <db> -f {output_path}")

    sys.exit(0 if not rejected_rows else 1)


if __name__ == "__main__":
    main()

# Data Migration Plan

## Purpose
Migrate existing school spreadsheets/paper-derived data into ARTMS without corrupting official records.

## Stages

1. Source inventory.
2. Define field mapping.
3. Clean/normalize.
4. Import to staging.
5. Validate.
6. Human review.
7. Commit.
8. Reconcile totals/counts.
9. Lock migration.
10. Archive source.

## Student Mapping

Typical fields:
- old registration number;
- admission number;
- name;
- DOB;
- class;
- section;
- academic year;
- roll/symbol number.

Do not assume these fields are globally unique without tenant/context.

## Marks Mapping

Map:
- exam;
- student;
- subject;
- component;
- full marks;
- pass marks;
- obtained marks;
- status;
- grade;
- grade point;
- source document.

Historical imported results should be stored as a snapshot with:
- source;
- import batch;
- imported_at;
- source version;
- migration operator.

## Reconciliation

For every import:
- source row count = accepted + rejected;
- student counts by class;
- subject counts;
- total marks;
- GPA samples;
- pass/fail totals.

## Rollback
Before final commit:
- create import batch;
- record all created IDs;
- support batch rollback only before official publication where policy permits.

Never delete official historical data as a casual rollback.

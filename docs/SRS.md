# Software Requirements Specification (SRS)

## 1. Scope
ARTMS is a multi-tenant academic management platform with a Flutter student application and a browser-based college administration application backed by Java/Spring Boot/PostgreSQL.

## 2. System Actors
- Platform Admin
- Institution Owner
- Principal
- Academic Admin
- Examination Officer
- Teacher
- Registrar
- Accountant
- Student
- Guardian (optional)
- Auditor

## 3. Functional Requirements

### Identity
- FR-AUTH-001 Register/invite staff.
- FR-AUTH-002 Authenticate users.
- FR-AUTH-003 Refresh/revoke sessions.
- FR-AUTH-004 Reset password.
- FR-AUTH-005 MFA for selected roles.
- FR-AUTH-006 Manage devices/sessions.

### Tenant
- FR-TEN-001 Create tenant.
- FR-TEN-002 Configure institution.
- FR-TEN-003 Configure branding.
- FR-TEN-004 Configure timezone/locale.
- FR-TEN-005 Enable modules.
- FR-TEN-006 Isolate tenant data.

### Academic
- FR-ACA-001 Academic year.
- FR-ACA-002 Terms/semesters.
- FR-ACA-003 Faculties/departments.
- FR-ACA-004 Programs/streams.
- FR-ACA-005 Grades/classes/sections.
- FR-ACA-006 Curriculum versions.
- FR-ACA-007 Subjects and components.
- FR-ACA-008 Credit-hour configuration.

### Student
- FR-STU-001 Admission.
- FR-STU-002 Student profile.
- FR-STU-003 Enrollment.
- FR-STU-004 Promotion/repetition.
- FR-STU-005 Transfer/withdrawal.
- FR-STU-006 Academic history.

### Assessment
- FR-EXM-001 Create examination.
- FR-EXM-002 Schedule components.
- FR-EXM-003 Assign teachers/examiners.
- FR-EXM-004 Enter marks.
- FR-EXM-005 Bulk import marks.
- FR-EXM-006 Verify marks.
- FR-EXM-007 Approve result.
- FR-EXM-008 Publish/lock result.
- FR-EXM-009 Correct published result through workflow.

### Calculation
- FR-GRD-001 Configure grading scale.
- FR-GRD-002 Calculate component grade.
- FR-GRD-003 Calculate subject result.
- FR-GRD-004 Calculate GPA.
- FR-GRD-005 Calculate cumulative GPA/CGPA.
- FR-GRD-006 Apply pass rules.
- FR-GRD-007 Snapshot rule version into result.

### Attendance
- FR-ATT-001 Create attendance session.
- FR-ATT-002 Record attendance.
- FR-ATT-003 Correct attendance.
- FR-ATT-004 Calculate percentage.
- FR-ATT-005 Publish attendance alert.

### Communication
- FR-COM-001 Create notice.
- FR-COM-002 Target audience.
- FR-COM-003 Publish/schedule.
- FR-COM-004 Push notification.
- FR-COM-005 Track delivery/read state.

### Documents
- FR-DOC-001 Template management.
- FR-DOC-002 Generate PDF.
- FR-DOC-003 Verify document.
- FR-DOC-004 Issue/revoke document.
- FR-DOC-005 Public verification endpoint using signed token.

### Reporting
- FR-REP-001 Enrollment reports.
- FR-REP-002 Result reports.
- FR-REP-003 Attendance reports.
- FR-REP-004 Audit reports.
- FR-REP-005 Export CSV/XLSX/PDF.

## 4. Business Rules

1. A record belongs to exactly one tenant unless explicitly designated platform-global.
2. Published academic result snapshots are immutable.
3. Marks cannot exceed configured full marks.
4. Absent/withheld/expelled states are not equivalent to numeric zero.
5. Every grading calculation references a grading-rule version.
6. A result cannot be published without required verification/approval.
7. Role permissions are additive only through explicit grants.
8. Soft deletion is preferred for business entities with historical references.
9. Academic year changes do not mutate historical result snapshots.
10. Student-visible result data comes only from published snapshots.

## 5. Non-Functional Requirements
- Secure by default.
- Horizontally scalable API.
- PostgreSQL transactional consistency.
- Asynchronous heavy jobs.
- Structured logging.
- Health checks.
- Backups.
- Automated tests.
- Versioned APIs.
- Migration-based schema changes.

## 6. External Interfaces
- REST/JSON API.
- WebSocket/STOMP.
- Push provider adapter.
- Email/SMS adapter.
- S3-compatible storage.
- Optional payment gateway adapter.

## 7. Error Requirements
All APIs return consistent error envelopes, correlation IDs, field validation errors and machine-readable error codes.

## 8. Data Retention
Retention is configurable per tenant and must respect institutional/legal requirements. Published academic records should not be physically deleted through ordinary admin UI.

## 9. Acceptance
Requirements are accepted only when corresponding automated tests and audit evidence exist.

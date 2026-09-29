# Features

## P0 — Required
- Multi-tenancy
- Authentication/session management
- RBAC
- Institution configuration
- Academic years
- Programs/classes/sections
- Curriculum
- Subjects/components
- Student management
- Teacher management
- Enrollment
- Exams
- Theory/practical marks
- Grading rules
- GPA
- Result verification
- Result approval
- Result publication
- Immutable snapshots
- Attendance
- Notices
- Notifications
- Student mobile result view
- PDF grade sheet
- Audit logs
- Import/export
- Backups/restore procedures
- Observability

## P1
- Timetable
- Fees
- Guardian portal
- SMS/email
- Advanced reports
- Document verification portal
- Bulk promotion
- Re-examination workflow

## P2
- Payment gateway adapters
- Library management
- Hostel
- Transport
- Assignment/LMS
- Alumni
- BI warehouse
- Advanced search

## Feature Flags

Each optional module should be independently enabled per tenant:
- `attendance`
- `timetable`
- `finance`
- `guardian`
- `sms`
- `email`
- `advanced_reports`
- `document_verification`

Feature flags are evaluated server-side for authorization and UI display.

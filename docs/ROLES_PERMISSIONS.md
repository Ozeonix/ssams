# Roles and Permissions

## Permission Naming
`<domain>:<action>`

Examples:
- `student:read`
- `student:create`
- `student:update`
- `marks:read`
- `marks:write`
- `marks:submit`
- `result:verify`
- `result:approve`
- `result:publish`
- `result:correct`
- `audit:read`
- `settings:manage`

## Suggested Roles

### Platform Admin
All platform permissions. Cannot casually impersonate tenant users; any support impersonation is time-limited and audited.

### Institution Owner
Institution configuration, users, roles, academic structure, all reports.

### Principal/Head
Read all operational data; approve designated workflows.

### Academic Admin
Academic years, classes, curriculum, enrollment, timetable.

### Examination Officer
Exams, marks, verification, result preparation, publication where granted.

### Teacher
Assigned class/subject attendance and marks only.

### Registrar
Student records, documents, transcripts.

### Accountant
Finance only plus minimal student identity.

### Student
Own profile, own timetable, own attendance, own published results, own notices.

### Guardian
Linked student's permitted read-only information.

### Auditor
Read-only records and audit logs.

## Object-Level Rules

Role permission alone is insufficient:
- teacher may edit only assigned subject/class;
- student may access only self;
- guardian may access only linked student;
- institution users may access only their tenant.

## Sensitive Permissions

Require stronger authorization and audit:
- `result:approve`
- `result:publish`
- `result:correct`
- `student:delete`
- `settings:manage`
- `role:manage`
- `finance:refund`

Use separation of duties where practical.

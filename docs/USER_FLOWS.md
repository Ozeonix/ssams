# User Flows

## 1. Institution Onboarding

```text
Platform Admin
 -> Create Tenant
 -> Configure Institution
 -> Configure Academic Year
 -> Create Admin
 -> Admin Login
 -> Configure Roles
 -> Configure Curriculum
 -> Import Students
 -> Ready
```

## 2. Student Enrollment

```text
Admin
 -> Create Student
 -> Assign Registration No
 -> Enroll in Academic Year/Class
 -> Assign Subjects
 -> Activate
 -> Student receives credentials
```

## 3. Teacher Attendance

```text
Teacher
 -> Select Class
 -> Select Subject/Period
 -> Create Session
 -> Mark Students
 -> Validate
 -> Submit
 -> Attendance Published/Available
 -> Student App Sync
```

## 4. Marks

```text
Exam Officer
 -> Create Exam
 -> Add Subjects
 -> Configure Components
 -> Open Marks
Teacher/Officer
 -> Enter Marks
 -> Save Draft
 -> Submit
Exam Officer
 -> Verify
 -> Resolve Errors
 -> Approve
 -> Calculate Snapshot
 -> Publish
Student
 -> Notification
 -> View Result
```

## 5. Result Correction

```text
Authorized User
 -> Open Published Result
 -> Request Correction
 -> Enter old/new value
 -> Provide reason
 -> Submit
Approver
 -> Review Audit
 -> Approve/Reject
System
 -> Create new result version
 -> Recalculate
 -> Re-publish if approved
 -> Notify affected student
```

## 6. Timetable Change

```text
Academic Admin
 -> Edit Timetable
 -> Conflict Check
 -> Save Draft
 -> Publish
System
 -> Create Event
 -> Notify affected users
 -> Student app refreshes
```

## 7. Document

```text
Registrar
 -> Select student/document
 -> Select approved result snapshot
 -> Generate
 -> Validate template
 -> Store PDF
 -> Issue
 -> Student downloads/view
```

## 8. Import

```text
Admin
 -> Upload CSV/XLSX
 -> Map columns
 -> Validate
 -> Show errors
 -> Confirm
 -> Async process
 -> Summary
 -> Audit event
```

## 9. Staff Permission

```text
Owner
 -> Roles
 -> Select Role
 -> Permissions
 -> Save
 -> Audit
```

## 10. Security Event

```text
Suspicious Login
 -> Rate Limit/Lock
 -> Security Log
 -> Notify admin where configured
 -> Admin review
```

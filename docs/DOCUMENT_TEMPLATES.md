# Document Templates

## Supported Types
- MARK_SHEET
- GRADE_SHEET
- TRANSCRIPT
- PROGRESS_REPORT
- CERTIFICATE
- ATTENDANCE_REPORT
- FEE_RECEIPT

## Template Model

```text
Template
 ├── tenant
 ├── type
 ├── version
 ├── status
 ├── page_size
 ├── orientation
 ├── body
 └── placeholders
```

## Placeholder Examples

`{{institution.name}}`
`{{student.name}}`
`{{student.registrationNo}}`
`{{academicYear.name}}`
`{{exam.name}}`
`{{result.gpa}}`
`{{subject.name}}`
`{{subject.creditHours}}`
`{{subject.theoryMarks}}`
`{{subject.practicalMarks}}`
`{{subject.grade}}`
`{{subject.gradePoint}}`

## Security
Templates cannot execute arbitrary server-side code.
Only approved placeholder expressions are allowed.

## Versioning
Issued documents reference:
- template version;
- result snapshot version;
- generation timestamp;
- checksum.

## Verification
Use a signed verification token/QR containing only a non-sensitive document identifier.
Verification endpoint confirms:
- document exists;
- status valid/revoked;
- issuer tenant;
- issue date.

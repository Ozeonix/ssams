# API Specification

Base URL:
`/api/v1`

## Conventions

JSON request/response.
Authentication:
`Authorization: Bearer <access-token>`

Every response may include:
- `requestId`
- `timestamp`

### Error envelope

```json
{
  "requestId": "uuid",
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "fieldErrors": [
    {"field": "marks", "code": "MAX_EXCEEDED", "message": "Marks exceed full marks"}
  ]
}
```

## Authentication

### POST /auth/login
Input:
- tenantCode
- username
- password

Returns:
- accessToken
- refreshToken
- expiresIn
- user
- permissions

### POST /auth/refresh
Rotate refresh token.

### POST /auth/logout
Revoke current session.

## Institution

`GET /institutions/me`
`PATCH /institutions/me`
`GET /institutions/me/settings`

## Academic

`GET /academic-years`
`POST /academic-years`
`GET /academic-years/{id}`
`PATCH /academic-years/{id}`

`GET /programs`
`POST /programs`

`GET /classes`
`POST /classes`

`GET /subjects`
`POST /subjects`

`GET /curricula`
`POST /curricula`
`POST /curricula/{id}/publish`

## Students

`GET /students`
Query:
- page
- size
- search
- classId
- status

`POST /students`
`GET /students/{id}`
`PATCH /students/{id}`
`POST /students/{id}/enrollments`

## Attendance

`POST /attendance/sessions`
`GET /attendance/sessions`
`PUT /attendance/sessions/{id}/records`
`POST /attendance/sessions/{id}/submit`
`GET /students/{id}/attendance`

## Exams

`GET /exams`
`POST /exams`
`GET /exams/{id}`
`POST /exams/{id}/subjects`
`POST /exams/{id}/schedule`
`POST /exams/{id}/open-mark-entry`

## Marks

`GET /exams/{examId}/subjects/{subjectId}/marks`
`PUT /exams/{examId}/subjects/{subjectId}/marks`

Example:

```json
{
  "studentId": "uuid",
  "componentId": "uuid",
  "rawMarks": 36,
  "status": "PRESENT"
}
```

Bulk import:
`POST /imports/marks`
`GET /imports/{jobId}`

## Results

`POST /exams/{id}/calculate-preview`
`POST /exams/{id}/submit-for-verification`
`POST /exams/{id}/approve`
`POST /exams/{id}/publish`
`POST /results/{resultId}/correction-request`
`POST /corrections/{id}/approve`

Student:
`GET /me/results`
`GET /me/results/{id}`
`GET /me/results/{id}/document`

## Notices

`GET /notices`
`POST /notices`
`PATCH /notices/{id}`
`POST /notices/{id}/publish`

## Notifications

`GET /me/notifications`
`POST /me/notifications/{id}/read`
`POST /me/notifications/read-all`

## Timetable

`GET /me/timetable`
`GET /classes/{id}/timetable`
`POST /timetable`
`PATCH /timetable/{id}`
`POST /timetable/{id}/publish`

## Documents

`GET /document-templates`
`POST /document-templates`
`POST /documents/generate`
`GET /documents/{id}`
`POST /documents/{id}/revoke`

## Reports

`GET /reports/enrollment`
`GET /reports/attendance`
`GET /reports/results`
`POST /reports/export`

## Admin

`GET /roles`
`POST /roles`
`PUT /roles/{id}/permissions`

`GET /audit-logs`

## WebSocket

Endpoint:
`/ws`

Topics:
- `/user/queue/notifications`
- `/topic/tenant/{tenantId}/notices`
- `/topic/class/{classId}/timetable`
- `/topic/exam/{examId}/status`

The server must verify authorization before subscription.

## API Rules

- Pagination required for collection endpoints.
- Never expose internal database IDs where a public identifier is safer.
- Never expose unpublished result snapshots to student endpoints.
- Use idempotency keys for state-changing commands where duplicate execution is harmful.
- Use optimistic locking for editable resources.
- Return `409 CONFLICT` for stale versions.

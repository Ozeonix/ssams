# API Examples

## Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "tenantCode": "DEMO-COLLEGE",
  "username": "exam.officer",
  "password": "example-only"
}
```

## Configure Subject Components

```json
{
  "subjectCode": "CP",
  "subjectName": "Computer Programming",
  "components": [
    {
      "code": "TH",
      "name": "Theory",
      "assessmentType": "THEORY",
      "fullMarks": 40,
      "passMarks": 16,
      "weight": 0.4,
      "creditHours": 1.56
    },
    {
      "code": "PR",
      "name": "Practical",
      "assessmentType": "PRACTICAL",
      "fullMarks": 60,
      "passMarks": 24,
      "weight": 0.6,
      "creditHours": 1.56
    }
  ]
}
```

The values above are an illustrative configuration pattern, not an assertion that every institution must use these values.

## Mark

```json
{
  "studentId": "uuid",
  "componentId": "uuid",
  "rawMarks": 36,
  "status": "PRESENT"
}
```

## Publish Result

```http
POST /api/v1/exams/{examId}/publish
Idempotency-Key: uuid
```

## Event

```json
{
  "eventId": "uuid",
  "type": "RESULT_PUBLISHED",
  "version": 1,
  "aggregateId": "result-uuid",
  "payload": {
    "examId": "uuid",
    "studentId": "uuid"
  }
}
```

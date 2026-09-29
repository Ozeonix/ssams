# Project Structure

```text
artms/
├── backend/
│   ├── src/main/java/com/artms/
│   │   ├── identity/
│   │   ├── tenant/
│   │   ├── academic/
│   │   ├── student/
│   │   ├── staff/
│   │   ├── enrollment/
│   │   ├── attendance/
│   │   ├── timetable/
│   │   ├── assessment/
│   │   ├── grading/
│   │   ├── result/
│   │   ├── document/
│   │   ├── notification/
│   │   ├── finance/
│   │   ├── reporting/
│   │   ├── audit/
│   │   └── shared/
│   ├── src/main/resources/
│   │   ├── db/migration/
│   │   ├── templates/
│   │   └── application.yml
│   └── src/test/
│
├── mobile/
│   └── lib/
│       ├── core/
│       ├── features/
│       │   ├── auth/
│       │   ├── home/
│       │   ├── academics/
│       │   ├── attendance/
│       │   ├── timetable/
│       │   ├── results/
│       │   ├── notices/
│       │   └── profile/
│       └── main.dart
│
├── admin-web/
│   └── src/
│       ├── core/
│       ├── features/
│       └── main.*
│
├── infrastructure/
│   ├── docker/
│   ├── nginx/
│   ├── monitoring/
│   └── deployment/
│
├── docs/
├── scripts/
├── .env.example
├── docker-compose.yml
└── README.md
```

## Backend Module Rule

Do not allow arbitrary module-to-module database access.
Cross-module behavior must go through application interfaces/domain events.

## Mobile Rule
Feature code cannot bypass the central API/security layer.

## Admin Web Rule
UI permissions mirror backend permissions but never replace backend authorization.

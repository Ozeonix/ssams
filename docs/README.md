# ARTMS — Academic Real-Time Management System

Production-oriented project specification for a multi-tenant academic management platform.

## Read First

1. `PRD.md`
2. `SRS.md`
3. `SYSTEM_DESIGN.md`
4. `ARCHITECTURE.md`
5. `TECH_STACK.md`
6. `DATABASE_SCHEMA.md`
7. `API_SPEC.md`
8. `AGENT_RULES.md`

## Product Shape

```text
Students
  └── Flutter Mobile App

College
  └── Desktop/Web Admin
          |
          v
     Spring Boot API
          |
   PostgreSQL + Redis
          |
   Object Storage
```

## Core Principle

The system is configurable enough to reproduce different institutional academic schemes, including theory/practical technical-vocational subjects, without creating a separate codebase for each college.

## Local Development Target

Prerequisites:
- Java 21
- Flutter stable
- Docker
- PostgreSQL/Redis via Compose

Suggested commands after implementation:

```bash
docker compose up -d
cd backend && ./mvnw test
cd ../mobile && flutter test
```

These commands are targets for the implementation; do not claim the repository is runnable until the corresponding projects exist.

## Data Safety

The supplied marksheets are reference material for requirements. Do not commit personal names, registration numbers, dates of birth or other real identifiers into source control. Use synthetic fixtures.

## Licensing
Choose a commercial/open-source license before distribution.

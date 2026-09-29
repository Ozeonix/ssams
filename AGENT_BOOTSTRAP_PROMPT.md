# Agent Bootstrap Prompt

You are implementing ARTMS from this directory.

## First Action
Read, in order:
`PRD.md`, `SRS.md`, `SYSTEM_DESIGN.md`, `ARCHITECTURE.md`, `TECH_STACK.md`, `DATABASE_SCHEMA.md`, `API_SPEC.md`, `ROLES_PERMISSIONS.md`, `SECURITY.md`, `AGENT_RULES.md`, `PROJECT_STRUCTURE.md`, `IMPLEMENTATION_PLAN.md`, `TASKS.md`.

## Implementation Behavior
- Inspect existing code before modifying it.
- Build in vertical slices.
- Complete backend/API/database/tests before declaring a business feature complete.
- Then implement admin UI and Flutter UI against the real API.
- Keep all business rules server-side.
- Use synthetic data.
- Run tests after each meaningful slice.
- Update `TASKS.md`.
- Update `CHANGELOG.md` for user-visible/architectural changes.

## First Vertical Slice
Implement:
1. tenant;
2. user;
3. role/permission;
4. login;
5. academic year;
6. class;
7. subject;
8. student;
9. enrollment;
10. one test endpoint.

Then proceed to assessment and results.

## Quality Gate
Do not proceed to production deployment until:
- tests pass;
- migrations are reproducible;
- authorization tests pass;
- tenant isolation passes;
- backup restore has been exercised;
- observability is enabled.

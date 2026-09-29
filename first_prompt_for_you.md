MASTER AUTONOMOUS IMPLEMENTATION PROMPT
=======================================

PROJECT: SSAMS
FULL NAME: Shree Susanskrit Academic Management System
ORGANIZATION: Shree Susanskrit Secondary School

You are the lead software architect, senior backend engineer, senior Flutter engineer, frontend engineer, database engineer, DevOps engineer, QA engineer, security engineer, UI/UX engineer, and technical project manager for this project.

Your task is to FULLY IMPLEMENT the SSAMS production-ready academic real-time management system inside the current project directory.

THIS IS AN AUTONOMOUS EXECUTION TASK.

Do NOT stop after analysis.
Do NOT only create a plan.
Do NOT give me code snippets and wait for another prompt.
Do NOT ask me to provide the next instruction.
Do NOT stop after implementing only the backend.
Do NOT stop after implementing only the UI.
Do NOT leave major functionality as TODO.
Do NOT create fake implementations merely to make tests pass.

Continue working through the complete implementation until the system is in a runnable, tested, production-ready state.

If a decision is not explicitly specified, make the most reasonable engineering decision based on the existing documentation and standard production software practices.

Only ask me a question if there is a genuine external blocker that cannot reasonably be solved through engineering judgment. Otherwise make the decision yourself, document it, and continue.

==================================================
1. FIRST: UNDERSTAND THE ENTIRE PROJECT
==================================================

Before writing substantial implementation code, inspect the complete repository.

Read and understand:

/AGENT_BOOTSTRAP_PROMPT.md
/AGENT_RULES.md
/RULES.md
/README.md

Then read the complete documentation under:

/docs/

Especially:

PRD.md
SRS.md
SYSTEM_DESIGN.md
ARCHITECTURE.md
TECH_STACK.md
DATABASE_SCHEMA.md
API_SPEC.md
UI_UX_SPEC.md
USER_FLOWS.md
FEATURES.md
ROLES_PERMISSIONS.md
REALTIME_ARCHITECTURE.md
SECURITY.md
ERROR_HANDLING.md
TESTING_STRATEGY.md
TEST_CASES.md
DEPLOYMENT.md
ENVIRONMENT.md
CODING_STANDARDS.md
PROJECT_STRUCTURE.md
IMPLEMENTATION_PLAN.md
TASKS.md
GRADING_ENGINE.md
DATA_MIGRATION.md
DOCUMENT_TEMPLATES.md
API_EXAMPLES.md
REFERENCE_MARKSHEET_ANALYSIS.md

Treat these documents as the project's source of truth.

If two documents conflict:

1. Follow the most security-critical requirement.
2. Follow the architectural requirement.
3. Follow the detailed specification over a high-level description.
4. Preserve backward compatibility where reasonable.
5. Document the decision in CHANGELOG.md.

Do not silently ignore conflicts.

==================================================
2. UNDERSTAND THE ACTUAL PRODUCT
==================================================

SSAMS is a multi-role academic real-time management platform intended to be deployable by schools/colleges.

The system must support the academic patterns represented by the supplied reference marksheets, including concepts such as:

- Classes/grades
- Academic years
- Sections
- Students
- Teachers
- Subjects
- Theory subjects
- Practical subjects
- Credit hours
- Marks
- Grade points
- Letter grades
- Final grades
- GPA
- Examinations
- Results
- Result publishing
- Result correction
- Attendance
- Timetables
- Notices
- Notifications
- Academic records
- Student profiles
- Teacher workflows
- Administrative workflows
- Grade sheets
- Transcripts
- Reports
- Audit history

IMPORTANT:

Do NOT hard-code one school's grading system.

The system must be configurable so another school/college can configure:

- grading scales
- grade points
- pass marks
- theory/practical structure
- credit hours
- GPA calculation
- subjects
- academic levels
- examination types
- result templates
- attendance rules
- academic years
- classes
- sections
- institutional settings

The college/school administrator must be able to control these through the administration system.

==================================================
3. TARGET ARCHITECTURE
==================================================

Implement the architecture defined in the documentation.

Expected major components:

BACKEND
--------
Java
Spring Boot
Spring Security
PostgreSQL
Redis where specified
REST APIs
WebSocket/STOMP or the documented real-time mechanism
Database migrations
Validation
Audit logging
Background jobs where appropriate

MOBILE
------
Flutter
Dart

The Flutter application is the primary student-facing mobile application.

If native Android functionality is required, Java may be used for Android platform integration, but do not unnecessarily duplicate Flutter functionality using native Java.

ADMINISTRATION
--------------
Build the college/school management interface according to UI_UX_SPEC.md and PROJECT_STRUCTURE.md.

It must be suitable for desktop/laptop administrative use and responsive where practical.

DATABASE
--------
PostgreSQL must be treated as the primary relational database.

Use proper:

- foreign keys
- indexes
- constraints
- transactions
- migrations
- normalization
- auditability
- soft deletion where appropriate
- unique constraints
- tenant/institution isolation where required

==================================================
4. IMPLEMENT IN PHASES, BUT EXECUTE AUTONOMOUSLY
==================================================

Use the implementation plan as the roadmap.

Internally execute approximately:

PHASE 0
-------
Repository inspection
Documentation validation
Architecture validation
Environment detection

PHASE 1
-------
Project scaffolding
Backend
Database
Migration system
Configuration
Security foundation

PHASE 2
-------
Authentication
Authorization
Users
Roles
Permissions
Institution management

PHASE 3
-------
Academic structure
Academic years
Classes
Sections
Subjects
Subject components
Teachers
Students

PHASE 4
-------
Grading engine
Credit-hour handling
Theory/practical marks
Grade calculation
GPA calculation
Pass/fail rules
Result processing

PHASE 5
-------
Examination management
Exam creation
Exam schedules
Mark entry
Mark validation
Mark verification
Result approval
Result publishing
Result correction/versioning

PHASE 6
-------
Attendance
Timetable
Teacher workflows
Student academic dashboard

PHASE 7
-------
Notifications
Announcements
Real-time events
WebSocket functionality
Unread/read states

PHASE 8
-------
Reports
Grade sheets
Transcripts
Result reports
Academic reports
PDF generation
Print-ready documents

PHASE 9
-------
Flutter student application
Authentication
Dashboard
Profile
Results
Grades
GPA
Attendance
Timetable
Notices
Notifications
Documents
Settings

PHASE 10
--------
Administrative dashboard
Institution configuration
Academic management
Student management
Teacher management
Exam management
Marks management
Result management
Reports
Audit logs
System settings

PHASE 11
--------
Testing
Integration tests
API tests
Database tests
Security tests
Flutter tests
UI tests where practical
End-to-end tests

PHASE 12
--------
Production hardening
Docker
Environment configuration
Logging
Monitoring
Health checks
Backup strategy
Deployment configuration
Security review

Do not stop between phases.

Continue automatically.

==================================================
5. DATABASE IMPLEMENTATION
==================================================

Implement the complete database model defined by DATABASE_SCHEMA.md.

Do not create an oversimplified database.

Ensure proper relationships between entities.

Use migrations rather than manually modifying production schema.

Implement:

- primary keys
- foreign keys
- indexes
- unique constraints
- check constraints
- timestamps
- created_by / updated_by where required
- optimistic locking where appropriate
- audit fields
- soft delete where specified

Do not store derived values unnecessarily when they can safely be calculated.

Where derived academic results are persisted for historical correctness, clearly separate source marks from calculated/published results.

==================================================
6. GRADING ENGINE
==================================================

The grading engine is a critical subsystem.

Implement it as a proper isolated domain/service rather than scattering grading calculations throughout controllers.

It must support configurable:

- marks
- maximum marks
- pass marks
- grade boundaries
- letter grades
- grade points
- theory
- practical
- weighted components
- credit hours
- GPA
- final grade
- pass/fail
- absent
- withheld
- incomplete
- supplementary/re-examination
- result correction
- result publication

Never use floating-point arithmetic where it can create academic calculation errors.

Use appropriate decimal handling.

All important calculation logic must have automated tests.

The exact rules must come from the configurable grading system described in GRADING_ENGINE.md and related documentation.

==================================================
7. SECURITY
==================================================

Implement production-grade security.

At minimum:

- secure authentication
- password hashing
- token/session security according to architecture
- RBAC
- permission checks at API level
- server-side authorization
- input validation
- output validation
- SQL injection protection
- XSS protection where applicable
- CSRF protection where applicable
- rate limiting where specified
- secure headers
- secret management
- audit logging
- sensitive data protection
- tenant isolation
- secure file handling
- secure error responses

NEVER trust permissions from the client.

The backend must always enforce authorization.

Students must never be able to access another student's private academic data.

Teachers must only access resources allowed by their permissions and assignments.

Administrators must have controlled administrative privileges.

==================================================
8. API IMPLEMENTATION
==================================================

Implement the complete API defined by API_SPEC.md.

Use consistent:

- HTTP status codes
- request DTOs
- response DTOs
- validation
- pagination
- filtering
- sorting
- error responses
- authentication
- authorization
- API versioning strategy where documented

Do not expose database entities directly when DTOs are required.

Do not place business logic inside controllers.

Recommended flow:

Controller
    ↓
Application/Service layer
    ↓
Domain/business logic
    ↓
Repository/data layer

Use transactions correctly.

==================================================
9. REAL-TIME SYSTEM
==================================================

Implement the real-time architecture from REALTIME_ARCHITECTURE.md.

Real-time events may include:

- new notice
- result published
- result updated
- attendance update
- timetable change
- notification
- administrative event
- relevant academic updates

The system must gracefully handle:

- reconnects
- duplicate events
- stale clients
- authorization
- offline mobile users
- event ordering where required

Do not assume that WebSocket delivery alone guarantees persistence.

Important events must also be persisted appropriately.

==================================================
10. FLUTTER APPLICATION
==================================================

Build a real Flutter application, not a static UI prototype.

Use the architecture specified by the documentation.

Implement:

- authentication
- secure session handling
- dashboard
- student profile
- academic information
- subjects
- results
- grades
- GPA
- attendance
- timetable
- notices
- notifications
- documents
- settings
- logout
- loading states
- empty states
- error states
- offline/reconnect behavior where specified

The UI must be:

- clean
- modern
- professional
- accessible
- responsive
- consistent
- production-oriented

Avoid unnecessary visual complexity.

Use reusable widgets/components.

Do not duplicate API logic across screens.

==================================================
11. ADMINISTRATION SYSTEM
==================================================

Implement the administrative interface as a real operational management system.

Administrators must be able to manage the institution without modifying source code.

Include the documented controls for:

- institution
- academic years
- classes
- sections
- subjects
- grading systems
- examination types
- exams
- students
- teachers
- enrollments
- subject assignments
- marks
- attendance
- timetable
- notices
- notifications
- result publication
- reports
- users
- roles
- permissions
- system settings
- audit logs

Important configuration must be database-driven.

Do not hard-code values that administrators are expected to control.

==================================================
12. UI/UX
==================================================

Follow UI_UX_SPEC.md.

Build the actual screens required by the product.

Every important screen must handle:

1. Loading
2. Success
3. Empty
4. Error
5. Unauthorized
6. Offline/reconnect where applicable

Forms must have:

- validation
- clear labels
- useful errors
- confirmation for destructive actions
- success feedback
- prevention of accidental duplicate submissions

Tables must support appropriate:

- pagination
- filtering
- searching
- sorting
- bulk operations where specified

==================================================
13. DOCUMENTS AND RESULTS
==================================================

Implement production-quality academic documents according to DOCUMENT_TEMPLATES.md.

Support the required documents such as:

- grade sheet
- marksheet
- transcript
- result report
- student academic record

Do not blindly copy the reference marksheets.

Use them as domain references.

Institution branding and configurable fields should be supported where specified.

Published academic documents must be reproducible and historically consistent.

==================================================
14. TESTING
==================================================

Testing is mandatory.

Do not consider a feature complete merely because it compiles.

Implement:

BACKEND
- unit tests
- service tests
- repository/integration tests
- controller/API tests
- security tests
- grading-engine tests

DATABASE
- migration validation
- constraint tests
- important query tests

FLUTTER
- unit tests
- widget tests
- relevant integration tests

END-TO-END
- critical authentication flow
- student result flow
- administrator result workflow
- marks entry
- grading calculation
- result publication
- permission boundaries

Use TEST_CASES.md as the baseline.

Add tests for bugs discovered during implementation.

==================================================
15. ERROR HANDLING
==================================================

Implement the architecture from ERROR_HANDLING.md.

Never expose:

- stack traces
- SQL errors
- secrets
- internal infrastructure information

to end users.

Errors should have:

- stable error codes
- useful messages
- appropriate HTTP status
- correlation/request ID where specified
- server-side detailed logs

The frontend must display human-readable errors.

==================================================
16. OBSERVABILITY
==================================================

Implement production logging and observability.

Include where appropriate:

- structured logs
- request IDs
- health endpoints
- readiness checks
- liveness checks
- database health
- Redis health
- error logging
- audit logs

Never log passwords, access tokens, secrets, or sensitive academic information unnecessarily.

==================================================
17. DEVOPS
==================================================

Implement the deployment structure from DEPLOYMENT.md and ENVIRONMENT.md.

Provide:

- Docker configuration
- environment templates
- local development configuration
- production configuration guidance
- database migration execution
- health checks
- build scripts
- startup configuration
- deployment documentation

Never commit actual secrets.

Create safe example environment files where required.

==================================================
18. CODE QUALITY
==================================================

Follow CODING_STANDARDS.md.

Code must be:

- modular
- readable
- maintainable
- testable
- strongly typed where applicable
- properly layered
- documented where necessary
- free from unnecessary duplication

Avoid:

- giant classes
- giant controllers
- giant Flutter screens
- hard-coded business rules
- magic numbers
- magic strings
- duplicated API code
- duplicated grading logic
- unnecessary abstractions
- premature microservices

Keep the initial production architecture maintainable.

==================================================
19. DO NOT FAKE COMPLETION
==================================================

This rule is extremely important.

Do NOT do things like:

TODO: implement later

NotImplementedException

return dummy data

hardcoded student list

fake GPA

fake API response

mock result in production code

placeholder authentication

button that does nothing

screen that only looks complete

empty service methods

comment saying "implement later"

If a feature is required, implement it.

Mocks are allowed ONLY inside tests and development tooling where explicitly appropriate.

==================================================
20. CONTINUOUS SELF-VERIFICATION
==================================================

After implementing each major subsystem:

1. Build it.
2. Run tests.
3. Inspect errors.
4. Fix errors.
5. Run tests again.
6. Check integration with existing components.
7. Continue.

Do not wait for user confirmation.

If a build fails because of your own implementation, fix it yourself.

If a test fails because of your own implementation, fix it yourself.

If an API contract conflicts with the implementation, resolve it according to the documentation and update the relevant documentation if necessary.

==================================================
21. KEEP DOCUMENTATION SYNCHRONIZED
==================================================

If implementation decisions materially change:

- API
- database
- architecture
- project structure
- deployment
- environment variables
- behavior

update the appropriate documentation.

Update:

CHANGELOG.md

and the relevant technical document.

Do not allow documentation to become substantially different from the implementation.

==================================================
22. TASK MANAGEMENT
==================================================

Use TASKS.md as the implementation checklist.

As work progresses:

- mark completed tasks
- add newly discovered tasks
- record blockers
- record architectural decisions where appropriate

Do not mark something complete merely because files were created.

A task is complete only when:

- implementation exists
- integration works
- tests pass where applicable
- no known critical defect remains

Continue until the meaningful project scope is completed.

==================================================
23. FINAL QUALITY GATE
==================================================

Before considering the project complete, perform a final audit.

Check:

[ ] Backend builds
[ ] Database migrations work
[ ] Application starts
[ ] Authentication works
[ ] Authorization works
[ ] RBAC works
[ ] Student workflow works
[ ] Teacher workflow works
[ ] Administrator workflow works
[ ] Academic configuration works
[ ] Subject management works
[ ] Examination workflow works
[ ] Marks entry works
[ ] Marks validation works
[ ] Grading engine works
[ ] GPA calculation works
[ ] Result approval works
[ ] Result publication works
[ ] Result correction/versioning works
[ ] Attendance works
[ ] Timetable works
[ ] Notices work
[ ] Notifications work
[ ] Real-time functionality works
[ ] Reports work
[ ] Academic documents work
[ ] Flutter application builds
[ ] Flutter critical flows work
[ ] API tests pass
[ ] Backend tests pass
[ ] Grading tests pass
[ ] Security checks pass
[ ] No critical TODOs remain
[ ] No fake production data paths remain
[ ] No hard-coded academic configuration remains
[ ] Environment configuration is documented
[ ] Docker/deployment configuration works
[ ] Logging works
[ ] Health checks work
[ ] Documentation is synchronized
[ ] CHANGELOG is updated

==================================================
24. IMPORTANT AGENT BEHAVIOR
==================================================

You have autonomy to make implementation decisions.

Do not repeatedly ask:

"Should I continue?"

"Should I implement X?"

"Would you like me to proceed?"

"Which option should I choose?"

Continue automatically.

If there are multiple reasonable technical options, choose the one that best satisfies:

1. Security
2. Correctness
3. Maintainability
4. Production readiness
5. Simplicity
6. Performance
7. Documentation requirements

Do not optimize prematurely.

==================================================
25. EXISTING PROJECT FILES
==================================================

Do not blindly overwrite existing work.

First inspect what already exists.

If implementation already exists:

- preserve good work
- improve it where necessary
- integrate it with the documented architecture
- avoid unnecessary rewrites

If the project is empty except for documentation:

build the complete implementation from the documentation.

==================================================
26. PRODUCTION MINDSET
==================================================

This is NOT a college assignment.

Treat SSAMS as a real software product that will eventually be deployed and sold to educational institutions.

The system must therefore be designed for:

- reliability
- security
- maintainability
- scalability
- auditability
- data integrity
- multi-institution deployment
- configurable academic rules
- real-world administrative workflows

Academic records are sensitive and must be treated accordingly.

Never compromise data integrity merely to make development faster.

==================================================
27. MULTI-INSTITUTION / PRODUCTIZATION
==================================================

The ultimate product should be reusable by multiple schools/colleges.

Avoid organization-specific hard-coding.

Use configurable institution-level settings.

Where the architecture specifies tenant/institution isolation, enforce it at the backend and database/application layers.

The current organization is:

Shree Susanskrit Secondary School

but the codebase should be capable of supporting another institution without modifying source code for ordinary academic configuration.

==================================================
28. START NOW
==================================================

Begin immediately.

STEP 1:
Inspect the entire repository and all project documentation.

STEP 2:
Compare the actual repository against PROJECT_STRUCTURE.md.

STEP 3:
Identify missing implementation components.

STEP 4:
Create/update the implementation roadmap internally using IMPLEMENTATION_PLAN.md and TASKS.md.

STEP 5:
Start implementing from the foundational architecture.

STEP 6:
Continuously build, test, debug, integrate, and improve.

STEP 7:
Continue through all required subsystems without waiting for another user prompt.

STEP 8:
Perform the final production-readiness audit.

STEP 9:
Leave the repository in a clean, runnable state.

When the complete implementation is finished, provide a concise final report containing:

- what was implemented
- major modules completed
- tests executed
- build status
- any remaining non-critical issues
- how to run the backend
- how to run the admin application
- how to run the Flutter application
- important environment variables
- deployment status

DO NOT stop merely because one phase is finished.

DO NOT wait for another prompt.

START IMPLEMENTATION NOW.
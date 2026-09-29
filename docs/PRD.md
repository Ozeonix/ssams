# Product Requirements Document (PRD)
## Academic Real-Time Management System (ARTMS)

**Document status:** Production baseline / implementation contract  
**Target:** Schools, technical & vocational schools, colleges, and higher-secondary institutions  
**Primary clients:** Institution administrators, examination/academic offices, teachers, students, guardians (optional), super-admin/SaaS operator  
**Architecture target:** Multi-tenant, API-first, real-time, production-ready

---

## 1. Product Vision

Build a configurable academic management platform that digitizes the workflow visible in traditional Nepalese school/technical-vocational grade sheets and extends it into a real-time institution management system.

The system must support both:
1. **Student mobile application** for academic information and self-service.
2. **Institution/College web desktop application** for controlled management, examination processing, academic operations, reporting, and administration.

The product must not hard-code one school's mark sheet. Each institution must be able to configure its own academic years, grades/classes, programs, subjects, theory/practical structure, credit hours, grading rules, pass rules, report templates, numbering, and publication workflow.

### Reference academic patterns captured from the supplied examples

The system must model:
- Class/grade levels such as Grade 9, Grade 10, Grade 11, Grade 12.
- General subjects such as Nepali, English, Mathematics, Science, Physics, Chemistry.
- Technical/vocational subjects such as Engineering Drawing, Computer Fundamentals, C Programming, Digital Systems, Electrical Engineering, Web Page Development, Computer Repair & Maintenance, Computer Networks, Database Management Systems, Electronics & Circuits, Microprocessor, Object Oriented Programming.
- Theory/practical components.
- Full marks and pass marks.
- Obtained theory/practical marks.
- Subject total.
- Credit hours.
- Grade point.
- Letter grade.
- Final grade.
- GPA.
- Remarks and result status.
- Annual examination / national examination contexts.
- Student registration number, symbol/roll number, date of birth and institution identity.
- Grade sheets, mark sheets, transcripts and school-leaving/certificate-style outputs.

Personal identifiers from the supplied documents are intentionally not used as seed data.

---

## 2. Problem Statement

Institutions commonly manage student records across paper forms, spreadsheets, disconnected examination files, attendance registers and messaging channels. This creates:
- duplicate data entry;
- inconsistent grading calculations;
- delayed publication;
- weak auditability;
- difficult correction workflows;
- manual transcript/report preparation;
- poor student visibility;
- limited institutional analytics;
- inability to reuse the same system across different programs and grading schemes.

ARTMS provides one controlled source of truth.

---

## 3. Product Goals

### G1 — Configurable academic management
Administrators can configure academic structure without code changes.

### G2 — Accurate examination processing
Marks, practicals, credits, grade points, grades, GPA and result status are calculated by versioned rules.

### G3 — Controlled publication
Draft → verification → approval → publication is enforced by permissions.

### G4 — Real-time information
Published notices, result availability, timetable changes and operational events reach connected users in real time.

### G5 — Student self-service
Students can securely view their profile, attendance, timetable, subjects, results, notices, documents and notifications.

### G6 — Multi-tenant product
One deployment can host multiple institutions with strict tenant isolation.

### G7 — Auditability
Every sensitive academic mutation is traceable to actor, time, tenant, reason and before/after values.

### G8 — Productization
A new institution can be onboarded through configuration, not source-code forks.

---

## 4. Non-Goals for MVP

- Automated admission decision making.
- Biometric identification.
- AI-generated grades.
- Automatic alteration of official examination results without approval.
- Unverified third-party academic integrations.
- Replacing legally required national examination authority processes.

---

## 5. Personas

| Persona | Main responsibilities |
|---|---|
| Platform Super Admin | SaaS tenants, plans, system health |
| Institution Owner | Institution-wide configuration |
| Principal/Head | Oversight and approvals |
| Academic Admin | Programs, classes, subjects, enrollment |
| Examination Officer | Exams, marks, verification, results |
| Teacher | Attendance, marks, academic content |
| Accountant | Fees/payments if enabled |
| Registrar | Student records, transcripts, documents |
| Student | Personal academic information |
| Guardian | Optional read-only linked student information |
| Auditor | Read-only compliance/audit access |

---

## 6. Core Modules

1. Tenant and institution management
2. Authentication and identity
3. Role/permission management
4. Academic year/term management
5. Faculty/department/program management
6. Class/section/cohort management
7. Student lifecycle management
8. Teacher/staff management
9. Subject/curriculum management
10. Subject components: theory/practical/internal/project
11. Enrollment and subject registration
12. Attendance
13. Timetable
14. Examination management
15. Marks entry
16. Marks verification and approval
17. Grading/rule engine
18. Result publication
19. GPA/CGPA/transcript engine
20. Grade-sheet/mark-sheet document generation
21. Notices and announcements
22. Real-time notifications
23. Fees/payment ledger (feature-flagged)
24. Document management
25. Reports and analytics
26. Audit logs
27. Institution settings and branding
28. Import/export
29. Integration/webhook layer
30. Platform operations

---

## 7. Functional Requirements

### FR-01 Tenant management
- Create institution tenant.
- Configure institution identity, address, logo, timezone, locale and academic calendar.
- Isolate all tenant data.
- Suspend/reactivate tenant.
- Configure enabled modules.

### FR-02 Authentication
- Student login using institution-approved identifier plus password/OTP.
- Staff login with username/email and password.
- Refresh-token based sessions.
- Password reset.
- Device/session management.
- Optional MFA for staff.
- Rate limiting and lockout.

### FR-03 Academic configuration
Admin can create:
- academic year;
- term/semester;
- level;
- grade/class;
- section;
- stream;
- faculty;
- department;
- program;
- curriculum version;
- subject;
- subject component;
- credit-hour rule;
- grading scheme;
- pass rule.

No subject, grade or grading scheme may be globally hard-coded.

### FR-04 Student lifecycle
States:
`APPLICANT → ACTIVE → PROMOTED/REPEATED → GRADUATED/COMPLETED → TRANSFERRED/ALUMNI/INACTIVE`

Support:
- admission;
- registration number;
- roll/symbol number;
- profile;
- guardian links;
- enrollment;
- previous education;
- document attachments;
- status history.

### FR-05 Technical/vocational curriculum
A subject can have multiple components.

Example:
- Hardware and Architecture — Theory + Practical
- Computer Programming — Theory + Practical

Each component stores:
- full marks;
- pass marks;
- weight;
- assessment type;
- credit hours;
- grade calculation rule.

### FR-06 Examination
Support:
- annual;
- terminal;
- unit;
- mid-term;
- pre-board;
- board-style imported result;
- practical;
- internal assessment;
- supplementary/re-examination.

Exam lifecycle:
`DRAFT → SCHEDULED → MARK_ENTRY → SUBMITTED → VERIFIED → APPROVED → PUBLISHED → LOCKED`

### FR-07 Marks
Marks entry must support:
- theory;
- practical;
- internal;
- project;
- attendance/internal assessment;
- absent;
- withheld;
- expelled;
- not applicable.

Validation:
- obtained marks cannot exceed full marks;
- pass thresholds configurable;
- missing marks cannot silently become zero;
- absent/withheld status is distinct from zero;
- finalized marks cannot be edited without authorized correction workflow.

### FR-08 Grading engine
Input:
- raw marks;
- component weights;
- pass rules;
- grading scale;
- credit hours.

Output:
- component grade;
- subject total;
- letter grade;
- grade point;
- final grade;
- GPA contribution;
- pass/fail/conditional status.

All calculations must be deterministic and versioned.

### FR-09 GPA
Configurable weighted calculation:
`GPA = Σ(credit × grade_point) / Σ(credit)`

The institution can define exceptions, rounding precision and inclusion/exclusion rules.

### FR-10 Result publication
- Preview result.
- Verify calculations.
- Approve.
- Publish to selected cohort/section.
- Notify students.
- Lock published result.
- Corrections require reason + authorized approval + audit event.
- Re-publication creates a new result version.

### FR-11 Grade sheets and reports
Generate PDF/print-ready:
- mark sheet;
- grade sheet;
- transcript;
- progress report;
- certificate;
- student academic history;
- class result summary;
- subject result summary.

Templates must be configurable with institution branding and placeholders.

### FR-12 Attendance
- Daily/period attendance.
- Present, absent, late, excused.
- Teacher submission.
- Correction request.
- Attendance percentage.
- Publication to students.
- Optional minimum-attendance rules.

### FR-13 Timetable
- class timetable;
- teacher timetable;
- room allocation;
- conflict detection;
- published changes;
- real-time notifications.

### FR-14 Notices
- institution;
- department;
- program;
- class/section;
- individual student;
- scheduled publish/unpublish;
- attachments;
- priority;
- acknowledgement.

### FR-15 Notifications
Channels:
- in-app;
- push;
- email/SMS through adapters.

Events:
- result published;
- marks correction approved;
- timetable changed;
- attendance alert;
- fee reminder;
- notice published;
- account/security event.

### FR-16 Fees
Optional module:
- fee structures;
- invoices;
- discounts/scholarships;
- payment recording;
- receipts;
- outstanding balances;
- reporting.

Payment gateways must be adapter-based and institution-configurable.

### FR-17 Reports
- enrollment;
- attendance;
- marks;
- GPA;
- pass/fail;
- subject performance;
- class performance;
- teacher workload;
- fee collection;
- audit;
- document issuance.

### FR-18 Import/export
Support validated CSV/XLSX import for:
- students;
- subjects;
- curriculum;
- marks;
- attendance.

Import process:
`UPLOADED → VALIDATING → PREVIEW → CONFIRMED → PROCESSING → COMPLETED/FAILED`

Never mutate production data before confirmation.

---

## 8. Real-Time Requirements

Use WebSocket/STOMP or equivalent real-time channel.

Events include:
- notification.created;
- notice.published;
- result.published;
- timetable.changed;
- attendance.updated;
- exam.status_changed;
- document.ready;
- admin.broadcast.

The server remains the source of truth. Real-time messages are hints/events; clients must reconcile with REST APIs.

Fallback:
- reconnect with exponential backoff;
- fetch missed events;
- periodic synchronization;
- no data loss if socket disconnects.

---

## 9. Dynamic Control System

The college must control the product without developer intervention.

Admin-configurable:
- institution branding;
- academic calendar;
- classes/programs;
- subjects;
- theory/practical structure;
- grading scales;
- grade boundaries;
- GPA precision;
- pass/fail rules;
- attendance threshold;
- result visibility;
- notice categories;
- document templates;
- roles/permissions;
- notification policies;
- enabled modules;
- numbering sequences;
- import mappings;
- academic workflow stages;
- feature flags.

Configuration changes must:
- validate;
- create an audit record;
- have effective dates where relevant;
- avoid silently changing historical results;
- create new configuration versions when calculation behavior changes.

---

## 10. Non-Functional Requirements

### Performance
- P95 API latency target < 500 ms for normal CRUD operations under expected load.
- Heavy reports run asynchronously.
- Pagination required for large collections.
- Database indexes on tenant and business lookup fields.

### Availability
- Target 99.9% monthly application availability for production deployments, excluding planned maintenance.

### Scalability
Start as a modular monolith.
Design domain boundaries so services can be extracted later.

### Security
- TLS.
- Argon2id or strong adaptive password hashing.
- JWT access tokens + rotating refresh tokens.
- RBAC.
- tenant isolation.
- object-level authorization.
- audit logging.
- OWASP-aligned validation.
- secrets outside source control.

### Privacy
Collect only required student information.
Support data export, retention and deletion workflows subject to institutional/legal requirements.

### Accessibility
Web UI target WCAG 2.1 AA principles.
Mobile UI supports scalable text and accessible semantics.

### Observability
Logs, metrics, traces, health endpoints and audit events.

---

## 11. Product UX

### Student app
Bottom navigation:
- Home
- Academics
- Attendance
- Results
- More

Home:
- current class;
- next timetable item;
- attendance snapshot;
- latest result;
- notices;
- unread notifications.

Results:
- exam selector;
- subject cards;
- theory/practical marks;
- grade;
- grade point;
- GPA;
- downloadable grade sheet.

### College desktop
Primary navigation:
Dashboard → Students → Academics → Attendance → Timetable → Exams → Marks → Results → Documents → Notices → Finance → Reports → Settings → Audit

Use:
- dense data tables;
- filters;
- saved views;
- bulk actions;
- import wizard;
- confirmation dialogs;
- status badges;
- activity timeline.

---

## 12. Success Metrics

- ≥95% of standard student academic lookups completed without staff assistance.
- ≥99.9% calculation reproducibility for test fixtures.
- 100% of published result mutations auditable.
- Institution onboarding possible primarily through configuration/import.
- No cross-tenant data exposure in security testing.
- Import validation catches invalid records before commit.

---

## 13. MVP

Must include:
- multi-tenancy;
- auth;
- institution setup;
- academic structure;
- students;
- teachers;
- subjects/components;
- examinations;
- marks entry;
- grading;
- GPA;
- verification/approval/publication;
- student result view;
- notices;
- notifications;
- attendance;
- audit;
- PDF grade sheet;
- import/export;
- admin settings.

Phase 2:
- fees;
- timetable optimization;
- guardian app;
- advanced analytics;
- SMS/email integrations;
- payment gateways.

---

## 14. Acceptance Criteria

The product is considered production-ready only when:
1. A new institution can be configured without changing Java source.
2. A technical subject with theory/practical can be configured.
3. Marks can be entered and validated.
4. GPA is reproducible from versioned rules.
5. Draft results cannot be seen as published results.
6. Published results cannot be silently changed.
7. Students receive the published result in the mobile app.
8. All sensitive actions are audited.
9. Tenant isolation passes automated tests.
10. Backup and restore are tested.
11. CI runs tests, static checks and security checks.
12. Production deployment is reproducible from documented environment configuration.

---

## 15. Risks and Mitigations

| Risk | Mitigation |
|---|---|
| Different institutions use different grading | Versioned configuration |
| Historical results change after rule update | Immutable result snapshots |
| Accidental marks overwrite | Draft/version/correction workflow |
| Tenant data leak | Tenant context + repository guards + integration tests |
| Real-time disconnect | REST reconciliation |
| Large imports fail | Async validated jobs |
| Staff resistance | Spreadsheet-like bulk workflows |
| Regulatory changes | Configurable templates/rules and migration strategy |

---

## 16. Product Principle

**Configuration over customization, auditability over convenience, immutable published academic records over silent edits, and API-first architecture over UI-coupled logic.**

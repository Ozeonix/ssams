# ARTMS Pilot Institution Operational Runbook

## 1. Overview & Objective
This runbook governs the deployment, operations, and evaluation of the **Phase 12 Pilot Program** for the first onboarding institution:
- **Tenant:** Himalayan Model Academy (`PILOT_HMA`)
- **Scope:** Secondary Education Cohort (Grades 9 & 10)
- **Primary Objective:** Validate end-to-end workflows (admissions, attendance, exams, calculation engine, document generation) in an operational setting with real teachers and administrators.

---

## 2. Pre-Pilot Gate Checklist
Before initiating pilot operations, ensure all phase gate criteria are verified:
- [x] Zero unresolved critical/high security vulnerabilities.
- [x] Multi-tenant isolation verified with automated integration tests (`AuthIntegrationTest`).
- [x] Automated database backup & restore test verified (`database/scripts/backup_restore_test.sh`).
- [x] Actuator health probe active and reporting UP (`/actuator/health`).
- [x] Pilot synthetic seed loaded (`database/seeds/pilot_institution_seed.sql`).
- [x] Staff roles provisioned (School Admin, Class Teachers, Exam Officer).

---

## 3. Day 1 Onboarding Workflow

### Step 1: Initial Login & Password Rotation
1. School Administrator signs in via web admin console using temporary credentials.
2. Mandatory password reset triggered on first login.
3. Verify tenant branding (institution logo, theme color, locale).

### Step 2: Cohort Verification
1. Navigate to **Academic Structure** -> Verify Academic Year (`2026-2027`) and active terms.
2. Confirm Class Groups: `Grade 9 - Section A` and `Grade 10 - Section A`.
3. Verify Subject Curriculum assignments and assessment weightings (Theory vs Practical).

### Step 3: Student Enrollment Verification
1. Audit student roster under **Student Management**.
2. Confirm auto-enrolled mandatory subjects per curriculum rules.
3. Validate student symbol numbers and registration numbers.

---

## 4. Daily & Weekly Operational Routines

### Daily: Attendance Tracking
1. Class teachers log into mobile/web portal at the start of each period.
2. Mark attendance (Present, Absent, Late, Excused) with optional remarks.
3. Check attendance percentage alerts for students falling below the 75% threshold.

### Periodic: Continuous Assessment & Exam Marks Grid
1. Teachers enter component marks (Theory 75, Practical 25).
2. The marks grid performs real-time validation:
   - Rejects marks exceeding component `full_marks`.
   - Flags missing entries.
3. Submit marks batch for verification.

### Term End: Result Publishing & Transcripts
1. Exam Controller reviews submitted marks grid.
2. Triggers calculation engine to generate immutable result snapshots.
3. Academic Head reviews draft results and submits formal approval.
4. Results are published to student portal and mobile app.
5. Generate tamper-evident grade sheets with cryptographic checksums and verification URLs.

---

## 5. Monitoring & Observability

### Key Metrics to Monitor
- **API Response Latency:** P95 latency < 500ms (`http_req_duration`).
- **Error Rate:** 5xx error rate < 0.1%.
- **Outbox Queue Depth:** Undelivered events processed within 5 seconds.
- **Database Connection Pool:** Active connections < 75% of max pool size.

### Health Verification Command
```bash
curl -s http://localhost:8080/actuator/health | jq .
```

---

## 6. Feedback & Issue Triage

| Severity | Definition | Target Resolution Time | Action |
|---|---|---|---|
| **P0 - Blocker** | System down, data loss, calculation error in published results | < 1 hour | Immediate rollback or hotfix; notify pilot coordinator |
| **P1 - Critical** | Feature broken (e.g. attendance cannot be submitted) | < 4 hours | Workaround provided, patch deployed same day |
| **P2 - Moderate** | Non-critical UI glitch, formatting inconsistency | < 48 hours | Scheduled in next sprint |
| **P3 - Minor** | Usability suggestion, enhancement request | Backlog review | Triaged during weekly feedback sync |

---

## 7. Pilot Exit Criteria
The pilot will be deemed successful when:
1. 100% of attendance sessions are recorded digitally for 14 consecutive days.
2. At least one terminal exam cycle is completely calculated, approved, and published with zero calculation discrepancies.
3. Document verification QR codes authenticate 100% of generated sample transcripts.
4. User satisfaction score among pilot staff exceeds 85%.

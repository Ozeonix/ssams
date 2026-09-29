# ARTMS Staff Training & Operational Guide

## 1. Introduction
Welcome to **ARTMS** (Academic Records & Tenant Management System). This guide provides role-specific standard operating procedures (SOPs) for school staff, teachers, exam controllers, and system administrators.

---

## 2. Platform Architecture & Concepts

### Key Principles
- **Multi-Tenant Isolation:** All data belongs exclusively to your institution. Users from one institution cannot access records of another.
- **Backend-Authoritative Calculations:** GPA, grade bands, and pass/fail statuses are calculated strictly by the server. No manual overriding without an audited correction process.
- **Immutable Result Snapshots:** Once an exam result is approved and published, it is sealed as an immutable snapshot. Any change generates a new audited version.
- **Audited Operations:** Sensitive operations (grade edits, student status changes, permission modifications) are permanently recorded in the system audit log.

---

## 3. Role: Institution Administrator

### Initial Setup & Configuration
1. **Academic Year Setup:**
   - Navigate to `Academic` -> `Academic Years`.
   - Click `New Academic Year` (e.g., `2026-2027`). Set start and end dates.
   - Configure Terms/Semesters: Sequence 1 (`First Term`), Sequence 2 (`Second Term`), Sequence 3 (`Final Term`).
2. **Programs & Departments:**
   - Define Academic Departments (e.g. Science, Mathematics).
   - Configure Programs (e.g. `Secondary Education Grade 9-10`).
3. **Class Groups & Sections:**
   - Create Class Groups under active programs: Grade Level (e.g., `Grade 10`), Section (`A`), and maximum student capacity (e.g., `40`).
   - Assign a designated Class Teacher.
4. **Grading Scheme & Curriculum:**
   - Select or configure the Grading Scheme (e.g., `NEB Standard 4.0 Scale`).
   - Define Grade Bands with minimum/maximum percentages, letter grades (`A+` to `NG`), and grade points (`4.00` to `0.00`).
   - Create and **Publish** the curriculum version for the academic year, setting mandatory and elective subjects with credit hours.

---

## 4. Role: Registrar & Admissions Officer

### Student Admission & Registration
1. **Single Student Admission:**
   - Navigate to `Students` -> `Register New Student`.
   - Enter mandatory details: Admission Number, First Name, Last Name, Date of Birth, Gender.
   - Optional: Registration Number, Board Symbol Number, Phone, Email, Address.
   - Add Primary Guardian contact information.
2. **Batch Import Wizard:**
   - Navigate to `Students` -> `Batch Import`.
   - Download the standard CSV template.
   - Upload the completed CSV file. The system will pre-validate rows:
     - Detects duplicate admission numbers.
     - Validates date formatting (`YYYY-MM-DD`).
     - Shows accepted vs rejected record counts before committing.
3. **Class Enrollment:**
   - Enroll students into target Class Groups and Academic Years.
   - **Automatic Subject Enrollment:** When enrolling a student into a class group, all mandatory curriculum subjects are automatically registered.
4. **Cohort Promotion & Transfers:**
   - **Promotion:** End-of-year batch promotion moves selected students from their current class to the subsequent grade level (e.g., Grade 9 -> Grade 10).
   - **Transfer:** Intra-year section transfer with reason tracking (e.g., Section A -> Section B).

---

## 5. Role: Teacher

### Daily Attendance Workflow
1. Navigate to `Attendance` -> `Take Attendance`.
2. Select your assigned Class Group and current date/period.
3. Mark status for each student:
   - `PRESENT` (Default)
   - `ABSENT`
   - `LATE`
   - `EXCUSED`
4. Add remarks if necessary (e.g., "Medical leave").
5. Click **Submit Attendance**. Real-time attendance percentage alerts will trigger if a student drops below the 75% threshold.

### Marks Entry Grid
1. Navigate to `Exams` -> `Marks Entry`.
2. Select Exam, Class Group, and your assigned Subject.
3. The marks grid displays students alongside configured assessment components (e.g., `Theory [75 Marks]`, `Practical [25 Marks]`).
4. **Validation Rules:**
   - Entering marks higher than the component `full_marks` is rejected automatically.
   - Negative marks are disallowed.
5. Save draft entries progressively. Once all marks are entered, click **Submit for Verification**.

---

## 6. Role: Examination Controller

### Exam Setup & Result Publication Cycle
1. **Exam Configuration:**
   - Create the Examination entity (e.g., `First Terminal Examination 2026`).
   - Attach Academic Year, Term, and participating Class Groups.
   - Confirm subject component weights and passing thresholds.
2. **Mark Verification:**
   - Review submitted marks grids from subject teachers.
   - Verify completeness across all enrolled students.
   - Lock marks entry to prevent further modifications.
3. **Calculation Engine Execution:**
   - Click **Run Calculations**.
   - The engine computes component percentages, subject letter grades, subject grade points, total GPA, and pass/fail indicators.
4. **Approval & Publication:**
   - Preview generated result cards and batch summary statistics.
   - Submit formal approval signature.
   - Click **Publish Results**. Results become instantly visible on student portals and mobile apps.
5. **Audited Result Corrections:**
   - If an official re-evaluation or correction is required post-publication:
     - Click **Initiate Correction**.
     - Enter justification reason and updated marks.
     - System archives the previous version and produces an audited Version 2 snapshot.

---

## 7. Role: Front Office & Student Verification

### Document Generation & Anti-Tamper Verification
1. **Official Documents:**
   - Generate official **Grade Sheets**, **Transcripts**, and **Character Certificates**.
   - Documents are digitally stamped with a SHA-256 cryptographic checksum and verification QR code.
2. **Public Verification Portal:**
   - Employers or external institutions can scan the QR code or enter the document checksum at `/api/v1/documents/verify/{hash}`.
   - Authenticity is validated in real time without exposing student private records.

---

## 8. Common Troubleshooting & FAQs

- **Q: Why does the system reject my student enrollment?**  
  *A: Verify that the class group has not reached its maximum capacity, and that the student is not already enrolled in another class for the same academic year.*
- **Q: Why are subject marks locked for editing?**  
  *A: Once marks are submitted for verification or the exam is locked by the exam controller, edits are disabled. Contact the exam controller to request an unlock.*
- **Q: What happens if an exam result needs to be changed after publishing?**  
  *A: Under strict academic policy, published results cannot be silently altered. An authorized exam officer must initiate an Audited Correction, creating a versioned record.*

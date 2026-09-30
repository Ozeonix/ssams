# SSAMS / ARTMS Batch Onboarding Kit
**Institution Target:** Shree Susanskrit Secondary School (`SHREE_SUSANSKRIT`)

This kit provides standardized CSV templates and validation rules for mass onboarding students, parents/guardians, and academic/administrative staff into SSAMS before school go-live.

---

## 1. File Descriptions

| Template File | Purpose | Target SSAMS Entity |
|---|---|---|
| `students_onboarding_template.csv` | Student biodata, academic enrollment, guardian contacts, bus route | `student`, `student_guardian`, `enrollment`, `user_account` |
| `staff_onboarding_template.csv` | Teaching and non-teaching staff, designations, roles, subject mappings | `teacher`, `user_account`, `user_tenant_membership`, `user_role` |

---

## 2. Student Onboarding Specification

### Column Definitions

| Header | Type | Required | Allowed Values / Format | Description |
|---|---|---|---|---|
| `admission_no` | String | **Yes** | Alphanumeric (e.g. `SK-2083-001`) | Unique school admission number. Primary key identifier. |
| `registration_no` | String | No | Alphanumeric (e.g. `REG-83-1001`) | Government/NEB or internal registration number. |
| `symbol_no` | String | No | Alphanumeric (e.g. `SYM-1001`) | Examination symbol number for board exams. |
| `first_name` | String | **Yes** | Max 128 characters | Given name (e.g., `Aarav`). |
| `middle_name` | String | No | Max 128 characters | Middle name if applicable (e.g., `Bahadur`, `Kumar`). |
| `last_name` | String | **Yes** | Max 128 characters | Surname/caste (e.g., `Shrestha`, `Sharma`). |
| `gender` | Enum | **Yes** | `MALE`, `FEMALE`, `OTHER` | Official gender. |
| `date_of_birth_bs`| Date | **Yes** | `YYYY-MM-DD` (Bikram Sambat) | Nepali calendar date of birth (e.g. `2067-02-01`). |
| `date_of_birth_ad`| Date | **Yes** | `YYYY-MM-DD` (Gregorian) | Converted English date of birth (e.g. `2010-05-15`). |
| `grade_level` | String | **Yes** | `Grade 1` to `Grade 12` | Grade standard. |
| `section` | String | **Yes** | `A`, `B`, `C` | Assigned class section. |
| `stream` | String | No | `General`, `Science`, `Management` | Stream for Grades 9-12. |
| `roll_no` | String | **Yes** | e.g. `10-A-01` | Unique roll number within section. |
| `student_phone` | String | No | `+977-98XXXXXXXX` | Student personal contact (if applicable). |
| `student_email` | Email | No | Valid email | Student school portal email. |
| `address` | String | **Yes** | Text (e.g. `"Sinamangal, Kathmandu"`) | Home address. Wrap with quotes if comma included. |
| `guardian_name` | String | **Yes** | Max 255 characters | Parent / legal guardian full name. |
| `guardian_relation`| Enum| **Yes** | `FATHER`, `MOTHER`, `GUARDIAN` | Relationship to student. |
| `guardian_phone`| String | **Yes** | `+977-98XXXXXXXX` or `+977-97XXXXXXXX` | Parent phone for SMS alerts & eSewa payment receipt notifications. |
| `guardian_email`| Email | No | Valid email | Parent email for term invoice statements. |
| `bus_route` | String | No | Route Name / `Self / Walking` | Assigned bus stop or transport route. |

---

## 3. Staff Onboarding Specification

### Column Definitions

| Header | Type | Required | Allowed Values / Format | Description |
|---|---|---|---|---|
| `employee_code` | String | **Yes** | Unique Code (e.g. `EMP-001`) | Internal payroll/staff code. |
| `first_name` | String | **Yes** | Max 128 chars | Given name. |
| `last_name` | String | **Yes** | Max 128 chars | Surname. |
| `designation` | String | **Yes** | e.g., `Principal`, `Senior Teacher` | Professional designation. |
| `department_code`| String | **Yes** | `PRI_DEPT`, `SEC_DEPT`, `HSEC_DEPT` | Department assignment. |
| `role` | Enum | **Yes** | `INSTITUTION_ADMIN`, `ACCOUNTANT`, `EXAM_CONTROLLER`, `TEACHER` | System RBAC authorization role. |
| `gender` | Enum | **Yes** | `MALE`, `FEMALE`, `OTHER` | Gender. |
| `phone` | String | **Yes** | `+977-98XXXXXXXX` | Contact phone for 2FA and notifications. |
| `email` | Email | **Yes** | Valid unique email | Staff login email and system notifications. |
| `qualification` | String | No | e.g., `M.Sc. Mathematics` | Highest degree attained. |
| `assigned_grades`| String | No | e.g. `"Grade 9, Grade 10"` | Assigned grades for timetable/attendance. |
| `assigned_subjects`| String| No | e.g. `Science and Technology` | CDC subjects assigned for marks entry. |

---

## 4. Critical Instructions for School Administration

1. **Character Encoding (UTF-8)**:
   - Always save files in **CSV (Comma Delimited) (*.csv) with UTF-8 encoding**.
   - If using Microsoft Excel: Click **File -> Save As -> CSV UTF-8 (Comma delimited) (*.csv)**. This prevents Devanagari or special character corruption.

2. **Phone Number Formatting**:
   - Every Nepalese mobile number must be 10 digits prefixed with `+977-` (e.g. `+977-9851000001`).
   - Do NOT omit leading zeroes or country codes.

3. **Date Formats**:
   - Dates must strictly follow `YYYY-MM-DD` (4-digit year, 2-digit month, 2-digit day).
   - Both Bikram Sambat (`date_of_birth_bs`) and Gregorian (`date_of_birth_ad`) are supported.

4. **Quotes around commas**:
   - Any column containing commas (such as addresses: `"Sinamangal-09, Kathmandu"`) MUST be enclosed in double quotes.

5. **Default Credentials**:
   - Once imported, SSAMS creates inactive user credentials. Students log in via their `admission_no` or `email`, and staff log in via their `email`. Initial password reset tokens are sent via SMS/Email.

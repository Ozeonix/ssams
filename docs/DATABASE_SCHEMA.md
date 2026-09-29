# Database Schema

## Conventions
- PostgreSQL.
- UUID primary keys for major entities.
- `created_at`, `updated_at`.
- `created_by`, `updated_by` where useful.
- `tenant_id` on tenant-owned tables.
- soft delete only where historical references require it.
- optimistic `version` on concurrent entities.

## Core Tables

### tenant
- id UUID PK
- code UNIQUE
- name
- status
- timezone
- locale
- branding_jsonb
- created_at
- updated_at

### user_account
- id UUID PK
- username
- email
- phone
- password_hash
- status
- last_login_at

### user_tenant_membership
- id
- tenant_id FK
- user_id FK
- status

### role
- id
- tenant_id nullable for platform role
- code
- name

### permission
- id
- code
- description

### role_permission
- role_id
- permission_id

### user_role
- membership_id
- role_id

### academic_year
- id
- tenant_id
- name
- start_date
- end_date
- status

### term
- id
- tenant_id
- academic_year_id
- name
- sequence
- start_date
- end_date

### department
- id
- tenant_id
- code
- name

### program
- id
- tenant_id
- department_id
- code
- name
- level

### class_group
- id
- tenant_id
- program_id
- academic_year_id
- grade_level
- section
- stream
- capacity

### subject
- id
- tenant_id
- code
- name
- subject_type
- active

### curriculum
- id
- tenant_id
- program_id
- academic_year_id
- version
- status

### curriculum_subject
- id
- curriculum_id
- subject_id
- sequence
- mandatory
- credit_hours

### subject_component
- id
- curriculum_subject_id
- code
- name
- assessment_type
- full_marks
- pass_marks
- weight
- credit_hours

### student
- id
- tenant_id
- admission_no
- registration_no
- symbol_no
- first_name
- middle_name
- last_name
- date_of_birth
- gender
- phone
- email
- address
- status

### student_guardian
- id
- student_id
- name
- relationship
- phone
- email

### enrollment
- id
- tenant_id
- student_id
- class_group_id
- academic_year_id
- roll_no
- status

### subject_enrollment
- id
- enrollment_id
- curriculum_subject_id
- status

### teacher
- id
- tenant_id
- user_id
- employee_code
- name
- department_id
- status

### teacher_subject
- teacher_id
- curriculum_subject_id

### attendance_session
- id
- tenant_id
- class_group_id
- subject_id
- session_date
- period
- teacher_id
- status

### attendance_record
- id
- session_id
- student_id
- status
- remarks
- unique(session_id, student_id)

### timetable_entry
- id
- tenant_id
- class_group_id
- subject_id
- teacher_id
- room
- weekday
- start_time
- end_time
- effective_from
- effective_to
- version

### grading_scheme
- id
- tenant_id
- name
- version
- effective_from
- status

### grade_band
- id
- grading_scheme_id
- min_percentage
- max_percentage
- letter_grade
- grade_point
- pass_flag

### exam
- id
- tenant_id
- academic_year_id
- term_id
- name
- exam_type
- status
- grading_scheme_id

### exam_subject
- id
- exam_id
- curriculum_subject_id
- status

### mark_entry
- id
- tenant_id
- exam_subject_id
- student_id
- component_id
- raw_marks
- status
- submitted_by
- version

### result_snapshot
- id
- tenant_id
- exam_id
- student_id
- result_version
- total_credits
- earned_points
- gpa
- result_status
- grading_scheme_version
- published_at
- immutable_hash

### result_subject_snapshot
- id
- result_snapshot_id
- subject_id
- subject_name_snapshot
- credit_hours
- theory_marks
- practical_marks
- total_marks
- letter_grade
- grade_point
- final_grade
- remarks

### notice
- id
- tenant_id
- title
- body
- priority
- publish_at
- expires_at
- status

### notice_target
- id
- notice_id
- target_type
- target_id

### notification
- id
- tenant_id
- user_id
- type
- title
- body
- data_jsonb
- read_at
- created_at

### document_template
- id
- tenant_id
- type
- name
- version
- template_body
- status

### generated_document
- id
- tenant_id
- student_id nullable
- document_type
- object_key
- checksum
- status
- generated_at

### audit_log
- id
- tenant_id
- actor_user_id
- action
- entity_type
- entity_id
- before_jsonb
- after_jsonb
- reason
- correlation_id
- ip_hash/metadata
- created_at

### outbox_event
- id
- tenant_id
- aggregate_type
- aggregate_id
- event_type
- payload_jsonb
- occurred_at
- published_at
- attempts
- last_error

## Important Constraints

- Unique student registration number per tenant.
- Unique symbol number per exam/academic context as configured.
- No duplicate subject enrollment.
- `raw_marks <= full_marks`.
- Grade bands cannot overlap.
- Grade bands must cover intended range.
- Result snapshots reference the exact grading scheme version.
- Published result snapshots are immutable.

## Indexes

At minimum:
- `(tenant_id, status)` common entities;
- `(tenant_id, registration_no)`;
- `(tenant_id, symbol_no)`;
- `(class_group_id, student_id)`;
- `(exam_id, student_id)`;
- `(exam_subject_id, student_id)`;
- `(user_id, read_at)`;
- `(tenant_id, created_at)` audit/outbox;
- timetable conflict lookup indexes.

## Migration Policy

All schema changes use Flyway migrations.
Never edit production schema manually.
Every migration must be backward-compatible with the deployed application during rolling deployment where applicable.

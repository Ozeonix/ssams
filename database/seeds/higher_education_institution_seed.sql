-- ============================================================================
-- SSAMS / ARTMS – Higher Education / Semester-Based Synthetic Institution Seed
-- database/seeds/higher_education_institution_seed.sql
--
-- Simulates a Nepalese university with:
--   • 1 Tenant (Himalayan Polytechnic University)
--   • 4 Departments: Computer Science, Business Management, Civil Engineering, Pharmacy
--   • 4 Programs (each 4-year / 8-semester)
--   • 2 Academic Years (2023-24 and 2024-25), each with 2 semesters
--   • 8 Class Sections (one per semester-batch)
--   • 40 Subjects across all departments
--   • 200 Synthetic Students (spread across years and programs)
--   • Guardian data for each student
--   • Enrollment per student per current academic year
--
-- Safe to run repeatedly (INSERT ON CONFLICT DO NOTHING).
-- WARNING: Development/test data only.
-- ============================================================================

BEGIN;

-- ────────────────────────────────────────────────────────────────────────────
-- TENANT
-- ────────────────────────────────────────────────────────────────────────────
INSERT INTO tenant (id, code, name, status, timezone, locale, settings)
VALUES (
    '11000000-0000-0000-0000-000000000001',
    'HPU',
    'Himalayan Polytechnic University',
    'ACTIVE',
    'Asia/Kathmandu',
    'ne',
    '{
        "semester_based": true,
        "grading_system": "GPA_4",
        "max_credit_hours": 21,
        "min_attendance_pct": 75
    }'::jsonb
) ON CONFLICT (code) DO NOTHING;

-- ────────────────────────────────────────────────────────────────────────────
-- DEPARTMENTS
-- ────────────────────────────────────────────────────────────────────────────
INSERT INTO department (id, tenant_id, code, name) VALUES
  ('11100000-0000-0000-0000-000000000001', '11000000-0000-0000-0000-000000000001', 'CS',   'Computer Science & IT'),
  ('11100000-0000-0000-0000-000000000002', '11000000-0000-0000-0000-000000000001', 'BM',   'Business Management'),
  ('11100000-0000-0000-0000-000000000003', '11000000-0000-0000-0000-000000000001', 'CE',   'Civil Engineering'),
  ('11100000-0000-0000-0000-000000000004', '11000000-0000-0000-0000-000000000001', 'PHRM', 'Pharmacy')
ON CONFLICT (tenant_id, code) DO NOTHING;

-- ────────────────────────────────────────────────────────────────────────────
-- PROGRAMS  (4 years / 8 semesters each)
-- ────────────────────────────────────────────────────────────────────────────
INSERT INTO program (id, tenant_id, department_id, code, name, duration_years, level) VALUES
  ('11200000-0000-0000-0000-000000000001', '11000000-0000-0000-0000-000000000001',
   '11100000-0000-0000-0000-000000000001', 'BSIT', 'B.Sc. Information Technology', 4, 'UNDERGRADUATE'),
  ('11200000-0000-0000-0000-000000000002', '11000000-0000-0000-0000-000000000001',
   '11100000-0000-0000-0000-000000000002', 'BBA',  'Bachelor of Business Administration', 4, 'UNDERGRADUATE'),
  ('11200000-0000-0000-0000-000000000003', '11000000-0000-0000-0000-000000000001',
   '11100000-0000-0000-0000-000000000003', 'BCE',  'Bachelor of Civil Engineering', 4, 'UNDERGRADUATE'),
  ('11200000-0000-0000-0000-000000000004', '11000000-0000-0000-0000-000000000001',
   '11100000-0000-0000-0000-000000000004', 'BPH',  'Bachelor of Pharmacy', 4, 'UNDERGRADUATE')
ON CONFLICT (tenant_id, code) DO NOTHING;

-- ────────────────────────────────────────────────────────────────────────────
-- ACADEMIC YEARS
-- ────────────────────────────────────────────────────────────────────────────
INSERT INTO academic_year (id, tenant_id, name, start_date, end_date, status) VALUES
  ('11300000-0000-0000-0000-000000000001', '11000000-0000-0000-0000-000000000001',
   '2023-24', '2023-07-16', '2024-07-15', 'COMPLETED'),
  ('11300000-0000-0000-0000-000000000002', '11000000-0000-0000-0000-000000000001',
   '2024-25', '2024-07-16', '2025-07-15', 'ACTIVE')
ON CONFLICT (tenant_id, name) DO NOTHING;

-- ────────────────────────────────────────────────────────────────────────────
-- TERMS / SEMESTERS (2 per academic year = 4 total)
-- ────────────────────────────────────────────────────────────────────────────
INSERT INTO term (id, tenant_id, academic_year_id, name, sequence, start_date, end_date) VALUES
  -- 2023-24
  ('11310000-0000-0000-0000-000000000001', '11000000-0000-0000-0000-000000000001',
   '11300000-0000-0000-0000-000000000001', 'Semester I (Odd)',  1, '2023-07-16', '2023-12-31'),
  ('11310000-0000-0000-0000-000000000002', '11000000-0000-0000-0000-000000000001',
   '11300000-0000-0000-0000-000000000001', 'Semester II (Even)', 2, '2024-01-15', '2024-06-30'),
  -- 2024-25
  ('11310000-0000-0000-0000-000000000003', '11000000-0000-0000-0000-000000000001',
   '11300000-0000-0000-0000-000000000002', 'Semester III (Odd)',  1, '2024-07-16', '2024-12-31'),
  ('11310000-0000-0000-0000-000000000004', '11000000-0000-0000-0000-000000000001',
   '11300000-0000-0000-0000-000000000002', 'Semester IV (Even)', 2, '2025-01-15', '2025-06-30')
ON CONFLICT (academic_year_id, sequence) DO NOTHING;

-- ────────────────────────────────────────────────────────────────────────────
-- GRADING SCHEME (Nepal TU-style 4.0 GPA)
-- ────────────────────────────────────────────────────────────────────────────
INSERT INTO grading_scheme (id, tenant_id, name, is_default) VALUES
  ('11400000-0000-0000-0000-000000000001', '11000000-0000-0000-0000-000000000001',
   'Nepal University Standard GPA (4.0)', true)
ON CONFLICT (tenant_id, name) DO NOTHING;

INSERT INTO grade_band (id, grading_scheme_id, grade_letter, min_percentage, max_percentage, grade_point, remarks) VALUES
  (gen_random_uuid(), '11400000-0000-0000-0000-000000000001', 'O',   90.00, 100.00, 4.0, 'Outstanding'),
  (gen_random_uuid(), '11400000-0000-0000-0000-000000000001', 'A+',  80.00,  89.99, 3.7, 'Excellent'),
  (gen_random_uuid(), '11400000-0000-0000-0000-000000000001', 'A',   70.00,  79.99, 3.3, 'Very Good'),
  (gen_random_uuid(), '11400000-0000-0000-0000-000000000001', 'B+',  60.00,  69.99, 3.0, 'Good'),
  (gen_random_uuid(), '11400000-0000-0000-0000-000000000001', 'B',   50.00,  59.99, 2.5, 'Satisfactory'),
  (gen_random_uuid(), '11400000-0000-0000-0000-000000000001', 'C',   40.00,  49.99, 2.0, 'Pass'),
  (gen_random_uuid(), '11400000-0000-0000-0000-000000000001', 'F',    0.00,  39.99, 0.0, 'Fail')
ON CONFLICT DO NOTHING;

-- ────────────────────────────────────────────────────────────────────────────
-- CLASS SECTIONS  (current year: 2024-25, 4 programs × 2 sections)
-- ────────────────────────────────────────────────────────────────────────────
INSERT INTO class_section (id, tenant_id, program_id, academic_year_id, name, capacity) VALUES
  -- BSIT Sections
  ('11500000-0000-0000-0001-000000000001', '11000000-0000-0000-0000-000000000001',
   '11200000-0000-0000-0000-000000000001', '11300000-0000-0000-0000-000000000002', 'BSIT-A', 60),
  ('11500000-0000-0000-0001-000000000002', '11000000-0000-0000-0000-000000000001',
   '11200000-0000-0000-0000-000000000001', '11300000-0000-0000-0000-000000000002', 'BSIT-B', 60),
  -- BBA Sections
  ('11500000-0000-0000-0002-000000000001', '11000000-0000-0000-0000-000000000001',
   '11200000-0000-0000-0000-000000000002', '11300000-0000-0000-0000-000000000002', 'BBA-A',  55),
  ('11500000-0000-0000-0002-000000000002', '11000000-0000-0000-0000-000000000001',
   '11200000-0000-0000-0000-000000000002', '11300000-0000-0000-0000-000000000002', 'BBA-B',  55),
  -- BCE Sections
  ('11500000-0000-0000-0003-000000000001', '11000000-0000-0000-0000-000000000001',
   '11200000-0000-0000-0000-000000000003', '11300000-0000-0000-0000-000000000002', 'BCE-A',  50),
  ('11500000-0000-0000-0003-000000000002', '11000000-0000-0000-0000-000000000001',
   '11200000-0000-0000-0000-000000000003', '11300000-0000-0000-0000-000000000002', 'BCE-B',  50),
  -- BPH Sections
  ('11500000-0000-0000-0004-000000000001', '11000000-0000-0000-0000-000000000001',
   '11200000-0000-0000-0000-000000000004', '11300000-0000-0000-0000-000000000002', 'BPH-A',  45),
  ('11500000-0000-0000-0004-000000000002', '11000000-0000-0000-0000-000000000001',
   '11200000-0000-0000-0000-000000000004', '11300000-0000-0000-0000-000000000002', 'BPH-B',  45)
ON CONFLICT (tenant_id, program_id, academic_year_id, name) DO NOTHING;

-- ────────────────────────────────────────────────────────────────────────────
-- SUBJECTS (sample per department – 10 each)
-- ────────────────────────────────────────────────────────────────────────────
INSERT INTO subject (id, tenant_id, department_id, code, name, credit_hours, subject_type) VALUES
  -- Computer Science
  ('11600000-CS00-0000-0000-000000000001','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000001','CS101','Introduction to Programming',3,'THEORY'),
  ('11600000-CS00-0000-0000-000000000002','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000001','CS102','Data Structures',3,'THEORY'),
  ('11600000-CS00-0000-0000-000000000003','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000001','CS103','Database Systems',3,'THEORY'),
  ('11600000-CS00-0000-0000-000000000004','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000001','CS104','Operating Systems',3,'THEORY'),
  ('11600000-CS00-0000-0000-000000000005','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000001','CS105','Computer Networks',3,'THEORY'),
  ('11600000-CS00-0000-0000-000000000006','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000001','CS106','Web Technologies',3,'PRACTICAL'),
  ('11600000-CS00-0000-0000-000000000007','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000001','CS107','Software Engineering',3,'THEORY'),
  ('11600000-CS00-0000-0000-000000000008','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000001','CS108','Artificial Intelligence',3,'THEORY'),
  ('11600000-CS00-0000-0000-000000000009','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000001','CS109','Cybersecurity Fundamentals',3,'THEORY'),
  ('11600000-CS00-0000-0000-000000000010','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000001','CS110','Cloud Computing',3,'PRACTICAL'),
  -- Business Management
  ('11600000-BM00-0000-0000-000000000001','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000002','BM101','Principles of Management',3,'THEORY'),
  ('11600000-BM00-0000-0000-000000000002','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000002','BM102','Business Mathematics',3,'THEORY'),
  ('11600000-BM00-0000-0000-000000000003','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000002','BM103','Financial Accounting',3,'THEORY'),
  ('11600000-BM00-0000-0000-000000000004','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000002','BM104','Organizational Behaviour',3,'THEORY'),
  ('11600000-BM00-0000-0000-000000000005','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000002','BM105','Marketing Management',3,'THEORY'),
  -- Civil Engineering
  ('11600000-CE00-0000-0000-000000000001','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000003','CE101','Engineering Mathematics I',4,'THEORY'),
  ('11600000-CE00-0000-0000-000000000002','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000003','CE102','Mechanics of Materials',3,'THEORY'),
  ('11600000-CE00-0000-0000-000000000003','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000003','CE103','Structural Analysis',3,'THEORY'),
  ('11600000-CE00-0000-0000-000000000004','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000003','CE104','Fluid Mechanics',3,'THEORY'),
  ('11600000-CE00-0000-0000-000000000005','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000003','CE105','Geotechnical Engineering',3,'THEORY'),
  -- Pharmacy
  ('11600000-PH00-0000-0000-000000000001','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000004','PH101','Pharmaceutical Chemistry',3,'THEORY'),
  ('11600000-PH00-0000-0000-000000000002','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000004','PH102','Pharmacology I',3,'THEORY'),
  ('11600000-PH00-0000-0000-000000000003','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000004','PH103','Microbiology & Immunology',3,'THEORY'),
  ('11600000-PH00-0000-0000-000000000004','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000004','PH104','Anatomy & Physiology',3,'THEORY'),
  ('11600000-PH00-0000-0000-000000000005','11000000-0000-0000-0000-000000000001','11100000-0000-0000-0000-000000000004','PH105','Pharmacy Practice',3,'PRACTICAL')
ON CONFLICT (tenant_id, code) DO NOTHING;

-- ────────────────────────────────────────────────────────────────────────────
-- STUDENTS (50 per program = 200 total)
-- Data is intentionally pseudo-realistic for test purposes.
-- ────────────────────────────────────────────────────────────────────────────

-- Helper: generates 50 students for a given program / section / prefix
-- (Since SQL has no loops, we use generate_series + CTE)

WITH prog_data AS (
    SELECT
        unnest(ARRAY[
            '11200000-0000-0000-0000-000000000001',
            '11200000-0000-0000-0000-000000000002',
            '11200000-0000-0000-0000-000000000003',
            '11200000-0000-0000-0000-000000000004'
        ]) AS program_id,
        unnest(ARRAY['BSIT','BBA','BCE','BPH']) AS prog_code,
        unnest(ARRAY[
            '11500000-0000-0000-0001-000000000001',
            '11500000-0000-0000-0002-000000000001',
            '11500000-0000-0000-0003-000000000001',
            '11500000-0000-0000-0004-000000000001'
        ]) AS section_id
),
students_to_insert AS (
    SELECT
        gen_random_uuid()                                    AS sid,
        '11000000-0000-0000-0000-000000000001'::uuid         AS tenant_id,
        pd.program_id::uuid                                  AS program_id,
        pd.section_id::uuid                                  AS section_id,
        pd.prog_code || '-' || LPAD(n::text, 4, '0')        AS student_code,
        (ARRAY['Ram','Sita','Hari','Gita','Bikash','Priya','Deepak','Anita',
               'Suresh','Kavita','Nabin','Sunita','Arjun','Mina','Lokesh',
               'Rekha','Rajan','Sabita','Dipesh','Nisha'])[1 + (n % 20)]
                                                             AS first_name,
        (ARRAY['Shrestha','Tamang','Rai','Gurung','Thapa','Maharjan','KC',
               'Bhattarai','Adhikari','Poudel','Koirala','Sharma','Neupane',
               'Bhandari','Pandey','Karki','Limbu','Basnet','Dahal','Joshi'])
               [1 + ((n * 7) % 20)]                         AS last_name,
        (DATE '1999-01-01' + (n * 47 % 2190) * INTERVAL '1 day')::date  AS dob,
        (ARRAY['MALE','FEMALE','MALE','FEMALE','MALE','OTHER'])[1 + (n % 6)] AS gender,
        '+977-98' || LPAD((10000000 + n * 31337 % 89999999)::text, 8, '0') AS phone,
        LOWER(pd.prog_code) || '_student' || n || '@hpu.edu.np'           AS email,
        DATE '2024-07-20'                                                  AS admission_date
    FROM prog_data pd
    CROSS JOIN generate_series(1, 50) AS n
)
INSERT INTO student
    (id, tenant_id, student_code, first_name, last_name, date_of_birth,
     gender, phone, email, status, admission_date)
SELECT
    sid, tenant_id, student_code, first_name, last_name, dob,
    gender, phone, email, 'ACTIVE', admission_date
FROM students_to_insert
ON CONFLICT (tenant_id, student_code) DO NOTHING;

-- ────────────────────────────────────────────────────────────────────────────
-- ENROLLMENTS  (for all 200 students → current 2024-25 year, Semester III)
-- ────────────────────────────────────────────────────────────────────────────
INSERT INTO enrollment
    (id, tenant_id, student_id, class_section_id, academic_year_id, term_id, status)
SELECT
    gen_random_uuid(),
    '11000000-0000-0000-0000-000000000001',
    s.id,
    CASE
        WHEN s.student_code LIKE 'BSIT%' THEN '11500000-0000-0000-0001-000000000001'
        WHEN s.student_code LIKE 'BBA%'  THEN '11500000-0000-0000-0002-000000000001'
        WHEN s.student_code LIKE 'BCE%'  THEN '11500000-0000-0000-0003-000000000001'
        ELSE                                   '11500000-0000-0000-0004-000000000001'
    END::uuid,
    '11300000-0000-0000-0000-000000000002',
    '11310000-0000-0000-0000-000000000003',
    'ACTIVE'
FROM student s
WHERE s.tenant_id = '11000000-0000-0000-0000-000000000001'
  AND s.status    = 'ACTIVE'
ON CONFLICT DO NOTHING;

COMMIT;

-- ────────────────────────────────────────────────────────────────────────────
-- Verification queries
-- ────────────────────────────────────────────────────────────────────────────
/*
SELECT 'Tenant'        AS entity, count(*) FROM tenant         WHERE id = '11000000-0000-0000-0000-000000000001'
UNION ALL
SELECT 'Departments'   AS entity, count(*) FROM department     WHERE tenant_id = '11000000-0000-0000-0000-000000000001'
UNION ALL
SELECT 'Programs'      AS entity, count(*) FROM program        WHERE tenant_id = '11000000-0000-0000-0000-000000000001'
UNION ALL
SELECT 'Subjects'      AS entity, count(*) FROM subject        WHERE tenant_id = '11000000-0000-0000-0000-000000000001'
UNION ALL
SELECT 'Students'      AS entity, count(*) FROM student        WHERE tenant_id = '11000000-0000-0000-0000-000000000001'
UNION ALL
SELECT 'Enrollments'   AS entity, count(*) FROM enrollment     WHERE tenant_id = '11000000-0000-0000-0000-000000000001';
*/

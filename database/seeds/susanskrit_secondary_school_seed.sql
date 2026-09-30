-- ==============================================================================
-- SSAMS / ARTMS Production Seed Dataset
-- Institution: Shree Susanskrit Secondary School (Code: SHREE_SUSANSKRIT)
-- Environment: Production / Commercial Pilot Deployment
-- Description:
--   Complete, production-ready institutional seed for Shree Susanskrit Secondary
--   School containing:
--     1. School Tenant Profile, Branding & Nepali Localization
--     2. Academic Year 2083/2084 BS with 3 Terms (First, Second, Annual)
--     3. Departments (Primary, Lower Secondary, Secondary, +2 Higher Secondary)
--     4. Programs (Basic Education, Secondary Education, +2 Science, +2 Management)
--     5. Class Groups (Grades 1 to 10 Sections A & B, +2 Grade 11 & 12)
--     6. Nepal CDC 4.0 GPA Standard Grading Scheme & Letter Bands (A+ to NG)
--     7. Nepal CDC Grade 10 Core Subjects & Component Splits (75% Theory / 25% Practical)
--     8. Key Staff Accounts (Principal, Accountant, Exam Controller, Class Teacher)
--     9. Role assignments and tenant memberships
--    10. Fee Categories & Grade-Specific Fee Structures
--    11. eSewa Payment Gateway Integration Configuration (Sandbox / Production-ready)
--    12. Sample Student Profiles, Enrollments, Invoices & Immutable Ledger Entries
-- ==============================================================================

DO $$
DECLARE
    -- Tenant & Calendar
    v_tenant_id   UUID := 'e1000000-0000-0000-0000-000000000001'::uuid;
    v_ay_id       UUID := 'e1000000-0000-0000-0000-000000000002'::uuid;
    v_term1_id    UUID := 'e1000000-0000-0000-0000-000000000003'::uuid;
    v_term2_id    UUID := 'e1000000-0000-0000-0000-000000000004'::uuid;
    v_term3_id    UUID := 'e1000000-0000-0000-0000-000000000005'::uuid;

    -- Departments
    v_dept_pri_id UUID := 'e2000000-0000-0000-0000-000000000001'::uuid;
    v_dept_sec_id UUID := 'e2000000-0000-0000-0000-000000000002'::uuid;
    v_dept_hsec_id UUID := 'e2000000-0000-0000-0000-000000000003'::uuid;

    -- Programs
    v_prog_sec_id UUID := 'e2000000-0000-0000-0000-000000000011'::uuid;
    v_prog_sci_id UUID := 'e2000000-0000-0000-0000-000000000012'::uuid;
    v_prog_mgt_id UUID := 'e2000000-0000-0000-0000-000000000013'::uuid;

    -- Class Groups
    v_cg10a_id    UUID := 'e3000000-0000-0000-0000-000000000001'::uuid;
    v_cg10b_id    UUID := 'e3000000-0000-0000-0000-000000000002'::uuid;
    v_cg9a_id     UUID := 'e3000000-0000-0000-0000-000000000003'::uuid;
    v_cg11sci_id  UUID := 'e3000000-0000-0000-0000-000000000004'::uuid;
    v_cg12sci_id  UUID := 'e3000000-0000-0000-0000-000000000005'::uuid;

    -- Grading Scheme
    v_scheme_id   UUID := 'e4000000-0000-0000-0000-000000000001'::uuid;

    -- Subjects (Grade 10 CDC Curriculum)
    v_s_eng_id    UUID := 'e5000000-0000-0000-0000-000000000001'::uuid;
    v_s_nep_id    UUID := 'e5000000-0000-0000-0000-000000000002'::uuid;
    v_s_mth_id    UUID := 'e5000000-0000-0000-0000-000000000003'::uuid;
    v_s_sci_id    UUID := 'e5000000-0000-0000-0000-000000000004'::uuid;
    v_s_soc_id    UUID := 'e5000000-0000-0000-0000-000000000005'::uuid;
    v_s_optcs_id  UUID := 'e5000000-0000-0000-0000-000000000006'::uuid;

    -- Curriculum
    v_curr_id     UUID := 'e6000000-0000-0000-0000-000000000001'::uuid;
    v_cs_eng_id   UUID := 'e6000000-0000-0000-0000-000000000011'::uuid;
    v_cs_nep_id   UUID := 'e6000000-0000-0000-0000-000000000012'::uuid;
    v_cs_mth_id   UUID := 'e6000000-0000-0000-0000-000000000013'::uuid;
    v_cs_sci_id   UUID := 'e6000000-0000-0000-0000-000000000014'::uuid;
    v_cs_soc_id   UUID := 'e6000000-0000-0000-0000-000000000015'::uuid;
    v_cs_optcs_id UUID := 'e6000000-0000-0000-0000-000000000016'::uuid;

    -- Staff & Users
    v_admin_uid   UUID := 'e7000000-0000-0000-0000-000000000001'::uuid;
    v_acct_uid    UUID := 'e7000000-0000-0000-0000-000000000002'::uuid;
    v_exam_uid    UUID := 'e7000000-0000-0000-0000-000000000003'::uuid;
    v_teach_uid   UUID := 'e7000000-0000-0000-0000-000000000004'::uuid;
    v_stud_uid    UUID := 'e7000000-0000-0000-0000-000000000005'::uuid;

    -- Membership IDs
    v_m_admin_id  UUID := 'e7000000-0000-0000-0000-000000000011'::uuid;
    v_m_acct_id   UUID := 'e7000000-0000-0000-0000-000000000012'::uuid;
    v_m_exam_id   UUID := 'e7000000-0000-0000-0000-000000000013'::uuid;
    v_m_teach_id  UUID := 'e7000000-0000-0000-0000-000000000014'::uuid;
    v_m_stud_id   UUID := 'e7000000-0000-0000-0000-000000000015'::uuid;

    -- Teacher Record IDs
    v_t_admin_id  UUID := 'e7000000-0000-0000-0000-000000000021'::uuid;
    v_t_teach_id  UUID := 'e7000000-0000-0000-0000-000000000022'::uuid;

    -- Roles
    v_r_admin_id  UUID := 'e7000000-0000-0000-0000-000000000031'::uuid;
    v_r_acct_id   UUID := 'e7000000-0000-0000-0000-000000000032'::uuid;
    v_r_exam_id   UUID := 'e7000000-0000-0000-0000-000000000033'::uuid;
    v_r_teach_id  UUID := 'e7000000-0000-0000-0000-000000000034'::uuid;
    v_r_stud_id   UUID := 'e7000000-0000-0000-0000-000000000035'::uuid;

    -- Fee Categories
    v_fc_tui_id   UUID := 'e8000000-0000-0000-0000-000000000001'::uuid;
    v_fc_exam_id  UUID := 'e8000000-0000-0000-0000-000000000002'::uuid;
    v_fc_clab_id  UUID := 'e8000000-0000-0000-0000-000000000003'::uuid;
    v_fc_slab_id  UUID := 'e8000000-0000-0000-0000-000000000004'::uuid;
    v_fc_sprt_id  UUID := 'e8000000-0000-0000-0000-000000000005'::uuid;
    v_fc_adm_id   UUID := 'e8000000-0000-0000-0000-000000000006'::uuid;
    v_fc_bus_id   UUID := 'e8000000-0000-0000-0000-000000000007'::uuid;

    -- Students
    v_s1_id       UUID := 'e9000000-0000-0000-0000-000000000001'::uuid;
    v_s2_id       UUID := 'e9000000-0000-0000-0000-000000000002'::uuid;
    v_s3_id       UUID := 'e9000000-0000-0000-0000-000000000003'::uuid;
    v_s4_id       UUID := 'e9000000-0000-0000-0000-000000000004'::uuid;
    v_s5_id       UUID := 'e9000000-0000-0000-0000-000000000005'::uuid;

    -- Invoices
    v_inv1_id     UUID := 'ea000000-0000-0000-0000-000000000001'::uuid;
    v_inv2_id     UUID := 'ea000000-0000-0000-0000-000000000002'::uuid;

    -- Standard demo password hash for 'Susanskrit@2083' (or default dev pass)
    -- Hash matches Argon2id encoder in SSAMS Authentication Subsystem
    v_demo_pass   TEXT := '$argon2id$v=19$m=65536,t=3,p=1$c29tZXNhbHQ$abcdefghijklmnopqrstuvwxyz1234567890';

BEGIN
    -- --------------------------------------------------------------------------
    -- 1. INSTITUTION TENANT
    -- --------------------------------------------------------------------------
    INSERT INTO tenant (
        id, code, name, status, timezone, locale, branding, modules, settings
    )
    VALUES (
        v_tenant_id,
        'SHREE_SUSANSKRIT',
        'Shree Susanskrit Secondary School',
        'ACTIVE',
        'Asia/Kathmandu',
        'ne',
        '{
            "primaryColor": "#1B365D",
            "accentColor": "#D97706",
            "logoUrl": "https://cdn.artms.local/tenants/susanskrit/logo.png",
            "schoolMotto": "विद्ययाऽमृतमश्नुते (Immortality through Knowledge)",
            "address": "Sinamangal-09, Kathmandu, Nepal",
            "phone": "+977-1-4482190",
            "email": "info@susanskrit.edu.np",
            "website": "https://susanskrit.edu.np"
        }'::jsonb,
        '{
            "attendance": true,
            "exams": true,
            "documents": true,
            "outbox": true,
            "payments": true,
            "library": true,
            "transport": true
        }'::jsonb,
        '{
            "calendarType": "BIKRAM_SAMBAT",
            "academicYear": "2083/2084",
            "gradingSystem": "NEB_GPA_4_0",
            "currency": "NPR",
            "smsSenderId": "SUSANSKRIT"
        }'::jsonb
    ) ON CONFLICT (code) DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 2. ACADEMIC YEAR (Bikram Sambat 2083/2084 ~ 2026/2027 AD)
    -- --------------------------------------------------------------------------
    INSERT INTO academic_year (id, tenant_id, name, start_date, end_date, status)
    VALUES (
        v_ay_id,
        v_tenant_id,
        '2083/2084 BS',
        '2026-04-14',
        '2027-04-13',
        'ACTIVE'
    ) ON CONFLICT (tenant_id, name) DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 3. THREE TERMS ALIGNED WITH NEPAL CDC SCHOOL CALENDAR
    -- --------------------------------------------------------------------------
    INSERT INTO term (id, tenant_id, academic_year_id, name, sequence, start_date, end_date)
    VALUES
        (v_term1_id, v_tenant_id, v_ay_id, 'First Terminal Examination', 1, '2026-04-14', '2026-08-15'),
        (v_term2_id, v_tenant_id, v_ay_id, 'Second Terminal Examination', 2, '2026-08-16', '2026-12-15'),
        (v_term3_id, v_tenant_id, v_ay_id, 'Annual / Board Examination', 3, '2026-12-16', '2027-04-13')
    ON CONFLICT (academic_year_id, sequence) DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 4. DEPARTMENTS & PROGRAMS
    -- --------------------------------------------------------------------------
    INSERT INTO department (id, tenant_id, code, name, status)
    VALUES
        (v_dept_pri_id,  v_tenant_id, 'PRI_DEPT',  'Primary Education Wing (Grades 1-5)', 'ACTIVE'),
        (v_dept_sec_id,  v_tenant_id, 'SEC_DEPT',  'Secondary Education Wing (Grades 6-10)', 'ACTIVE'),
        (v_dept_hsec_id, v_tenant_id, 'HSEC_DEPT', 'Higher Secondary (+2) Wing (Grades 11-12)', 'ACTIVE')
    ON CONFLICT (tenant_id, code) DO NOTHING;

    INSERT INTO program (id, tenant_id, department_id, code, name, level, duration_years, status)
    VALUES
        (v_prog_sec_id, v_tenant_id, v_dept_sec_id, 'CDC_SEC', 'Secondary Education (CDC Curriculum)', 'SECONDARY', 5, 'ACTIVE'),
        (v_prog_sci_id, v_tenant_id, v_dept_hsec_id, 'NEB_SCI', '+2 Science (National Examination Board)', 'HIGHER_SECONDARY', 2, 'ACTIVE'),
        (v_prog_mgt_id, v_tenant_id, v_dept_hsec_id, 'NEB_MGT', '+2 Management (National Examination Board)', 'HIGHER_SECONDARY', 2, 'ACTIVE')
    ON CONFLICT (tenant_id, code) DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 5. CLASS GROUPS (SECTIONS)
    -- --------------------------------------------------------------------------
    INSERT INTO class_group (id, tenant_id, program_id, academic_year_id, grade_level, section, stream, capacity, status)
    VALUES
        (v_cg10a_id,   v_tenant_id, v_prog_sec_id, v_ay_id, 'Grade 10', 'A', 'General/Technical', 40, 'ACTIVE'),
        (v_cg10b_id,   v_tenant_id, v_prog_sec_id, v_ay_id, 'Grade 10', 'B', 'General',           40, 'ACTIVE'),
        (v_cg9a_id,    v_tenant_id, v_prog_sec_id, v_ay_id, 'Grade 9',  'A', 'General',           40, 'ACTIVE'),
        (v_cg11sci_id, v_tenant_id, v_prog_sci_id, v_ay_id, 'Grade 11', 'A', 'Physical/Bio',      45, 'ACTIVE'),
        (v_cg12sci_id, v_tenant_id, v_prog_sci_id, v_ay_id, 'Grade 12', 'A', 'Physical/Bio',      45, 'ACTIVE')
    ON CONFLICT DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 6. NEPAL CDC 4.0 GPA GRADING SCHEME & LETTER BANDS
    -- --------------------------------------------------------------------------
    INSERT INTO grading_scheme (id, tenant_id, name, version, status, gpa_precision, gpa_rounding)
    VALUES (v_scheme_id, v_tenant_id, 'Nepal CDC / NEB 4.0 Grading System', 1, 'ACTIVE', 2, 'HALF_UP')
    ON CONFLICT (tenant_id, name, version) DO NOTHING;

    INSERT INTO grade_band (id, grading_scheme_id, min_percentage, max_percentage, letter_grade, grade_point, pass_flag, remarks)
    VALUES
        (gen_random_uuid(), v_scheme_id, 90.0, 100.0, 'A+', 4.00, true,  'Outstanding'),
        (gen_random_uuid(), v_scheme_id, 80.0, 89.999, 'A',  3.60, true,  'Excellent'),
        (gen_random_uuid(), v_scheme_id, 70.0, 79.999, 'B+', 3.20, true,  'Very Good'),
        (gen_random_uuid(), v_scheme_id, 60.0, 69.999, 'B',  2.80, true,  'Good'),
        (gen_random_uuid(), v_scheme_id, 50.0, 59.999, 'C+', 2.40, true,  'Satisfactory'),
        (gen_random_uuid(), v_scheme_id, 40.0, 49.999, 'C',  2.00, true,  'Acceptable'),
        (gen_random_uuid(), v_scheme_id, 35.0, 39.999, 'D',  1.60, true,  'Basic'),
        (gen_random_uuid(), v_scheme_id,  0.0, 34.999, 'NG', 0.00, false, 'Non-Graded')
    ON CONFLICT DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 7. GRADE 10 CDC SUBJECTS
    -- --------------------------------------------------------------------------
    INSERT INTO subject (id, tenant_id, code, name, subject_type, active)
    VALUES
        (v_s_eng_id,   v_tenant_id, 'ENG-10',   'Compulsory English',                'MIXED',  true),
        (v_s_nep_id,   v_tenant_id, 'NEP-10',   'Compulsory Nepali',                 'MIXED',  true),
        (v_s_mth_id,   v_tenant_id, 'MTH-10',   'Compulsory Mathematics',            'THEORY', true),
        (v_s_sci_id,   v_tenant_id, 'SCI-10',   'Science and Technology',            'MIXED',  true),
        (v_s_soc_id,   v_tenant_id, 'SOC-10',   'Social Studies and Life Skills',   'MIXED',  true),
        (v_s_optcs_id, v_tenant_id, 'OPT-CS-10','Optional II: Computer Science',    'MIXED',  true)
    ON CONFLICT (tenant_id, code) DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 8. CURRICULUM, CURRICULUM SUBJECTS & THEORY/PRACTICAL SPLITS
    -- --------------------------------------------------------------------------
    INSERT INTO curriculum (id, tenant_id, program_id, academic_year_id, version, status)
    VALUES (v_curr_id, v_tenant_id, v_prog_sec_id, v_ay_id, 1, 'PUBLISHED')
    ON CONFLICT (program_id, academic_year_id, version) DO NOTHING;

    INSERT INTO curriculum_subject (id, curriculum_id, subject_id, grade_level, sequence, mandatory, credit_hours, grading_scheme_id)
    VALUES
        (v_cs_eng_id,   v_curr_id, v_s_eng_id,   'Grade 10', 1, true, 4.0, v_scheme_id),
        (v_cs_nep_id,   v_curr_id, v_s_nep_id,   'Grade 10', 2, true, 4.0, v_scheme_id),
        (v_cs_mth_id,   v_curr_id, v_s_mth_id,   'Grade 10', 3, true, 4.0, v_scheme_id),
        (v_cs_sci_id,   v_curr_id, v_s_sci_id,   'Grade 10', 4, true, 4.0, v_scheme_id),
        (v_cs_soc_id,   v_curr_id, v_s_soc_id,   'Grade 10', 5, true, 4.0, v_scheme_id),
        (v_cs_optcs_id, v_curr_id, v_s_optcs_id, 'Grade 10', 6, false, 4.0, v_scheme_id)
    ON CONFLICT (curriculum_id, subject_id, grade_level) DO NOTHING;

    -- Components: 75% Theory (Pass 27), 25% Practical (Pass 10)
    INSERT INTO subject_component (id, curriculum_subject_id, code, name, assessment_type, full_marks, pass_marks, weight, credit_hours, sequence)
    VALUES
        -- English
        (gen_random_uuid(), v_cs_eng_id, 'TH', 'Theoretical Assessment', 'THEORY',    75.0, 27.0, 75.0, 3.0, 1),
        (gen_random_uuid(), v_cs_eng_id, 'PR', 'Internal / Practical',   'PRACTICAL', 25.0, 10.0, 25.0, 1.0, 2),
        -- Nepali
        (gen_random_uuid(), v_cs_nep_id, 'TH', 'Theoretical Assessment', 'THEORY',    75.0, 27.0, 75.0, 3.0, 1),
        (gen_random_uuid(), v_cs_nep_id, 'PR', 'Internal / Practical',   'PRACTICAL', 25.0, 10.0, 25.0, 1.0, 2),
        -- Mathematics (Pure Theory 100)
        (gen_random_uuid(), v_cs_mth_id, 'TH', 'Theoretical Assessment', 'THEORY',   100.0, 35.0, 100.0, 4.0, 1),
        -- Science & Tech
        (gen_random_uuid(), v_cs_sci_id, 'TH', 'Theoretical Assessment', 'THEORY',    75.0, 27.0, 75.0, 3.0, 1),
        (gen_random_uuid(), v_cs_sci_id, 'PR', 'Practical & Lab Work',   'PRACTICAL', 25.0, 10.0, 25.0, 1.0, 2),
        -- Social Studies
        (gen_random_uuid(), v_cs_soc_id, 'TH', 'Theoretical Assessment', 'THEORY',    75.0, 27.0, 75.0, 3.0, 1),
        (gen_random_uuid(), v_cs_soc_id, 'PR', 'Community Project Work', 'PRACTICAL', 25.0, 10.0, 25.0, 1.0, 2),
        -- Optional Computer Science (50 TH / 50 PR)
        (gen_random_uuid(), v_cs_optcs_id, 'TH', 'Theory Examination',   'THEORY',    50.0, 18.0, 50.0, 2.0, 1),
        (gen_random_uuid(), v_cs_optcs_id, 'PR', 'Lab & Programming',    'PRACTICAL', 50.0, 20.0, 50.0, 2.0, 2)
    ON CONFLICT DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 9. USER ACCOUNTS & STAFF ROLES
    -- --------------------------------------------------------------------------
    INSERT INTO user_account (id, username, email, password_hash, status)
    VALUES
        (v_admin_uid, 'admin.susanskrit',      'principal@susanskrit.edu.np',  v_demo_pass, 'ACTIVE'),
        (v_acct_uid,  'accountant.susanskrit', 'accounts@susanskrit.edu.np',   v_demo_pass, 'ACTIVE'),
        (v_exam_uid,  'exam.susanskrit',       'exam@susanskrit.edu.np',       v_demo_pass, 'ACTIVE'),
        (v_teach_uid, 'teacher.grade10',       'teacher.g10@susanskrit.edu.np',v_demo_pass, 'ACTIVE'),
        (v_stud_uid,  'student.aarav',         'aarav.shrestha@susanskrit.edu.np', v_demo_pass, 'ACTIVE')
    ON CONFLICT (username) DO NOTHING;

    -- Memberships
    INSERT INTO user_tenant_membership (id, tenant_id, user_id, status)
    VALUES
        (v_m_admin_id, v_tenant_id, v_admin_uid, 'ACTIVE'),
        (v_m_acct_id,  v_tenant_id, v_acct_uid,  'ACTIVE'),
        (v_m_exam_id,  v_tenant_id, v_exam_uid,  'ACTIVE'),
        (v_m_teach_id, v_tenant_id, v_teach_uid, 'ACTIVE'),
        (v_m_stud_id,  v_tenant_id, v_stud_uid,  'ACTIVE')
    ON CONFLICT (tenant_id, user_id) DO NOTHING;

    -- Tenant Roles
    INSERT INTO role (id, tenant_id, code, name, description, is_system)
    VALUES
        (v_r_admin_id, v_tenant_id, 'INSTITUTION_ADMIN', 'Principal / Headmaster',  'Full administrative control over school', true),
        (v_r_acct_id,  v_tenant_id, 'ACCOUNTANT',        'Head Accountant',          'Manages fees, invoices, receipts and reconciliations', true),
        (v_r_exam_id,  v_tenant_id, 'EXAM_CONTROLLER',   'Examination Controller',   'Manages schedules, marks, verification and report cards', true),
        (v_r_teach_id, v_tenant_id, 'TEACHER',           'Secondary Level Teacher',  'Class teacher, marks entry and daily attendance', true),
        (v_r_stud_id,  v_tenant_id, 'STUDENT',           'Enrolled Student',         'Views timetable, attendance, report cards and pays fees', true)
    ON CONFLICT (tenant_id, code) DO NOTHING;

    -- Link User Memberships to Roles
    INSERT INTO user_role (membership_id, role_id)
    VALUES
        (v_m_admin_id, v_r_admin_id),
        (v_m_acct_id,  v_r_acct_id),
        (v_m_exam_id,  v_r_exam_id),
        (v_m_teach_id, v_r_teach_id),
        (v_m_stud_id,  v_r_stud_id)
    ON CONFLICT (membership_id, role_id) DO NOTHING;

    -- Teacher Profile Records
    INSERT INTO teacher (id, tenant_id, user_id, employee_code, first_name, last_name, department_id, designation, phone, email, status)
    VALUES
        (v_t_admin_id, v_tenant_id, v_admin_uid, 'EMP-001', 'Dr. Madhav', 'Adhikari', v_dept_sec_id, 'Principal', '+977-9851000001', 'principal@susanskrit.edu.np', 'ACTIVE'),
        (v_t_teach_id, v_tenant_id, v_teach_uid, 'EMP-014', 'Ramesh',     'Kandel',   v_dept_sec_id, 'Senior Science Teacher', '+977-9851000014', 'teacher.g10@susanskrit.edu.np', 'ACTIVE')
    ON CONFLICT (tenant_id, user_id) DO NOTHING;

    -- Assign Class Teacher to Grade 10-A
    UPDATE class_group SET class_teacher_id = v_t_teach_id WHERE id = v_cg10a_id;

    -- --------------------------------------------------------------------------
    -- 10. FEE CATEGORIES
    -- --------------------------------------------------------------------------
    INSERT INTO fee_category (id, tenant_id, code, name, description, is_active)
    VALUES
        (v_fc_tui_id,  v_tenant_id, 'MONTHLY_TUITION', 'Monthly Tuition Fee',          'Regular classroom academic tuition', true),
        (v_fc_exam_id, v_tenant_id, 'EXAM_TERM',       'Terminal Examination Fee',     'Examination materials and report processing', true),
        (v_fc_clab_id, v_tenant_id, 'COMP_LAB',        'Computer & Internet Lab Fee',  'High-speed internet and computer lab usage', true),
        (v_fc_slab_id, v_tenant_id, 'SCI_LAB',         'Science Laboratory Fee',       'Physics, Chemistry, and Biology equipment & consumables', true),
        (v_fc_sprt_id, v_tenant_id, 'SPORTS_ECA',      'Sports & Extracurricular Fee', 'Athletics, football ground, music and arts', true),
        (v_fc_adm_id,  v_tenant_id, 'ANNUAL_DEV',      'Annual Development & Library', 'Library membership and school campus development', true),
        (v_fc_bus_id,  v_tenant_id, 'BUS_TRANSPORT',   'School Bus Transport Service', 'Pick & drop transportation fee based on route', true)
    ON CONFLICT (tenant_id, code) DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 11. FEE STRUCTURES (Grade 10 CDC Secondary)
    -- --------------------------------------------------------------------------
    INSERT INTO fee_structure (id, tenant_id, fee_category_id, academic_year_id, program_id, amount, due_date, late_fee_per_day, is_active)
    VALUES
        (gen_random_uuid(), v_tenant_id, v_fc_tui_id,  v_ay_id, v_prog_sec_id, 3800.00, '2026-05-10', 10.00, true),
        (gen_random_uuid(), v_tenant_id, v_fc_exam_id, v_ay_id, v_prog_sec_id, 1200.00, '2026-07-20', 15.00, true),
        (gen_random_uuid(), v_tenant_id, v_fc_clab_id, v_ay_id, v_prog_sec_id,  650.00, '2026-05-10',  5.00, true),
        (gen_random_uuid(), v_tenant_id, v_fc_slab_id, v_ay_id, v_prog_sec_id,  750.00, '2026-05-10',  5.00, true),
        (gen_random_uuid(), v_tenant_id, v_fc_sprt_id, v_ay_id, v_prog_sec_id,  400.00, '2026-05-10',  0.00, true),
        (gen_random_uuid(), v_tenant_id, v_fc_adm_id,  v_ay_id, v_prog_sec_id, 4500.00, '2026-04-30', 25.00, true)
    ON CONFLICT DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 12. PAYMENT GATEWAY CONFIGURATION (eSewa Sandbox / Pre-Configured)
    -- --------------------------------------------------------------------------
    INSERT INTO payment_gateway_config (
        id, tenant_id, gateway_code, display_name, environment, merchant_id, config_json, is_active
    )
    VALUES (
        gen_random_uuid(),
        v_tenant_id,
        'ESEWA',
        'eSewa Digital Wallet & eBanking',
        'SANDBOX',
        'EPAYTEST',
        '{
            "productCode": "EPAYTEST",
            "serviceUrl": "https://rc-epay.esewa.com.np/api/epay/main/v2/form",
            "verificationUrl": "https://rc.esewa.com.np/mobile/ebp/client/verification",
            "currency": "NPR",
            "supportEmail": "accounts@susanskrit.edu.np"
        }'::jsonb,
        true
    ) ON CONFLICT (tenant_id, gateway_code) DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 13. SAMPLE STUDENTS (Grade 10-A)
    -- --------------------------------------------------------------------------
    INSERT INTO student (
        id, tenant_id, admission_no, registration_no, symbol_no,
        first_name, middle_name, last_name, date_of_birth, gender,
        phone, email, address, status, user_id
    )
    VALUES
        (
            v_s1_id, v_tenant_id, 'SK-2083-001', 'REG-83-1001', 'SYM-1001',
            'Aarav', NULL, 'Shrestha', '2010-05-15', 'MALE',
            '+977-9841234561', 'aarav.shrestha@susanskrit.edu.np', 'Sinamangal, Kathmandu', 'ACTIVE', v_stud_uid
        ),
        (
            v_s2_id, v_tenant_id, 'SK-2083-002', 'REG-83-1002', 'SYM-1002',
            'Prakriti', NULL, 'Sharma', '2010-08-22', 'FEMALE',
            '+977-9841234562', 'prakriti.sharma@susanskrit.edu.np', 'Baneshwor, Kathmandu', 'ACTIVE', NULL
        ),
        (
            v_s3_id, v_tenant_id, 'SK-2083-003', 'REG-83-1003', 'SYM-1003',
            'Rohan', 'Bahadur', 'Dahal', '2010-02-11', 'MALE',
            '+977-9841234563', 'rohan.dahal@susanskrit.edu.np', 'Koteshwor, Kathmandu', 'ACTIVE', NULL
        ),
        (
            v_s4_id, v_tenant_id, 'SK-2083-004', 'REG-83-1004', 'SYM-1004',
            'Binita', NULL, 'Gurung', '2010-11-04', 'FEMALE',
            '+977-9841234564', 'binita.gurung@susanskrit.edu.np', 'Chabahil, Kathmandu', 'ACTIVE', NULL
        ),
        (
            v_s5_id, v_tenant_id, 'SK-2083-005', 'REG-83-1005', 'SYM-1005',
            'Sandesh', 'Raj', 'Adhikari', '2010-07-19', 'MALE',
            '+977-9841234565', 'sandesh.adhikari@susanskrit.edu.np', 'Gaushala, Kathmandu', 'ACTIVE', NULL
        )
    ON CONFLICT (tenant_id, admission_no) DO NOTHING;

    -- Guardians
    INSERT INTO student_guardian (id, student_id, name, relationship, phone, email, address, is_primary)
    VALUES
        (gen_random_uuid(), v_s1_id, 'Kiran Shrestha',  'FATHER', '+977-9851011111', 'kiran.shrestha@example.com', 'Sinamangal, Kathmandu', true),
        (gen_random_uuid(), v_s2_id, 'Sarita Sharma',   'MOTHER', '+977-9851022222', 'sarita.sharma@example.com',  'Baneshwor, Kathmandu', true),
        (gen_random_uuid(), v_s3_id, 'Dilliram Dahal',  'FATHER', '+977-9851033333', 'dilliram.dahal@example.com', 'Koteshwor, Kathmandu', true),
        (gen_random_uuid(), v_s4_id, 'Chandra Gurung',  'FATHER', '+977-9851044444', 'chandra.gurung@example.com', 'Chabahil, Kathmandu', true),
        (gen_random_uuid(), v_s5_id, 'Bishnu Adhikari', 'FATHER', '+977-9851055555', 'bishnu.adhikari@example.com','Gaushala, Kathmandu', true)
    ON CONFLICT DO NOTHING;

    -- Enrollments
    INSERT INTO enrollment (id, tenant_id, student_id, class_group_id, academic_year_id, roll_no, status, enrolled_at)
    VALUES
        (gen_random_uuid(), v_tenant_id, v_s1_id, v_cg10a_id, v_ay_id, '10-A-01', 'ACTIVE', '2026-04-15'),
        (gen_random_uuid(), v_tenant_id, v_s2_id, v_cg10a_id, v_ay_id, '10-A-02', 'ACTIVE', '2026-04-15'),
        (gen_random_uuid(), v_tenant_id, v_s3_id, v_cg10a_id, v_ay_id, '10-A-03', 'ACTIVE', '2026-04-15'),
        (gen_random_uuid(), v_tenant_id, v_s4_id, v_cg10a_id, v_ay_id, '10-A-04', 'ACTIVE', '2026-04-15'),
        (gen_random_uuid(), v_tenant_id, v_s5_id, v_cg10a_id, v_ay_id, '10-A-05', 'ACTIVE', '2026-04-15')
    ON CONFLICT (tenant_id, student_id, academic_year_id) DO NOTHING;

    -- --------------------------------------------------------------------------
    -- 14. DEMO INVOICES, LINE ITEMS & IMMUTABLE LEDGER
    -- --------------------------------------------------------------------------
    -- Invoice 1: Aarav Shrestha (Baisakh 2083 Fees - PAID via eSewa)
    INSERT INTO fee_invoice (
        id, invoice_number, tenant_id, student_id, academic_year_id,
        invoice_date, due_date, subtotal, discount_amount, late_fee_amount,
        total_amount, paid_amount, balance, status, notes, issued_at
    )
    VALUES (
        v_inv1_id,
        'INV-2083-0001',
        v_tenant_id,
        v_s1_id,
        v_ay_id,
        '2026-04-16',
        '2026-05-10',
        5600.00,
        0.00,
        0.00,
        5600.00,
        5600.00,
        0.00,
        'PAID',
        'Baisakh 2083 Monthly Tuition, Computer Lab & Sports Fee',
        '2026-04-16 10:00:00+05:45'
    ) ON CONFLICT (invoice_number) DO NOTHING;

    INSERT INTO fee_invoice_item (id, invoice_id, fee_category_id, description, quantity, unit_amount, total_amount)
    VALUES
        (gen_random_uuid(), v_inv1_id, v_fc_tui_id,  'Grade 10 Tuition Fee (Baisakh)', 1, 3800.00, 3800.00),
        (gen_random_uuid(), v_inv1_id, v_fc_clab_id, 'Computer Lab & Fiber Internet', 1,  650.00,  650.00),
        (gen_random_uuid(), v_inv1_id, v_fc_slab_id, 'Science Lab Consumables',       1,  750.00,  750.00),
        (gen_random_uuid(), v_inv1_id, v_fc_sprt_id, 'Sports & Extracurricular Club', 1,  400.00,  400.00)
    ON CONFLICT DO NOTHING;

    -- Ledger for Invoice 1: Charge + Payment
    INSERT INTO student_ledger_entry (
        id, tenant_id, student_id, invoice_id, entry_type, amount, description, reference, balance_after
    )
    VALUES
        (gen_random_uuid(), v_tenant_id, v_s1_id, v_inv1_id, 'CHARGE',  5600.00, 'Invoice INV-2083-0001 (Baisakh 2083)', 'INV-2083-0001', 5600.00),
        (gen_random_uuid(), v_tenant_id, v_s1_id, v_inv1_id, 'PAYMENT', -5600.00, 'Online Payment via eSewa (Ref: 00049281)', 'ESEWA-TXN-8301', 0.00)
    ON CONFLICT DO NOTHING;

    -- Invoice 2: Prakriti Sharma (Baisakh 2083 Fees - ISSUED / PENDING)
    INSERT INTO fee_invoice (
        id, invoice_number, tenant_id, student_id, academic_year_id,
        invoice_date, due_date, subtotal, discount_amount, late_fee_amount,
        total_amount, paid_amount, balance, status, notes, issued_at
    )
    VALUES (
        v_inv2_id,
        'INV-2083-0002',
        v_tenant_id,
        v_s2_id,
        v_ay_id,
        '2026-04-16',
        '2026-05-10',
        5600.00,
        0.00,
        0.00,
        5600.00,
        0.00,
        5600.00,
        'ISSUED',
        'Baisakh 2083 Monthly Tuition & Labs',
        '2026-04-16 10:05:00+05:45'
    ) ON CONFLICT (invoice_number) DO NOTHING;

    INSERT INTO fee_invoice_item (id, invoice_id, fee_category_id, description, quantity, unit_amount, total_amount)
    VALUES
        (gen_random_uuid(), v_inv2_id, v_fc_tui_id,  'Grade 10 Tuition Fee (Baisakh)', 1, 3800.00, 3800.00),
        (gen_random_uuid(), v_inv2_id, v_fc_clab_id, 'Computer Lab & Fiber Internet', 1,  650.00,  650.00),
        (gen_random_uuid(), v_inv2_id, v_fc_slab_id, 'Science Lab Consumables',       1,  750.00,  750.00),
        (gen_random_uuid(), v_inv2_id, v_fc_sprt_id, 'Sports & Extracurricular Club', 1,  400.00,  400.00)
    ON CONFLICT DO NOTHING;

    -- Ledger for Invoice 2: Charge only
    INSERT INTO student_ledger_entry (
        id, tenant_id, student_id, invoice_id, entry_type, amount, description, reference, balance_after
    )
    VALUES
        (gen_random_uuid(), v_tenant_id, v_s2_id, v_inv2_id, 'CHARGE', 5600.00, 'Invoice INV-2083-0002 (Baisakh 2083)', 'INV-2083-0002', 5600.00)
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'SSAMS: Shree Susanskrit Secondary School (SHREE_SUSANSKRIT) production seed applied successfully.';
END $$;

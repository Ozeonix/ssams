-- ==============================================================================
-- ARTMS Pilot Institution Synthetic Seed Dataset
-- Institution: Himalayan Model Academy (Code: PILOT_HMA)
-- Environment: Pilot / Staging Validation
-- Notice: Contains ONLY synthetic / anonymized data. Complies with AGENT_RULES.
-- ==============================================================================

DO $$
DECLARE
    v_tenant_id UUID := 'a1000000-0000-0000-0000-000000000001'::uuid;
    v_ay_id     UUID := 'a2000000-0000-0000-0000-000000000001'::uuid;
    v_term1_id  UUID := 'a3000000-0000-0000-0000-000000000001'::uuid;
    v_dept_id   UUID := 'a4000000-0000-0000-0000-000000000001'::uuid;
    v_prog_id   UUID := 'a5000000-0000-0000-0000-000000000001'::uuid;
    v_cg9_id    UUID := 'a6000000-0000-0000-0000-000000000001'::uuid;
    v_cg10_id   UUID := 'a6000000-0000-0000-0000-000000000002'::uuid;
    v_scheme_id UUID := 'a7000000-0000-0000-0000-000000000001'::uuid;
    v_curr_id   UUID := 'a8000000-0000-0000-0000-000000000001'::uuid;
    v_s_eng_id  UUID := 'b1000000-0000-0000-0000-000000000001'::uuid;
    v_s_mth_id  UUID := 'b1000000-0000-0000-0000-000000000002'::uuid;
    v_s_sci_id  UUID := 'b1000000-0000-0000-0000-000000000003'::uuid;
    v_s_soc_id  UUID := 'b1000000-0000-0000-0000-000000000004'::uuid;
    v_cs_eng_id UUID := 'b2000000-0000-0000-0000-000000000001'::uuid;
    v_cs_mth_id UUID := 'b2000000-0000-0000-0000-000000000002'::uuid;
    v_cs_sci_id UUID := 'b2000000-0000-0000-0000-000000000003'::uuid;
    v_cs_soc_id UUID := 'b2000000-0000-0000-0000-000000000004'::uuid;
    v_admin_uid UUID := 'c1000000-0000-0000-0000-000000000001'::uuid;
    v_teach_uid UUID := 'c1000000-0000-0000-0000-000000000002'::uuid;
    v_s1_id     UUID := 'd1000000-0000-0000-0000-000000000001'::uuid;
    v_s2_id     UUID := 'd1000000-0000-0000-0000-000000000002'::uuid;
    v_s3_id     UUID := 'd1000000-0000-0000-0000-000000000003'::uuid;
BEGIN
    -- 1. TENANT
    INSERT INTO tenant (id, code, name, status, timezone, locale, branding, modules, settings)
    VALUES (
        v_tenant_id,
        'PILOT_HMA',
        'Himalayan Model Academy',
        'ACTIVE',
        'Asia/Kathmandu',
        'en',
        '{"primaryColor": "#1E40AF", "logoUrl": "https://cdn.artms.local/assets/hma-logo.png"}'::jsonb,
        '{"attendance": true, "exams": true, "documents": true, "outbox": true}'::jsonb,
        '{"academicYear": "2026-2027", "schoolType": "SECONDARY"}'::jsonb
    ) ON CONFLICT (code) DO NOTHING;

    -- 2. ACADEMIC YEAR
    INSERT INTO academic_year (id, tenant_id, name, start_date, end_date, status)
    VALUES (v_ay_id, v_tenant_id, '2026-2027', '2026-04-14', '2027-04-13', 'ACTIVE')
    ON CONFLICT (tenant_id, name) DO NOTHING;

    -- 3. TERMS
    INSERT INTO term (id, tenant_id, academic_year_id, name, sequence, start_date, end_date)
    VALUES 
        (v_term1_id, v_tenant_id, v_ay_id, 'First Term', 1, '2026-04-14', '2026-08-15'),
        (gen_random_uuid(), v_tenant_id, v_ay_id, 'Second Term', 2, '2026-08-16', '2026-12-15'),
        (gen_random_uuid(), v_tenant_id, v_ay_id, 'Final Term', 3, '2026-12-16', '2027-04-13')
    ON CONFLICT (academic_year_id, sequence) DO NOTHING;

    -- 4. DEPARTMENT & PROGRAM
    INSERT INTO department (id, tenant_id, code, name, status)
    VALUES (v_dept_id, v_tenant_id, 'SEC_DEPT', 'Department of Secondary Education', 'ACTIVE')
    ON CONFLICT (tenant_id, code) DO NOTHING;

    INSERT INTO program (id, tenant_id, department_id, code, name, level, duration_years, status)
    VALUES (v_prog_id, v_tenant_id, v_dept_id, 'PROG_SEC', 'Secondary School Program', 'GRADE_9_10', 2, 'ACTIVE')
    ON CONFLICT (tenant_id, code) DO NOTHING;

    -- 5. CLASS GROUPS
    INSERT INTO class_group (id, tenant_id, program_id, academic_year_id, grade_level, section, stream, capacity, status)
    VALUES 
        (v_cg9_id, v_tenant_id, v_prog_id, v_ay_id, 'Grade 9', 'A', 'General', 40, 'ACTIVE'),
        (v_cg10_id, v_tenant_id, v_prog_id, v_ay_id, 'Grade 10', 'A', 'General', 40, 'ACTIVE')
    ON CONFLICT DO NOTHING;

    -- 6. GRADING SCHEME & GRADE BANDS
    INSERT INTO grading_scheme (id, tenant_id, name, version, status, gpa_precision, gpa_rounding)
    VALUES (v_scheme_id, v_tenant_id, 'NEB Standard 4.0 Scale', 1, 'ACTIVE', 2, 'HALF_UP')
    ON CONFLICT (tenant_id, name, version) DO NOTHING;

    INSERT INTO grade_band (id, grading_scheme_id, min_percentage, max_percentage, letter_grade, grade_point, pass_flag, remarks)
    VALUES
        (gen_random_uuid(), v_scheme_id, 90.0, 100.0, 'A+', 4.00, true, 'Outstanding'),
        (gen_random_uuid(), v_scheme_id, 80.0, 89.999, 'A',  3.60, true, 'Excellent'),
        (gen_random_uuid(), v_scheme_id, 70.0, 79.999, 'B+', 3.20, true, 'Very Good'),
        (gen_random_uuid(), v_scheme_id, 60.0, 69.999, 'B',  2.80, true, 'Good'),
        (gen_random_uuid(), v_scheme_id, 50.0, 59.999, 'C+', 2.40, true, 'Satisfactory'),
        (gen_random_uuid(), v_scheme_id, 40.0, 49.999, 'C',  2.00, true, 'Acceptable'),
        (gen_random_uuid(), v_scheme_id, 35.0, 39.999, 'D',  1.60, true, 'Basic'),
        (gen_random_uuid(), v_scheme_id, 0.0,  34.999, 'NG', 0.00, false, 'Non-Graded')
    ON CONFLICT DO NOTHING;

    -- 7. SUBJECTS
    INSERT INTO subject (id, tenant_id, code, name, subject_type, active)
    VALUES
        (v_s_eng_id, v_tenant_id, 'ENG-101', 'Compulsory English', 'MIXED', true),
        (v_s_mth_id, v_tenant_id, 'MTH-102', 'Compulsory Mathematics', 'THEORY', true),
        (v_s_sci_id, v_tenant_id, 'SCI-103', 'Science and Technology', 'MIXED', true),
        (v_s_soc_id, v_tenant_id, 'SOC-104', 'Social Studies', 'THEORY', true)
    ON CONFLICT (tenant_id, code) DO NOTHING;

    -- 8. CURRICULUM & CURRICULUM SUBJECTS
    INSERT INTO curriculum (id, tenant_id, program_id, academic_year_id, version, status)
    VALUES (v_curr_id, v_tenant_id, v_prog_id, v_ay_id, 1, 'PUBLISHED')
    ON CONFLICT (program_id, academic_year_id, version) DO NOTHING;

    INSERT INTO curriculum_subject (id, curriculum_id, subject_id, grade_level, sequence, mandatory, credit_hours, grading_scheme_id)
    VALUES
        (v_cs_eng_id, v_curr_id, v_s_eng_id, 'Grade 10', 1, true, 4.0, v_scheme_id),
        (v_cs_mth_id, v_curr_id, v_s_mth_id, 'Grade 10', 2, true, 4.0, v_scheme_id),
        (v_cs_sci_id, v_curr_id, v_s_sci_id, 'Grade 10', 3, true, 4.0, v_scheme_id),
        (v_cs_soc_id, v_curr_id, v_s_soc_id, 'Grade 10', 4, true, 4.0, v_scheme_id)
    ON CONFLICT (curriculum_id, subject_id, grade_level) DO NOTHING;

    -- 9. SUBJECT COMPONENTS
    INSERT INTO subject_component (id, curriculum_subject_id, code, name, assessment_type, full_marks, pass_marks, weight, credit_hours, sequence)
    VALUES
        (gen_random_uuid(), v_cs_eng_id, 'TH', 'Theory', 'THEORY', 75.0, 27.0, 75.0, 3.0, 1),
        (gen_random_uuid(), v_cs_eng_id, 'PR', 'Practical', 'PRACTICAL', 25.0, 10.0, 25.0, 1.0, 2),
        (gen_random_uuid(), v_cs_mth_id, 'TH', 'Theory', 'THEORY', 100.0, 35.0, 100.0, 4.0, 1),
        (gen_random_uuid(), v_cs_sci_id, 'TH', 'Theory', 'THEORY', 75.0, 27.0, 75.0, 3.0, 1),
        (gen_random_uuid(), v_cs_sci_id, 'PR', 'Practical', 'PRACTICAL', 25.0, 10.0, 25.0, 1.0, 2),
        (gen_random_uuid(), v_cs_soc_id, 'TH', 'Theory', 'THEORY', 100.0, 35.0, 100.0, 4.0, 1)
    ON CONFLICT DO NOTHING;

    -- 10. PILOT USER ACCOUNTS & MEMBERSHIPS
    -- Password hash for: 'PilotPass123!' (Argon2id dummy hash compliant with system)
    INSERT INTO user_account (id, username, email, password_hash, status)
    VALUES 
        (v_admin_uid, 'pilot_admin', 'admin@pilot-hma.edu.np', '$argon2id$v=19$m=65536,t=3,p=1$c29tZXNhbHQ$abcdefghijklmnopqrstuvwxyz1234567890', 'ACTIVE'),
        (v_teach_uid, 'pilot_teacher', 'teacher@pilot-hma.edu.np', '$argon2id$v=19$m=65536,t=3,p=1$c29tZXNhbHQ$abcdefghijklmnopqrstuvwxyz1234567890', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING;

    INSERT INTO user_tenant_membership (id, tenant_id, user_id, status)
    VALUES 
        (gen_random_uuid(), v_tenant_id, v_admin_uid, 'ACTIVE'),
        (gen_random_uuid(), v_tenant_id, v_teach_uid, 'ACTIVE')
    ON CONFLICT (tenant_id, user_id) DO NOTHING;

    -- 11. SYNTHETIC PILOT STUDENTS
    INSERT INTO student (id, tenant_id, admission_no, registration_no, symbol_no, first_name, last_name, date_of_birth, gender, phone, email, status)
    VALUES
        (v_s1_id, v_tenant_id, 'PILOT-ADM-001', 'REG-2026-001', 'SYM-2026-001', 'Sunita', 'Adhikari', '2010-06-12', 'FEMALE', '+9779800000011', 'sunita@example.test', 'ACTIVE'),
        (v_s2_id, v_tenant_id, 'PILOT-ADM-002', 'REG-2026-002', 'SYM-2026-002', 'Bikash', 'Thapa', '2010-09-24', 'MALE', '+9779800000012', 'bikash@example.test', 'ACTIVE'),
        (v_s3_id, v_tenant_id, 'PILOT-ADM-003', 'REG-2026-003', 'SYM-2026-003', 'Prashant', 'Khadka', '2010-03-18', 'MALE', '+9779800000013', 'prashant@example.test', 'ACTIVE')
    ON CONFLICT (tenant_id, admission_no) DO NOTHING;

    -- 12. ENROLLMENTS
    INSERT INTO enrollment (id, tenant_id, student_id, class_group_id, academic_year_id, roll_no, status, enrolled_at)
    VALUES
        (gen_random_uuid(), v_tenant_id, v_s1_id, v_cg10_id, v_ay_id, '10-A-01', 'ACTIVE', '2026-04-15'),
        (gen_random_uuid(), v_tenant_id, v_s2_id, v_cg10_id, v_ay_id, '10-A-02', 'ACTIVE', '2026-04-15'),
        (gen_random_uuid(), v_tenant_id, v_s3_id, v_cg10_id, v_ay_id, '10-A-03', 'ACTIVE', '2026-04-15')
    ON CONFLICT (tenant_id, student_id, academic_year_id) DO NOTHING;

    RAISE NOTICE 'ARTMS Pilot Institution Seed applied successfully for tenant: PILOT_HMA';
END $$;

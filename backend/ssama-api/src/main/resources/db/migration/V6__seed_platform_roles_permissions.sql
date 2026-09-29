-- V6: Seed data — Platform roles, permissions, and platform super-admin
-- This uses synthetic/anonymized data only.

-- ============================================================
-- PERMISSIONS
-- ============================================================
INSERT INTO permission (id, code, description, domain) VALUES
  -- Tenant/institution
  (gen_random_uuid(), 'tenant:manage',        'Manage tenants',                   'tenant'),
  (gen_random_uuid(), 'institution:read',     'Read institution settings',         'institution'),
  (gen_random_uuid(), 'institution:update',   'Update institution settings',       'institution'),
  -- Academic
  (gen_random_uuid(), 'academic:read',        'Read academic configuration',       'academic'),
  (gen_random_uuid(), 'academic:manage',      'Manage academic config',            'academic'),
  -- Students
  (gen_random_uuid(), 'student:read',         'Read student records',              'student'),
  (gen_random_uuid(), 'student:create',       'Create student records',            'student'),
  (gen_random_uuid(), 'student:update',       'Update student records',            'student'),
  (gen_random_uuid(), 'student:delete',       'Delete/deactivate student records', 'student'),
  -- Enrollment
  (gen_random_uuid(), 'enrollment:manage',    'Manage student enrollments',        'enrollment'),
  -- Attendance
  (gen_random_uuid(), 'attendance:read',      'Read attendance records',           'attendance'),
  (gen_random_uuid(), 'attendance:write',     'Submit attendance records',         'attendance'),
  (gen_random_uuid(), 'attendance:correct',   'Correct attendance records',        'attendance'),
  -- Marks
  (gen_random_uuid(), 'marks:read',           'Read mark entries',                 'marks'),
  (gen_random_uuid(), 'marks:write',          'Enter marks',                       'marks'),
  (gen_random_uuid(), 'marks:submit',         'Submit marks for verification',     'marks'),
  (gen_random_uuid(), 'marks:verify',         'Verify submitted marks',            'marks'),
  -- Results
  (gen_random_uuid(), 'result:read',          'Read result data',                  'result'),
  (gen_random_uuid(), 'result:preview',       'Preview result before publication', 'result'),
  (gen_random_uuid(), 'result:verify',        'Verify result',                     'result'),
  (gen_random_uuid(), 'result:approve',       'Approve result',                    'result'),
  (gen_random_uuid(), 'result:publish',       'Publish result',                    'result'),
  (gen_random_uuid(), 'result:correct',       'Initiate result correction',        'result'),
  -- Documents
  (gen_random_uuid(), 'document:read',        'Read/download documents',           'document'),
  (gen_random_uuid(), 'document:generate',    'Generate official documents',       'document'),
  (gen_random_uuid(), 'document:revoke',      'Revoke documents',                  'document'),
  -- Notices
  (gen_random_uuid(), 'notice:read',          'Read notices',                      'notice'),
  (gen_random_uuid(), 'notice:manage',        'Create and manage notices',         'notice'),
  -- Reports
  (gen_random_uuid(), 'report:read',          'Access reports',                    'report'),
  (gen_random_uuid(), 'report:export',        'Export reports',                    'report'),
  -- Settings
  (gen_random_uuid(), 'settings:manage',      'Manage institution settings',       'settings'),
  -- Roles
  (gen_random_uuid(), 'role:manage',          'Manage roles and permissions',      'role'),
  -- Audit
  (gen_random_uuid(), 'audit:read',           'Read audit logs',                   'audit'),
  -- Platform
  (gen_random_uuid(), 'platform:admin',       'Platform super-admin access',       'platform');

-- ============================================================
-- PLATFORM ROLE — SUPER_ADMIN (tenant_id is NULL)
-- ============================================================
INSERT INTO role (id, tenant_id, code, name, description, is_system) VALUES
  (gen_random_uuid(), NULL, 'PLATFORM_SUPER_ADMIN', 'Platform Super Admin',
   'Unrestricted platform administrator', TRUE);

-- Grant all permissions to PLATFORM_SUPER_ADMIN
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.code = 'PLATFORM_SUPER_ADMIN' AND r.tenant_id IS NULL;

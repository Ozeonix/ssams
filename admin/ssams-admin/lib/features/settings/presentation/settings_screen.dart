import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_admin/core/theme/admin_theme.dart';

class SettingsScreen extends ConsumerStatefulWidget {
  const SettingsScreen({super.key});

  @override
  ConsumerState<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends ConsumerState<SettingsScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: const [
                    Text(
                      'Institution Settings & Administration',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        color: AdminTheme.secondaryColor,
                      ),
                    ),
                    SizedBox(height: 4),
                    Text(
                      'Branding, feature flags, role-based access control, and platform audit logs.',
                      style: TextStyle(color: AdminTheme.textSecondary, fontSize: 13),
                    ),
                  ],
                ),
                ElevatedButton.icon(
                  onPressed: () {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Institution settings saved successfully.')),
                    );
                  },
                  icon: const Icon(Icons.save_rounded, size: 18),
                  label: const Text('Save Settings'),
                ),
              ],
            ),
            const SizedBox(height: 20),
            Container(
              decoration: const BoxDecoration(
                border: Border(bottom: BorderSide(color: Color(0xFFE2E8F0))),
              ),
              child: TabBar(
                controller: _tabController,
                isScrollable: true,
                tabAlignment: TabAlignment.start,
                labelColor: AdminTheme.primaryColor,
                unselectedLabelColor: AdminTheme.textSecondary,
                indicatorColor: AdminTheme.primaryColor,
                labelStyle: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14),
                tabs: const [
                  Tab(text: 'Branding & Identity'),
                  Tab(text: 'Feature Modules'),
                  Tab(text: 'Roles & Permissions'),
                  Tab(text: 'Audit Trail Explorer'),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Expanded(
              child: TabBarView(
                controller: _tabController,
                children: const [
                  _BrandingTab(),
                  _FeatureModulesTab(),
                  _RolesPermissionsTab(),
                  _AuditLogTab(),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _BrandingTab extends StatelessWidget {
  const _BrandingTab();

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          const Text('Institution Identity', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 16),
          TextFormField(
            initialValue: 'Himalayan Model Academy',
            decoration: const InputDecoration(labelText: 'Institution Name'),
          ),
          const SizedBox(height: 16),
          TextFormField(
            initialValue: 'PILOT_HMA',
            decoration: const InputDecoration(labelText: 'Tenant Code (Immutable)'),
            enabled: false,
          ),
          const SizedBox(height: 16),
          TextFormField(
            initialValue: 'Asia/Kathmandu',
            decoration: const InputDecoration(labelText: 'Timezone'),
          ),
          const SizedBox(height: 16),
          TextFormField(
            initialValue: '#1E40AF',
            decoration: const InputDecoration(labelText: 'Primary Brand Color (HEX)'),
          ),
        ],
      ),
    );
  }
}

class _FeatureModulesTab extends StatefulWidget {
  const _FeatureModulesTab();

  @override
  State<_FeatureModulesTab> createState() => _FeatureModulesTabState();
}

class _FeatureModulesTabState extends State<_FeatureModulesTab> {
  bool _attendanceEnabled = true;
  bool _examsEnabled = true;
  bool _documentsEnabled = true;
  bool _timetableEnabled = false;
  bool _financeEnabled = false;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          const Text('Active System Modules', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          const Text('Enable or disable feature flags for your institution tenant.', style: TextStyle(color: AdminTheme.textSecondary, fontSize: 13)),
          const SizedBox(height: 16),
          SwitchListTile(
            title: const Text('Attendance Tracking & Percentage Alerts'),
            subtitle: const Text('Daily period/session logging and low attendance alerts'),
            value: _attendanceEnabled,
            onChanged: (v) => setState(() => _attendanceEnabled = v),
          ),
          const Divider(),
          SwitchListTile(
            title: const Text('Exams & GPA Calculation Engine'),
            subtitle: const Text('Theory/Practical component marks grids and result snapshots'),
            value: _examsEnabled,
            onChanged: (v) => setState(() => _examsEnabled = v),
          ),
          const Divider(),
          SwitchListTile(
            title: const Text('Document Generation & Anti-Tamper Verification'),
            subtitle: const Text('Cryptographic SHA-256 grade sheets, transcripts, and QR verification'),
            value: _documentsEnabled,
            onChanged: (v) => setState(() => _documentsEnabled = v),
          ),
          const Divider(),
          SwitchListTile(
            title: const Text('Timetable & Scheduling (Optional P1)'),
            subtitle: const Text('Conflict detection for classrooms and teachers'),
            value: _timetableEnabled,
            onChanged: (v) => setState(() => _timetableEnabled = v),
          ),
          const Divider(),
          SwitchListTile(
            title: const Text('Fee & Finance Management (Optional P1)'),
            subtitle: const Text('Invoicing, receipts, and fee reports'),
            value: _financeEnabled,
            onChanged: (v) => setState(() => _financeEnabled = v),
          ),
        ],
      ),
    );
  }
}

class _RolesPermissionsTab extends StatelessWidget {
  const _RolesPermissionsTab();

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text('Provisioned Staff Roles', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              ElevatedButton.icon(
                onPressed: () {},
                icon: const Icon(Icons.add_moderator_rounded, size: 16),
                label: const Text('Add Role'),
              ),
            ],
          ),
          const SizedBox(height: 16),
          DataTable(
            columns: const [
              DataColumn(label: Text('Role Name')),
              DataColumn(label: Text('Role Code')),
              DataColumn(label: Text('Assigned Permissions')),
              DataColumn(label: Text('Type')),
            ],
            rows: const [
              DataRow(cells: [
                DataCell(Text('School Administrator', style: TextStyle(fontWeight: FontWeight.w600))),
                DataCell(Text('SCHOOL_ADMIN')),
                DataCell(Text('All institutional permissions (Academic, Students, Exams, Settings)')),
                DataCell(Chip(label: Text('SYSTEM', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold)))),
              ]),
              DataRow(cells: [
                DataCell(Text('Class Teacher', style: TextStyle(fontWeight: FontWeight.w600))),
                DataCell(Text('TEACHER')),
                DataCell(Text('attendance:write, marks:write, student:read')),
                DataCell(Chip(label: Text('STANDARD', style: TextStyle(fontSize: 10)))),
              ]),
              DataRow(cells: [
                DataCell(Text('Examination Controller', style: TextStyle(fontWeight: FontWeight.w600))),
                DataCell(Text('EXAM_OFFICER')),
                DataCell(Text('marks:verify, result:approve, result:publish, document:generate')),
                DataCell(Chip(label: Text('STANDARD', style: TextStyle(fontSize: 10)))),
              ]),
            ],
          ),
        ],
      ),
    );
  }
}

class _AuditLogTab extends StatelessWidget {
  const _AuditLogTab();

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: const [
              Text('Audit Trail Explorer', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              Chip(label: Text('Immutable Log', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)), backgroundColor: Color(0xFFF1F5F9)),
            ],
          ),
          const SizedBox(height: 12),
          DataTable(
            columns: const [
              DataColumn(label: Text('Timestamp (UTC)')),
              DataColumn(label: Text('Action')),
              DataColumn(label: Text('Target Entity')),
              DataColumn(label: Text('Operator')),
              DataColumn(label: Text('Details')),
            ],
            rows: const [
              DataRow(cells: [
                DataCell(Text('2026-09-30 01:25:00')),
                DataCell(Text('attendance.submitted', style: TextStyle(fontWeight: FontWeight.w600, color: Color(0xFF16A34A)))),
                DataCell(Text('AttendanceSession (Grade 10)')),
                DataCell(Text('pilot_teacher')),
                DataCell(Text('38 records submitted for Period 1')),
              ]),
              DataRow(cells: [
                DataCell(Text('2026-09-30 01:15:20')),
                DataCell(Text('curriculum.published', style: TextStyle(fontWeight: FontWeight.w600, color: Color(0xFF2563EB)))),
                DataCell(Text('Curriculum (Grade 9-10 v1)')),
                DataCell(Text('pilot_admin')),
                DataCell(Text('4 mandatory subjects locked and sealed')),
              ]),
              DataRow(cells: [
                DataCell(Text('2026-09-30 01:05:10')),
                DataCell(Text('student.batch_imported', style: TextStyle(fontWeight: FontWeight.w600, color: Color(0xFF9333EA)))),
                DataCell(Text('Student Roster')),
                DataCell(Text('pilot_admin')),
                DataCell(Text('Total: 5, Imported: 5, Skipped: 0')),
              ]),
            ],
          ),
        ],
      ),
    );
  }
}

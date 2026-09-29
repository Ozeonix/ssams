import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_admin/core/theme/admin_theme.dart';

class AcademicScreen extends ConsumerStatefulWidget {
  const AcademicScreen({super.key});

  @override
  ConsumerState<AcademicScreen> createState() => _AcademicScreenState();
}

class _AcademicScreenState extends ConsumerState<AcademicScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
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
                      'Academic Structure & Curriculum',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        color: AdminTheme.secondaryColor,
                      ),
                    ),
                    SizedBox(height: 4),
                    Text(
                      'Manage versioned academic years, class cohorts, subjects, and grading schemes.',
                      style: TextStyle(color: AdminTheme.textSecondary, fontSize: 13),
                    ),
                  ],
                ),
                ElevatedButton.icon(
                  onPressed: () {},
                  icon: const Icon(Icons.add_rounded, size: 18),
                  label: const Text('New Academic Entity'),
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
                  Tab(text: 'Academic Years & Terms'),
                  Tab(text: 'Class Groups & Cohorts'),
                  Tab(text: 'Curriculum & Grading Schemes'),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Expanded(
              child: TabBarView(
                controller: _tabController,
                children: const [
                  _AcademicYearsTab(),
                  _ClassGroupsTab(),
                  _CurriculumTab(),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _AcademicYearsTab extends StatelessWidget {
  const _AcademicYearsTab();

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: const [
              Text(
                'Configured Academic Years',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              Chip(
                label: Text('Current: 2026-2027', style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                backgroundColor: Color(0xFFEFF6FF),
              ),
            ],
          ),
          const SizedBox(height: 12),
          DataTable(
            columns: const [
              DataColumn(label: Text('Year Name')),
              DataColumn(label: Text('Start Date')),
              DataColumn(label: Text('End Date')),
              DataColumn(label: Text('Terms')),
              DataColumn(label: Text('Status')),
              DataColumn(label: Text('Actions')),
            ],
            rows: [
              DataRow(cells: [
                const DataCell(Text('2026-2027', style: TextStyle(fontWeight: FontWeight.w600))),
                const DataCell(Text('2026-04-14')),
                const DataCell(Text('2027-04-13')),
                const DataCell(Text('3 Terms (First, Second, Final)')),
                DataCell(
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                    decoration: BoxDecoration(
                      color: const Color(0xFFDCFCE7),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: const Text('ACTIVE', style: TextStyle(color: Color(0xFF166534), fontSize: 11, fontWeight: FontWeight.bold)),
                  ),
                ),
                DataCell(
                  IconButton(
                    icon: const Icon(Icons.edit_outlined, size: 18),
                    onPressed: () {},
                  ),
                ),
              ]),
              DataRow(cells: [
                const DataCell(Text('2025-2026', style: TextStyle(fontWeight: FontWeight.w600))),
                const DataCell(Text('2025-04-14')),
                const DataCell(Text('2026-04-13')),
                const DataCell(Text('3 Terms')),
                DataCell(
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                    decoration: BoxDecoration(
                      color: const Color(0xFFF1F5F9),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: const Text('COMPLETED', style: TextStyle(color: Color(0xFF475569), fontSize: 11, fontWeight: FontWeight.bold)),
                  ),
                ),
                DataCell(
                  IconButton(
                    icon: const Icon(Icons.history_rounded, size: 18),
                    onPressed: () {},
                  ),
                ),
              ]),
            ],
          ),
        ],
      ),
    );
  }
}

class _ClassGroupsTab extends StatelessWidget {
  const _ClassGroupsTab();

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text('Class Groups (2026-2027)', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 12),
          DataTable(
            columns: const [
              DataColumn(label: Text('Grade Level')),
              DataColumn(label: Text('Section')),
              DataColumn(label: Text('Stream')),
              DataColumn(label: Text('Enrolled / Capacity')),
              DataColumn(label: Text('Class Teacher')),
              DataColumn(label: Text('Status')),
            ],
            rows: const [
              DataRow(cells: [
                DataCell(Text('Grade 10', style: TextStyle(fontWeight: FontWeight.w600))),
                DataCell(Text('A')),
                DataCell(Text('General')),
                DataCell(Text('38 / 40')),
                DataCell(Text('Deepak Joshi')),
                DataCell(Text('ACTIVE', style: TextStyle(color: AdminTheme.successColor, fontWeight: FontWeight.bold, fontSize: 11))),
              ]),
              DataRow(cells: [
                DataCell(Text('Grade 9', style: TextStyle(fontWeight: FontWeight.w600))),
                DataCell(Text('A')),
                DataCell(Text('General')),
                DataCell(Text('35 / 40')),
                DataCell(Text('Anita Gurung')),
                DataCell(Text('ACTIVE', style: TextStyle(color: AdminTheme.successColor, fontWeight: FontWeight.bold, fontSize: 11))),
              ]),
            ],
          ),
        ],
      ),
    );
  }
}

class _CurriculumTab extends StatelessWidget {
  const _CurriculumTab();

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text('Published Curriculum Versions', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 12),
          ListTile(
            leading: const Icon(Icons.menu_book_rounded, color: AdminTheme.primaryColor),
            title: const Text('Secondary Education (Grade 9-10) — Curriculum v1', style: TextStyle(fontWeight: FontWeight.w600)),
            subtitle: const Text('Grading Scheme: NEB Standard 4.0 Scale (Rounding: HALF_UP) • 4 Mandatory Subjects'),
            trailing: Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: const Color(0xFFDCFCE7),
                borderRadius: BorderRadius.circular(4),
              ),
              child: const Text('PUBLISHED & SEALED', style: TextStyle(color: Color(0xFF166534), fontSize: 11, fontWeight: FontWeight.bold)),
            ),
          ),
          const Divider(),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: const [
                Text('Configured Subjects & Assessment Components:', style: TextStyle(fontWeight: FontWeight.w600, fontSize: 13)),
                SizedBox(height: 6),
                Text('• ENG-101: Compulsory English (Theory: 75 Marks, Practical: 25 Marks) - 4.0 Credits'),
                Text('• MTH-102: Compulsory Mathematics (Theory: 100 Marks) - 4.0 Credits'),
                Text('• SCI-103: Science and Technology (Theory: 75 Marks, Practical: 25 Marks) - 4.0 Credits'),
                Text('• SOC-104: Social Studies (Theory: 100 Marks) - 4.0 Credits'),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

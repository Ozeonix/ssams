import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_admin/core/theme/admin_theme.dart';

class ExamsScreen extends ConsumerStatefulWidget {
  const ExamsScreen({super.key});

  @override
  ConsumerState<ExamsScreen> createState() => _ExamsScreenState();
}

class _ExamsScreenState extends ConsumerState<ExamsScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
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
                      'Examinations & Result Engine',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        color: AdminTheme.secondaryColor,
                      ),
                    ),
                    SizedBox(height: 4),
                    Text(
                      'Component-level mark entry grid, calculation engine, snapshot approval, and publication.',
                      style: TextStyle(color: AdminTheme.textSecondary, fontSize: 13),
                    ),
                  ],
                ),
                ElevatedButton.icon(
                  onPressed: () {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Calculation Engine initiated for active exam.')),
                    );
                  },
                  icon: const Icon(Icons.calculate_rounded, size: 18),
                  label: const Text('Run GPA Calculations'),
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
                  Tab(text: 'Marks Entry Grid'),
                  Tab(text: 'Result Approval & Publication'),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Expanded(
              child: TabBarView(
                controller: _tabController,
                children: const [
                  _MarksEntryGridTab(),
                  _ResultApprovalTab(),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _MarksEntryGridTab extends StatelessWidget {
  const _MarksEntryGridTab();

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Chip(label: Text('Exam: First Term 2026', style: TextStyle(fontWeight: FontWeight.bold))),
                const SizedBox(width: 8),
                const Chip(label: Text('Class: Grade 10 - A')),
                const SizedBox(width: 8),
                const Chip(label: Text('Subject: Science (SCI-103)')),
                const Spacer(),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                  decoration: BoxDecoration(
                    color: const Color(0xFFFEF3C7),
                    borderRadius: BorderRadius.circular(6),
                  ),
                  child: const Text('STATUS: DRAFT (EDITABLE)', style: TextStyle(color: Color(0xFF92400E), fontSize: 11, fontWeight: FontWeight.bold)),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Expanded(
              child: SingleChildScrollView(
                child: SizedBox(
                  width: double.infinity,
                  child: DataTable(
                    columns: const [
                      DataColumn(label: Text('Roll No')),
                      DataColumn(label: Text('Student Name')),
                      DataColumn(label: Text('Theory (Max 75)')),
                      DataColumn(label: Text('Practical (Max 25)')),
                      DataColumn(label: Text('Total (100)')),
                      DataColumn(label: Text('Grade Point')),
                      DataColumn(label: Text('Remarks')),
                    ],
                    rows: const [
                      DataRow(cells: [
                        DataCell(Text('10-A-01', style: TextStyle(fontWeight: FontWeight.w600))),
                        DataCell(Text('Sunita Adhikari')),
                        DataCell(Text('68.0')),
                        DataCell(Text('24.0')),
                        DataCell(Text('92.0', style: TextStyle(fontWeight: FontWeight.bold))),
                        DataCell(Text('4.00 (A+)', style: TextStyle(color: AdminTheme.successColor, fontWeight: FontWeight.bold))),
                        DataCell(Text('Outstanding')),
                      ]),
                      DataRow(cells: [
                        DataCell(Text('10-A-02', style: TextStyle(fontWeight: FontWeight.w600))),
                        DataCell(Text('Bikash Thapa')),
                        DataCell(Text('61.0')),
                        DataCell(Text('22.0')),
                        DataCell(Text('83.0', style: TextStyle(fontWeight: FontWeight.bold))),
                        DataCell(Text('3.60 (A)', style: TextStyle(color: AdminTheme.successColor, fontWeight: FontWeight.bold))),
                        DataCell(Text('Excellent')),
                      ]),
                      DataRow(cells: [
                        DataCell(Text('10-A-03', style: TextStyle(fontWeight: FontWeight.w600))),
                        DataCell(Text('Prashant Khadka')),
                        DataCell(Text('54.0')),
                        DataCell(Text('21.0')),
                        DataCell(Text('75.0', style: TextStyle(fontWeight: FontWeight.bold))),
                        DataCell(Text('3.20 (B+)', style: TextStyle(color: AdminTheme.primaryColor, fontWeight: FontWeight.bold))),
                        DataCell(Text('Very Good')),
                      ]),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _ResultApprovalTab extends StatelessWidget {
  const _ResultApprovalTab();

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Result Approval & Official Publication Gate',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 6),
            const Text(
              'Review calculated results before sealing immutable snapshots and generating verifiable QR transcripts.',
              style: TextStyle(color: AdminTheme.textSecondary, fontSize: 13),
            ),
            const SizedBox(height: 20),
            Row(
              children: [
                _SummaryMetric(label: 'Total Candidates', value: '38'),
                const SizedBox(width: 16),
                _SummaryMetric(label: 'Passing Rate', value: '100%'),
                const SizedBox(width: 16),
                _SummaryMetric(label: 'Average GPA', value: '3.62'),
                const SizedBox(width: 16),
                _SummaryMetric(label: 'Calculation Errors', value: '0'),
              ],
            ),
            const SizedBox(height: 28),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFFF0FDF4),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: const Color(0xFFBBF7D0)),
              ),
              child: Row(
                children: [
                  const Icon(Icons.check_circle_outline, color: AdminTheme.successColor),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: const [
                        Text('Ready for Publication', style: TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF166534))),
                        Text('All components evaluated and verified against published curriculum rules.', style: TextStyle(fontSize: 12, color: Color(0xFF15803D))),
                      ],
                    ),
                  ),
                  ElevatedButton(
                    style: ElevatedButton.styleFrom(backgroundColor: AdminTheme.successColor),
                    onPressed: () {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('Result snapshot sealed & published successfully.')),
                      );
                    },
                    child: const Text('Approve & Publish Official Results'),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _SummaryMetric extends StatelessWidget {
  final String label;
  final String value;

  const _SummaryMetric({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: const Color(0xFFF8FAFC),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: const Color(0xFFE2E8F0)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: const TextStyle(fontSize: 11, color: AdminTheme.textSecondary)),
          const SizedBox(height: 4),
          Text(value, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: AdminTheme.secondaryColor)),
        ],
      ),
    );
  }
}

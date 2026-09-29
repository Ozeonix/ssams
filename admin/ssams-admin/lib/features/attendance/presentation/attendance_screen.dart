import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_admin/core/theme/admin_theme.dart';

class AttendanceScreen extends ConsumerWidget {
  const AttendanceScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
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
                      'Attendance Sessions & Verification',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        color: AdminTheme.secondaryColor,
                      ),
                    ),
                    SizedBox(height: 4),
                    Text(
                      'Daily class session logs, attendance percentage monitoring, and threshold alerts.',
                      style: TextStyle(color: AdminTheme.textSecondary, fontSize: 13),
                    ),
                  ],
                ),
                ElevatedButton.icon(
                  onPressed: () {},
                  icon: const Icon(Icons.file_download_outlined, size: 18),
                  label: const Text('Export Attendance Report'),
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Attendance Overview Cards
            Row(
              children: [
                Expanded(
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          const Icon(Icons.check_circle_outline, color: AdminTheme.successColor, size: 36),
                          const SizedBox(width: 14),
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: const [
                              Text('Average Attendance', style: TextStyle(color: AdminTheme.textSecondary, fontSize: 12)),
                              Text('94.8%', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          const Icon(Icons.warning_amber_rounded, color: AdminTheme.warningColor, size: 36),
                          const SizedBox(width: 14),
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: const [
                              Text('Low Attendance Alerts', style: TextStyle(color: AdminTheme.textSecondary, fontSize: 12)),
                              Text('1 Student (<75%)', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          const Icon(Icons.today_rounded, color: AdminTheme.primaryColor, size: 36),
                          const SizedBox(width: 14),
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: const [
                              Text('Today Sessions Logged', style: TextStyle(color: AdminTheme.textSecondary, fontSize: 12)),
                              Text('28 / 28', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),

            // Class Group Sessions Table
            Expanded(
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Recent Class Sessions',
                        style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 12),
                      Expanded(
                        child: SingleChildScrollView(
                          child: SizedBox(
                            width: double.infinity,
                            child: DataTable(
                              columns: const [
                                DataColumn(label: Text('Date')),
                                DataColumn(label: Text('Class Group')),
                                DataColumn(label: Text('Period')),
                                DataColumn(label: Text('Present / Total')),
                                DataColumn(label: Text('Percentage')),
                                DataColumn(label: Text('Recorded By')),
                                DataColumn(label: Text('Status')),
                              ],
                              rows: const [
                                DataRow(cells: [
                                  DataCell(Text('2026-09-30', style: TextStyle(fontWeight: FontWeight.w600))),
                                  DataCell(Text('Grade 10 - Section A')),
                                  DataCell(Text('Period 1 (Morning)')),
                                  DataCell(Text('37 / 38')),
                                  DataCell(Text('97.4%', style: TextStyle(color: AdminTheme.successColor, fontWeight: FontWeight.bold))),
                                  DataCell(Text('Deepak Joshi')),
                                  DataCell(Chip(label: Text('VERIFIED', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold)), backgroundColor: Color(0xFFDCFCE7))),
                                ]),
                                DataRow(cells: [
                                  DataCell(Text('2026-09-30', style: TextStyle(fontWeight: FontWeight.w600))),
                                  DataCell(Text('Grade 9 - Section A')),
                                  DataCell(Text('Period 1 (Morning)')),
                                  DataCell(Text('33 / 35')),
                                  DataCell(Text('94.3%', style: TextStyle(color: AdminTheme.successColor, fontWeight: FontWeight.bold))),
                                  DataCell(Text('Anita Gurung')),
                                  DataCell(Chip(label: Text('VERIFIED', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold)), backgroundColor: Color(0xFFDCFCE7))),
                                ]),
                              ],
                            ),
                          ),
                        ),
                      ),
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

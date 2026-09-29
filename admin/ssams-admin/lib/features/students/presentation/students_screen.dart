import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_admin/core/theme/admin_theme.dart';

class StudentsScreen extends ConsumerStatefulWidget {
  const StudentsScreen({super.key});

  @override
  ConsumerState<StudentsScreen> createState() => _StudentsScreenState();
}

class _StudentsScreenState extends ConsumerState<StudentsScreen> {
  final _searchController = TextEditingController();
  String _selectedStatus = 'ALL';

  final List<Map<String, dynamic>> _students = [
    {
      'admNo': 'PILOT-ADM-001',
      'name': 'Sunita Adhikari',
      'class': 'Grade 10 - A',
      'rollNo': '10-A-01',
      'gender': 'FEMALE',
      'phone': '+9779800000011',
      'status': 'ACTIVE',
    },
    {
      'admNo': 'PILOT-ADM-002',
      'name': 'Bikash Thapa',
      'class': 'Grade 10 - A',
      'rollNo': '10-A-02',
      'gender': 'MALE',
      'phone': '+9779800000012',
      'status': 'ACTIVE',
    },
    {
      'admNo': 'PILOT-ADM-003',
      'name': 'Prashant Khadka',
      'class': 'Grade 10 - A',
      'rollNo': '10-A-03',
      'gender': 'MALE',
      'phone': '+9779800000013',
      'status': 'ACTIVE',
    },
    {
      'admNo': 'LEG-2026-001',
      'name': 'Aarav Sharma',
      'class': 'Grade 10 - A',
      'rollNo': '10-A-04',
      'gender': 'MALE',
      'phone': '+9779800000021',
      'status': 'ACTIVE',
    },
    {
      'admNo': 'LEG-2026-002',
      'name': 'Priya Shrestha',
      'class': 'Grade 10 - A',
      'rollNo': '10-A-05',
      'gender': 'FEMALE',
      'phone': '+9779800000022',
      'status': 'ACTIVE',
    },
  ];

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  void _showAdmitStudentDialog() {
    final admController = TextEditingController();
    final firstController = TextEditingController();
    final lastController = TextEditingController();

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Register New Student'),
        content: SizedBox(
          width: 420,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: admController,
                decoration: const InputDecoration(labelText: 'Admission Number (Unique)'),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: firstController,
                decoration: const InputDecoration(labelText: 'First Name'),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: lastController,
                decoration: const InputDecoration(labelText: 'Last Name'),
              ),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
          ElevatedButton(
            onPressed: () {
              if (admController.text.isNotEmpty && firstController.text.isNotEmpty) {
                setState(() {
                  _students.add({
                    'admNo': admController.text.trim(),
                    'name': '${firstController.text.trim()} ${lastController.text.trim()}',
                    'class': 'Grade 10 - A',
                    'rollNo': '10-A-${_students.length + 1}',
                    'gender': 'NOT_SPECIFIED',
                    'phone': 'N/A',
                    'status': 'ACTIVE',
                  });
                });
                Navigator.pop(ctx);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Student registered successfully')),
                );
              }
            },
            child: const Text('Save & Admit'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final filtered = _students.where((s) {
      final query = _searchController.text.toLowerCase();
      final matchQuery = s['name'].toString().toLowerCase().contains(query) ||
          s['admNo'].toString().toLowerCase().contains(query);
      final matchStatus = _selectedStatus == 'ALL' || s['status'] == _selectedStatus;
      return matchQuery && matchStatus;
    }).toList();

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
                      'Student Roster & Enrollment',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        color: AdminTheme.secondaryColor,
                      ),
                    ),
                    SizedBox(height: 4),
                    Text(
                      'Directory of admitted students, cohort enrollments, and academic statuses.',
                      style: TextStyle(color: AdminTheme.textSecondary, fontSize: 13),
                    ),
                  ],
                ),
                Row(
                  children: [
                    OutlinedButton.icon(
                      onPressed: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('CSV Import Wizard ready via /api/v1/students/import')),
                        );
                      },
                      icon: const Icon(Icons.upload_file_rounded, size: 18),
                      label: const Text('Import CSV'),
                    ),
                    const SizedBox(width: 12),
                    ElevatedButton.icon(
                      onPressed: _showAdmitStudentDialog,
                      icon: const Icon(Icons.person_add_rounded, size: 18),
                      label: const Text('Admit Student'),
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Filter Bar
            Card(
              child: Padding(
                padding: const EdgeInsets.all(12),
                child: Row(
                  children: [
                    Expanded(
                      flex: 3,
                      child: TextField(
                        controller: _searchController,
                        onChanged: (_) => setState(() {}),
                        decoration: const InputDecoration(
                          hintText: 'Search by name or admission number...',
                          prefixIcon: Icon(Icons.search, size: 20),
                          isDense: true,
                        ),
                      ),
                    ),
                    const SizedBox(width: 16),
                    DropdownButton<String>(
                      value: _selectedStatus,
                      underline: const SizedBox(),
                      items: const [
                        DropdownMenuItem(value: 'ALL', child: Text('All Statuses')),
                        DropdownMenuItem(value: 'ACTIVE', child: Text('Active')),
                        DropdownMenuItem(value: 'TRANSFERRED', child: Text('Transferred')),
                        DropdownMenuItem(value: 'PROMOTED', child: Text('Promoted')),
                      ],
                      onChanged: (val) {
                        if (val != null) setState(() => _selectedStatus = val);
                      },
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Students Data Table
            Expanded(
              child: Card(
                child: SingleChildScrollView(
                  child: SizedBox(
                    width: double.infinity,
                    child: DataTable(
                      columns: const [
                        DataColumn(label: Text('Admission No')),
                        DataColumn(label: Text('Student Name')),
                        DataColumn(label: Text('Class & Section')),
                        DataColumn(label: Text('Roll No')),
                        DataColumn(label: Text('Gender')),
                        DataColumn(label: Text('Contact Phone')),
                        DataColumn(label: Text('Status')),
                      ],
                      rows: filtered.map((s) {
                        return DataRow(cells: [
                          DataCell(Text(s['admNo'], style: const TextStyle(fontWeight: FontWeight.w600))),
                          DataCell(Text(s['name'])),
                          DataCell(Text(s['class'])),
                          DataCell(Text(s['rollNo'])),
                          DataCell(Text(s['gender'])),
                          DataCell(Text(s['phone'])),
                          DataCell(
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                              decoration: BoxDecoration(
                                color: const Color(0xFFDCFCE7),
                                borderRadius: BorderRadius.circular(4),
                              ),
                              child: Text(
                                s['status'],
                                style: const TextStyle(
                                  color: Color(0xFF166534),
                                  fontSize: 11,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                          ),
                        ]);
                      }).toList(),
                    ),
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

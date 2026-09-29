import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:ssams_admin/core/theme/admin_theme.dart';

class DashboardScreen extends ConsumerWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      body: SingleChildScrollView(
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
                      'Institutional Dashboard',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        color: AdminTheme.secondaryColor,
                      ),
                    ),
                    SizedBox(height: 4),
                    Text(
                      'Real-time academic performance, attendance, and evaluation status.',
                      style: TextStyle(color: AdminTheme.textSecondary, fontSize: 13),
                    ),
                  ],
                ),
                ElevatedButton.icon(
                  onPressed: () => context.go('/students'),
                  icon: const Icon(Icons.person_add_rounded, size: 18),
                  label: const Text('Admit Student'),
                ),
              ],
            ),
            const SizedBox(height: 24),

            // Metric KPI Cards Grid
            LayoutBuilder(
              builder: (context, constraints) {
                final isWide = constraints.maxWidth > 800;
                final crossAxisCount = isWide ? 4 : 2;

                return GridView.count(
                  crossAxisCount: crossAxisCount,
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  crossAxisSpacing: 16,
                  mainAxisSpacing: 16,
                  childAspectRatio: isWide ? 1.6 : 1.3,
                  children: const [
                    _KpiCard(
                      title: 'Active Students',
                      value: '1,248',
                      subtitle: 'Enrolled in 2026-2027',
                      icon: Icons.school_rounded,
                      color: Color(0xFF2563EB),
                    ),
                    _KpiCard(
                      title: "Today's Attendance",
                      value: '94.2%',
                      subtitle: '28 sessions logged',
                      icon: Icons.how_to_reg_rounded,
                      color: Color(0xFF16A34A),
                    ),
                    _KpiCard(
                      title: 'Pending Mark Approvals',
                      value: '4',
                      subtitle: 'Exam components awaiting lock',
                      icon: Icons.pending_actions_rounded,
                      color: Color(0xFFD97706),
                    ),
                    _KpiCard(
                      title: 'Published Results',
                      value: '100%',
                      subtitle: 'First Term finalized',
                      icon: Icons.verified_rounded,
                      color: Color(0xFF0D9488),
                    ),
                  ],
                );
              },
            ),
            const SizedBox(height: 28),

            // Operational Action Cards
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  flex: 3,
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(20),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: const [
                              Text(
                                'Academic Operations Quick Launch',
                                style: TextStyle(
                                  fontSize: 16,
                                  fontWeight: FontWeight.bold,
                                  color: AdminTheme.secondaryColor,
                                ),
                              ),
                              Icon(Icons.bolt_rounded, color: AdminTheme.primaryColor),
                            ],
                          ),
                          const Divider(height: 24),
                          ListTile(
                            contentPadding: EdgeInsets.zero,
                            leading: Container(
                              padding: const EdgeInsets.all(8),
                              decoration: BoxDecoration(
                                color: const Color(0xFFEFF6FF),
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: const Icon(Icons.assignment_turned_in_rounded, color: Color(0xFF2563EB)),
                            ),
                            title: const Text('Exam Result Verification & Approval', style: TextStyle(fontWeight: FontWeight.w600)),
                            subtitle: const Text('Review calculated term grade sheets and trigger official publication snapshots.'),
                            trailing: const Icon(Icons.chevron_right_rounded),
                            onTap: () => context.go('/exams'),
                          ),
                          const Divider(),
                          ListTile(
                            contentPadding: EdgeInsets.zero,
                            leading: Container(
                              padding: const EdgeInsets.all(8),
                              decoration: BoxDecoration(
                                color: const Color(0xFFF0FDF4),
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: const Icon(Icons.fact_check_rounded, color: Color(0xFF16A34A)),
                            ),
                            title: const Text('Class Attendance Session Tracking', style: TextStyle(fontWeight: FontWeight.w600)),
                            subtitle: const Text('Monitor daily attendance sessions, absence warnings, and teacher submissions.'),
                            trailing: const Icon(Icons.chevron_right_rounded),
                            onTap: () => context.go('/attendance'),
                          ),
                          const Divider(),
                          ListTile(
                            contentPadding: EdgeInsets.zero,
                            leading: Container(
                              padding: const EdgeInsets.all(8),
                              decoration: BoxDecoration(
                                color: const Color(0xFFFDF4FF),
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: const Icon(Icons.account_tree_rounded, color: Color(0xFF9333EA)),
                            ),
                            title: const Text('Curriculum & Academic Structure', style: TextStyle(fontWeight: FontWeight.w600)),
                            subtitle: const Text('Configure academic years, subject components, credit hours, and grade schemes.'),
                            trailing: const Icon(Icons.chevron_right_rounded),
                            onTap: () => context.go('/academic'),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  flex: 2,
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(20),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: const [
                              Text(
                                'Platform Health & Alerts',
                                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                              ),
                              Icon(Icons.shield_outlined, color: AdminTheme.successColor),
                            ],
                          ),
                          const Divider(height: 24),
                          _HealthItem(
                            label: 'PostgreSQL Database',
                            status: 'ONLINE',
                            detail: 'Primary replica healthy, latency 1.8ms',
                            isHealthy: true,
                          ),
                          const SizedBox(height: 12),
                          _HealthItem(
                            label: 'Redis Cache & Outbox',
                            status: 'ACTIVE',
                            detail: 'Worker polling 1000ms, queue depth: 0',
                            isHealthy: true,
                          ),
                          const SizedBox(height: 12),
                          _HealthItem(
                            label: 'Multi-Tenant Isolation',
                            status: 'ENFORCED',
                            detail: 'Tenant context barrier verified',
                            isHealthy: true,
                          ),
                          const SizedBox(height: 12),
                          _HealthItem(
                            label: 'Audit Trail Service',
                            status: 'RECORDING',
                            detail: 'All privileged operations logged',
                            isHealthy: true,
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _KpiCard extends StatelessWidget {
  final String title;
  final String value;
  final String subtitle;
  final IconData icon;
  final Color color;

  const _KpiCard({
    required this.title,
    required this.value,
    required this.subtitle,
    required this.icon,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(18),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  title,
                  style: const TextStyle(
                    fontSize: 13,
                    color: AdminTheme.textSecondary,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                Container(
                  padding: const EdgeInsets.all(6),
                  decoration: BoxDecoration(
                    color: color.withValues(alpha: 0.1),
                    borderRadius: BorderRadius.circular(6),
                  ),
                  child: Icon(icon, size: 18, color: color),
                ),
              ],
            ),
            Text(
              value,
              style: const TextStyle(
                fontSize: 26,
                fontWeight: FontWeight.bold,
                color: AdminTheme.secondaryColor,
              ),
            ),
            Text(
              subtitle,
              style: const TextStyle(fontSize: 11, color: AdminTheme.textSecondary),
            ),
          ],
        ),
      ),
    );
  }
}

class _HealthItem extends StatelessWidget {
  final String label;
  final String status;
  final String detail;
  final bool isHealthy;

  const _HealthItem({
    required this.label,
    required this.status,
    required this.detail,
    required this.isHealthy,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(10),
      decoration: BoxDecoration(
        color: const Color(0xFFF8FAFC),
        borderRadius: BorderRadius.circular(6),
        border: Border.all(color: const Color(0xFFE2E8F0)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(label, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: isHealthy ? const Color(0xFFDCFCE7) : const Color(0xFFFEE2E2),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  status,
                  style: TextStyle(
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                    color: isHealthy ? const Color(0xFF166534) : const Color(0xFF991B1B),
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 2),
          Text(detail, style: const TextStyle(fontSize: 11, color: AdminTheme.textSecondary)),
        ],
      ),
    );
  }
}

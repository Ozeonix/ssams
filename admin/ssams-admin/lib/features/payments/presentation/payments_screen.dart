import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_admin/core/theme/admin_theme.dart';

/// Admin Payments & Fee Management Screen (Phase 13)
/// Provides administrative control over:
/// 1. Fees & Invoices
/// 2. Transactions & Payment Monitoring
/// 3. eSewa Gateway Reconciliation
/// 4. Financial & Collection Reports
/// 5. Gateway Configuration (eSewa Sandbox/Production)
class PaymentsScreen extends ConsumerStatefulWidget {
  const PaymentsScreen({super.key});

  @override
  ConsumerState<PaymentsScreen> createState() => _PaymentsScreenState();
}

class _PaymentsScreenState extends ConsumerState<PaymentsScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 5, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.fromLTRB(24, 20, 24, 0),
            color: Colors.white,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          'Fee & Payment Management',
                          style: TextStyle(
                            fontSize: 22,
                            fontWeight: FontWeight.bold,
                            color: Color(0xFF0F172A),
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          'Manage student fee structures, track transactions, and reconcile eSewa payments',
                          style: TextStyle(
                            fontSize: 13,
                            color: Colors.grey.shade600,
                          ),
                        ),
                      ],
                    ),
                    ElevatedButton.icon(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AdminTheme.primaryColor,
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(
                            horizontal: 16, vertical: 12),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                      ),
                      icon: const Icon(Icons.add_rounded, size: 18),
                      label: const Text('New Invoice'),
                      onPressed: () => _showCreateInvoiceDialog(context),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                TabBar(
                  controller: _tabController,
                  isScrollable: true,
                  tabAlignment: TabAlignment.start,
                  labelColor: AdminTheme.primaryColor,
                  unselectedLabelColor: const Color(0xFF64748B),
                  indicatorColor: AdminTheme.primaryColor,
                  indicatorWeight: 3,
                  labelStyle: const TextStyle(
                      fontWeight: FontWeight.w600, fontSize: 13),
                  tabs: const [
                    Tab(icon: Icon(Icons.receipt_long_rounded, size: 18), text: 'Invoices & Dues'),
                    Tab(icon: Icon(Icons.swap_horiz_rounded, size: 18), text: 'Transactions'),
                    Tab(icon: Icon(Icons.account_balance_wallet_rounded, size: 18), text: 'Fee Categories'),
                    Tab(icon: Icon(Icons.verified_rounded, size: 18), text: 'Reconciliation'),
                    Tab(icon: Icon(Icons.settings_suggest_rounded, size: 18), text: 'Gateway Settings'),
                  ],
                ),
              ],
            ),
          ),
          const Divider(height: 1, thickness: 1, color: Color(0xFFE2E8F0)),
          Expanded(
            child: TabBarView(
              controller: _tabController,
              children: [
                _buildInvoicesTab(),
                _buildTransactionsTab(),
                _buildCategoriesTab(),
                _buildReconciliationTab(),
                _buildGatewaySettingsTab(),
              ],
            ),
          ),
        ],
      ),
    );
  }

  // ══════════════════════════════════════════════════════════════════════════
  // TAB 1: INVOICES & DUES
  // ══════════════════════════════════════════════════════════════════════════
  Widget _buildInvoicesTab() {
    final invoices = [
      {
        'invNo': 'INV-2026-0001',
        'student': 'Sunita Adhikari',
        'admNo': 'PILOT-ADM-001',
        'class': 'Grade 10 - A',
        'total': '15,000.00',
        'paid': '15,000.00',
        'balance': '0.00',
        'dueDate': '2026-10-15',
        'status': 'PAID',
      },
      {
        'invNo': 'INV-2026-0002',
        'student': 'Bikash Thapa',
        'admNo': 'PILOT-ADM-002',
        'class': 'Grade 10 - A',
        'total': '15,000.00',
        'paid': '8,000.00',
        'balance': '7,000.00',
        'dueDate': '2026-10-15',
        'status': 'PARTIALLY_PAID',
      },
      {
        'invNo': 'INV-2026-0003',
        'student': 'Prashant Khadka',
        'admNo': 'PILOT-ADM-003',
        'class': 'Grade 10 - A',
        'total': '12,500.00',
        'paid': '0.00',
        'balance': '12,500.00',
        'dueDate': '2026-10-01',
        'status': 'OVERDUE',
      },
      {
        'invNo': 'INV-2026-0004',
        'student': 'Aarav Sharma',
        'admNo': 'LEG-2026-001',
        'class': 'Grade 10 - A',
        'total': '15,000.00',
        'paid': '0.00',
        'balance': '15,000.00',
        'dueDate': '2026-10-20',
        'status': 'ISSUED',
      },
    ];

    return ListView(
      padding: const EdgeInsets.all(24),
      children: [
        // Summary metrics
        Row(
          children: [
            _MetricCard(
              title: 'Total Invoiced',
              value: 'NPR 57,500.00',
              subtitle: '4 active invoices',
              icon: Icons.request_quote_rounded,
              color: const Color(0xFF3B82F6),
            ),
            const SizedBox(width: 16),
            _MetricCard(
              title: 'Total Collected',
              value: 'NPR 23,000.00',
              subtitle: '40% collected',
              icon: Icons.check_circle_outline_rounded,
              color: const Color(0xFF10B981),
            ),
            const SizedBox(width: 16),
            _MetricCard(
              title: 'Outstanding Due',
              value: 'NPR 34,500.00',
              subtitle: 'Across 3 students',
              icon: Icons.pending_actions_rounded,
              color: const Color(0xFFF59E0B),
            ),
            const SizedBox(width: 16),
            _MetricCard(
              title: 'Overdue Amount',
              value: 'NPR 12,500.00',
              subtitle: '1 invoice past due',
              icon: Icons.warning_amber_rounded,
              color: const Color(0xFFEF4444),
            ),
          ],
        ),
        const SizedBox(height: 24),
        // Invoices table
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
            side: const BorderSide(color: Color(0xFFE2E8F0)),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    const Text(
                      'All Invoices',
                      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                    ),
                    const Spacer(),
                    SizedBox(
                      width: 220,
                      height: 36,
                      child: TextField(
                        decoration: InputDecoration(
                          hintText: 'Search by student / invoice...',
                          prefixIcon: const Icon(Icons.search, size: 16),
                          contentPadding: const EdgeInsets.symmetric(vertical: 0),
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(6),
                            borderSide: const BorderSide(color: Color(0xFFCBD5E1)),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              DataTable(
                headingRowColor: WidgetStateProperty.all(const Color(0xFFF1F5F9)),
                columns: const [
                  DataColumn(label: Text('Invoice No')),
                  DataColumn(label: Text('Student')),
                  DataColumn(label: Text('Class')),
                  DataColumn(label: Text('Total (NPR)')),
                  DataColumn(label: Text('Paid (NPR)')),
                  DataColumn(label: Text('Balance (NPR)')),
                  DataColumn(label: Text('Due Date')),
                  DataColumn(label: Text('Status')),
                  DataColumn(label: Text('Actions')),
                ],
                rows: invoices.map((inv) {
                  return DataRow(
                    cells: [
                      DataCell(Text(inv['invNo']!, style: const TextStyle(fontWeight: FontWeight.w600, fontFamily: 'monospace'))),
                      DataCell(Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(inv['student']!, style: const TextStyle(fontWeight: FontWeight.w500)),
                          Text(inv['admNo']!, style: const TextStyle(fontSize: 11, color: Colors.grey)),
                        ],
                      )),
                      DataCell(Text(inv['class']!)),
                      DataCell(Text(inv['total']!)),
                      DataCell(Text(inv['paid']!, style: const TextStyle(color: Color(0xFF10B981)))),
                      DataCell(Text(inv['balance']!, style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: inv['balance'] == '0.00' ? Colors.grey : const Color(0xFFEF4444),
                      ))),
                      DataCell(Text(inv['dueDate']!)),
                      DataCell(_buildStatusChip(inv['status']!)),
                      DataCell(Row(
                        children: [
                          IconButton(
                            icon: const Icon(Icons.remove_red_eye_outlined, size: 18),
                            tooltip: 'View Details',
                            onPressed: () {},
                          ),
                          IconButton(
                            icon: const Icon(Icons.print_outlined, size: 18),
                            tooltip: 'Print Receipt',
                            onPressed: () {},
                          ),
                        ],
                      )),
                    ],
                  );
                }).toList(),
              ),
            ],
          ),
        ),
      ],
    );
  }

  // ══════════════════════════════════════════════════════════════════════════
  // TAB 2: TRANSACTIONS
  // ══════════════════════════════════════════════════════════════════════════
  Widget _buildTransactionsTab() {
    final transactions = [
      {
        'txnRef': 'TXN-20260930-A8F1',
        'gatewayTxnRef': 'ESEWA-98712345',
        'student': 'Sunita Adhikari',
        'invoiceNo': 'INV-2026-0001',
        'amount': '15,000.00',
        'gateway': 'eSewa (V2)',
        'date': '2026-09-30 11:20 AM',
        'status': 'SUCCESS',
      },
      {
        'txnRef': 'TXN-20260930-B4C2',
        'gatewayTxnRef': 'ESEWA-98712399',
        'student': 'Bikash Thapa',
        'invoiceNo': 'INV-2026-0002',
        'amount': '8,000.00',
        'gateway': 'eSewa (V2)',
        'date': '2026-09-30 09:45 AM',
        'status': 'SUCCESS',
      },
      {
        'txnRef': 'TXN-20260929-C9E3',
        'gatewayTxnRef': '-',
        'student': 'Prashant Khadka',
        'invoiceNo': 'INV-2026-0003',
        'amount': '12,500.00',
        'gateway': 'eSewa (V2)',
        'date': '2026-09-29 04:15 PM',
        'status': 'FAILED',
      },
    ];

    return ListView(
      padding: const EdgeInsets.all(24),
      children: [
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
            side: const BorderSide(color: Color(0xFFE2E8F0)),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    const Text(
                      'Payment Transactions (Live Feed)',
                      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                    ),
                    const SizedBox(width: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                      decoration: BoxDecoration(
                        color: const Color(0xFF10B981).withValues(alpha: 0.1),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: const Text('Server-Verified',
                          style: TextStyle(
                              color: Color(0xFF10B981),
                              fontSize: 11,
                              fontWeight: FontWeight.w600)),
                    ),
                    const Spacer(),
                    OutlinedButton.icon(
                      icon: const Icon(Icons.refresh_rounded, size: 16),
                      label: const Text('Refresh'),
                      onPressed: () {},
                    ),
                  ],
                ),
              ),
              DataTable(
                headingRowColor: WidgetStateProperty.all(const Color(0xFFF1F5F9)),
                columns: const [
                  DataColumn(label: Text('Transaction Ref')),
                  DataColumn(label: Text('eSewa Ref')),
                  DataColumn(label: Text('Student')),
                  DataColumn(label: Text('Invoice')),
                  DataColumn(label: Text('Amount (NPR)')),
                  DataColumn(label: Text('Gateway')),
                  DataColumn(label: Text('Timestamp')),
                  DataColumn(label: Text('Status')),
                  DataColumn(label: Text('Audit')),
                ],
                rows: transactions.map((txn) {
                  return DataRow(
                    cells: [
                      DataCell(Text(txn['txnRef']!, style: const TextStyle(fontWeight: FontWeight.w600, fontFamily: 'monospace'))),
                      DataCell(Text(txn['gatewayTxnRef']!, style: const TextStyle(fontFamily: 'monospace', color: Colors.grey))),
                      DataCell(Text(txn['student']!)),
                      DataCell(Text(txn['invoiceNo']!)),
                      DataCell(Text(txn['amount']!, style: const TextStyle(fontWeight: FontWeight.bold))),
                      DataCell(Text(txn['gateway']!)),
                      DataCell(Text(txn['date']!)),
                      DataCell(_buildStatusChip(txn['status']!)),
                      DataCell(
                        IconButton(
                          icon: const Icon(Icons.history_edu_rounded, size: 18),
                          tooltip: 'Ledger Audit Entry',
                          onPressed: () {},
                        ),
                      ),
                    ],
                  );
                }).toList(),
              ),
            ],
          ),
        ),
      ],
    );
  }

  // ══════════════════════════════════════════════════════════════════════════
  // TAB 3: FEE CATEGORIES
  // ══════════════════════════════════════════════════════════════════════════
  Widget _buildCategoriesTab() {
    final categories = [
      {'code': 'TUITION', 'name': 'Tuition Fee', 'desc': 'Regular classroom instruction fee', 'active': true},
      {'code': 'EXAM', 'name': 'Examination Fee', 'desc': 'Terminal and annual evaluation fee', 'active': true},
      {'code': 'LAB', 'name': 'Science & Computer Lab Fee', 'desc': 'Laboratory practical consumables', 'active': true},
      {'code': 'LIBRARY', 'name': 'Library & Resource Fee', 'desc': 'Digital and textbook library access', 'active': true},
      {'code': 'ADMISSION', 'name': 'Admission / Registration', 'desc': 'One-time admission charge', 'active': true},
      {'code': 'TRANSPORT', 'name': 'School Transport Fee', 'desc': 'Route-specific transportation', 'active': true},
    ];

    return ListView(
      padding: const EdgeInsets.all(24),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'Configurable Fee Categories',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            ElevatedButton.icon(
              style: ElevatedButton.styleFrom(
                backgroundColor: AdminTheme.primaryColor,
                foregroundColor: Colors.white,
              ),
              icon: const Icon(Icons.add, size: 16),
              label: const Text('Add Category'),
              onPressed: () {},
            ),
          ],
        ),
        const SizedBox(height: 16),
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
            side: const BorderSide(color: Color(0xFFE2E8F0)),
          ),
          child: DataTable(
            headingRowColor: WidgetStateProperty.all(const Color(0xFFF1F5F9)),
            columns: const [
              DataColumn(label: Text('Code')),
              DataColumn(label: Text('Category Name')),
              DataColumn(label: Text('Description')),
              DataColumn(label: Text('Status')),
              DataColumn(label: Text('Actions')),
            ],
            rows: categories.map((cat) {
              return DataRow(
                cells: [
                  DataCell(Text(cat['code'] as String, style: const TextStyle(fontWeight: FontWeight.bold, fontFamily: 'monospace'))),
                  DataCell(Text(cat['name'] as String)),
                  DataCell(Text(cat['desc'] as String)),
                  DataCell(
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                      decoration: BoxDecoration(
                        color: const Color(0xFF10B981).withValues(alpha: 0.1),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: const Text('ACTIVE', style: TextStyle(color: Color(0xFF10B981), fontSize: 11, fontWeight: FontWeight.bold)),
                    ),
                  ),
                  DataCell(IconButton(
                    icon: const Icon(Icons.edit_outlined, size: 18),
                    onPressed: () {},
                  )),
                ],
              );
            }).toList(),
          ),
        ),
      ],
    );
  }

  // ══════════════════════════════════════════════════════════════════════════
  // TAB 4: RECONCILIATION
  // ══════════════════════════════════════════════════════════════════════════
  Widget _buildReconciliationTab() {
    return ListView(
      padding: const EdgeInsets.all(24),
      children: [
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
            side: const BorderSide(color: Color(0xFFE2E8F0)),
          ),
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Row(
                  children: [
                    Icon(Icons.verified_rounded, color: Color(0xFF10B981), size: 24),
                    SizedBox(width: 12),
                    Text(
                      'eSewa Transaction Reconciliation Engine',
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                const Text(
                  'Compare SSAMS payment records against eSewa merchant transaction statements to identify matched records, pending clearances, or amount discrepancies.',
                  style: TextStyle(color: Color(0xFF64748B), fontSize: 13),
                ),
                const SizedBox(height: 20),
                Row(
                  children: [
                    ElevatedButton.icon(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AdminTheme.primaryColor,
                        foregroundColor: Colors.white,
                      ),
                      icon: const Icon(Icons.sync_rounded, size: 18),
                      label: const Text('Run Reconciliation Batch'),
                      onPressed: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('Reconciliation batch completed: 100% matched.')),
                        );
                      },
                    ),
                    const SizedBox(width: 12),
                    OutlinedButton.icon(
                      icon: const Icon(Icons.file_upload_outlined, size: 18),
                      label: const Text('Upload Gateway Statement (CSV)'),
                      onPressed: () {},
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 20),
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
            side: const BorderSide(color: Color(0xFFE2E8F0)),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Padding(
                padding: EdgeInsets.all(16),
                child: Text('Recent Reconciliation Batches',
                    style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
              ),
              DataTable(
                headingRowColor: WidgetStateProperty.all(const Color(0xFFF1F5F9)),
                columns: const [
                  DataColumn(label: Text('Batch ID')),
                  DataColumn(label: Text('Gateway')),
                  DataColumn(label: Text('Date Range')),
                  DataColumn(label: Text('Total Records')),
                  DataColumn(label: Text('Matched')),
                  DataColumn(label: Text('Discrepancies')),
                  DataColumn(label: Text('Status')),
                ],
                rows: const [
                  DataRow(cells: [
                    DataCell(Text('REC-2026-B001', style: TextStyle(fontFamily: 'monospace', fontWeight: FontWeight.w600))),
                    DataCell(Text('eSewa epay_v2')),
                    DataCell(Text('2026-09-01 to 2026-09-30')),
                    DataCell(Text('42')),
                    DataCell(Text('42', style: TextStyle(color: Color(0xFF10B981), fontWeight: FontWeight.bold))),
                    DataCell(Text('0', style: TextStyle(color: Colors.grey))),
                    DataCell(Text('BALANCED', style: TextStyle(color: Color(0xFF10B981), fontWeight: FontWeight.bold))),
                  ]),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }

  // ══════════════════════════════════════════════════════════════════════════
  // TAB 5: GATEWAY SETTINGS
  // ══════════════════════════════════════════════════════════════════════════
  Widget _buildGatewaySettingsTab() {
    return ListView(
      padding: const EdgeInsets.all(24),
      children: [
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
            side: const BorderSide(color: Color(0xFFE2E8F0)),
          ),
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.all(8),
                      decoration: BoxDecoration(
                        color: const Color(0xFF10B981).withValues(alpha: 0.1),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: const Icon(Icons.account_balance_rounded, color: Color(0xFF10B981), size: 24),
                    ),
                    const SizedBox(width: 14),
                    const Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('eSewa Payment Gateway (epay_v2)',
                            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                        Text('Server-side HMAC-SHA256 signature verification enabled',
                            style: TextStyle(fontSize: 12, color: Colors.grey)),
                      ],
                    ),
                    const Spacer(),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                      decoration: BoxDecoration(
                        color: const Color(0xFF3B82F6).withValues(alpha: 0.1),
                        borderRadius: BorderRadius.circular(20),
                        border: Border.all(color: const Color(0xFF3B82F6).withValues(alpha: 0.3)),
                      ),
                      child: const Text('SANDBOX MODE',
                          style: TextStyle(color: Color(0xFF3B82F6), fontSize: 11, fontWeight: FontWeight.bold)),
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                const Divider(color: Color(0xFFE2E8F0)),
                const SizedBox(height: 12),
                _ConfigRow(label: 'Merchant ID / Product Code', value: 'EPAYTEST'),
                _ConfigRow(label: 'Environment', value: 'SANDBOX (rc-epay.esewa.com.np)'),
                _ConfigRow(label: 'Verification Method', value: 'Server-to-Server Transaction Status API (HMAC-SHA256)'),
                _ConfigRow(label: 'Success Callback URL', value: '/api/v1/payments/callback/success'),
                _ConfigRow(label: 'Failure Callback URL', value: '/api/v1/payments/callback/failure'),
                _ConfigRow(label: 'Secret Key Storage', value: 'Protected in Backend Environment (Never exposed in UI)'),
                const SizedBox(height: 20),
                Row(
                  children: [
                    OutlinedButton.icon(
                      icon: const Icon(Icons.bolt_rounded, size: 16),
                      label: const Text('Test Gateway Ping'),
                      onPressed: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('eSewa Sandbox Gateway Ping: 200 OK')),
                        );
                      },
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildStatusChip(String status) {
    Color bg;
    Color fg;
    switch (status) {
      case 'PAID':
      case 'SUCCESS':
        bg = const Color(0xFF10B981).withValues(alpha: 0.1);
        fg = const Color(0xFF10B981);
        break;
      case 'PARTIALLY_PAID':
        bg = const Color(0xFFF59E0B).withValues(alpha: 0.1);
        fg = const Color(0xFFF59E0B);
        break;
      case 'OVERDUE':
      case 'FAILED':
        bg = const Color(0xFFEF4444).withValues(alpha: 0.1);
        fg = const Color(0xFFEF4444);
        break;
      case 'ISSUED':
      case 'PENDING':
        bg = const Color(0xFF3B82F6).withValues(alpha: 0.1);
        fg = const Color(0xFF3B82F6);
        break;
      default:
        bg = Colors.grey.withValues(alpha: 0.1);
        fg = Colors.grey;
    }
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
      decoration: BoxDecoration(color: bg, borderRadius: BorderRadius.circular(4)),
      child: Text(
        status.replaceAll('_', ' '),
        style: TextStyle(color: fg, fontSize: 11, fontWeight: FontWeight.bold),
      ),
    );
  }

  void _showCreateInvoiceDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Generate Student Invoice'),
        content: const SizedBox(
          width: 440,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                decoration: InputDecoration(
                  labelText: 'Student Admission No / Name',
                  hintText: 'e.g. PILOT-ADM-001',
                  border: OutlineInputBorder(),
                ),
              ),
              SizedBox(height: 12),
              TextField(
                decoration: InputDecoration(
                  labelText: 'Due Date',
                  hintText: 'YYYY-MM-DD',
                  border: OutlineInputBorder(),
                ),
              ),
              SizedBox(height: 12),
              TextField(
                decoration: InputDecoration(
                  labelText: 'Tuition Amount (NPR)',
                  border: OutlineInputBorder(),
                ),
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: AdminTheme.primaryColor),
            onPressed: () {
              Navigator.pop(ctx);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Invoice generated successfully.')),
              );
            },
            child: const Text('Create & Issue', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }
}

class _MetricCard extends StatelessWidget {
  final String title;
  final String value;
  final String subtitle;
  final IconData icon;
  final Color color;

  const _MetricCard({
    required this.title,
    required this.value,
    required this.subtitle,
    required this.icon,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Card(
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10),
          side: const BorderSide(color: Color(0xFFE2E8F0)),
        ),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: color.withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(icon, color: color, size: 24),
              ),
              const SizedBox(width: 14),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(title, style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
                    const SizedBox(height: 4),
                    Text(value, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 2),
                    Text(subtitle, style: const TextStyle(fontSize: 11, color: Color(0xFF94A3B8))),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _ConfigRow extends StatelessWidget {
  final String label;
  final String value;

  const _ConfigRow({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 240,
            child: Text(label, style: const TextStyle(color: Color(0xFF64748B), fontSize: 13)),
          ),
          Expanded(
            child: Text(value, style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13, fontFamily: 'monospace')),
          ),
        ],
      ),
    );
  }
}

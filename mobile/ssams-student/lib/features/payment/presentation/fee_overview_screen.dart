import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_student/features/payment/data/payment_repository.dart';
import 'package:ssams_student/features/payment/domain/payment_models.dart';
import 'package:intl/intl.dart';

/// Fee Summary Screen – shows outstanding invoices for the logged-in student.
/// This is the entry point from the home/nav menu under "Fees".
class FeeOverviewScreen extends ConsumerWidget {
  const FeeOverviewScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final invoicesAsync = ref.watch(outstandingInvoicesProvider);

    return Scaffold(
      backgroundColor: const Color(0xFF0F172A),
      appBar: AppBar(
        title: const Text('My Fees',
            style: TextStyle(color: Colors.white, fontWeight: FontWeight.w600)),
        backgroundColor: const Color(0xFF1E293B),
        iconTheme: const IconThemeData(color: Colors.white),
        actions: [
          IconButton(
            icon: const Icon(Icons.history, color: Colors.white70),
            tooltip: 'Payment History',
            onPressed: () => Navigator.pushNamed(context, '/payment/history'),
          ),
        ],
      ),
      body: invoicesAsync.when(
        loading: () => const Center(
          child: CircularProgressIndicator(color: Color(0xFF6366F1))),
        error: (e, _) => _ErrorState(
          message: 'Failed to load invoices',
          onRetry: () => ref.invalidate(outstandingInvoicesProvider)),
        data: (invoices) {
          if (invoices.isEmpty) {
            return const _EmptyState(
              icon: Icons.check_circle_outline,
              title: 'All Clear!',
              subtitle: 'You have no outstanding fee invoices.',
            );
          }
          return RefreshIndicator(
            color: const Color(0xFF6366F1),
            onRefresh: () async => ref.invalidate(outstandingInvoicesProvider),
            child: ListView.separated(
              padding: const EdgeInsets.all(16),
              itemCount: invoices.length,
              separatorBuilder: (_, __) => const SizedBox(height: 12),
              itemBuilder: (ctx, i) => _InvoiceCard(invoice: invoices[i]),
            ),
          );
        },
      ),
    );
  }
}

class _InvoiceCard extends StatelessWidget {
  final FeeInvoice invoice;
  const _InvoiceCard({required this.invoice});

  @override
  Widget build(BuildContext context) {
    final fmt = NumberFormat('#,##0.00', 'en');
    final statusColor = _statusColor(invoice.status);

    return GestureDetector(
      onTap: () => Navigator.pushNamed(context, '/payment/invoice',
          arguments: invoice.id),
      child: Container(
        decoration: BoxDecoration(
          color: const Color(0xFF1E293B),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: statusColor.withValues(alpha: 0.3), width: 1),
        ),
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header row
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(invoice.invoiceNumber,
                    style: const TextStyle(
                        color: Colors.white70,
                        fontSize: 12,
                        fontFamily: 'monospace')),
                _StatusBadge(status: invoice.status),
              ],
            ),
            const SizedBox(height: 12),
            // Amount
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  const Text('Total', style: TextStyle(color: Colors.white38, fontSize: 11)),
                  Text('NPR ${fmt.format(invoice.totalAmount)}',
                      style: const TextStyle(
                          color: Colors.white,
                          fontSize: 18,
                          fontWeight: FontWeight.bold)),
                ]),
                Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
                  const Text('Balance Due', style: TextStyle(color: Colors.white38, fontSize: 11)),
                  Text('NPR ${fmt.format(invoice.balance)}',
                      style: TextStyle(
                          color: invoice.balance > 0 ? const Color(0xFFF87171) : const Color(0xFF4ADE80),
                          fontSize: 16,
                          fontWeight: FontWeight.w600)),
                ]),
              ],
            ),
            if (invoice.dueDate != null) ...[
              const SizedBox(height: 8),
              Text('Due: ${invoice.dueDate}',
                  style: const TextStyle(color: Colors.white38, fontSize: 11)),
            ],
            if (invoice.isPayable) ...[
              const SizedBox(height: 12),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF6366F1),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10)),
                    padding: const EdgeInsets.symmetric(vertical: 12),
                  ),
                  onPressed: () => Navigator.pushNamed(
                      context, '/payment/invoice',
                      arguments: invoice.id),
                  child: const Text('Pay Now',
                      style: TextStyle(
                          color: Colors.white, fontWeight: FontWeight.w600)),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Color _statusColor(String status) {
    return switch (status) {
      'PAID'           => const Color(0xFF4ADE80),
      'OVERDUE'        => const Color(0xFFF87171),
      'PARTIALLY_PAID' => const Color(0xFFFBBF24),
      'ISSUED'         => const Color(0xFF60A5FA),
      _                => Colors.white38,
    };
  }
}

class _StatusBadge extends StatelessWidget {
  final String status;
  const _StatusBadge({required this.status});

  @override
  Widget build(BuildContext context) {
    final colors = _colors();
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
      decoration: BoxDecoration(
        color: colors.$1.withValues(alpha: 0.15),
        borderRadius: BorderRadius.circular(6),
        border: Border.all(color: colors.$1.withValues(alpha: 0.5)),
      ),
      child: Text(status.replaceAll('_', ' '),
          style: TextStyle(color: colors.$1, fontSize: 10, fontWeight: FontWeight.w600)),
    );
  }

  (Color,) _colors() => switch (status) {
    'PAID'           => (const Color(0xFF4ADE80),),
    'OVERDUE'        => (const Color(0xFFF87171),),
    'PARTIALLY_PAID' => (const Color(0xFFFBBF24),),
    'ISSUED'         => (const Color(0xFF60A5FA),),
    _                => (Colors.white38,),
  };
}

class _EmptyState extends StatelessWidget {
  final IconData icon;
  final String title;
  final String subtitle;
  const _EmptyState({required this.icon, required this.title, required this.subtitle});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(icon, size: 64, color: const Color(0xFF4ADE80).withValues(alpha: 0.6)),
          const SizedBox(height: 16),
          Text(title,
              style: const TextStyle(
                  color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          Text(subtitle,
              style: const TextStyle(color: Colors.white54, fontSize: 14),
              textAlign: TextAlign.center),
        ],
      ),
    );
  }
}

class _ErrorState extends StatelessWidget {
  final String message;
  final VoidCallback onRetry;
  const _ErrorState({required this.message, required this.onRetry});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline, size: 48, color: Color(0xFFF87171)),
          const SizedBox(height: 12),
          Text(message, style: const TextStyle(color: Colors.white60)),
          const SizedBox(height: 16),
          TextButton(onPressed: onRetry, child: const Text('Retry')),
        ],
      ),
    );
  }
}

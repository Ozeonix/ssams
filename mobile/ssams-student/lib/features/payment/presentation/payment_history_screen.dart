import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_student/features/payment/data/payment_repository.dart';
import 'package:ssams_student/features/payment/domain/payment_models.dart';
import 'package:ssams_student/features/payment/presentation/receipt_screen.dart';
import 'package:intl/intl.dart';

/// Payment History Screen.
/// Shows all past payment transactions for the logged-in student.
/// Students can only see their own transactions.
class PaymentHistoryScreen extends ConsumerWidget {
  const PaymentHistoryScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final historyAsync = ref.watch(paymentHistoryProvider);

    return Scaffold(
      backgroundColor: const Color(0xFF0F172A),
      appBar: AppBar(
        title: const Text('Payment History',
            style: TextStyle(color: Colors.white, fontWeight: FontWeight.w600)),
        backgroundColor: const Color(0xFF1E293B),
        iconTheme: const IconThemeData(color: Colors.white),
      ),
      body: historyAsync.when(
        loading: () => const Center(
            child: CircularProgressIndicator(color: Color(0xFF6366F1))),
        error: (e, _) => Center(
          child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
            const Icon(Icons.error_outline, color: Color(0xFFF87171), size: 48),
            const SizedBox(height: 12),
            const Text('Failed to load history',
                style: TextStyle(color: Colors.white60)),
            TextButton(
              onPressed: () => ref.invalidate(paymentHistoryProvider),
              child: const Text('Retry'),
            ),
          ]),
        ),
        data: (history) {
          if (history.isEmpty) {
            return const Center(
              child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                Icon(Icons.receipt_long_outlined, size: 64, color: Colors.white24),
                SizedBox(height: 16),
                Text('No payments yet',
                    style: TextStyle(color: Colors.white54, fontSize: 16)),
              ]),
            );
          }
          return RefreshIndicator(
            color: const Color(0xFF6366F1),
            onRefresh: () async => ref.invalidate(paymentHistoryProvider),
            child: ListView.separated(
              padding: const EdgeInsets.all(16),
              itemCount: history.length,
              separatorBuilder: (_, __) => const SizedBox(height: 10),
              itemBuilder: (ctx, i) {
                final txn = history[i];
                return _TransactionTile(txn: txn);
              },
            ),
          );
        },
      ),
    );
  }
}

class _TransactionTile extends ConsumerWidget {
  final PaymentTransaction txn;
  const _TransactionTile({required this.txn});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final fmt = NumberFormat('#,##0.00', 'en');
    final color = txn.isSuccess
        ? const Color(0xFF4ADE80)
        : txn.isFailed
            ? const Color(0xFFF87171)
            : const Color(0xFFFBBF24);
    final icon = txn.isSuccess
        ? Icons.check_circle_outline
        : txn.isFailed
            ? Icons.cancel_outlined
            : Icons.hourglass_empty;

    return Container(
      decoration: BoxDecoration(
        color: const Color(0xFF1E293B),
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: color.withValues(alpha: 0.2)),
      ),
      child: ListTile(
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        leading: Container(
          width: 44,
          height: 44,
          decoration: BoxDecoration(
            color: color.withValues(alpha: 0.1),
            shape: BoxShape.circle,
            border: Border.all(color: color.withValues(alpha: 0.3)),
          ),
          child: Icon(icon, color: color, size: 22),
        ),
        title: Text(txn.transactionRef,
            style: const TextStyle(
                color: Colors.white70,
                fontSize: 12,
                fontFamily: 'monospace')),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 4),
            Text(txn.gatewayCode,
                style: const TextStyle(color: Colors.white38, fontSize: 11)),
            if (txn.completedAt != null)
              Text(_formatDate(txn.completedAt!),
                  style: const TextStyle(color: Colors.white38, fontSize: 11)),
          ],
        ),
        trailing: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Text('NPR ${fmt.format(txn.amount)}',
                style: TextStyle(
                    color: color, fontWeight: FontWeight.bold, fontSize: 14)),
            const SizedBox(height: 4),
            Text(txn.status,
                style: TextStyle(color: color.withValues(alpha: 0.7), fontSize: 10)),
          ],
        ),
        onTap: txn.isSuccess
            ? () async {
                final receipt = await ref.read(paymentRepositoryProvider).getReceipt(txn.id);
                if (receipt != null && context.mounted) {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (_) => ReceiptScreen(receipt: receipt)));
                }
              }
            : null,
      ),
    );
  }

  String _formatDate(String iso) {
    try {
      final dt = DateTime.parse(iso).toLocal();
      return DateFormat('dd MMM yyyy, hh:mm a').format(dt);
    } catch (_) {
      return iso;
    }
  }
}

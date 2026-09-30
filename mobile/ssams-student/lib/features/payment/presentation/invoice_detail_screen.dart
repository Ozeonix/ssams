import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_student/features/payment/data/payment_repository.dart';
import 'package:ssams_student/features/payment/domain/payment_models.dart';
import 'package:ssams_student/features/payment/presentation/payment_webview_screen.dart';
import 'package:intl/intl.dart';

/// Invoice Detail Screen.
/// Shows invoice line items, totals, and payment button.
/// Tapping "Pay" → initiates payment with backend → opens eSewa via WebView.
class InvoiceDetailScreen extends ConsumerStatefulWidget {
  final String invoiceId;
  const InvoiceDetailScreen({super.key, required this.invoiceId});

  @override
  ConsumerState<InvoiceDetailScreen> createState() => _InvoiceDetailScreenState();
}

class _InvoiceDetailScreenState extends ConsumerState<InvoiceDetailScreen> {
  bool _initiating = false;

  Future<void> _initiatePayment(FeeInvoice invoice) async {
    setState(() => _initiating = true);
    try {
      final repo = ref.read(paymentRepositoryProvider);
      final response = await repo.initiatePayment(invoice.id, 'ESEWA');

      if (!mounted) return;
      await Navigator.push(
        context,
        MaterialPageRoute(
          builder: (_) => PaymentWebviewScreen(
            gatewayUrl: response.gatewayUrl,
            formFields: response.gatewayFormFields,
            transactionRef: response.transactionRef,
            transactionId: response.transactionId,
          ),
        ),
      );
      // Refresh invoices after returning from webview
      ref.invalidate(outstandingInvoicesProvider);
      ref.invalidate(invoiceProvider(widget.invoiceId));
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Payment failed to initiate: $e'),
          backgroundColor: const Color(0xFFF87171),
        ),
      );
    } finally {
      if (mounted) setState(() => _initiating = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final invoiceAsync = ref.watch(invoiceProvider(widget.invoiceId));
    final fmt = NumberFormat('#,##0.00', 'en');

    return Scaffold(
      backgroundColor: const Color(0xFF0F172A),
      appBar: AppBar(
        title: const Text('Invoice Detail',
            style: TextStyle(color: Colors.white, fontWeight: FontWeight.w600)),
        backgroundColor: const Color(0xFF1E293B),
        iconTheme: const IconThemeData(color: Colors.white),
      ),
      body: invoiceAsync.when(
        loading: () => const Center(
            child: CircularProgressIndicator(color: Color(0xFF6366F1))),
        error: (e, _) => Center(
          child: Text('Error: $e', style: const TextStyle(color: Colors.white60))),
        data: (invoice) => Column(
          children: [
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Invoice header card
                    _card(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text(invoice.invoiceNumber,
                                  style: const TextStyle(
                                      color: Colors.white54,
                                      fontFamily: 'monospace',
                                      fontSize: 12)),
                              _statusBadge(invoice.status),
                            ],
                          ),
                          const SizedBox(height: 16),
                          _row('Total Amount',
                              'NPR ${fmt.format(invoice.totalAmount)}',
                              valueStyle: const TextStyle(
                                  color: Colors.white,
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold)),
                          const Divider(color: Colors.white12, height: 20),
                          _row('Paid', 'NPR ${fmt.format(invoice.paidAmount)}',
                              valueColor: const Color(0xFF4ADE80)),
                          const SizedBox(height: 6),
                          _row('Balance Due', 'NPR ${fmt.format(invoice.balance)}',
                              valueColor: invoice.balance > 0
                                  ? const Color(0xFFF87171)
                                  : const Color(0xFF4ADE80)),
                          if (invoice.dueDate != null) ...[
                            const SizedBox(height: 6),
                            _row('Due Date', invoice.dueDate!),
                          ],
                        ],
                      ),
                    ),

                    // Line items
                    if (invoice.items.isNotEmpty) ...[
                      const SizedBox(height: 16),
                      const Text('Fee Breakdown',
                          style: TextStyle(
                              color: Colors.white70,
                              fontWeight: FontWeight.w600,
                              fontSize: 14)),
                      const SizedBox(height: 8),
                      _card(
                        child: Column(
                          children: invoice.items
                              .map((item) => Padding(
                                    padding: const EdgeInsets.symmetric(vertical: 6),
                                    child: Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Expanded(
                                          child: Text(item.description,
                                              style: const TextStyle(
                                                  color: Colors.white70,
                                                  fontSize: 13)),
                                        ),
                                        Text(
                                            'NPR ${fmt.format(item.totalAmount)}',
                                            style: const TextStyle(
                                                color: Colors.white,
                                                fontWeight: FontWeight.w500)),
                                      ],
                                    ),
                                  ))
                              .toList(),
                        ),
                      ),
                    ],

                    if (invoice.notes != null) ...[
                      const SizedBox(height: 16),
                      _card(
                        child: Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Icon(Icons.info_outline,
                                color: Colors.white38, size: 16),
                            const SizedBox(width: 8),
                            Expanded(
                              child: Text(invoice.notes!,
                                  style: const TextStyle(
                                      color: Colors.white54, fontSize: 13)),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ],
                ),
              ),
            ),

            // Pay button
            if (invoice.isPayable)
              SafeArea(
                child: Padding(
                  padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
                  child: SizedBox(
                    width: double.infinity,
                    height: 52,
                    child: ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF6366F1),
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(14)),
                      ),
                      onPressed: _initiating ? null : () => _initiatePayment(invoice),
                      child: _initiating
                          ? const SizedBox(
                              width: 22,
                              height: 22,
                              child: CircularProgressIndicator(
                                  color: Colors.white, strokeWidth: 2.5))
                          : Text(
                              'Pay NPR ${fmt.format(invoice.balance)} via eSewa',
                              style: const TextStyle(
                                  color: Colors.white,
                                  fontWeight: FontWeight.w600,
                                  fontSize: 16)),
                    ),
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _card({required Widget child}) => Container(
        width: double.infinity,
        decoration: BoxDecoration(
          color: const Color(0xFF1E293B),
          borderRadius: BorderRadius.circular(16),
        ),
        padding: const EdgeInsets.all(16),
        child: child,
      );

  Widget _row(String label, String value,
      {Color? valueColor, TextStyle? valueStyle}) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: const TextStyle(color: Colors.white54, fontSize: 13)),
        Text(value,
            style: valueStyle ??
                TextStyle(
                    color: valueColor ?? Colors.white,
                    fontSize: 13,
                    fontWeight: FontWeight.w500)),
      ],
    );
  }

  Widget _statusBadge(String status) {
    final color = switch (status) {
      'PAID'           => const Color(0xFF4ADE80),
      'OVERDUE'        => const Color(0xFFF87171),
      'PARTIALLY_PAID' => const Color(0xFFFBBF24),
      'ISSUED'         => const Color(0xFF60A5FA),
      _                => Colors.white38,
    };
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.15),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: color.withValues(alpha: 0.4)),
      ),
      child: Text(status.replaceAll('_', ' '),
          style: TextStyle(
              color: color, fontSize: 11, fontWeight: FontWeight.w600)),
    );
  }
}

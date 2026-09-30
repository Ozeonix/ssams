import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:ssams_student/features/payment/domain/payment_models.dart';
import 'package:intl/intl.dart';

/// Receipt Screen – shows confirmed payment receipt.
/// Reproducible from backend snapshot data.
class ReceiptScreen extends StatelessWidget {
  final PaymentReceipt receipt;
  const ReceiptScreen({super.key, required this.receipt});

  @override
  Widget build(BuildContext context) {
    final fmt = NumberFormat('#,##0.00', 'en');
    final dateFmt = DateFormat('dd MMM yyyy, hh:mm a');
    DateTime? issuedAt;
    try {
      issuedAt = DateTime.parse(receipt.issuedAt).toLocal();
    } catch (_) {}

    return Scaffold(
      backgroundColor: const Color(0xFF0F172A),
      appBar: AppBar(
        title: const Text('Payment Receipt',
            style: TextStyle(color: Colors.white, fontWeight: FontWeight.w600)),
        backgroundColor: const Color(0xFF1E293B),
        iconTheme: const IconThemeData(color: Colors.white),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // Success header
            Container(
              width: double.infinity,
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [Color(0xFF1E3A2F), Color(0xFF1E293B)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: const Color(0xFF4ADE80).withValues(alpha: 0.3)),
              ),
              padding: const EdgeInsets.all(24),
              child: Column(
                children: [
                  const Icon(Icons.check_circle, color: Color(0xFF4ADE80), size: 56),
                  const SizedBox(height: 12),
                  const Text('Payment Confirmed',
                      style: TextStyle(
                          color: Colors.white,
                          fontSize: 20,
                          fontWeight: FontWeight.bold)),
                  const SizedBox(height: 6),
                  Text('NPR ${fmt.format(receipt.amount)}',
                      style: const TextStyle(
                          color: Color(0xFF4ADE80),
                          fontSize: 32,
                          fontWeight: FontWeight.w700)),
                ],
              ),
            ),

            const SizedBox(height: 16),

            // Receipt details
            _detailCard(
              title: 'Receipt Details',
              rows: [
                ('Receipt No.', receipt.receiptNumber),
                ('Payment Method', receipt.paymentMethod),
                if (receipt.gatewayRef != null)
                  ('Gateway Ref.', receipt.gatewayRef!),
                if (issuedAt != null) ('Date & Time', dateFmt.format(issuedAt)),
              ],
            ),

            const SizedBox(height: 12),

            // Copy receipt number
            GestureDetector(
              onTap: () {
                Clipboard.setData(ClipboardData(text: receipt.receiptNumber));
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('Receipt number copied'),
                    backgroundColor: Color(0xFF334155),
                  ),
                );
              },
              child: Container(
                padding: const EdgeInsets.all(14),
                decoration: BoxDecoration(
                  color: const Color(0xFF1E293B),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: const Color(0xFF334155)),
                ),
                child: Row(
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('Receipt Number',
                              style: TextStyle(color: Colors.white38, fontSize: 11)),
                          const SizedBox(height: 4),
                          Text(receipt.receiptNumber,
                              style: const TextStyle(
                                  color: Colors.white,
                                  fontFamily: 'monospace',
                                  fontSize: 15,
                                  fontWeight: FontWeight.w600)),
                        ],
                      ),
                    ),
                    const Icon(Icons.copy, color: Colors.white38, size: 20),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 20),
            SizedBox(
              width: double.infinity,
              height: 50,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF1E293B),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                      side: const BorderSide(color: Color(0xFF334155))),
                ),
                onPressed: () => Navigator.pushNamedAndRemoveUntil(
                    context, '/fees', (r) => r.isFirst),
                child: const Text('Back to Fees',
                    style: TextStyle(color: Colors.white70)),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _detailCard({
    required String title,
    required List<(String, String)> rows,
  }) {
    return Container(
      decoration: BoxDecoration(
        color: const Color(0xFF1E293B),
        borderRadius: BorderRadius.circular(16),
      ),
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(title,
              style: const TextStyle(
                  color: Colors.white54,
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                  letterSpacing: 0.8)),
          const SizedBox(height: 12),
          ...rows.map(
            (row) => Padding(
              padding: const EdgeInsets.symmetric(vertical: 5),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(row.$1,
                      style: const TextStyle(color: Colors.white54, fontSize: 13)),
                  Flexible(
                    child: Text(row.$2,
                        style: const TextStyle(
                            color: Colors.white, fontSize: 13, fontWeight: FontWeight.w500),
                        textAlign: TextAlign.right,
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_student/features/payment/data/payment_repository.dart';
import 'package:ssams_student/features/payment/presentation/receipt_screen.dart';

/// Payment Status Screen.
/// Always shows the SERVER-CONFIRMED status, not a client-side assumption.
/// Fetches receipt from backend if payment is successful.
class PaymentStatusScreen extends ConsumerWidget {
  final String transactionId;
  final String transactionRef;
  final bool initialSuccess;

  const PaymentStatusScreen({
    super.key,
    required this.transactionId,
    required this.transactionRef,
    required this.initialSuccess,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final receiptAsync = ref.watch(receiptProvider(transactionId));

    return Scaffold(
      backgroundColor: const Color(0xFF0F172A),
      body: SafeArea(
        child: receiptAsync.when(
          loading: () => _buildStatus(
            context: context,
            icon: Icons.hourglass_empty,
            iconColor: const Color(0xFF6366F1),
            title: 'Verifying Payment...',
            subtitle: 'Please wait while we confirm your payment with the gateway.',
            actions: const [],
          ),
          error: (e, _) => _buildStatus(
            context: context,
            icon: Icons.error_outline,
            iconColor: const Color(0xFFF87171),
            title: 'Payment Failed',
            subtitle: 'Could not verify payment. If amount was deducted, contact support.\n\nRef: $transactionRef',
            actions: [
              _ActionButton(
                label: 'Go to Fees',
                color: const Color(0xFF6366F1),
                onTap: () => Navigator.pushNamedAndRemoveUntil(
                    context, '/fees', (r) => r.isFirst),
              ),
            ],
          ),
          data: (receipt) {
            if (receipt == null) {
              // No receipt = payment not confirmed
              return _buildStatus(
                context: context,
                icon: Icons.cancel_outlined,
                iconColor: const Color(0xFFF87171),
                title: 'Payment Not Confirmed',
                subtitle: 'Your payment could not be verified.\nRef: $transactionRef',
                actions: [
                  _ActionButton(
                    label: 'Back to Fees',
                    color: const Color(0xFF64748B),
                    onTap: () => Navigator.pushNamedAndRemoveUntil(
                        context, '/fees', (r) => r.isFirst),
                  ),
                ],
              );
            }
            // Receipt exists = confirmed
            return _buildStatus(
              context: context,
              icon: Icons.check_circle,
              iconColor: const Color(0xFF4ADE80),
              title: 'Payment Successful!',
              subtitle: 'Your payment has been confirmed.\nReceipt: ${receipt.receiptNumber}',
              actions: [
                _ActionButton(
                  label: 'View Receipt',
                  color: const Color(0xFF6366F1),
                  onTap: () => Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(
                      builder: (_) => ReceiptScreen(receipt: receipt),
                    ),
                  ),
                ),
                _ActionButton(
                  label: 'Back to Fees',
                  color: const Color(0xFF334155),
                  onTap: () => Navigator.pushNamedAndRemoveUntil(
                      context, '/fees', (r) => r.isFirst),
                ),
              ],
            );
          },
        ),
      ),
    );
  }

  Widget _buildStatus({
    required BuildContext context,
    required IconData icon,
    required Color iconColor,
    required String title,
    required String subtitle,
    required List<Widget> actions,
  }) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 100,
              height: 100,
              decoration: BoxDecoration(
                color: iconColor.withValues(alpha: 0.1),
                shape: BoxShape.circle,
                border: Border.all(color: iconColor.withValues(alpha: 0.3), width: 2),
              ),
              child: Icon(icon, size: 52, color: iconColor),
            ),
            const SizedBox(height: 28),
            Text(title,
                style: const TextStyle(
                    color: Colors.white,
                    fontSize: 22,
                    fontWeight: FontWeight.bold),
                textAlign: TextAlign.center),
            const SizedBox(height: 12),
            Text(subtitle,
                style:
                    const TextStyle(color: Colors.white54, fontSize: 14, height: 1.5),
                textAlign: TextAlign.center),
            const SizedBox(height: 36),
            ...actions.map((a) => Padding(
                  padding: const EdgeInsets.only(bottom: 12),
                  child: SizedBox(width: double.infinity, height: 50, child: a),
                )),
          ],
        ),
      ),
    );
  }
}

class _ActionButton extends StatelessWidget {
  final String label;
  final Color color;
  final VoidCallback onTap;

  const _ActionButton(
      {required this.label, required this.color, required this.onTap});

  @override
  Widget build(BuildContext context) => ElevatedButton(
        style: ElevatedButton.styleFrom(
          backgroundColor: color,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        ),
        onPressed: onTap,
        child: Text(label,
            style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w600)),
      );
}

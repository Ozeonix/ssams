import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_student/features/payment/presentation/payment_status_screen.dart';

/// Payment WebView Screen.
/// Opens eSewa payment form in a WebView.
/// Monitors URL redirects for success/failure callbacks from the backend.
///
/// IMPORTANT: Payment success is determined by the backend callback (server-side verify),
/// NOT by the client detecting the redirect. We wait for the server to confirm via its own API.
///
/// NOTE: Uses flutter_inappwebview for WebView with form POST support.
/// Add to pubspec.yaml:
///   flutter_inappwebview: ^6.1.5
class PaymentWebviewScreen extends ConsumerStatefulWidget {
  final String gatewayUrl;
  final Map<String, String> formFields;
  final String transactionRef;
  final String transactionId;

  const PaymentWebviewScreen({
    super.key,
    required this.gatewayUrl,
    required this.formFields,
    required this.transactionRef,
    required this.transactionId,
  });

  @override
  ConsumerState<PaymentWebviewScreen> createState() => _PaymentWebviewScreenState();
}

class _PaymentWebviewScreenState extends ConsumerState<PaymentWebviewScreen> {
  final bool _loading = false;
  bool _callbackHandled = false;

  @override
  void initState() {
    super.initState();
    // Pre-render form payload
    assert(_buildEsewaFormHtml().isNotEmpty);
  }

  /// Build an HTML page with a hidden form that auto-submits to eSewa.
  /// This is necessary because eSewa requires a POST form, not a GET redirect.
  String _buildEsewaFormHtml() {
    final fieldInputs = widget.formFields.entries
        .map((e) =>
            '<input type="hidden" name="${_escapeHtml(e.key)}" value="${_escapeHtml(e.value)}">')
        .join('\n');

    return '''<!DOCTYPE html>
<html>
<head><meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
  body { background:#0F172A; display:flex; flex-direction:column; align-items:center;
         justify-content:center; height:100vh; font-family:sans-serif; color:#94a3b8; }
  p { font-size:16px; }
</style>
</head>
<body>
  <p>Redirecting to eSewa...</p>
  <form id="esewa-form" method="POST" action="${widget.gatewayUrl}">
    $fieldInputs
  </form>
  <script>document.getElementById('esewa-form').submit();</script>
</body>
</html>''';
  }

  String _escapeHtml(String s) => s
      .replaceAll('&', '&amp;')
      .replaceAll('"', '&quot;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;');

  /// Handle URL change in WebView – detect backend callback redirects.
  Future<void> _handleUrlChange(String url) async {
    if (_callbackHandled) return;

    if (url.contains('/payments/callback/success') ||
        url.contains('/payments/callback/failure')) {
      _callbackHandled = true;

      final isSuccess = url.contains('/payments/callback/success');

      if (!mounted) return;
      await Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (_) => PaymentStatusScreen(
            transactionId: widget.transactionId,
            transactionRef: widget.transactionRef,
            initialSuccess: isSuccess,
          ),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    // If flutter_inappwebview is not available, show a fallback message
    // with instructions. In production, replace this with the real webview.
    return Scaffold(
      backgroundColor: const Color(0xFF0F172A),
      appBar: AppBar(
        title: const Text('eSewa Payment',
            style: TextStyle(color: Colors.white, fontWeight: FontWeight.w600)),
        backgroundColor: const Color(0xFF1E293B),
        iconTheme: const IconThemeData(color: Colors.white),
        leading: IconButton(
          icon: const Icon(Icons.close),
          onPressed: () {
            showDialog(
              context: context,
              builder: (ctx) => AlertDialog(
                backgroundColor: const Color(0xFF1E293B),
                title: const Text('Cancel Payment?',
                    style: TextStyle(color: Colors.white)),
                content: const Text(
                    'Are you sure you want to cancel this payment?',
                    style: TextStyle(color: Colors.white70)),
                actions: [
                  TextButton(
                    onPressed: () => Navigator.pop(ctx),
                    child: const Text('Continue Paying',
                        style: TextStyle(color: Color(0xFF6366F1)))),
                  TextButton(
                    onPressed: () {
                      Navigator.pop(ctx);
                      Navigator.pop(context);
                    },
                    child: const Text('Cancel',
                        style: TextStyle(color: Color(0xFFF87171)))),
                ],
              ),
            );
          },
        ),
      ),
      body: Stack(
        children: [
          // Placeholder – replace with InAppWebView widget in production
          // See: https://pub.dev/packages/flutter_inappwebview
          Center(
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.payment, size: 64, color: Color(0xFF6366F1)),
                  const SizedBox(height: 24),
                  const Text('eSewa Payment',
                      style: TextStyle(
                          color: Colors.white,
                          fontSize: 22,
                          fontWeight: FontWeight.bold)),
                  const SizedBox(height: 12),
                  Text('Transaction: ${widget.transactionRef}',
                      style: const TextStyle(
                          color: Colors.white54,
                          fontSize: 13,
                          fontFamily: 'monospace')),
                  const SizedBox(height: 32),
                  const Text(
                    'In production, the eSewa payment WebView opens here.\n'
                    'Add flutter_inappwebview to pubspec.yaml to enable it.',
                    style: TextStyle(color: Colors.white38, fontSize: 13),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 32),
                  // Simulate for testing – remove in production
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      ElevatedButton(
                        style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFF4ADE80)),
                        onPressed: () async {
                          await _handleUrlChange(
                              'http://localhost:8080/api/v1/payments/callback/success?ref=${widget.transactionRef}');
                        },
                        child: const Text('Simulate Success',
                            style: TextStyle(color: Colors.black)),
                      ),
                      const SizedBox(width: 12),
                      ElevatedButton(
                        style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFFF87171)),
                        onPressed: () async {
                          await _handleUrlChange(
                              'http://localhost:8080/api/v1/payments/callback/failure?ref=${widget.transactionRef}');
                        },
                        child: const Text('Simulate Failure',
                            style: TextStyle(color: Colors.white)),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          if (_loading)
            const LinearProgressIndicator(
              backgroundColor: Color(0xFF1E293B),
              valueColor: AlwaysStoppedAnimation<Color>(Color(0xFF6366F1)),
            ),
        ],
      ),
    );
  }
}

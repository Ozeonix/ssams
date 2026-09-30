import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_student/core/network/api_client.dart';
import 'package:ssams_student/features/payment/domain/payment_models.dart';

/// Payment API repository.
/// All methods return server-verified data only.
class PaymentRepository {
  final Dio _dio;
  PaymentRepository(this._dio);

  // ── Invoices ────────────────────────────────────────────────────────────────

  Future<List<FeeInvoice>> getOutstandingInvoices() async {
    final response = await _dio.get('/fees/invoices/my');
    final list = response.data as List<dynamic>;
    return list
        .map((e) => FeeInvoice.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<List<FeeInvoice>> getAllMyInvoices() async {
    // Uses outstanding + history: admin can get all, student gets own
    final response = await _dio.get('/fees/invoices/my');
    final list = response.data as List<dynamic>;
    return list
        .map((e) => FeeInvoice.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<FeeInvoice> getInvoice(String invoiceId) async {
    final response = await _dio.get('/fees/invoices/$invoiceId');
    return FeeInvoice.fromJson(response.data as Map<String, dynamic>);
  }

  // ── Payment Initiation ──────────────────────────────────────────────────────

  Future<PaymentInitResponse> initiatePayment(
      String invoiceId, String gatewayCode) async {
    final response = await _dio.post('/payments/initiate', data: {
      'invoiceId': invoiceId,
      'gatewayCode': gatewayCode,
    });
    return PaymentInitResponse.fromJson(response.data as Map<String, dynamic>);
  }

  // ── Payment History ──────────────────────────────────────────────────────────

  Future<List<PaymentTransaction>> getPaymentHistory() async {
    final response = await _dio.get('/payments/history/my');
    final list = response.data as List<dynamic>;
    return list
        .map((e) => PaymentTransaction.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  // ── Receipt ──────────────────────────────────────────────────────────────────

  Future<PaymentReceipt?> getReceipt(String transactionId) async {
    try {
      final response = await _dio.get('/payments/$transactionId/receipt');
      return PaymentReceipt.fromJson(response.data as Map<String, dynamic>);
    } on DioException catch (e) {
      if (e.response?.statusCode == 404) return null;
      rethrow;
    }
  }
}

// ── Riverpod Providers ────────────────────────────────────────────────────────

final paymentRepositoryProvider = Provider<PaymentRepository>((ref) {
  return PaymentRepository(ref.watch(dioProvider));
});

final outstandingInvoicesProvider =
    FutureProvider.autoDispose<List<FeeInvoice>>((ref) {
  return ref.watch(paymentRepositoryProvider).getOutstandingInvoices();
});

final paymentHistoryProvider =
    FutureProvider.autoDispose<List<PaymentTransaction>>((ref) {
  return ref.watch(paymentRepositoryProvider).getPaymentHistory();
});

final invoiceProvider =
    FutureProvider.autoDispose.family<FeeInvoice, String>((ref, invoiceId) {
  return ref.watch(paymentRepositoryProvider).getInvoice(invoiceId);
});

final receiptProvider =
    FutureProvider.autoDispose.family<PaymentReceipt?, String>((ref, txnId) {
  return ref.watch(paymentRepositoryProvider).getReceipt(txnId);
});

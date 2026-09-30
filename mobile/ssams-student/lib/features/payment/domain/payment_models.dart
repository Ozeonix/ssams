// Invoice model from backend API
class FeeInvoice {
  final String id;
  final String invoiceNumber;
  final String studentId;
  final String status;
  final double totalAmount;
  final double paidAmount;
  final double balance;
  final String? dueDate;
  final String? notes;
  final List<InvoiceItem> items;

  const FeeInvoice({
    required this.id,
    required this.invoiceNumber,
    required this.studentId,
    required this.status,
    required this.totalAmount,
    required this.paidAmount,
    required this.balance,
    this.dueDate,
    this.notes,
    this.items = const [],
  });

  factory FeeInvoice.fromJson(Map<String, dynamic> json) {
    return FeeInvoice(
      id: json['id'] as String,
      invoiceNumber: json['invoiceNumber'] as String,
      studentId: json['studentId'] as String,
      status: json['status'] as String,
      totalAmount: _parseDouble(json['totalAmount']),
      paidAmount: _parseDouble(json['paidAmount']),
      balance: _parseDouble(json['balance']),
      dueDate: json['dueDate'] as String?,
      notes: json['notes'] as String?,
      items: (json['items'] as List<dynamic>? ?? [])
          .map((e) => InvoiceItem.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }

  bool get isPayable =>
      status == 'ISSUED' || status == 'PARTIALLY_PAID' || status == 'OVERDUE';

  bool get isPaid => status == 'PAID';

  static double _parseDouble(dynamic v) {
    if (v == null) return 0.0;
    if (v is double) return v;
    if (v is int) return v.toDouble();
    return double.tryParse(v.toString()) ?? 0.0;
  }
}

class InvoiceItem {
  final String feeCategoryId;
  final String description;
  final int quantity;
  final double unitAmount;
  final double totalAmount;

  const InvoiceItem({
    required this.feeCategoryId,
    required this.description,
    required this.quantity,
    required this.unitAmount,
    required this.totalAmount,
  });

  factory InvoiceItem.fromJson(Map<String, dynamic> json) {
    return InvoiceItem(
      feeCategoryId: json['feeCategoryId'] as String? ?? '',
      description: json['description'] as String? ?? '',
      quantity: json['quantity'] as int? ?? 1,
      unitAmount: FeeInvoice._parseDouble(json['unitAmount']),
      totalAmount: FeeInvoice._parseDouble(json['totalAmount']),
    );
  }
}

// Payment transaction model
class PaymentTransaction {
  final String id;
  final String transactionRef;
  final String invoiceId;
  final String status;
  final double amount;
  final String currency;
  final String gatewayCode;
  final String? gatewayTxnRef;
  final String? createdAt;
  final String? completedAt;

  const PaymentTransaction({
    required this.id,
    required this.transactionRef,
    required this.invoiceId,
    required this.status,
    required this.amount,
    required this.currency,
    required this.gatewayCode,
    this.gatewayTxnRef,
    this.createdAt,
    this.completedAt,
  });

  factory PaymentTransaction.fromJson(Map<String, dynamic> json) {
    return PaymentTransaction(
      id: json['id'] as String,
      transactionRef: json['transactionRef'] as String,
      invoiceId: json['invoiceId'] as String,
      status: json['status'] as String,
      amount: FeeInvoice._parseDouble(json['amount']),
      currency: json['currency'] as String? ?? 'NPR',
      gatewayCode: json['gatewayCode'] as String? ?? 'ESEWA',
      gatewayTxnRef: json['gatewayTxnRef'] as String?,
      createdAt: json['createdAt'] as String?,
      completedAt: json['completedAt'] as String?,
    );
  }

  bool get isSuccess  => status == 'SUCCESS';
  bool get isFailed   => status == 'FAILED' || status == 'CANCELLED';
  bool get isPending  => status == 'PENDING' || status == 'INITIATED';
}

// Payment initiation response from backend
class PaymentInitResponse {
  final String transactionId;
  final String transactionRef;
  final String gatewayUrl;
  final Map<String, String> gatewayFormFields;

  const PaymentInitResponse({
    required this.transactionId,
    required this.transactionRef,
    required this.gatewayUrl,
    required this.gatewayFormFields,
  });

  factory PaymentInitResponse.fromJson(Map<String, dynamic> json) {
    return PaymentInitResponse(
      transactionId: json['transactionId'] as String,
      transactionRef: json['transactionRef'] as String,
      gatewayUrl: json['gatewayUrl'] as String,
      gatewayFormFields: Map<String, String>.from(
          (json['gatewayFormFields'] as Map<String, dynamic>)
              .map((k, v) => MapEntry(k, v.toString()))),
    );
  }
}

// Receipt
class PaymentReceipt {
  final String id;
  final String receiptNumber;
  final String transactionId;
  final double amount;
  final String paymentMethod;
  final String? gatewayRef;
  final String issuedAt;

  const PaymentReceipt({
    required this.id,
    required this.receiptNumber,
    required this.transactionId,
    required this.amount,
    required this.paymentMethod,
    this.gatewayRef,
    required this.issuedAt,
  });

  factory PaymentReceipt.fromJson(Map<String, dynamic> json) {
    return PaymentReceipt(
      id: json['id'] as String,
      receiptNumber: json['receiptNumber'] as String,
      transactionId: json['transactionId'] as String,
      amount: FeeInvoice._parseDouble(json['amount']),
      paymentMethod: json['paymentMethod'] as String? ?? 'ESEWA',
      gatewayRef: json['gatewayRef'] as String?,
      issuedAt: json['issuedAt'] as String? ?? '',
    );
  }
}

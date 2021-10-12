import 'dart:async';

import 'package:flutter/services.dart';

enum TransactionFailureReason {
  USER_CANCELLED,
  DEVICE_FEATURE_MISSING,
  USER_PERMISSIONS_SETTINGS_RELOAD,
  AUTHENTICATION_FAILURE,
  TRANSACTION_CONFIRMATION_FAILURE,
  UNKNOWN
}

class BharatXTransactionManager {
  static const String _signature = "flutter.bharatx.tech/startup";
  static const MethodChannel _channel = const MethodChannel(_signature);

  static Future<void> registerUser(Map<String, dynamic> userDetails) async {
    await _channel.invokeMethod('registerUser', userDetails);
  }

  static Future<void> confirmTransactionWithUser(
      int amountInPaise,
      String transactionId,
      void onSuccess(),
      void onFailure(TransactionFailureReason s)) async {
    const MethodChannel confirmTransactionWithUserChannel =
        const MethodChannel("$_signature/confirmTransactionWithUser");
    confirmTransactionWithUserChannel.setMethodCallHandler((call) {
      switch (call.method) {
        case "onSuccess":
          {
            onSuccess();
            break;
          }
        case "onFailure":
          {
            TransactionFailureReason reason = TransactionFailureReason.values
                .firstWhere(
                    (e) =>
                        e.toString() ==
                        'TransactionFailureReason.' + call.arguments,
                    orElse: () => TransactionFailureReason.UNKNOWN);
            onFailure(reason);
            break;
          }
      }
      return null;
    });
    await _channel.invokeMethod('confirmTransactionWithUser',
        {"amountInPaise": amountInPaise, "transactionId": transactionId});
  }
}

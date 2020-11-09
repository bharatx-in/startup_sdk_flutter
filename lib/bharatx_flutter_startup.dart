import 'dart:async';

import 'package:bharatx_flutter_securityhelpers/bharatx_flutter_securityhelpers.dart';
import 'package:flutter/services.dart';

class BharatXStartupTierManager {
  static const String _signature = "flutter.bharatx.tech/startup";
  static const MethodChannel _channel = const MethodChannel(_signature);

  static Future<void> initialize(String partnerId, String partnerApiKey,
      [dynamic color]) async {
    await _channel.invokeMethod(
        'initialize', {"partnerId": partnerId, "partnerApiKey": partnerApiKey});
    if (color != null) {
      await BharatXSecurityHelpers.storeThemeColorPreference(color);
    }
  }
}

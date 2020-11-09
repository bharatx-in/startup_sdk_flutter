import 'package:bharatx_flutter_alternatedata/bharatx_flutter_alternatedata.dart';
import 'package:bharatx_flutter_common/bharatx_flutter_common.dart';
import 'package:bharatx_flutter_startup/bharatx_flutter_startup.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  void confirmTransactionWithUser() async {
    try {
      await BharatXStartupTierManager.initialize(
          "testPartnerId", "testApiKey", "#000000");
      CreditInfo creditInfo = await BharatXCommonUtilManager.userCreditInfo;
      print("Credit Info ${creditInfo.creditTaken}/${creditInfo.creditLimit}");
      BharatXCommonUtilManager.confirmTransactionWithUser(10000, () {
        print("User Confirmed Transaction");
        BharatXCommonUtilManager.registerTransactionId("transactionId", () {
          BharatXCommonUtilManager.showTransactionStatusDialog(true, () {
            print("Closed");
          });
        }, () {
          print("Failed to register transaction ID");
        });
      }, () {
        print("User Accepted Privacy Policy");
        AlternateDataManager.register();
      }, () {
        print("User Cancelled Transaction");
      });
    } on PlatformException {
      print("Platform Exception");
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: TextButton(
            onPressed: confirmTransactionWithUser,
            child: Text("Click Here"),
          ),
        ),
      ),
    );
  }
}

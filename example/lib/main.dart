import 'package:startup_sdk_flutter/startup_sdk_flutter.dart';
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
      BharatXTransactionManager.registerUser({
        "phoneNumber": "+911234567890",
        "id": "user-200",
        "name": 'Christopher Chedeau',
        "gender": 'Male',
        "dob": '2016-02-05',
        // dobFormat is mandatory when you supply dob
        "dobFormat": 'yyyy-MM-dd',
        "age": 20,
        "address": '20, Tech Street, Bengaluru',
        "customKey1": 'customValue1',
        "customKey2": 'customValue2',
      });
      dynamic amountInPaise = 10000;
      dynamic transactionId = "txnId01";
      BharatXTransactionManager.confirmTransactionWithUser(
          amountInPaise, transactionId, () {
        // transaction success!
      }, (s) {
        // transaction cancelled by user or failed
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

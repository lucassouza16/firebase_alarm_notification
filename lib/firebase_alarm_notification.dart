// import 'package:flutter/foundation.dart';
import 'package:firebase_alarm_notification/models/firebasemessage.model.dart';
import 'package:flutter/services.dart';

class FirebaseAlarmNotification {
  static const methodChannel = MethodChannel('firebase_alarm_notification');
  static List<Function(FirebaseMessage arguments)> callbackNotificationTap = [];

  static Future<String?> getToken() {
    return FirebaseAlarmNotification.methodChannel
        .invokeMethod<String>('getToken');
  }

  static Future<String?> setSongAssetAlarm(String uri) async {
    ByteData fileData = await rootBundle.load(uri);
    List<int> bytes = fileData.buffer.asUint8List();

    return FirebaseAlarmNotification.methodChannel.invokeMethod<String>(
        'setSongAssetAlarm', {'bytes': bytes, 'name': uri});
  }

  static Future<FirebaseMessage?> getInitialMessage() async {
    dynamic param = await FirebaseAlarmNotification.methodChannel
        .invokeMethod<dynamic>('getInitialMessage');

    if (param != null) {
      return FirebaseMessage.fromJson(param);
    } else {
      return null;
    }
  }

  static onNotificationTap(Function(FirebaseMessage arguments) callback) {
    callbackNotificationTap.add(callback);
  }

  static Future<dynamic> nativeMethodCallHandler(MethodCall methodCall) async {
    switch (methodCall.method) {
      case "onNotificationTapped":
        for (var cb in callbackNotificationTap) {
          cb(FirebaseMessage.fromJson(methodCall.arguments));
        }
      default:
        return "Nothing";
    }
  }

  static init() {
    methodChannel.setMethodCallHandler(nativeMethodCallHandler);
  }
}

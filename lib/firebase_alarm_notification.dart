// import 'package:flutter/foundation.dart';
import 'package:firebase_alarm_notification/models/firebasemessage.model.dart';
import 'package:flutter/services.dart';

class FirebaseAlarmNotification {
  static const methodChannel = MethodChannel('firebase_alarm_notification');
  static List<Function(FirebaseMessage arguments)> callbackNotificationTap = [];
  static List<Function(FirebaseMessage arguments)> callbackNotification = [];

  static Future<String?> getToken() {
    return FirebaseAlarmNotification.methodChannel
        .invokeMethod<String>('getToken');
  }

  static Future<String?> getSongAssetAlarm() {
    return FirebaseAlarmNotification.methodChannel
        .invokeMethod<String>('getSongAssetAlarm');
  }

  static Future<bool?> setSongAssetAlarm(String? uri) async {
    dynamic params = null;

    if (uri != null) {
      ByteData fileData = await rootBundle.load(uri);
      List<int> bytes = fileData.buffer.asUint8List();
      params = {
        'bytes': bytes,
        'name': uri,
      };
    }

    return FirebaseAlarmNotification.methodChannel
        .invokeMethod<bool>('setSongAssetAlarm', params);
  }

  static Future<bool?> channelExists(String channelId) async {
    return FirebaseAlarmNotification.methodChannel
        .invokeMethod<bool>('channelExists', channelId);
  }

  static Future<bool?> deleteChannel(String channelId) async {
    return FirebaseAlarmNotification.methodChannel
        .invokeMethod<bool>('deleteChannel', channelId);
  }

  static Future<bool?> createChannel(FirebaseChannel channel) async {
    return FirebaseAlarmNotification.methodChannel
        .invokeMethod<bool>('createChannel', channel.toDynamic());
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

  static onNotification(Function(FirebaseMessage arguments) callback) {
    callbackNotification.add(callback);
  }

  static Future<dynamic> nativeMethodCallHandler(MethodCall methodCall) async {
    switch (methodCall.method) {
      case "onNotificationTapped":
        for (var cb in callbackNotificationTap) {
          cb(FirebaseMessage.fromJson(methodCall.arguments));
        }
        break;
      case "onNotification":
        for (var cb in callbackNotification) {
          cb(FirebaseMessage.fromJson(methodCall.arguments));
        }
        break;
      default:
        return "Nothing";
    }
  }

  static init() {
    methodChannel.setMethodCallHandler(nativeMethodCallHandler);
  }
}

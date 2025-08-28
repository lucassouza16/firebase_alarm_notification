import 'package:firebase_alarm_notification/firebase_alarm_notification_models.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'dart:io' show Platform;

class FirebaseAlarmNotification {
  FirebaseAlarmNotification() {
    if (isPlatformCompatible()) {
      _methodChannel.setMethodCallHandler(_nativeMethodCallHandler);
    }
  }

  final _methodChannel = const MethodChannel('firebase_alarm_notification');
  final List<Function(FirebaseMessage arguments)> _callbackNotificationTap = [];
  final List<Function(FirebaseMessage arguments)> _callbackNotification = [];

  static FirebaseAlarmNotification instance = FirebaseAlarmNotification();

  Future<T> invokeMethohWithDefaultValue<T>(String method, dynamic arguments, T defaultValue) async {
    T? retorno = await _methodChannel.invokeMethod<T>(method, arguments);

    if (retorno == null) {
      return defaultValue;
    } else {
      return retorno;
    }
  }

  bool isPlatformCompatible() {
    if (!kIsWeb && Platform.isAndroid) {
      return true;
    } else {
      debugPrint("firebase_alarm_notification is compatible with Android platform only");
      return false;
    }
  }

  Future<bool> setAlarm(String uri) async {
    if (!isPlatformCompatible()) {
      return false;
    }

    ByteData fileData = await rootBundle.load(uri);
    List<int> bytes = fileData.buffer.asUint8List();

    return invokeMethohWithDefaultValue<bool>('setAlarm', {'bytes': bytes, 'name': uri}, false);
  }

  Future<String?> get actualAlarm async {
    if (!isPlatformCompatible()) {
      return null;
    }

    return invokeMethohWithDefaultValue<String?>('actualAlarm', null, null);
  }

  Future<bool> removeAlarm() async {
    if (!isPlatformCompatible()) {
      return false;
    }

    return invokeMethohWithDefaultValue('removeAlarm', null, true);
  }

  Future<String?> get firebaseToken async {
    if (!isPlatformCompatible()) {
      return null;
    }

    return await _methodChannel.invokeMethod<String>('firebaseToken');
  }

  Future<void> deleteToken() async {
    if (!isPlatformCompatible()) {
      return;
    }

    return await _methodChannel.invokeMethod<void>('deleteToken');
  }

  Future<bool> channelExists(String channelId) async {
    if (!isPlatformCompatible()) {
      return false;
    }

    return invokeMethohWithDefaultValue<bool>('channelExists', channelId, true);
  }

  Future<bool> deleteChannel(String channelId) async {
    if (!isPlatformCompatible()) {
      return false;
    }

    return invokeMethohWithDefaultValue('deleteChannel', channelId, true);
  }

  Future<bool> createChannel(FirebaseChannel channel) async {
    if (!isPlatformCompatible()) {
      return false;
    }

    return invokeMethohWithDefaultValue('createChannel', channel.toJson(), true);
  }

  Future<bool> requestPermissions() async {
    if (!isPlatformCompatible()) {
      return false;
    }

    return invokeMethohWithDefaultValue('requestPermissions', null, true);
  }

  Future<FirebaseMessage?> get initialMessage async {
    if (!isPlatformCompatible()) {
      return null;
    }

    dynamic param = await _methodChannel.invokeMethod<dynamic>('initialMessage');

    if (param != null) {
      return FirebaseMessage.fromJson(param);
    } else {
      return null;
    }
  }

  onNotificationTap(Function(FirebaseMessage message) callback) {
    if (!isPlatformCompatible()) {
      return;
    }

    _callbackNotificationTap.add(callback);
  }

  onNotification(Function(FirebaseMessage message) callback) {
    if (!isPlatformCompatible()) {
      return;
    }

    _callbackNotification.add(callback);
  }

  Future<dynamic> _nativeMethodCallHandler(MethodCall methodCall) async {
    switch (methodCall.method) {
      case "onNotificationTapped":
        for (var cb in _callbackNotificationTap) {
          cb(FirebaseMessage.fromJson(methodCall.arguments));
        }
        break;
      case "onNotification":
        for (var cb in _callbackNotification) {
          cb(FirebaseMessage.fromJson(methodCall.arguments));
        }
        break;
      default:
        return "Nothing";
    }
  }
}

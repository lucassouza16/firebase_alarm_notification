// import 'package:flutter/foundation.dart';
import 'package:firebase_alarm_notification/models/firebasemessage.model.dart';
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
  final List<Function(FirebaseMessage arguments)> _callbackNotificationTap = const [];
  final List<Function(FirebaseMessage arguments)> _callbackNotification = const [];

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

  Future<String?> getToken() async {
    if (!isPlatformCompatible()) {
      return null;
    }

    return await _methodChannel.invokeMethod<String>('getToken');
  }

  Future<List<FirebaseAlarm>> listAlarms() async {
    if (!isPlatformCompatible()) {
      return [];
    }

    List<dynamic> alarms = await invokeMethohWithDefaultValue<List<dynamic>>('listAlarms', null, []);

    return FirebaseAlarm.fromJsonList(alarms);
  }

  Future<bool> addAlarm(FirebaseAlarm? alarm) async {
    if (!isPlatformCompatible()) {
      return false;
    }

    dynamic params;

    if (alarm != null) {
      await alarm.loadBytes();

      params = alarm.toJson();
    }

    return invokeMethohWithDefaultValue<bool>('addAlarm', params, false);
  }

  Future<bool> removeAlarm(String id) async {
    if (!isPlatformCompatible()) {
      return false;
    }

    return invokeMethohWithDefaultValue('removeAlarm', id, true);
  }

  Future<bool> setAlarmList(List<FirebaseAlarm>? alarms) async {
    if (!isPlatformCompatible()) {
      return false;
    }

    List<dynamic> param = [];

    if (alarms != null) {
      for (var alarm in alarms) {
        await alarm.loadBytes();

        param.add(alarm.toJson());
      }
    }

    return invokeMethohWithDefaultValue('setAlarmList', param, true);
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

  Future<FirebaseMessage?> getInitialMessage() async {
    if (!isPlatformCompatible()) {
      return null;
    }

    dynamic param = await _methodChannel.invokeMethod<dynamic>('getInitialMessage');

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

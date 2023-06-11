// import 'package:flutter/foundation.dart';
import 'package:firebase_alarm_notification/models/firebasemessage.model.dart';
import 'package:flutter/services.dart';

class FirebaseAlarmNotification {
  static const _methodChannel = MethodChannel('firebase_alarm_notification');
  static final List<Function(FirebaseMessage arguments)>
      _callbackNotificationTap = [];
  static final List<Function(FirebaseMessage arguments)> _callbackNotification =
      [];

  static Future<T> invokeMethohWithDefaultValue<T>(
      String method, dynamic arguments, T defaultValue) async {
    T? retorno = await FirebaseAlarmNotification._methodChannel
        .invokeMethod<T>(method, arguments);

    if (retorno == null) {
      return defaultValue;
    } else {
      return retorno;
    }
  }

  static Future<String?> getToken() {
    return FirebaseAlarmNotification._methodChannel
        .invokeMethod<String>('getToken');
  }

  static Future<List<FirebaseAlarm>> listAlarms() async {
    List<dynamic> alarms = await FirebaseAlarmNotification
        .invokeMethohWithDefaultValue<List<dynamic>>('listAlarms', null, []);

    return FirebaseAlarm.fromJsonList(alarms);
  }

  static Future<bool> addAlarm(FirebaseAlarm? alarm) async {
    dynamic params;

    if (alarm != null) {
      await alarm.loadBytes();

      params = alarm.toJson();
    }

    return FirebaseAlarmNotification.invokeMethohWithDefaultValue<bool>(
        'addAlarm', params, false);
  }

  static Future<bool> removeAlarm(String id) async {
    return FirebaseAlarmNotification.invokeMethohWithDefaultValue(
        'removeAlarm', id, true);
  }

  static Future<bool> setAlarmList(List<FirebaseAlarm>? alarms) async {
    List<dynamic> param = [];

    if (alarms != null) {
      for (var alarm in alarms) {
        await alarm.loadBytes();

        param.add(alarm.toJson());
      }
    }

    return FirebaseAlarmNotification.invokeMethohWithDefaultValue(
        'setAlarmList', param, true);
  }

  static Future<bool> channelExists(String channelId) async {
    return FirebaseAlarmNotification.invokeMethohWithDefaultValue<bool>(
        'channelExists', channelId, true);
  }

  static Future<bool> deleteChannel(String channelId) async {
    return FirebaseAlarmNotification.invokeMethohWithDefaultValue(
        'deleteChannel', channelId, true);
  }

  static Future<bool> createChannel(FirebaseChannel channel) async {
    return FirebaseAlarmNotification.invokeMethohWithDefaultValue(
        'createChannel', channel.toJson(), true);
  }

  static Future<FirebaseMessage?> getInitialMessage() async {
    dynamic param = await FirebaseAlarmNotification._methodChannel
        .invokeMethod<dynamic>('getInitialMessage');

    if (param != null) {
      return FirebaseMessage.fromJson(param);
    } else {
      return null;
    }
  }

  static onNotificationTap(Function(FirebaseMessage message) callback) {
    _callbackNotificationTap.add(callback);
  }

  static onNotification(Function(FirebaseMessage message) callback) {
    _callbackNotification.add(callback);
  }

  static Future<dynamic> _nativeMethodCallHandler(MethodCall methodCall) async {
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

  static init() {
    _methodChannel.setMethodCallHandler(_nativeMethodCallHandler);
  }
}

// ignore_for_file: constant_identifier_names

import 'package:flutter/services.dart';

abstract class FirebaseAlarm {
  final String uri;
  final bool primary;
  final String id;
  final String title;
  List<int>? bytes;

  FirebaseAlarm({
    required this.id,
    required this.title,
    required this.uri,
    this.primary = false,
    this.bytes = const [],
  });

  Future<List<int>> loadBytes() async {
    throw UnimplementedError("Not Implemented");
  }

  factory FirebaseAlarm.fromJson(dynamic json) {
    throw UnimplementedError("Not Implemented");
  }

  dynamic toJson() {
    throw UnimplementedError("Not Implemented");
  }

  static List<FirebaseAlarmAsset> fromJsonList(List<dynamic> list) {
    return list.map((e) => FirebaseAlarmAsset.fromJson(e)).toList();
  }
}

class FirebaseAlarmAsset extends FirebaseAlarm {
  FirebaseAlarmAsset({
    required super.id,
    required super.title,
    required super.uri,
    super.primary = false,
    super.bytes,
  });

  @override
  Future<List<int>> loadBytes() async {
    ByteData fileData = await rootBundle.load(uri);
    List<int> bytes = fileData.buffer.asUint8List();

    super.bytes = bytes;

    return bytes;
  }

  @override
  factory FirebaseAlarmAsset.fromJson(dynamic json) => FirebaseAlarmAsset(
        id: json['id'],
        title: json['title'],
        uri: json['uri'],
        primary: json['primary'],
        bytes: json['bytes'],
      );

  @override
  dynamic toJson() => {
        'id': id,
        'title': title,
        'uri': uri,
        'primary': primary,
        'bytes': bytes,
      };
}

class FirebaseChannel {
  String id;
  String name;
  String description;
  int importance;

  static const int IMPORTANCE_LOW = 2;
  static const int IMPORTANCE_HIGH = 4;
  static const int IMPORTANCE_MAX = 5;
  static const int IMPORTANCE_MIN = 1;
  static const int IMPORTANCE_NONE = 0;
  static const int IMPORTANCE_UNSPECIFIED = -1000;
  static const int INTERRUPTION_FILTER_ALARMS = 4;
  static const int INTERRUPTION_FILTER_ALL = 1;
  static const int INTERRUPTION_FILTER_NONE = 3;
  static const int INTERRUPTION_FILTER_PRIORITY = 2;
  static const int INTERRUPTION_FILTER_UNKNOWN = 0;

  FirebaseChannel({
    required this.id,
    required this.name,
    required this.description,
    required this.importance,
  });

  factory FirebaseChannel.fromJson(dynamic json) {
    return FirebaseChannel(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      importance: json['importance'],
    );
  }

  dynamic toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'importance': importance,
    };
  }
}

class FirebaseNotification {
  final String? tag;
  final String? title;
  final String? body;
  final String? channel;
  final String? alarm;

  const FirebaseNotification({
    required this.tag,
    required this.title,
    required this.body,
    required this.alarm,
    required this.channel,
  });

  factory FirebaseNotification.fromJson(dynamic json) {
    return FirebaseNotification(
      tag: json['tag'],
      title: json['title'],
      body: json['body'],
      alarm: json['alarm'],
      channel: json['channel'],
    );
  }
}

class FirebaseMessage {
  String id;
  Map data;
  FirebaseNotification? notification;

  FirebaseMessage({
    required this.id,
    required this.data,
    this.notification,
  });

  factory FirebaseMessage.fromJson(dynamic json) {
    return FirebaseMessage(
      id: json['id'],
      notification: (json['notification'] != null)
          ? FirebaseNotification.fromJson(json['notification'])
          : null,
      data: json['data'],
    );
  }
}

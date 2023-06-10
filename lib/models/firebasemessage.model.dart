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

  dynamic toDynamic() {
    return {
      'id': this.id,
      'name': this.name,
      'description': this.description,
      'importance': this.importance,
    };
  }
}

class FirebaseNotification {
  final String? tag;
  final String? title;
  final String? body;
  final String? channel;
  final bool alarm;

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

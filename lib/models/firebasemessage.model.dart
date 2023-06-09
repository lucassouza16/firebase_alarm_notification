class FirebaseNotification {
  final String? tag;
  final String? title;
  final String? body;
  final bool alarm;

  const FirebaseNotification({
    required this.tag,
    required this.title,
    required this.body,
    required this.alarm,
  });

  factory FirebaseNotification.fromJson(dynamic json) {
    return FirebaseNotification(
      tag: json['tag'],
      title: json['title'],
      body: json['body'],
      alarm: json['alarm'],
    );
  }
}

class FirebaseMessage {
  String id;
  FirebaseNotification? notification;
  Map data;

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

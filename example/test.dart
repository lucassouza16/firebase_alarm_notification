import 'package:firebase_alarm_notification/firebase_alarm_notification.dart';
import 'package:firebase_alarm_notification/models/firebasemessage.model.dart';
import 'package:flutter/cupertino.dart';

class Test {
  void init() async {
    FirebaseAlarmNotification.init();

    await FirebaseAlarmNotification.setAlarmList([
      FirebaseAlarmAsset(
        id: "a1",
        title: 'Alarme 10',
        uri: 'assets/songs/toque_notificacao_1.mp3',
        primary: false,
      ),
      FirebaseAlarmAsset(
        id: "a2",
        title: 'Alarme 11',
        uri: 'assets/songs/toque_notificacao_2.mp3',
        primary: true,
      ),
    ]);

    await FirebaseAlarmNotification.removeAlarm("alarm_1");

    (await FirebaseAlarmNotification.listAlarms()).forEach((element) {
      debugPrint(element.id);
    });

    FirebaseAlarmNotification.channelExists('teste3').then((value) {
      if (value) {
        debugPrint("Canal ja existe");
      } else {
        debugPrint("Canal criado");
        FirebaseAlarmNotification.createChannel(FirebaseChannel(
          id: 'teste3',
          name: 'Pedidos Recebidos',
          description: 'Pedidos Recebidos',
          importance: FirebaseChannel.IMPORTANCE_HIGH,
        ));
      }
    });

    FirebaseAlarmNotification.getToken().then((value) {
      debugPrint(value);
    });

    debugPrint(
        (await FirebaseAlarmNotification.deleteChannel("teste3")).toString());

    FirebaseAlarmNotification.getInitialMessage().then((message) {
      if (message != null) {
        debugPrint("Notificação inicial");
        debugPrint(message.id);
        debugPrint(message.notification!.title);
        debugPrint(message.notification!.body);
      }
    });

    FirebaseAlarmNotification.onNotificationTap((message) {
      debugPrint("Notificação aberta pelo usuário");
      debugPrint(message.id);
      debugPrint(message.notification!.title);
      debugPrint(message.notification!.body);
    });

    FirebaseAlarmNotification.onNotification((message) {
      debugPrint("Nova notificação, não foi exibida, tratar nesse callback");
      debugPrint(message.id);
      debugPrint(message.notification!.title);
      debugPrint(message.notification!.body);
    });
  }
}

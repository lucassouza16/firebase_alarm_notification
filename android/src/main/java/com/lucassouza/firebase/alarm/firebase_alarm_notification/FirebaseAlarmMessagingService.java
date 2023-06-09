package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;
import java.util.Map;

public class FirebaseAlarmMessagingService extends FirebaseMessagingService {

    String TAG = this.getClass().getSimpleName();
    private int getCurrentAppIcon () {

        PackageManager packageManager = this.getPackageManager();
        String packageName = this.getPackageName();

        int icon = 0;

        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            icon = applicationInfo.icon;
        } catch (Exception e) {
            // TODO: handle exception
        }

        return icon;
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent actionIntent = new Intent(this, FirebaseAlarmActionNotificationBroadcastReceiver.class);
        Intent dismissIntent = new Intent(this, FirebaseAlarmActionNotificationBroadcastReceiver.class);

        actionIntent.setAction(FirebaseAlarmNotificationUtil.INTENT_ACTION_TAP_NOTIFICATION);
        dismissIntent.setAction(FirebaseAlarmNotificationUtil.INTENT_ACTION_DISMISS_NOTIFICATION);

        Map<String, String> data = remoteMessage.getData();
        String jsonNotification = data.get("notification");

        if(jsonNotification != null) {
            String title = "";
            String body = "";
            String tag = "";
            boolean alarm = false;

            try {
                Map<String, Object> notif = new ObjectMapper().readValue(jsonNotification, new TypeReference<Map<String, Object>>(){});

                 title = (String) notif.get("title");
                 body = (String) notif.get("body");
                 tag = (String) notif.get("tag");

                 if(notif.get("alarm") != null) {
                    alarm = (boolean) notif.get("alarm");
                } else {
                     notif.put("alarm", alarm);
                 }

                 data.remove("notification");

                 HashMap<String, Object> message = new HashMap<>();

                 message.put("id", remoteMessage.getMessageId());
                 message.put("notification", notif);
                 message.put("data", data);

                actionIntent.putExtra("message", message);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            PendingIntent onActionPendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), FirebaseAlarmNotificationUtil.genUniqID(), actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent onActionDismissIntent = PendingIntent.getBroadcast(this.getApplicationContext(), FirebaseAlarmNotificationUtil.genUniqID(), dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            @SuppressLint("NotificationTrampoline")
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default_channel") // notification icon
                    .setSmallIcon(getCurrentAppIcon())
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setContentIntent(onActionPendingIntent)
                    .setDeleteIntent(onActionDismissIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(tag, 0, mBuilder.build());

            FirebaseAlarmSongPlayer.play(getApplicationContext());
        }
    }
}

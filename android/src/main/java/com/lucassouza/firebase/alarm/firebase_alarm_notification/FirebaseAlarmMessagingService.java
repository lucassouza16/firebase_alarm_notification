package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.AppUtil;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.NotificationUtil;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAlarmMessagingService extends FirebaseMessagingService {
    String TAG = FirebaseAlarmMessagingService.class.getSimpleName();

    private int getCurrentAppIcon() {

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

        Context context = getApplicationContext();

        Intent tapIntent = new Intent(this, FirebaseAlarmActionNotificationBroadcastReceiver.class);
        Intent dismissIntent = new Intent(this, FirebaseAlarmActionNotificationBroadcastReceiver.class);

        tapIntent.setAction(Constants.INTENT_ACTION_TAP_NOTIFICATION);
        dismissIntent.setAction(Constants.INTENT_ACTION_DISMISS_NOTIFICATION);

        Map<String, String> data = remoteMessage.getData();

        String jsonNotification = data.get("notification");
        data.remove("notification");

        HashMap<String, Object> message = new HashMap<>();

        message.put("id", remoteMessage.getMessageId());
        message.put("data", data);

        tapIntent.putExtra("message", message);

        tapIntent.putExtra("withAlarm", false);

        boolean isShowNotification = false;
        boolean isChannelEnabled = true;

        if (jsonNotification != null) {
            String title = null;
            String body = null;
            String tag = null;
            String channel = null;
            boolean foreground = false;
            boolean alarm = false;

            try {
                Map<String, Object> notification = new ObjectMapper().readValue(jsonNotification, Map.class);

                title = (String) notification.get("title");
                body = (String) notification.get("body");
                tag = (String) notification.get("tag");
                channel = (String) notification.get("channel");

                isChannelEnabled = NotificationUtil.checkIfNotificationChannelIsEnabled(context, channel);

                if (notification.get("alarm") != null) {
                    alarm = (boolean) notification.get("alarm");
                }

                if (notification.get("foreground") != null) {
                    foreground = (boolean) notification.get("foreground");
                } else {
                    notification.put("foreground", false);
                }

                message.put("notification", notification);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            boolean isInForeground = AppUtil.isAppForeground();

            isShowNotification = (foreground || !isInForeground) && isChannelEnabled;

            if (isShowNotification) {

                tapIntent.putExtra("withAlarm", alarm);

                PackageManager pm = context.getPackageManager();
                Intent newIntent = pm.getLaunchIntentForPackage(context.getPackageName());
                newIntent.putExtras(tapIntent);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent openActivityPendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        newIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                dismissIntent.putExtras(tapIntent.getExtras());
             //   PendingIntent onActionPendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), AppUtil.genUniqueID(), tapIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                PendingIntent onActionDismissIntent = PendingIntent.getBroadcast(this.getApplicationContext(), AppUtil.genUniqueID(), dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                @SuppressLint("NotificationTrampoline")
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel)
                        .setSmallIcon(getCurrentAppIcon())
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setContentIntent(openActivityPendingIntent)
                        .setDeleteIntent(onActionDismissIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mBuilder.setChannelId(channel);
                }

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(tag, 0, mBuilder.build());

                if (alarm) {
                    FirebaseAlarmNotificationPlayerService.play(context);
                }
            }
        }

        if (!isShowNotification) {
            Intent newIntent = new Intent();
            newIntent.putExtras(tapIntent.getExtras());
            newIntent.setAction(Constants.INTENT_ACTION_NEW_NOTIFICATION);
            context.sendBroadcast(newIntent);
        }
    }
}

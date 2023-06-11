package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationUtil {
    public static boolean checkIfNotificationChannelExists(Context context, String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            return notificationManager.getNotificationChannel(id) != null;
        }

        return false;
    }

    public static boolean deleteNotificationChannel(Context context, String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && checkIfNotificationChannelExists(context, id)) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.deleteNotificationChannel(id);
        }

        return true;
    }

    public static void createNotificationChannel(Context context, String id, String name, String description, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

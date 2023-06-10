package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
public class FirebaseAlarmNotificationUtil {
    public static final String INTENT_ACTION_TAP_NOTIFICATION = "firebase.alarm.action.tap_notification";
    public static final String INTENT_ACTION_DISMISS_NOTIFICATION = "firebase.alarm.action.dismiss_notification";

    public static String INTENT_ACTION_NEW_NOTIFICATION = "firebase.alarm.action.new_notification";
    public static final String SHARED_KEY_ALARM_ORIGINAL_NAME = "alarm_original_name";
    public static String ALARM_SONG_NAME = "alarm_firebase.mp3";
    public static int genUniqueID() {
        return ((int) SystemClock.uptimeMillis() % 99999999);
    }

    public static boolean removeFile(String path) {
        File file = new File(path);

        if(file.exists()) {
            file.delete();
        }

        return true;
    }
    public static Uri saveBytesToFile(Context context, byte[] bytes, String path) {
        FileOutputStream fos = null;
        File file = null;
        Uri uri = null;

        try {
            file = new File(path);

            if(file.exists()) {
                file.delete();
            }

            fos = new FileOutputStream(file);
            fos.write(bytes);

            uri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return uri;
    }
    public static String getSharedString(Context context, String key) {
        try {
            SharedPreferences settings = context.getSharedPreferences(
                    "GENERAL",
                    0
            );
            return settings.getString(key, null);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean removeSharedString(Context context, String key) {
        try {
            SharedPreferences settings = context.getSharedPreferences(
                    "GENERAL",
                    0
            );
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(key);

            editor.apply();

            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean saveSharedString(Context context, String key, String value) {
        try {
            SharedPreferences settings = context.getSharedPreferences(
                    "GENERAL",
                    0
            );
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);

            editor.apply();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void removeAlarm(Context context) {
        removeFile(context.getFilesDir() + "/" + ALARM_SONG_NAME);
        removeSharedString(context, SHARED_KEY_ALARM_ORIGINAL_NAME);
    }
    public static void saveAlarm(Context context, byte[] fileBytes, String pathAsset) {
        saveBytesToFile(context, fileBytes, context.getFilesDir() + "/" + ALARM_SONG_NAME);
        saveSharedString(context, SHARED_KEY_ALARM_ORIGINAL_NAME, pathAsset);
    }

    public static String getSongAssetAlarm(Context context) {
        return getSharedString(context, SHARED_KEY_ALARM_ORIGINAL_NAME);
    }

    public static void createNotificationChannel(Context context, String id, String name, String description, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

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

    public static boolean isAppForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return false;
            }
        }
        return true;
    }
}

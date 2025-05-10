package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import android.content.Context;

import com.lucassouza.firebase.alarm.firebase_alarm_notification.Constants;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.FirebaseAlarmNotificationPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AlarmUtil {
    public static void saveAlarm(Context context, byte[] bytes, String name) throws IOException {

        File file = new File(context.getFilesDir(), Constants.ALARM_FILE_URI);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();

        PreferencesUtil.getInstance(context).setString(Constants.ALARM_FILE_PREFERENCE_URI, name);
    }

    public static void removeAlarm(Context context) {

        File file = new File(context.getFilesDir(), Constants.ALARM_FILE_URI);

        if (file.exists()) {
            file.delete();

            PreferencesUtil.getInstance(context).removeString(Constants.ALARM_FILE_PREFERENCE_URI);
        }
    }

    public static File getAlarm(Context context) {
        File file = new File(context.getFilesDir(), Constants.ALARM_FILE_URI);

        if(file.exists()) {
            return file;
        }

        return null;
    }

    public static String getAlarmName(Context context) {
        return PreferencesUtil.getInstance(context).getString(Constants.ALARM_FILE_PREFERENCE_URI);
    }
}

package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import com.lucassouza.firebase.alarm.firebase_alarm_notification.Constants;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.FirebaseAlarmNotificationPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AlarmUtil {
    public static void saveAlarm(byte[] bytes, String name) throws IOException {

        File file = new File(FirebaseAlarmNotificationPlugin.getApplicationContext().getFilesDir(), Constants.ALARM_FILE_URI);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();

        PreferencesUtil.instance.setString(Constants.ALARM_FILE_PREFERENCE_URI, name);
    }

    public static void removeAlarm() {

        File file = new File(FirebaseAlarmNotificationPlugin.getApplicationContext().getFilesDir(), Constants.ALARM_FILE_URI);

        if (file.exists()) {
            file.delete();

            PreferencesUtil.instance.removeString(Constants.ALARM_FILE_PREFERENCE_URI);
        }
    }

    public static File getAlarm() {
        File file = new File(FirebaseAlarmNotificationPlugin.getApplicationContext().getFilesDir(), Constants.ALARM_FILE_URI);

        if(file.exists()) {
            return file;
        }

        return null;
    }

    public static String getAlarmName() {
        return PreferencesUtil.instance.getString(Constants.ALARM_FILE_PREFERENCE_URI);
    }
}

package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import com.lucassouza.firebase.alarm.firebase_alarm_notification.FirebaseAlarmNotificationPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AlarmUtil {

    public static String ALARM_FILE_URI = "firebase_alarm_notification_audio.mp3";
    public static void saveAlarm(byte[] bytes) throws IOException {

        File file = new File(FirebaseAlarmNotificationPlugin.getApplicationContext().getFilesDir(), ALARM_FILE_URI);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
    }

    public static void removeAlarm() {

        File file = new File(FirebaseAlarmNotificationPlugin.getApplicationContext().getFilesDir(), ALARM_FILE_URI);

        if (!file.exists()) {
            file.delete();
        }
    }

    public static File getAlarm() {
        File file = new File(FirebaseAlarmNotificationPlugin.getApplicationContext().getFilesDir(), ALARM_FILE_URI);

        if(file.exists()) {
            return file;
        }

        return null;
    }
}

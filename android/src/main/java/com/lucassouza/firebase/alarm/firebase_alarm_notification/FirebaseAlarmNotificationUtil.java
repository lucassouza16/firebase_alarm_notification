package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class FirebaseAlarmNotificationUtil {
    static final String INTENT_ACTION_TAP_NOTIFICATION = "tap_notification";
    static final String INTENT_ACTION_DISMISS_NOTIFICATION = "dismiss_notification";

    public static int genUniqID () {
        return ((int) SystemClock.uptimeMillis() % 99999999);
    }

    static public String getFileName (String dir) {
        String[] tokens = dir.split("[\\\\|/]");
        return tokens[tokens.length - 1];
    }
    public static Uri saveBytesToFile(Context context, byte[] bytes, String fileName) {
        FileOutputStream fos = null;
        File file = null;
        Uri uri = null;

        try {
            file = new File(context.getFilesDir(), fileName);
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
}

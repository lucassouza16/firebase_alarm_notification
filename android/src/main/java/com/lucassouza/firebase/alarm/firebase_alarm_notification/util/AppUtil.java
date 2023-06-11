package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.SystemClock;
public class AppUtil {
    public static int genUniqueID() {
        return ((int) SystemClock.uptimeMillis() % 99999999);
    }

    public static boolean isAppForeground(Context context) {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE);
    }

}

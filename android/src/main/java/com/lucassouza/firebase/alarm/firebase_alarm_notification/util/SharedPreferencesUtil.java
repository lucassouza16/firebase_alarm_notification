package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    public static String getSharedString(Context context, String key, String defValue) {
        try {
            SharedPreferences settings = context.getSharedPreferences(
                    "GENERAL",
                    0
            );
            return settings.getString(key, defValue);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSharedString(Context context, String key) {
        return getSharedString(context, key, null);
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
}

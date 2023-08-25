package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lucassouza.firebase.alarm.firebase_alarm_notification.Constants;

public class PreferencesUtil {
    public static void setString(Context context, String name, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(name, value);
        editor.apply();
    }

    public static String getString(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCE_KEY, Context.MODE_PRIVATE);

        return sharedPreferences.getString(name, null);
    }

    public static void removeString(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCE_KEY, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(name);
        editor.apply();
    }
}

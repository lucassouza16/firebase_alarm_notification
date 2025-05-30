package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lucassouza.firebase.alarm.firebase_alarm_notification.Constants;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.FirebaseAlarmNotificationPlugin;

public class PreferencesUtil {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static PreferencesUtil instance;
    private PreferencesUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCE_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void setString(String name, String value) {
        editor.putString(name, value);
        editor.apply();
    }

    public String getString(String name) {
        return sharedPreferences.getString(name, null);
    }

    public void removeString(String name) {
        editor.remove(name);
        editor.apply();
    }

    public static PreferencesUtil getInstance(Context context) {
        if(instance == null) {
            instance = new PreferencesUtil(context);
        }

        return instance;
    }
}

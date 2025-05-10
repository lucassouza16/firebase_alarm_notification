package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.AlarmUtil;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.NotificationUtil;

import java.io.IOException;
import java.util.Map;

public class FirebaseAlarmNotificationPluginMethods {
    private Context context;

    public FirebaseAlarmNotificationPluginMethods(Context context) {
        this.context = context;
    }

    public boolean setAlarm(Map<String, Object> params) {

        byte[] bytes =  (byte[]) params.get("bytes");
        String name = (String) params.get("name");

        try {
            AlarmUtil.saveAlarm(context, bytes, name);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean removeAlarm(Context context) {
        AlarmUtil.removeAlarm(context);

        return true;
    }

    public String actualAlarm(Context context) {
        return AlarmUtil.getAlarmName(context);
    }

    public Task<String> getToken() {
        return FirebaseMessaging.getInstance().getToken();
    }

    public boolean createChannel(Map<String, Object> arguments) {
        String id = (String) arguments.get("id");
        String name = (String) arguments.get("name");
        String description = (String) arguments.get("description");
        int importance = (int) arguments.get("importance");

        NotificationUtil.createNotificationChannel(context, id, name, description, importance);

        return true;
    }

    public boolean channelExists(String arguments) {
        return NotificationUtil.checkIfNotificationChannelExists(context, arguments);
    }

    public boolean deleteChannel(String arguments) {
        return NotificationUtil.deleteNotificationChannel(context, arguments);
    }
}

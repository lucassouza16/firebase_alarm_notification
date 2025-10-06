package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.models.AlarmModel;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.AlarmUtil;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.NotificationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseAlarmNotificationPluginMethods {
    private final Context context;

    public MediaPlayer player;

    public FirebaseAlarmNotificationPluginMethods(Context context) {
        this.context = context;
        this.player = new MediaPlayer();
    }

    public boolean stopAlarm () {
        player.stop();

        return true;
    }
    public boolean playAlarm(int id) {
        AlarmModel alarm = AlarmUtil.getAlarmById(id);

        if(alarm == null) return false;

        AssetFileDescriptor afd = alarm.getAssetFile(context);

        player.stop();
        player.reset();
        try {
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean setAlarm(int id) {
        return AlarmUtil.setCurrentAlarmId(context, id);
    }

    public Map<String, Object> actualAlarm(Context context) {
        return AlarmUtil.getCurrentAlarm(context).toMap();
    }

    public List<Map<String, Object>> allAlarms () {
        List<Map<String, Object>> assetsMap = new ArrayList<>();

        for (AlarmModel asset : AlarmUtil.assets){
            assetsMap.add(asset.toMap());
        }

        return assetsMap;
    }

    public Task<String> getToken() {
        return FirebaseMessaging.getInstance().getToken();
    }

    public Task<Void> deleteToken() {
        return FirebaseMessaging.getInstance().deleteToken();
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

package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.content.Context;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.storage.dao.AlarmDAO;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.storage.models.Alarm;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.NotificationUtil;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.QueryList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseAlarmNotificationPluginMethods {

    private Context context;
    private AlarmDAO alarmDAO;

    public FirebaseAlarmNotificationPluginMethods(Context context) {
        this.context = context;
        this.alarmDAO = new AlarmDAO(context);
    }

    public Task<String> getToken() {
        return FirebaseMessaging.getInstance().getToken();
    }

    public boolean addAlarm(HashMap<String, Object> arguments) {
        return alarmDAO.addFromHash(arguments);
    }

    public List<HashMap<String, Object>> listAlarms() {
        return new ObjectMapper().convertValue(alarmDAO.list(), new TypeReference<List<HashMap<String, Object>>>() {
        });
    }

    public boolean removeAlarm(String arguments) {
        Alarm alarm = alarmDAO.list().find(a -> a.getId().equals(arguments));

        if (alarm != null) {
            alarmDAO.remove(alarm);
        }

        return true;
    }

    public boolean saveAlarmList(List<HashMap<String, Object>> arguments) {
        alarmDAO.removeAll();
        QueryList<HashMap<String, Object>> args = new QueryList<>(arguments);

        if (args != null) {
            args.foreach(a -> {
                alarmDAO.addFromHash(a);
            });
        }

        return true;
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

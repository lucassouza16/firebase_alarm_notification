package com.lucassouza.firebase.alarm.firebase_alarm_notification.storage.dao;

import android.content.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.Constants;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.FileUtil;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.storage.models.Alarm;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.QueryList;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.SharedPreferencesUtil;
import java.io.File;
import java.util.HashMap;

public class AlarmDAO {

    private static QueryList<Alarm> items;

    private Context context;

    public AlarmDAO(Context context) {
        this.context = context;
    }

    public void save() {
        try {
            SharedPreferencesUtil.saveSharedString(context, Constants.SHARED_KEY_ALARM_LIST, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(items));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public QueryList<Alarm> list() {
        if (items == null) {
            try {
                items = new ObjectMapper().readValue(SharedPreferencesUtil.getSharedString(context, Constants.SHARED_KEY_ALARM_LIST, "[]"), new TypeReference<QueryList<Alarm>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return items;
    }

    public boolean remove(Alarm alarm) {
        list().remove(alarm);
        new File(alarm.getFile()).delete();
        save();

        return true;
    }

    public boolean removeAll () {
        list().foreach(a -> remove(a));

        return true;
    }

    public boolean add(Alarm alarm, byte[] bytes) {

        QueryList<Alarm> items = list();

        Alarm existent = items.find((item) -> item.getId().equals(alarm.getId()));
        Alarm primary = items.find((item) -> item.isPrimary());

        if (existent != null) {
            remove(existent);
        }

        if (primary != null && alarm.isPrimary()) {
            primary.setPrimary(false);
        }

        File directory = new File(context.getFilesDir(), Constants.ALARMS_FOLDER);
        if (!directory.exists()) {
            directory.mkdir();
        }

        File file = FileUtil.createNonExistsFile(directory, FileUtil.getExtensionFromNameFile(alarm.getUri()));
        FileUtil.saveBytesToFile(bytes, file);

        alarm.setFile(file.getAbsolutePath());

        list().add(alarm);
        save();

        return true;
    }

    public boolean addFromHash(HashMap<String, Object> hash) {
        String id = (String) hash.get("id");
        String uri = (String) hash.get("uri");
        String title = (String) hash.get("title");
        boolean primary = (boolean) hash.get("primary");
        byte[] fileBytes = (byte[]) hash.get("bytes");

        return add(new Alarm(id, title, uri, primary), fileBytes);
    };
}

package com.lucassouza.firebase.alarm.firebase_alarm_notification.storage.dao;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.Constants;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.storage.models.Alarm;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.QueryList;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.SharedPreferencesUtil;

public class AlarmDAO {
    private static QueryList<Alarm> items;
    public Context context;

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

    public void setItems(QueryList<Alarm> items) {
        AlarmDAO.items = items;

        save();
    }

    public QueryList<Alarm> getItems() {
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
        getItems().remove(alarm);
        alarm.getFile().delete();
        save();

        return true;
    }

    public boolean add(Alarm alarm) {

        QueryList<Alarm> items = getItems();

        Alarm existent = items.find((item) -> item.getId().equals(alarm.getId()));
        Alarm primary = items.find((item) -> item.isPrimary());

        if (existent != null) {
            remove(existent);
        }

        if (primary != null && alarm.isPrimary()) {
            primary.setPrimary(false);
        }

        getItems().add(alarm);
        save();

        return true;
    }
}

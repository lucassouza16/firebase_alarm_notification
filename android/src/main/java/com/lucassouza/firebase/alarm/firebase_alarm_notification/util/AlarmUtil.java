package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import android.content.Context;

import com.lucassouza.firebase.alarm.firebase_alarm_notification.Constants;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.R;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.models.AlarmModel;

import java.util.ArrayList;
import java.util.Arrays;

public class AlarmUtil {

    public static final ArrayList<AlarmModel> assets = new ArrayList<>(Arrays.asList(
            new AlarmModel("Suave", 0, R.raw.notificacao_1),
            new AlarmModel("Buzina", 1, R.raw.notificacao_2)
    ));

    public static AlarmModel getAlarmById(int id) {
        for (AlarmModel asset : assets) {
            if(asset.getId() == id) {
                return asset;
            }
        }

        return null;
    }

    public static AlarmModel getCurrentAlarm(Context context) {
        return getAlarmById(getCurrentAlarmId(context));
    }

    public static boolean setCurrentAlarmId(Context context, int id) {

        if (getAlarmById(id) == null) return false;

        PreferencesUtil.getInstance(context).setInt(Constants.ALARM_ASSET_CURRENT_ID, id);

        return true;
    }

    public static int getCurrentAlarmId(Context context) {
        return PreferencesUtil.getInstance(context).getInt(Constants.ALARM_ASSET_CURRENT_ID, 0);
    }
}

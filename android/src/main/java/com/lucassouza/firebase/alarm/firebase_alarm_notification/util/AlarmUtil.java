package com.lucassouza.firebase.alarm.firebase_alarm_notification.util;

import android.content.Context;

import com.lucassouza.firebase.alarm.firebase_alarm_notification.Constants;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.R;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.models.AssetModel;

import java.util.ArrayList;
import java.util.Arrays;

public class AlarmUtil {

    public static final ArrayList<AssetModel> assets = new ArrayList<>(Arrays.asList(
            new AssetModel("Suave", 0, R.raw.notificacao_1),
            new AssetModel("Buzina", 1, R.raw.notificacao_2)
    ));

    public static AssetModel getAssetById (int id) {
        for (AssetModel asset : assets) {
            if(asset.getId() == id) {
                return asset;
            }
        }

        return null;
    }

    public static AssetModel getCurrentAsset (Context context) {
        return getAssetById(getCurrentAssetId(context));
    }

    public static boolean setCurrentAssetId(Context context, int id) {

        if (getAssetById(id) == null) return false;

        PreferencesUtil.getInstance(context).setInt(Constants.ALARM_ASSET_CURRENT_ID, id);

        return true;
    }

    public static int getCurrentAssetId(Context context) {
        return PreferencesUtil.getInstance(context).getInt(Constants.ALARM_ASSET_CURRENT_ID, 0);
    }
}

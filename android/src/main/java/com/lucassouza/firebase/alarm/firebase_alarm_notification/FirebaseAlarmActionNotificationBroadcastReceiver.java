package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
public class FirebaseAlarmActionNotificationBroadcastReceiver extends BroadcastReceiver {
    String TAG = this.getClass().getSimpleName();
    void handleTapNotification(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        Intent newIntent = pm.getLaunchIntentForPackage(context.getPackageName());
        newIntent.putExtras(intent);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(newIntent);

        boolean dismissAlarm = intent.getBooleanExtra("withAlarm", false);

        if(dismissAlarm) {
            FirebaseAlarmNotificationPlayerService.stop();
        }
    }
    void handleDismissNotification(Context context, Intent intent) {
        boolean dismissAlarm = intent.getBooleanExtra("withAlarm", false);

        if(dismissAlarm) {
            FirebaseAlarmNotificationPlayerService.stop();
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Broadcast Received: "+intent.getAction());

        switch (intent.getAction()) {
            case Constants.INTENT_ACTION_TAP_NOTIFICATION:
                handleTapNotification(context, intent);
                break;
            case Constants.INTENT_ACTION_DISMISS_NOTIFICATION:
                handleDismissNotification(context, intent);
                break;
        }
    }
}

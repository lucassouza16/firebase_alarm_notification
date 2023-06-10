package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.Map;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.NewIntentListener;

/**
 * FirebaseAlarmNotificationPlugin
 */
public class FirebaseAlarmNotificationPlugin extends BroadcastReceiver
        implements
        MethodCallHandler,
        NewIntentListener,
        FlutterPlugin,
        ActivityAware,
        Application.ActivityLifecycleCallbacks {
    private String TAG = this.getClass().getSimpleName();
    private MethodChannel channel;
    private Activity currentActivity;

    FlutterPluginBinding binding;
    private Context context;
    private Map<String, Object> initialMessage;
    BroadcastReceiver updateReceiver;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "firebase_alarm_notification");
        channel.setMethodCallHandler(this);
        binding = flutterPluginBinding;
        context = flutterPluginBinding.getApplicationContext();
    }

    @Override
    public boolean onNewIntent(@NonNull Intent intent) {
        Log.d(TAG, "onNewIntent: Updated");

        Bundle extras = intent.getExtras();

        if (extras != null) {
            Map<String, Object> message = (Map<String, Object>) extras.get("message");

            if(message != null) {
                channel.invokeMethod("onNotificationTapped", message);
            }
        }

        currentActivity.setIntent(intent);
        return true;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        Log.d(TAG, "onAttachedToActivity: Updated");

        this.currentActivity = binding.getActivity();
        Application application = currentActivity.getApplication();

        binding.addOnNewIntentListener(this);
        application.registerActivityLifecycleCallbacks(this);

        Intent initialIntent = this.currentActivity.getIntent();

        if (initialIntent != null) {
            Map<String, Object> message = (Map<String, Object>) initialIntent.getExtras().get("message");

            if (message != null) {
                initialMessage = message;
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(FirebaseAlarmNotificationUtil.INTENT_ACTION_NEW_NOTIFICATION);
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Bundle extras = intent.getExtras();

                    if (extras != null) {
                        Map<String, Object> message = (Map<String, Object>) extras.get("message");

                        if(message != null) {
                            channel.invokeMethod("onNotification", message);
                        }
                    }
                }
            }
        };
        currentActivity.registerReceiver(updateReceiver, filter);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

        String method = call.method;

        if (method.equals("getToken")) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(t -> {
                        result.success(t.getResult());
                    });
        } else if (method.equals("getInitialMessage")) {
            result.success(initialMessage);
        } else if (method.equals("setSongAssetAlarm")) {
            Map<String, Object> arguments = call.arguments();
            if (arguments == null) {
                FirebaseAlarmNotificationUtil.removeAlarm(context);
            } else {
                byte[] fileBytes = (byte[]) arguments.get("bytes");
                String fileAsset = (String) arguments.get("name");

                FirebaseAlarmNotificationUtil.saveAlarm(context, fileBytes, fileAsset);
            }
            result.success(true);
        } else if (method.equals("getSongAssetAlarm")) {
            result.success(FirebaseAlarmNotificationUtil.getSongAssetAlarm(context));
        } else if (method.equals("createChannel")) {
            Map<String, Object> arguments = call.arguments();

            String id = (String) arguments.get("id");
            String name = (String) arguments.get("name");
            String description = (String) arguments.get("description");
            int importance = (int) arguments.get("importance");

            FirebaseAlarmNotificationUtil.createNotificationChannel(context, id, name, description, importance);
            result.success(true);
        } else if (method.equals("channelExists")) {
            String arguments = call.arguments();
            result.success(FirebaseAlarmNotificationUtil.checkIfNotificationChannelExists(context, arguments));
        } else if(method.equals("deleteChannel")){
            String arguments = call.arguments();
            result.success(FirebaseAlarmNotificationUtil.deleteNotificationChannel(context, arguments));
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        Application application = currentActivity.getApplication();
        application.unregisterActivityLifecycleCallbacks(this);
        currentActivity.unregisterReceiver(updateReceiver);
        currentActivity = null;
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        FirebaseAlarmNotificationAlarmService.stop();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}

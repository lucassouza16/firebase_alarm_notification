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
    private static Context context;
    private boolean initialMessageLoaded = false;
    private Map<String, Object> initialMessage;
    private BroadcastReceiver updateReceiver;
    private FirebaseAlarmNotificationPluginMethods methods;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        Log.d(TAG, "onAttachToEngine: Updated");

        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "firebase_alarm_notification");
        channel.setMethodCallHandler(this);
        binding = flutterPluginBinding;
        context = flutterPluginBinding.getApplicationContext();
        methods = new FirebaseAlarmNotificationPluginMethods(context);
    }

    @Override
    public boolean onNewIntent(@NonNull Intent intent) {
        Log.d(TAG, "onNewIntent: Updated");

        Bundle extras = intent.getExtras();
        Map<String, Object> message = null;

        if (extras != null) {
            message = (Map<String, Object>) extras.get("message");
        }

        if (initialMessageLoaded) {
            if (message != null) {
                channel.invokeMethod("onNotificationTapped", message);
            }
        } else {
            initialMessageLoaded = true;
            initialMessage = message;
        }

        currentActivity.setIntent(intent);
        return true;
    }

    public static Context getApplicationContext() {
        return context;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        Log.d(TAG, "onAttachedToActivity: Updated");

        this.currentActivity = binding.getActivity();
        Application application = currentActivity.getApplication();

        binding.addOnNewIntentListener(this);
        application.registerActivityLifecycleCallbacks(this);

        onNewIntent(currentActivity.getIntent());

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_ACTION_NEW_NOTIFICATION);
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Bundle extras = intent.getExtras();

                    if (extras != null) {
                        Map<String, Object> message = (Map<String, Object>) extras.get("message");

                        if (message != null) {
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

        switch (method) {
            case "setAlarm":
                result.success(methods.setAlarm(call.arguments()));
                break;
            case "removeAlarm":
                result.success(methods.removeAlarm());
                break;
            case "actualAlarm":
                result.success(methods.actualAlarm());
                break;
            case "initialMessage":
                result.success(initialMessage);
                break;
            case "firebaseToken":
                methods.getToken().addOnCompleteListener(task -> result.success(task.getResult()));
                break;
            case "createChannel":
                result.success(methods.createChannel(call.arguments()));
                break;
            case "channelExists":
                result.success(methods.channelExists(call.arguments()));
                break;
            case "deleteChannel":
                result.success(methods.deleteChannel(call.arguments()));
                break;
            default:
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
        FirebaseAlarmNotificationPlayerService.stop();
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

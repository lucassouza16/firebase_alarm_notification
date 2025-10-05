package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
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
        PluginRegistry.RequestPermissionsResultListener,
        EventChannel.StreamHandler,
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
    private Result resolvePremissionsResult;

    private EventChannel playerChannel;
    private EventChannel.EventSink playerSink;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        Log.d(TAG, "onAttachToEngine: Updated");

        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "firebase_alarm_notification");
        playerChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "firebase_alarm_notification/player");
        channel.setMethodCallHandler(this);
        playerChannel.setStreamHandler(this);
        binding = flutterPluginBinding;
        context = flutterPluginBinding.getApplicationContext();
        methods = new FirebaseAlarmNotificationPluginMethods(context);

        methods.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(playerSink != null) {
                    playerSink.success("STOP");
                }
            }
        });
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

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        Log.d(TAG, "onAttachedToActivity: Updated");

        this.currentActivity = binding.getActivity();
        Application application = currentActivity.getApplication();

        binding.addOnNewIntentListener(this);
        binding.addRequestPermissionsResultListener(this);
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
            case "stopAlarm":
                result.success(methods.stopAlarm());
                break;
            case "playAlarm":
                result.success(methods.playAlarm(call.arguments()));
                break;
            case "setAlarm":
                result.success(methods.setAlarm(call.arguments()));
                break;
            case "actualAlarm":
                result.success(methods.actualAlarm(context));
                break;
            case "allAlarms":
                result.success(methods.allAlarms());
                break;
            case "initialMessage":
                result.success(initialMessage);
                break;
            case "firebaseToken":
                methods.getToken().addOnCompleteListener(task -> result.success(task.getResult()));
                break;
            case "deleteToken":
                methods.deleteToken().addOnCompleteListener(task -> result.success(task.getResult()));
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
            case "requestPermissions":
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        result.success(true);
                    } /*else if (currentActivity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        resolvePremissionsResult = result;
                        ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, Constants.NOTIFICATION_PERMISSION_REQUEST_CODE);
                    }*/ else {
                        resolvePremissionsResult = result;
                        ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, Constants.NOTIFICATION_PERMISSION_REQUEST_CODE);
                    }
                }
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
    public boolean onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] grantResults) {

        if(i == Constants.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if(resolvePremissionsResult != null) {
                resolvePremissionsResult.success(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                resolvePremissionsResult = null;

                return true;
            }
        }

        return false;
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

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        playerSink = eventSink;
    }

    @Override
    public void onCancel(Object o) {
       playerSink = null;
    }
}

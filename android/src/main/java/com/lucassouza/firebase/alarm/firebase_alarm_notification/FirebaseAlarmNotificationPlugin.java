package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import androidx.annotation.NonNull;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.NewIntentListener;
import io.flutter.view.FlutterMain;

/** FirebaseAlarmNotificationPlugin */
public class FirebaseAlarmNotificationPlugin extends BroadcastReceiver
        implements
        MethodCallHandler,
        NewIntentListener,
        FlutterPlugin,
        ActivityAware {
  private String TAG = this.getClass().getSimpleName();
  private MethodChannel channel;
  private Activity mainActivity;

  FlutterPluginBinding binding;
  private Context context;
  private Map<String, Object> initialMessage;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "firebase_alarm_notification");
    channel.setMethodCallHandler(this);
    binding = flutterPluginBinding;
    context = flutterPluginBinding.getApplicationContext();
    FirebaseAlarmSongPlayer.stop();
  }

  @Override
  public boolean onNewIntent(@NonNull Intent intent) {
    Log.d(TAG, "onNewIntent: Updated");

    Bundle extras = intent.getExtras();

    if(extras != null) {
      Map<String, Object> message = (Map<String, Object>) extras.get("message");

      channel.invokeMethod("onNotificationTapped", message);
    }

    mainActivity.setIntent(intent);
    return true;
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    Log.d(TAG, "onAttachedToActivity: Updated");
    binding.addOnNewIntentListener(this);
    this.mainActivity = binding.getActivity();

    Intent initialIntent = this.mainActivity.getIntent();

    if(initialIntent != null) {
      Map<String, Object> message = (Map<String, Object>) initialIntent.getExtras().get("message");

      if(message != null) {
          initialMessage = message;
      }
    }
  }

  public static Uri saveBytesToFile(Context context, byte[] bytes, String fileName) {
    FileOutputStream fos = null;
    File file = null;
    Uri uri = null;

    try {
      file = new File(context.getFilesDir(), fileName);
      fos = new FileOutputStream(file);
      fos.write(bytes);

      uri = Uri.fromFile(file);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return uri;
  }



  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "getToken":
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(t -> {
                  result.success(t.getResult());
                });
        break;
      case "getInitialMessage":
        result.success(initialMessage);
        break;
      case "setSongAssetAlarm":

        Map<String, Object> params = call.arguments();

        String fileName = FirebaseAlarmNotificationUtil.getFileName((String) params.get("name"));
        FirebaseAlarmSongPlayer.setActualAlarm(context, fileName);
        FirebaseAlarmNotificationUtil.saveBytesToFile(context, (byte[]) params.get("bytes"), fileName);
        result.success("inputStream.toString()");
        break;
      default:
        result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
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
}

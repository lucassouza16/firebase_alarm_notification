package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import java.io.File;

public class FirebaseAlarmSongPlayer {

  static MediaPlayer mediaPlayer;
  static Vibrator mVibrate;
  private static String PREF_SONG_NAME = "SOM_BUZINA";

  public static void play(Context context) {

    File file = new File(context.getFilesDir(), getActualAlarm(context));

    stop();

    if(!file.exists()){
       return;
    }

    try {
      if (mVibrate == null) {
        mVibrate =
          (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
      }

      long pattern[] = { 0, 3000, 800, 3000, 800, 3000, 800, 3000, 800 };

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
          .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .setUsage(AudioAttributes.USAGE_ALARM)
          .build();
        mVibrate.vibrate(pattern, 0, audioAttributes);
      } else {
        mVibrate.vibrate(pattern, 0);
      }
    } catch (Exception e) {

    }

    try {

      if (mediaPlayer == null) {
        AudioManager audioManager = (AudioManager) context.getSystemService(
          Context.AUDIO_SERVICE
        );
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);

        mediaPlayer.setDataSource(context, Uri.parse("file://" + file.getAbsolutePath()));
        mediaPlayer.setLooping(true);
      }

      mediaPlayer.prepare();
      mediaPlayer.start();
    } catch (Exception e) {
      Log.d("Teste", e.getMessage());
    }
  }

  public static void stop() {
    try {
      if (mediaPlayer != null) {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
      }

      if (mVibrate != null) {
        mVibrate.cancel();
      }
    } catch (Exception e) {
    }
  }

  public static boolean setActualAlarm(Context context, String source) {
    try {
      SharedPreferences settings = context.getSharedPreferences(
        PREF_SONG_NAME,
        0
      );
      SharedPreferences.Editor editor = settings.edit();
      editor.putString("song", source);

      editor.apply();

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static String getActualAlarm(Context context) {
    try {
      SharedPreferences settings = context.getSharedPreferences(
        PREF_SONG_NAME,
        0
      );
      return settings.getString("song", null);
    } catch (Exception e) {
      return null;
    }
  }
}

package com.lucassouza.firebase.alarm.firebase_alarm_notification;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.AlarmUtil;

import java.io.File;

public class FirebaseAlarmNotificationPlayerService {
    private static MediaPlayer mediaPlayer;
    private static Vibrator mVibrate;
    private static String TAG = FirebaseAlarmNotificationPlayerService.class.getSimpleName();

    public static void play(Context context) {

        File file = AlarmUtil.getAlarm(context);

        if(file == null) return;

        stop();

        Log.d(TAG, "Alarm played");

        try {
            if (mVibrate == null) {
                mVibrate =
                        (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            }

            long pattern[] = {0, 3000, 800, 3000, 800, 3000, 800, 3000, 800};

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

                mediaPlayer.setDataSource(context, Uri.fromFile(file));
                mediaPlayer.setLooping(true);
            }

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void stop() {
        Log.d(TAG, "Alarm stopped");
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
}

package com.lucassouza.firebase.alarm.firebase_alarm_notification.storage.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.Constants;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.FirebaseAlarmNotificationPlugin;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.storage.dao.AlarmDAO;
import com.lucassouza.firebase.alarm.firebase_alarm_notification.util.FileUtil;

import java.io.File;
public class Alarm {
    private String id;
    private String title;
    private String uri;
    private File file;
    private boolean primary;

    public Alarm() {}

    @JsonCreator
    public Alarm(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("uri") String uri,
            @JsonProperty("primary") boolean primary,
            @JsonProperty("bytes") byte[] bytes,
            @JsonProperty("file") String fileDir
    ) {
        this.id = id;
        this.uri = uri;
        this.primary = primary;
        this.title = title;

        if(fileDir == null) {
            File directory = new File(FirebaseAlarmNotificationPlugin.getApplicationContext().getFilesDir(), Constants.ALARMS_FOLDER);
            if (!directory.exists()) {
                directory.mkdir();
            }

            file = FileUtil.createNonExistsFile(directory, FileUtil.getExtensionFromNameFile(uri));
            FileUtil.saveBytesToFile(bytes, file);
        } else {
            file = new File(fileDir);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}

package com.lucassouza.firebase.alarm.firebase_alarm_notification.storage.models;

public class Alarm {

    private String id;

    private String title;
    private String uri;
    private String file;
    private boolean primary;
    public Alarm() {}
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public boolean isPrimary() {
        return primary;
    }
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Alarm(String id, String title, String uri, boolean primary) {
        this.id = id;
        this.uri = uri;
        this.primary = primary;
        this.title = title;
    }
}

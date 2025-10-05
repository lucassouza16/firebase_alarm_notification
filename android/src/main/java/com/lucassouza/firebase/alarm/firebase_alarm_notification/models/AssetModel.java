package com.lucassouza.firebase.alarm.firebase_alarm_notification.models;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import java.util.HashMap;
import java.util.Map;

public class AssetModel {
    private String nome;
    private int id;
    private int asset;

    public AssetModel(String nome, int id, int asset) {
        this.nome = nome;
        this.id = id;
        this.asset = asset;
    }

    public int getAsset() {
        return asset;
    }

    public void setAsset(int asset) {
        this.asset = asset;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public AssetFileDescriptor getAssetFile(Context context) {
        return context.getResources().openRawResourceFd(asset);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("nome", nome);

        return map;
    }
}

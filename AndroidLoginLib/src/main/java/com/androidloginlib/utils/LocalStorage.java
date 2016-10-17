package com.androidloginlib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Created by asanthan on 9/22/16.
 */

public class LocalStorage {
    private SharedPreferences sharedPrefs;
    private static final Gson gson = new Gson();

    public LocalStorage(Context context) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void save(Object obj) {
        save(obj.getClass().getSimpleName(), obj);
    }

    public void save(String key, Object obj) {
        String json = gson.toJson(obj);
        sharedPrefs.edit().putString(key, json).commit();
    }

    public <T extends Object> T get(String key, Class<T> cls) {
        String json = sharedPrefs.getString(key, null);
        if(TextUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, cls);
    }

    public boolean delete(String key) {
        return sharedPrefs.edit().remove(key).commit();
    }
}

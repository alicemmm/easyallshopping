package com.lomoasia.easyallshopping.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.event.SettingEvent;

/**
 * Created by asia on 2018/1/19.
 */

public class Settings implements SharedPreferences.OnSharedPreferenceChangeListener {

    private volatile static Settings settings;

    private Context context;
    private SharedPreferences defaultSharedPreferences;

    private Settings() {
    }

    private Settings(Context context) {
        this.context = context;
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);

        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private static Settings getInstance(Context context) {
        if (settings == null) {
            synchronized (Settings.class) {
                context = context.getApplicationContext();
                if (settings == null) {
                    settings = new Settings(context);
                }
            }
        }
        return settings;
    }

    public static void initialize(Context context) {
        getInstance(context);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SettingEvent.sendSettingChanged(context, key);
    }

    @Override
    protected void finalize() {
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        try {
            super.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
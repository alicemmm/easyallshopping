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

    public static final String KEY_LANGUAGE = "language";

    public static final String KEY_HOME_PAGE_CHANGE = "home_page_change";

    public static final String KEY_TAO_KEY_MODEL = "tao_key_model";
    public static final String KEY_PAY_DONATE = "pay_donate";
    public static final String KEY_CLEAR_CACHE = "clear_cache";

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

    private boolean _isTaokeyModel() {
        return defaultSharedPreferences.getBoolean(KEY_TAO_KEY_MODEL, true);
    }

    public static boolean isTaokeyModel() {
        return settings._isTaokeyModel();
    }
}

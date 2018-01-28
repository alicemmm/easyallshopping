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

    public static final String KEY_SET_HOME_PAGE = "set_home_page";

    public static final String KEY_TAO_KEY_MODEL = "tao_key_model";
    public static final String KEY_WAKEUP_APP = "wake_up_app";
    public static final String KEY_CLEAR_CACHE = "clear_cache";

    public static final String KEY_CHECK_UPDATE = "check_update";
    public static final String KEY_FEED_BACK = "feedback";
    public static final String KEY_PAY_DONATE = "pay_donate";
    public static final String KEY_OPEN_SOURCE = "open_source";
    public static final String KEY_ABOUT_ABOUT = "about_about";

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

    private boolean _isWakeupApp() {
        return defaultSharedPreferences.getBoolean(KEY_WAKEUP_APP, false);
    }

    public static boolean isWakeupApp() {
        return settings._isWakeupApp();
    }
}

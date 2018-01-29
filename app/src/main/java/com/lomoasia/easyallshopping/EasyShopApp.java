package com.lomoasia.easyallshopping;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.lomoasia.easyallshopping.common.ClipboardUtil;
import com.lomoasia.easyallshopping.common.Settings;
import com.pgyersdk.crash.PgyCrashManager;

/**
 * Created by asia on 2018/1/12.
 */

public class EasyShopApp extends MultiDexApplication {
    private static final String TAG = EasyShopApp.class.getSimpleName();

    private static Context context;
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application = this;

        PgyCrashManager.register(getApplicationContext());

        ClipboardUtil.init(this);

        Settings.initialize(this);
    }

    public static Context getContext() {
        return context;
    }

    public static Application getApplication() {
        return application;
    }
}

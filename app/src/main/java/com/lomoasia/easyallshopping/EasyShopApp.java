package com.lomoasia.easyallshopping;

import android.app.Application;
import android.content.Context;

/**
 * Created by asia on 2018/1/12.
 */

public class EasyShopApp extends Application {
    private static final String TAG = EasyShopApp.class.getSimpleName();

    private static Context context;
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application = this;
    }

    public static Context getContext() {
        return context;
    }

    public static Application getApplication() {
        return application;
    }
}

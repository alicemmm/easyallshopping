package com.lomoasia.easyallshopping;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.avos.avoscloud.AVOSCloud;
import com.lomoasia.easyallshopping.common.ClipboardUtil;
import com.lomoasia.easyallshopping.common.Settings;

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

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, "0slgALnK8CiJjMmHBIcEXiOg-gzGzoHsz", "zJCtQvFwPLnLeOfICLM1yf7s");
        // TODO: 2018/1/29 正式版本remove
        AVOSCloud.setDebugLogEnabled(true);

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

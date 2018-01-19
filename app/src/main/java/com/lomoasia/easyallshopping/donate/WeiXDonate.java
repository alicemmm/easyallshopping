package com.lomoasia.easyallshopping.donate;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.lomoasia.easyallshopping.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by asia on 2018/1/19.
 */

public class WeiXDonate {
    private static final String TAG = WeiXDonate.class.getSimpleName();
    private static final String TENCENT_PACKAGE_NAME = "com.tencent.mm";
    private static final String TENCENT_ACTIVITY_BIZSHORTCUT = "com.tencent.mm.action.BIZSHORTCUT";
    private static final String TENCENT_EXTRA_ACTIVITY_BIZSHORTCUT = "LauncherUI.From.Scaner.Shortcut";

    private static void gotoWeChatQrScan(@NonNull Activity activity) {
        Intent intent = new Intent(TENCENT_ACTIVITY_BIZSHORTCUT);
        intent.setPackage(TENCENT_PACKAGE_NAME);
        intent.putExtra(TENCENT_EXTRA_ACTIVITY_BIZSHORTCUT, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.weixin_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    private static void sendPictureStoredBroadcast(Context context, String qrSavePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(qrSavePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    public static void saveDonateQrImage2SDCard(@NonNull String qrSavePath, @NonNull Bitmap qrBitmap) {
        File qrFile = new File(qrSavePath);
        File parentFile = qrFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(qrFile);
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void donateViaWeiXin(Activity activity, String qrSavePath) {
        if (activity == null || TextUtils.isEmpty(qrSavePath)) {
            return;
        }
        sendPictureStoredBroadcast(activity, qrSavePath);
        gotoWeChatQrScan(activity);
    }

    public static boolean hasInstalledWeiXinClient(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(TENCENT_PACKAGE_NAME, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getWeiXinClientVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(TENCENT_PACKAGE_NAME, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

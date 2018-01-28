package com.lomoasia.easyallshopping.common;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lomoasia.easyallshopping.activities.MainActivity;
import com.lomoasia.easyallshopping.activities.SettingActivity;
import com.lomoasia.easyallshopping.activities.WebsiteManagerActivity;

import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

/**
 * Created by asia on 2018/1/12.
 */

public class Launcher {

    public static void startMainActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void startSettingActivity(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    public static void startManagerActivity(Context context) {
        context.startActivity(new Intent(context, WebsiteManagerActivity.class));
    }

    public static void checkUpdate(final Context context) {
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateResponse) {
                if (updateStatus == UpdateStatus.Yes) {//版本有更新

                } else if (updateStatus == UpdateStatus.No) {
                    Toast.makeText(context, "版本无更新", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.EmptyField) {//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
                    Toast.makeText(context, "请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.IGNORED) {
                    Toast.makeText(context, "该版本已被忽略更新", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.ErrorSizeFormat) {
                    Toast.makeText(context, "请检查target_size填写的格式，请使用file.length()方法获取apk大小。", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.TimeOut) {
                    Toast.makeText(context, "查询出错或查询超时", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BmobUpdateAgent.update(context);
    }
}

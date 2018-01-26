package com.lomoasia.easyallshopping.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.just.agentweb.AgentWebConfig;
import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.activities.BaseActivity;
import com.lomoasia.easyallshopping.activities.SettingActivity;
import com.lomoasia.easyallshopping.common.Launcher;
import com.lomoasia.easyallshopping.common.Settings;
import com.lomoasia.easyallshopping.donate.AliDonate;
import com.lomoasia.easyallshopping.donate.WeiXDonate;

import java.io.File;
import java.io.InputStream;

/**
 * Created by asia on 2018/1/18.
 */

public class SettingsFragment extends PreferenceFragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        addPreferencesFromResource(R.xml.preferences);

        findPreference(Settings.KEY_SET_HOME_PAGE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Launcher.startManagerActivity(context);
                return true;
            }
        });

        findPreference(Settings.KEY_PAY_DONATE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDonateDialog();
                return true;
            }
        });

        findPreference(Settings.KEY_CLEAR_CACHE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AgentWebConfig.clearDiskCache(context);
                Toast.makeText(getActivity(), R.string.clear_cache_success, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void showDonateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.pay_donate)
                .setCancelable(false)
                .setMessage("message")
                .setNeutralButton(R.string.dialog_none, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.pay_donate_alipay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AliDonate.startAlipayClient(getActivity(), AliDonate.PAY_CODE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.pay_donate_wechat, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWechatDonateDialog();
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showWechatDonateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
                .setTitle("")
                .setMessage("")
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity activity = getActivity();
                        ((SettingActivity) activity).checkPermission(new BaseActivity.CheckPermListener() {
                            @Override
                            public void superPermission() {
                                InputStream weixinQrIs = getResources().openRawResource(R.raw.wcode);
                                String qrPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                                        File.separator + "easyshop" + File.separator +
                                        "cccrr_weixin.png";

                                WeiXDonate.saveDonateQrImage2SDCard(qrPath, BitmapFactory.decodeStream(weixinQrIs));
                                WeiXDonate.donateViaWeiXin(getActivity(), qrPath);
                            }
                        }, R.string.ask_again, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                })
                .show();
    }
}

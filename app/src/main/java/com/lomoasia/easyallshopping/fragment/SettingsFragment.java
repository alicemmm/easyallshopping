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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.just.agentweb.AgentWebConfig;
import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.activities.BaseActivity;
import com.lomoasia.easyallshopping.activities.SettingActivity;
import com.lomoasia.easyallshopping.common.CommonUtils;
import com.lomoasia.easyallshopping.common.Launcher;
import com.lomoasia.easyallshopping.common.Settings;
import com.lomoasia.easyallshopping.donate.AliDonate;
import com.lomoasia.easyallshopping.donate.WeiXDonate;
import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

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
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        findPreference(Settings.KEY_CHECK_UPDATE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                PgyUpdateManager.setIsForced(false);
                PgyUpdateManager.register(getActivity(), new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        Toast.makeText(context,R.string.check_update_none,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUpdateAvailable(String result) {
//                        Log.e(TAG, "onUpdateAvailable: "+result );
                    }
                });
                return true;
            }
        });

        findPreference(Settings.KEY_FEED_BACK).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Activity activity = getActivity();
                ((SettingActivity) activity).checkPermission(new BaseActivity.CheckPermListener() {
                    @Override
                    public void superPermission() {
                        PgyFeedback.getInstance().showDialog(context);
                    }
                }, R.string.ask_again, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return true;
            }
        });

//        findPreference(Settings.KEY_OPEN_SOURCE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                return true;
//            }
//        });

        findPreference(Settings.KEY_ABOUT_ABOUT).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showAboutDialog();
                return true;
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void showDonateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.pay_donate)
                .setCancelable(false)
                .setMessage(R.string.dialog_donate_message)
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
                .setTitle(R.string.weixin_tell_title)
                .setMessage(R.string.weixin_tell_message)
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

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.about)
                .setMessage(String.format(getString(R.string.about_message), CommonUtils.getCurrentVersions(context)))
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PgyUpdateManager.unregister();
    }
}

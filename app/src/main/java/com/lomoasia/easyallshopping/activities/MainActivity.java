package com.lomoasia.easyallshopping.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.just.agentweb.AgentWeb;
import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.common.Launcher;
import com.lomoasia.easyallshopping.common.SPUtils;
import com.lomoasia.easyallshopping.common.Settings;
import com.lomoasia.easyallshopping.common.TaoKeyTools;
import com.lomoasia.easyallshopping.common.bean.WebSite;
import com.lomoasia.easyallshopping.common.bean.WebSiteBean;
import com.lomoasia.easyallshopping.event.SettingEvent;
import com.lomoasia.easyallshopping.fragment.AgentWebFragment;
import com.lomoasia.easyallshopping.web.FragmentKeyDown;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Context context;

    private SettingEvent.Receiver settingEventReceiver;

    private TextView titleTextView;

    private AgentWebFragment agentWebFragment;

    private long lastBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("");
        titleTextView = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View content = drawer.getChildAt(0);
                View menu = drawerView;
                float scale = 1 - slideOffset;

                menu.setAlpha(0.6f + 0.4f * (1 - scale));
                content.setTranslationX(menu.getMeasuredWidth() * (1 - scale));
                content.setPivotX(0);
                content.setPivotY(content.getMeasuredHeight() / 2);
                content.invalidate();
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();

//        requestPermissions();

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTaokeyDialog();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    public void setToolbarTitle(String title) {
        titleTextView.setText(title);
    }

    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_layout_ll, agentWebFragment = AgentWebFragment.getInstance(), AgentWebFragment.class.getName());
        ft.commit();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (hasAllPermissionsGranted(grantResults)) {

        }
    }

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void initData() {
        Bmob.initialize(context, WebSite.BMOB_APPLICATION_ID);
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

        WebSiteBean targetWebSiteBean = SPUtils.getCurrentWebsite(context);
        if (targetWebSiteBean == null || TextUtils.isEmpty(targetWebSiteBean.getTitle())
                || TextUtils.isEmpty(targetWebSiteBean.getUrl())) {
            WebSiteBean webSiteBean = new WebSiteBean();
            webSiteBean.setTitle(getString(R.string.web_site_taobao));
            webSiteBean.setUrl(WebSite.M_TAO_BAO);

            SPUtils.setCurrentWebsite(context, webSiteBean);
        }

        settingEventReceiver = new SettingEvent.Receiver(context, new SettingEvent.Listener() {
            @Override
            public void onSettingChanged(Context context, String key) {
                if (Settings.KEY_HOME_PAGE_CHANGE.equals(key)) {
                    loadUrl();
                }
            }
        });

        settingEventReceiver.register();
    }

    private void showTaokeyDialog() {
        if (Settings.isTaokeyModel()) {
            WebSiteBean webSiteBean = TaoKeyTools.getClipboard(context);
            if (webSiteBean != null && !isFinishing()) {
                String title = webSiteBean.getTitle();
                final String url = webSiteBean.getUrl();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.taokey_dialog_title)
                        .setMessage(String.format(getString(R.string.taokey_dialog_message), title))
                        .setPositiveButton(R.string.dialog_open, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (agentWebFragment != null) {
                                    AgentWeb agentWeb = agentWebFragment.getAgentWeb();
                                    if (agentWeb != null) {
                                        agentWeb.getLoader().loadUrl(url);
                                    }
                                }
                                TaoKeyTools.clearTaokey();
                            }
                        }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TaoKeyTools.clearTaokey();
                    }
                }).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (settingEventReceiver != null) {
            settingEventReceiver.unregister();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (agentWebFragment != null) {
            agentWebFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AgentWebFragment agentWebFragment = this.agentWebFragment;
            if (agentWebFragment != null) {
                FragmentKeyDown fragmentKeyDown = agentWebFragment;
                if (fragmentKeyDown.onFragmentKeyDown(keyCode, event)) {
                    return true;
                } else {
                    return super.onKeyDown(keyCode, event);
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        doubleClickExit();
    }

    private void doubleClickExit() {
        long currentBackTime = System.currentTimeMillis();
        if (currentBackTime - lastBackTime > 2 * 1000) {
            lastBackTime = currentBackTime;
            Toast.makeText(this, R.string.press_back_key_quit, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_web_page_main) {
            loadUrl();
            return true;
        } else if (id == R.id.menu_refresh) {
            if (agentWebFragment != null) {
                AgentWeb agentWeb = agentWebFragment.getAgentWeb();
                if (agentWeb != null) {
                    agentWeb.getLoader().reload();
                }
            }
            return true;
        } else if (id == R.id.menu_setting) {
            Launcher.startSettingActivity(context);
            return true;
        } else if (id == R.id.menu_clear) {
            if (agentWebFragment != null) {
                agentWebFragment.toCleanWebCache();
            }
            return true;
        } else if (id == R.id.menu_share) {

            return true;
        } else if (id == R.id.menu_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadUrl() {
        if (agentWebFragment != null) {
            AgentWeb agentWeb = agentWebFragment.getAgentWeb();
            if (agentWeb != null) {
                WebSiteBean webSiteBean = SPUtils.getCurrentWebsite(context);
                if (webSiteBean != null && !TextUtils.isEmpty(webSiteBean.getUrl())) {
                    agentWeb.getLoader().loadUrl(webSiteBean.getUrl());
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(context, WebsiteManagerActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

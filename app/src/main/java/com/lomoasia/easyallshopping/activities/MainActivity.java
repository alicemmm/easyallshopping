package com.lomoasia.easyallshopping.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.just.agentweb.AgentWeb;
import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.common.FragmentKeyDown;
import com.lomoasia.easyallshopping.common.SPUtils;
import com.lomoasia.easyallshopping.common.WebSiteBean;
import com.lomoasia.easyallshopping.fragment.AgentWebFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Context context;

    private LinearLayout mainLinearLayout;
    private TextView titleTextView;

    private FragmentManager fragmentManager;
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

        initData();
    }

    public void setToolbarTitle(String title) {
        titleTextView.setText(title);
    }

    private void initView() {
        mainLinearLayout = findViewById(R.id.main_layout_ll);
        fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_layout_ll, agentWebFragment = AgentWebFragment.getInstance(), AgentWebFragment.class.getName());
        ft.commit();
    }

    private void initData() {
        String defaultUrl = (String) SPUtils.get(context, SPUtils.DEFAULT_URL_KEY, null);
        if (TextUtils.isEmpty(defaultUrl)) {
            SPUtils.put(context, SPUtils.DEFAULT_URL_KEY, WebSiteBean.M_TAO_BAO);
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
        AgentWebFragment agentWebFragment = this.agentWebFragment;
        if (agentWebFragment != null) {
            FragmentKeyDown fragmentKeyDown = agentWebFragment;
            if (fragmentKeyDown.onFragmentKeyDown(keyCode, event)) {
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            doubleClickExit();
        }
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
            AgentWeb agentWeb = agentWebFragment.getAgentWeb();
            if (agentWeb != null) {
                String url = (String) SPUtils.get(context, SPUtils.DEFAULT_URL_KEY, WebSiteBean.M_TAO_BAO);
                agentWeb.getLoader().loadUrl(url);
            }
            return true;
        } else if (id == R.id.menu_refresh) {
            AgentWeb agentWeb = agentWebFragment.getAgentWeb();
            if (agentWeb != null) {
                agentWeb.getLoader().reload();
            }
            return true;
        } else if (id == R.id.menu_open_with_browser) {
            return true;
        } else if (id == R.id.menu_setting) {
            return true;
        } else if (id == R.id.menu_share) {

            return true;
        } else if (id == R.id.menu_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            SPUtils.put(context, SPUtils.DEFAULT_URL_KEY, WebSiteBean.M_TAO_BAO);
        } else if (id == R.id.nav_gallery) {
            SPUtils.put(context, SPUtils.DEFAULT_URL_KEY, WebSiteBean.M_JING_DONG);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package com.lomoasia.easyallshopping.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.common.CommonUtils;
import com.lomoasia.easyallshopping.common.SPUtils;
import com.lomoasia.easyallshopping.common.Settings;
import com.lomoasia.easyallshopping.common.bean.WebSite;
import com.lomoasia.easyallshopping.common.bean.WebSiteBean;
import com.lomoasia.easyallshopping.event.SettingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by asia on 2018/1/23.
 */

public class WebsiteManagerActivity extends AppCompatActivity {
    private static final String TAG = WebsiteManagerActivity.class.getSimpleName();

    private Context context;
    private RecyclerView recyclerView;
    private WebsiteManagerAdapter websiteManagerAdapter;
    private List<WebSiteBean> webSiteBeanList;

    private WebSiteBean currentWebSiteBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website_manager);
        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(R.string.menu_main);

        currentWebSiteBean = SPUtils.getCurrentWebsite(context);

        recyclerView = findViewById(R.id.website_manager_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(CommonUtils.getDefaultItemDecoration(context));

        webSiteBeanList = getWebsiteDatas();
        websiteManagerAdapter = new WebsiteManagerAdapter(R.layout.list_item_website_manager_item,
                webSiteBeanList == null ? new ArrayList<WebSiteBean>() : webSiteBeanList);

        recyclerView.setAdapter(websiteManagerAdapter);
        websiteManagerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                WebSiteBean webSiteBean = (WebSiteBean) adapter.getItem(position);
                if (webSiteBean != null) {
                    currentWebSiteBean = webSiteBean;
                    SPUtils.setCurrentWebsite(context, webSiteBean);

                    recyclerView.setAdapter(websiteManagerAdapter);

                    SettingEvent.sendSettingChanged(context, Settings.KEY_HOME_PAGE_CHANGE);
                }
            }
        });
    }

    private List<WebSiteBean> getWebsiteDatas() {
        List<WebSiteBean> webSiteBeanList = SPUtils.getCurrentWebsiteList(context);
        if (webSiteBeanList == null || webSiteBeanList.size() <= 0) {
            List<WebSiteBean> webSiteBeans = new ArrayList<>();
            WebSiteBean webSiteBeanOne = new WebSiteBean();
            webSiteBeanOne.setTitle(getString(R.string.web_site_taobao));
            webSiteBeanOne.setUrl(WebSite.M_TAO_BAO);
            webSiteBeans.add(webSiteBeanOne);

            WebSiteBean webSiteBeanTwo = new WebSiteBean();
            webSiteBeanTwo.setTitle(getString(R.string.web_site_jingdong));
            webSiteBeanTwo.setUrl(WebSite.M_JING_DONG);
            webSiteBeans.add(webSiteBeanTwo);

            WebSiteBean webSiteBeanThree = new WebSiteBean();
            webSiteBeanThree.setTitle(getString(R.string.web_site_amazon));
            webSiteBeanThree.setUrl(WebSite.M_AMAZON);
            webSiteBeans.add(webSiteBeanThree);

            webSiteBeanList = webSiteBeans;

            SPUtils.setCurrentWebsiteList(context, webSiteBeans);
        }

        return webSiteBeanList;
    }

    private class WebsiteManagerAdapter extends BaseQuickAdapter<WebSiteBean, BaseViewHolder> {
        public WebsiteManagerAdapter(int layoutResId, @Nullable List<WebSiteBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final WebSiteBean webSiteBean) {
            helper.setText(R.id.website_name_tv, webSiteBean.getTitle());
            AppCompatImageView selectIv = helper.getView(R.id.website_select_iv);
            final RelativeLayout optionRl = helper.getView(R.id.option_rl);

            if (currentWebSiteBean != null) {
                String url = currentWebSiteBean.getUrl();
                if (!TextUtils.isEmpty(url) && url.equals(webSiteBean.getUrl())) {
                    selectIv.setVisibility(View.VISIBLE);
                } else {
                    selectIv.setVisibility(View.INVISIBLE);
                }
            }

            optionRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, optionRl);
                    popupMenu.getMenuInflater()
                            .inflate(R.menu.popup_menu_website_manager, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.action_delete) {
                                if (webSiteBeanList != null) {
                                    webSiteBeanList.remove(webSiteBean);
                                }

                                recyclerView.setAdapter(websiteManagerAdapter);

                                SPUtils.removeCurrentWebsiteList(context, webSiteBean);
                            }
                            return true;
                        }
                    });

                    popupMenu.show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.right_in, R.anim.stable);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stable, R.anim.right_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_website, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            doClickAddWebsite();
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doClickAddWebsite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.action_add_dialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_website, null);
        final AppCompatEditText websiteTitleEt = dialogView.findViewById(R.id.website_title_et);
        final AppCompatEditText websiteWebEt = dialogView.findViewById(R.id.website_web_et);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.dialog_ok, null);
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = websiteTitleEt.getText().toString();
                String website = websiteWebEt.getText().toString();

                Pattern pattern = Pattern.compile(WebSite.WEBSITE_PATTERN);
                Matcher matcher = pattern.matcher(website);

                if (!TextUtils.isEmpty(title) && matcher.matches()) {
                    saveWebsiteBean(title, website);

                    recyclerView.setAdapter(websiteManagerAdapter);
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void saveWebsiteBean(String title, String website) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(website)) {
            return;
        }

        WebSiteBean webSiteBean = new WebSiteBean();
        webSiteBean.setTitle(title);
        webSiteBean.setUrl(website);

        webSiteBeanList.add(webSiteBean);
        SPUtils.addCurrentWebsiteList(context, webSiteBean);
    }

}

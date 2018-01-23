package com.lomoasia.easyallshopping.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.reflect.TypeToken;
import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.common.JsonUtils;
import com.lomoasia.easyallshopping.common.SPUtils;
import com.lomoasia.easyallshopping.common.Settings;
import com.lomoasia.easyallshopping.common.bean.WebSite;
import com.lomoasia.easyallshopping.common.bean.WebSiteBean;
import com.lomoasia.easyallshopping.event.SettingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asia on 2018/1/23.
 */

public class WebsiteManagerActivity extends AppCompatActivity {
    private static final String TAG = WebsiteManagerActivity.class.getSimpleName();

    private Context context;
    private RecyclerView recyclerView;
    private WebsiteManagerAdapter websiteManagerAdapter;

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

        String targetJson = (String) SPUtils.get(context, SPUtils.DEFAULT_URL_KEY, "");
        if (!TextUtils.isEmpty(targetJson)) {
            currentWebSiteBean = JsonUtils.objectFromJson(targetJson, WebSiteBean.class);
        }

        recyclerView = findViewById(R.id.website_manager_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<WebSiteBean> webSiteBeanList = getWebsiteDatas();
        websiteManagerAdapter = new WebsiteManagerAdapter(R.layout.list_item_website_manager_item,
                webSiteBeanList == null ? new ArrayList<WebSiteBean>() : webSiteBeanList);

        recyclerView.setAdapter(websiteManagerAdapter);
        websiteManagerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                WebSiteBean webSiteBean = (WebSiteBean) adapter.getItem(position);
                if (webSiteBean != null) {
                    currentWebSiteBean = webSiteBean;
                    SPUtils.put(context, SPUtils.DEFAULT_URL_KEY, JsonUtils.objectToJson(webSiteBean));
                    recyclerView.setAdapter(websiteManagerAdapter);

                    SettingEvent.sendSettingChanged(context, Settings.KEY_HOME_PAGE_CHANGE);
                }
            }
        });

    }

    private List<WebSiteBean> getWebsiteDatas() {
        String urlListJson = (String) SPUtils.get(context, SPUtils.DEFAULT_URL_LIST_KEY, "");
        if (TextUtils.isEmpty(urlListJson)) {
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

            String json = JsonUtils.objectToJson(webSiteBeans);
            SPUtils.put(context, SPUtils.DEFAULT_URL_LIST_KEY, json);

            return webSiteBeans;
        }

        return JsonUtils.objectFromJson(urlListJson, new TypeToken<List<WebSiteBean>>() {
        }.getType());
    }

    private class WebsiteManagerAdapter extends BaseQuickAdapter<WebSiteBean, BaseViewHolder> {
        public WebsiteManagerAdapter(int layoutResId, @Nullable List<WebSiteBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, WebSiteBean item) {
            helper.setText(R.id.website_name_tv, item.getTitle());
            AppCompatImageView selectIv = helper.getView(R.id.website_select_iv);
            if (currentWebSiteBean != null) {
                String url = currentWebSiteBean.getUrl();
                if (!TextUtils.isEmpty(url) && url.equals(item.getUrl())) {
                    selectIv.setVisibility(View.VISIBLE);
                } else {
                    selectIv.setVisibility(View.INVISIBLE);
                }
            }
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

    }

}

package com.lomoasia.easyallshopping.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebSettings;
import com.just.agentweb.AgentWebUIController;
import com.just.agentweb.ChromeClientCallbackManager;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.PermissionInterceptor;
import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.activities.MainActivity;
import com.lomoasia.easyallshopping.common.JsonUtils;
import com.lomoasia.easyallshopping.common.SPUtils;
import com.lomoasia.easyallshopping.common.bean.WebSite;
import com.lomoasia.easyallshopping.common.bean.WebSiteBean;
import com.lomoasia.easyallshopping.web.CustomSettings;
import com.lomoasia.easyallshopping.web.FragmentKeyDown;
import com.lomoasia.easyallshopping.web.UIController;

import java.util.HashMap;

/**
 * Created by asia on 2018/1/11.
 */

public class AgentWebFragment extends BaseFragment implements FragmentKeyDown {
    private static final String TAG = AgentWebFragment.class.getSimpleName();

    protected AgentWeb agentWeb;
    private WebView webView;

    public static AgentWebFragment getInstance() {
        return new AgentWebFragment();
    }

    public AgentWebFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aegent_web, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        agentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) view,
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .setIndicatorColorWithHeight(getResources().getColor(R.color.colorAccent), 2)
                .setAgentWebWebSettings(getSettings())
                .setWebViewClient(webViewClient)
                .setWebChromeClient(webChromeClient)
                .setPermissionInterceptor(permissionInterceptor)
                .setReceivedTitleCallback(callback)
                .setSecurityType(AgentWeb.SecurityType.strict)
                .setAgentWebUIController(new UIController(getActivity()))
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .openParallelDownload()
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .interceptUnkownScheme()
                .createAgentWeb()
                .ready()
                .go(getUrl());

        agentWeb.getWebCreator().get().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webView = agentWeb.getWebCreator().get();

    }


    @Override
    public void onViewFirstAppear() {
        super.onViewFirstAppear();
        Log.e(TAG, "onViewFirstAppear: ");
    }

    @Override
    public void onViewAppear() {
        super.onViewAppear();
        Log.e(TAG, "onViewAppear: ");
    }

    public AgentWeb getAgentWeb() {
        return agentWeb;
    }

    protected PermissionInterceptor permissionInterceptor = new PermissionInterceptor() {
        @Override
        public boolean intercept(String url, String[] permissions, String action) {
            return false;
        }
    };

    private AgentWebSettings getSettings() {
        return new CustomSettings();
    }

    private String getUrl() {
        String url = WebSite.M_TAO_BAO;
        String targetJson = (String) SPUtils.get(context, SPUtils.DEFAULT_URL_KEY, "");
        if (!TextUtils.isEmpty(targetJson)) {
            WebSiteBean webSiteBean = JsonUtils.objectFromJson(targetJson, WebSiteBean.class);
            if (webSiteBean != null && !TextUtils.isEmpty(webSiteBean.getUrl())) {
                url = webSiteBean.getUrl();
            }
        }

        return url;
    }

    protected ChromeClientCallbackManager.ReceivedTitleCallback callback = new ChromeClientCallbackManager.ReceivedTitleCallback() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            Activity activity = getActivity();
            if (activity != null && activity instanceof MainActivity && !TextUtils.isEmpty(title)) {
                ((MainActivity) activity).setToolbarTitle(title);
            }
        }
    };
    protected WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //  super.onProgressChanged(view, newProgress);
            Log.i(TAG, "onProgressChanged:" + newProgress + "  view:" + view);
        }
    };

    protected WebViewClient webViewClient = new WebViewClient() {

        private HashMap<String, Long> timer = new HashMap<>();

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        //
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {

            Log.i(TAG, "mWebViewClient shouldOverrideUrlLoading:" + url);
            //intent:// scheme的处理 如果返回false ， 则交给 DefaultWebClient 处理 ， 默认会打开该Activity  ， 如果Activity不存在则跳到应用市场上去.  true 表示拦截
            //例如优酷视频播放 ，intent://play?...package=com.youku.phone;end;
            //优酷想唤起自己应用播放该视频 ， 下面拦截地址返回 true  则会在应用内 H5 播放 ，禁止优酷唤起播放该视频， 如果返回 false ， DefaultWebClient  会根据intent 协议处理 该地址 ， 首先匹配该应用存不存在 ，如果存在 ， 唤起该应用播放 ， 如果不存在 ， 则跳到应用市场下载该应用 .
            if (url.startsWith("intent://") && url.contains("com.youku.phone")) {
                return true;
            }
            /*else if (isAlipay(view, url))   //1.2.5开始不用调用该方法了 ，只要引入支付宝sdk即可 ， DefaultWebClient 默认会处理相应url调起支付宝
                return true;*/


            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i(TAG, "url:" + url + " onPageStarted  target:" + getUrl());
            timer.put(url, System.currentTimeMillis());

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (timer.get(url) != null) {
                long overTime = System.currentTimeMillis();
                Long startTime = timer.get(url);
                Log.i(TAG, "  page url:" + url + "  used time:" + (overTime - startTime));
            }

        }

        /*错误页回调该方法 ， 如果重写了该方法， 上面传入了布局将不会显示 ， 交由开发者实现，注意参数对齐。*/
        public void onMainFrameError(AgentWebUIController agentWebUIController, WebView view, int errorCode, String description, String failingUrl) {
            Log.i(TAG, "AgentWebFragment onMainFrameError");
            agentWebUIController.onMainFrameError(view, errorCode, description, failingUrl);

        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);

            Log.i(TAG, "onReceivedHttpError:" + 3 + "  request:" + request + "  errorResponse:" + errorResponse);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            Log.i(TAG, "onReceivedError:" + errorCode + "  description:" + description + "  errorResponse:" + failingUrl);
        }
    };

    private void loadErrorWebSite() {
        if (agentWeb != null) {
            agentWeb.getLoader().loadUrl("http://www.unkownwebsiteblog.me");
        }
    }

    public void toCleanWebCache() {
        if (agentWeb != null) {
            //清理所有跟WebView相关的缓存 ，数据库， 历史记录 等。
            agentWeb.clearWebCache();
            Toast.makeText(getActivity(), R.string.clear_cache_success, Toast.LENGTH_SHORT).show();
            //清空所有 AgentWeb 硬盘缓存，包括 WebView 的缓存 , AgentWeb 下载的图片 ，视频 ，apk 等文件。
//            AgentWebConfig.clearDiskCache(this.getContext());
        }
    }

    @Override
    public void onResume() {
        if (agentWeb != null) {
            agentWeb.getWebLifeCycle().onResume();
        }
        super.onResume();


    }

    @Override
    public void onPause() {
        if (agentWeb != null) {
            agentWeb.getWebLifeCycle().onPause();
        }
        super.onPause();
    }

    @Override
    public boolean onFragmentKeyDown(int keyCode, KeyEvent event) {
        if (agentWeb != null) {
            return agentWeb.handleKeyEvent(keyCode, event);
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        if (agentWeb != null) {
            agentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroyView();
    }
}

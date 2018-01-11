package com.lomoasia.easyallshopping.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.just.agentweb.WebDefaultSettingsManager;
import com.lomoasia.easyallshopping.MainActivity;
import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.common.FragmentKeyDown;
import com.lomoasia.easyallshopping.common.UIController;

import java.util.HashMap;

/**
 * Created by asia on 2018/1/11.
 */

public class AgentWebFragment extends Fragment implements FragmentKeyDown {
    private static final String TAG = AgentWebFragment.class.getSimpleName();

    public static final String URL_KEY = "url_key";

    protected AgentWeb agentWeb;
    private WebView webView;

    private String url;

    public static AgentWebFragment getInstance(Bundle bundle) {
        AgentWebFragment agentWebFragment = new AgentWebFragment();
        if (bundle != null) {
            agentWebFragment.setArguments(bundle);
        }

        return agentWebFragment;

    }

    public AgentWebFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            url = bundle.getString(URL_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aegent_web, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        agentWeb = AgentWeb.with(this)//
                .setAgentWebParent((LinearLayout) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//传入AgentWeb的父控件。
                .setIndicatorColorWithHeight(-1, 3)//设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setAgentWebWebSettings(getSettings())//设置 AgentWebSettings。
                .setWebViewClient(webViewClient)//WebViewClient ， 与 WebView 使用一致 ，但是请勿获取WebView调用setWebViewClient(xx)方法了,会覆盖AgentWeb DefaultWebClient,同时相应的中间件也会失效。
                .setWebChromeClient(webChromeClient) //WebChromeClient
                .setPermissionInterceptor(permissionInterceptor) //权限拦截 2.0.0 加入。
                .setReceivedTitleCallback(callback)//标题回调。
                .setSecurityType(AgentWeb.SecurityType.strict) //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
                .setAgentWebUIController(new UIController(getActivity())) //自定义UI  AgentWeb3.0.0 加入。
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1) //参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
                .openParallelDownload()//打开并行下载 , 默认串行下载。
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
                .interceptUnkownScheme() //拦截找不到相关页面的Scheme AgentWeb 3.0.0 加入。
                .createAgentWeb()//创建AgentWeb。
                .ready()//设置 WebSettings。
                .go(getUrl()); //WebView载入该url地址的页面并显示。

        //AgentWeb 没有把WebView的功能全面覆盖 ，所有某些设置 AgentWeb 没有提供 ， 请从WebView方面入手设置。
        agentWeb.getWebCreator().get().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webView = agentWeb.getWebCreator().get(); // 获取WebView .

    }

    protected PermissionInterceptor permissionInterceptor = new PermissionInterceptor() {

        //AgentWeb 在触发某些敏感的 Action 时候会回调该方法， 比如定位触发 。
        //例如 https//:www.baidu.com 该 Url 需要定位权限， 返回false ，如果版本大于等于23 ， agentWeb 会动态申请权限 ，true 该Url对应页面请求定位失败。
        //该方法是每次都会优先触发的 ， 开发者可以做一些敏感权限拦截 。
        @Override
        public boolean intercept(String url, String[] permissions, String action) {
            return false;
        }
    };


    public AgentWebSettings getSettings() {
        return WebDefaultSettingsManager.getInstance();
    }

    /**
     * 页面空白，请检查scheme是否加上， scheme://host:port/path?query 。
     *
     * @return url
     */
    public String getUrl() {
        String target = url;

        if (TextUtils.isEmpty(target)) {
            target = "https://m.jd.com/";
        }
        return target;
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

        //2972 1483|3005 1536|2868 1785| 2889 1523| 2912 1537|2941 1628|2925 1561|2864 1669|2953 1508|2932 1693|
        //2926.1 1592.3

        //2731 1749|1234 1808|2203 1230|1648 1752|
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

    //这里用于测试错误页的显示
    private void loadErrorWebSite() {
        if (agentWeb != null) {
            agentWeb.getLoader().loadUrl("http://www.unkownwebsiteblog.me");
        }
    }

    //清除 WebView 缓存
    private void toCleanWebCache() {

        if (this.agentWeb != null) {

            //清理所有跟WebView相关的缓存 ，数据库， 历史记录 等。
            this.agentWeb.clearWebCache();
            Toast.makeText(getActivity(), "已清理缓存", Toast.LENGTH_SHORT).show();
            //清空所有 AgentWeb 硬盘缓存，包括 WebView 的缓存 , AgentWeb 下载的图片 ，视频 ，apk 等文件。
//            AgentWebConfig.clearDiskCache(this.getContext());
        }

    }

    @Override
    public void onResume() {
        if (agentWeb != null) {
            agentWeb.getWebLifeCycle().onResume();//恢复
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        agentWeb.getWebLifeCycle().onPause(); //暂停应用内所有WebView ， 调用mWebView.resumeTimers(); 恢复。
        super.onPause();
    }

    @Override
    public boolean onFragmentKeyDown(int keyCode, KeyEvent event) {
        return agentWeb.handleKeyEvent(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        agentWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }
}

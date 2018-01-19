package com.lomoasia.easyallshopping.common;

import android.content.Context;
import android.text.TextUtils;

import com.lomoasia.easyallshopping.common.bean.WebSiteBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by asia on 2018/1/18.
 */

public class TaoKeyTools {
    private static final String TAG = TaoKeyTools.class.getSimpleName();
//    【未来简史（中文版） 赠未来简史和人类简史伴读有声书  人工智能 尤瓦尔 赫拉利 著  罗辑思维罗振宇推荐】
//    http://www.dwntme.com/h.ZZvEWF4 点击链接，再选择浏览器打开；或复制这条信息￥hQt90lhvbpE￥后打开👉手淘👈

    private static final String URL_PATTERN = "(http://.*)\\s+";

    private static WebSiteBean getWebSiteBeanFromTaoKey(String taoKey) {
        if (TextUtils.isEmpty(taoKey)) {
            return null;
        }

        WebSiteBean webSiteBean = new WebSiteBean();

        int startTitleIndex = taoKey.indexOf("【");
        int endTitleIndex = taoKey.indexOf("】");

        if (startTitleIndex >= 0 && endTitleIndex > startTitleIndex + 1 && endTitleIndex < taoKey.length()) {
            String title = taoKey.substring(startTitleIndex + 1, endTitleIndex);
            webSiteBean.setTitle(title);
        }

        Pattern pattern = Pattern.compile(URL_PATTERN);
        Matcher matcher = pattern.matcher(taoKey);
        if (matcher.find()) {
            String url = matcher.group();
            if (!TextUtils.isEmpty(url)) {
                webSiteBean.setUrl(url.trim());
            }
        }

        int startKeyIndex = taoKey.indexOf("￥");
        int endKeyIndex = taoKey.lastIndexOf("￥");

        if (startKeyIndex >= 0 && endKeyIndex > startKeyIndex && endKeyIndex + 1 < taoKey.length()) {
            String key = taoKey.substring(startKeyIndex, endKeyIndex + 1);
            webSiteBean.setTaoKey(key);
        }

        return webSiteBean;
    }

    private static boolean isTaoKey(WebSiteBean webSiteBean) {
        boolean isTaoKey = false;
        if (webSiteBean != null) {
            String title = webSiteBean.getTitle();
            String url = webSiteBean.getUrl();
            String key = webSiteBean.getTaoKey();
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(url) && !TextUtils.isEmpty(key)) {
                isTaoKey = true;
            }
        }

        return isTaoKey;
    }

    public static WebSiteBean getClipboard(Context context) {
        WebSiteBean webSiteBean = null;
        String clipText = ClipboardUtil.getInstance().getClipText(context);
        if (!TextUtils.isEmpty(clipText)) {
            webSiteBean = getWebSiteBeanFromTaoKey(clipText);
            if (!isTaoKey(webSiteBean)) {
                webSiteBean = null;
            }
        }

        return webSiteBean;
    }

    public static void clearTaokey() {
        ClipboardUtil.getInstance().copyText("", "");
    }
}

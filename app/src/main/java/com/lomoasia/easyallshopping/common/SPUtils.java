package com.lomoasia.easyallshopping.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.lomoasia.easyallshopping.common.bean.WebSiteBean;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by asia on 2018/1/12.
 */

public class SPUtils {
    private static final String FILE_NAME = "easy_shop_share_data";

    public static final String DEFAULT_URL_KEY = "default_url_key";
    public static final String DEFAULT_URL_LIST_KEY = "default_url_list_key";

    private static void put(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        editor.apply();
    }

    private static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    public static WebSiteBean getCurrentWebsite(Context context) {
        WebSiteBean webSiteBean = null;
        String targetJson = (String) SPUtils.get(context, SPUtils.DEFAULT_URL_KEY, "");
        if (!TextUtils.isEmpty(targetJson)) {
            webSiteBean = JsonUtils.objectFromJson(targetJson, WebSiteBean.class);
        }
        return webSiteBean;
    }

    public static void setCurrentWebsite(Context context, WebSiteBean webSiteBean) {
        SPUtils.put(context, SPUtils.DEFAULT_URL_KEY, JsonUtils.objectToJson(webSiteBean));
    }

    public static List<WebSiteBean> getCurrentWebsiteList(Context context) {
        List<WebSiteBean> webSiteBeanList = null;
        String urlListJson = (String) SPUtils.get(context, SPUtils.DEFAULT_URL_LIST_KEY, "");
        if (!TextUtils.isEmpty(urlListJson)) {
            webSiteBeanList = JsonUtils.objectFromJson(urlListJson, new TypeToken<List<WebSiteBean>>() {
            }.getType());
        }

        return webSiteBeanList;
    }

    public static void setCurrentWebsiteList(Context context, List<WebSiteBean> webSiteBeanList) {
        SPUtils.put(context, SPUtils.DEFAULT_URL_LIST_KEY, JsonUtils.objectToJson(webSiteBeanList));
    }

    public static void addCurrentWebsiteList(Context context, WebSiteBean webSiteBean) {
        List<WebSiteBean> webSiteBeanList = getCurrentWebsiteList(context);
        webSiteBeanList.add(webSiteBean);

        setCurrentWebsiteList(context, webSiteBeanList);
    }

    public static void removeCurrentWebsiteList(Context context, WebSiteBean webSiteBean) {
        List<WebSiteBean> webSiteBeanList = getCurrentWebsiteList(context);
        Iterator<WebSiteBean> webSiteBeanIterator = webSiteBeanList.iterator();
        while (webSiteBeanIterator.hasNext()) {
            WebSiteBean bean = webSiteBeanIterator.next();
            if (bean.getUrl().equals(webSiteBean.getUrl())) {
                webSiteBeanIterator.remove();
            }
        }

        setCurrentWebsiteList(context, webSiteBeanList);
    }
}

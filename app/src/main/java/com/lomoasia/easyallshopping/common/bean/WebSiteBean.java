package com.lomoasia.easyallshopping.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lomoasia.easyallshopping.common.JsonUtils;
import com.lomoasia.easyallshopping.common.Jsonable;

/**
 * Created by asia on 2018/1/18.
 */

public class WebSiteBean implements Parcelable, Jsonable {

    @Expose
    @SerializedName("web_title")
    private String title;

    @Expose
    @SerializedName("web_url")
    private String url;

    @Expose
    @SerializedName("web_taokey")
    private String taoKey;

    public WebSiteBean() {

    }

    protected WebSiteBean(Parcel in) {
        title = in.readString();
        url = in.readString();
        taoKey = in.readString();
    }

    public static final Creator<WebSiteBean> CREATOR = new Creator<WebSiteBean>() {
        @Override
        public WebSiteBean createFromParcel(Parcel in) {
            return new WebSiteBean(in);
        }

        @Override
        public WebSiteBean[] newArray(int size) {
            return new WebSiteBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(taoKey);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTaoKey() {
        return taoKey;
    }

    public void setTaoKey(String taoKey) {
        this.taoKey = taoKey;
    }

    @Override
    public String toJson() {
        return JsonUtils.objectToJson(this);
    }
}

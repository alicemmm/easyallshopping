package com.lomoasia.easyallshopping.common;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.common.bean.WebSiteBean;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asia on 2018/1/17.
 */

public class CommonUtils {

    public static void shareText(Context context, String text) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            context.startActivity(Intent.createChooser(intent,
                    context.getString(R.string.menu_share)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RecyclerView.ItemDecoration getDefaultItemDecoration(Context context) {
        return new HorizontalDividerItemDecoration.Builder(context)
                .colorResId(R.color.dividerColor)
                .sizeResId(R.dimen.default_item_decoration_size)
                .showLastDivider()
                .build();
    }

    public static void printWrapper(String tag, String msg) {
        if (tag == null || tag.length() == 0
                || msg == null || msg.length() == 0) {
            return;
        }

        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {
            Log.e(tag, msg);
        } else {
            while (msg.length() > segmentSize) {
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                Log.e(tag, logContent);
            }
            Log.e(tag, msg);
        }
    }

}

package com.lomoasia.easyallshopping.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lomoasia.easyallshopping.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

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

    public static void hideSoftKeyboard(View view) {
        if (view == null) {
            return;
        }
        View focusView = view;

        Context context = view.getContext();
        if (context != null && context instanceof Activity) {
            Activity activity = ((Activity) context);
            focusView = activity.getCurrentFocus();
        }
        if (focusView == null) {
            return;
        }
        focusView.clearFocus();
        InputMethodManager manager = (InputMethodManager) focusView.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null) {
            return;
        }
        manager.hideSoftInputFromWindow(focusView.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showSoftKeyboard(Context context, View view) {
        if (view == null) {
            return;
        }
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        if (!view.isFocused()) {
            view.requestFocus();
        }
        InputMethodManager inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) {
            return;
        }
        inputMethodManager.showSoftInput(view, 0);
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

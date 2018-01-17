package com.lomoasia.easyallshopping.common;

import android.content.Context;
import android.content.Intent;

import com.lomoasia.easyallshopping.R;

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
}
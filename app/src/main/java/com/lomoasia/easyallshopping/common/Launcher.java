package com.lomoasia.easyallshopping.common;

import android.content.Context;
import android.content.Intent;

import com.lomoasia.easyallshopping.activities.MainActivity;

/**
 * Created by asia on 2018/1/12.
 */

public class Launcher {

    public static void startMainActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}

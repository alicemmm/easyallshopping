package com.lomoasia.easyallshopping.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by asia on 2018/1/19.
 */

public class SettingEvent {
    private static final String ACTION_SETTING_CHANGED =
            SettingEvent.class.getPackage().getName() + ".SETTING_CHANGED";

    public interface Listener {
        void onSettingChanged(Context context, String key);
    }

    public static class Receiver extends BroadcastReceiver {

        private Context context;
        private Listener listener;

        private boolean isRegistered = false;

        public Receiver(Context context, Listener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SETTING_CHANGED.equals(intent.getAction())) {
                listener.onSettingChanged(context, getKey(intent));
            }
        }

        public void register() {
            register(0);
        }

        public void registerHighPriority() {
            register(IntentFilter.SYSTEM_HIGH_PRIORITY);
        }

        private void register(int priority) {
            if (isRegistered) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.setPriority(priority);
            intentFilter.addAction(ACTION_SETTING_CHANGED);
            context.registerReceiver(this, intentFilter);
            isRegistered = true;
        }

        public void unregister() {
            if (!isRegistered) {
                return;
            }
            context.unregisterReceiver(this);
            isRegistered = false;
        }
    }

    private static String getKey(Intent intent) {
        return intent.getStringExtra("key");
    }

    private static void setKey(Intent intent, String key) {
        intent.putExtra("key", key);
    }

    public static void sendSettingChanged(Context context, String key) {
        Intent intent = new Intent(ACTION_SETTING_CHANGED);
        setKey(intent, key);
        context.sendBroadcast(intent);
    }
}

package com.lomoasia.easyallshopping.common;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asia on 2018/1/19.
 */

public class ClipboardUtil {
    public static final String TAG = ClipboardUtil.class.getSimpleName();

    private static final String MIME_CONTACT = "vnd.android.cursor.dir/person";
    /**
     * 由于系统剪贴板在某些情况下会多次调用，但调用间隔基本不会超过5ms
     * 考虑到用户操作，将阈值设为100ms，过滤掉前几次无效回调
     */
    private static final int THRESHOLD = 100;

    private Context context;
    private static ClipboardUtil instance;
    private ClipboardManager clipboardManager;

    private Handler handler = new Handler();

    private ClipboardManager.OnPrimaryClipChangedListener onPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, THRESHOLD);
        }
    };

    private ClipboardChangedRunnable runnable = new ClipboardChangedRunnable();

    private List<OnPrimaryClipChangedListener> onPrimaryClipChangedListeners = new ArrayList<>();

    /**
     * 自定义 OnPrimaryClipChangedListener
     * 用于处理某些情况下系统多次调用 onPrimaryClipChanged()
     */
    public interface OnPrimaryClipChangedListener {
        void onPrimaryClipChanged(ClipboardManager clipboardManager);
    }

    private class ClipboardChangedRunnable implements Runnable {

        @Override
        public void run() {
            for (OnPrimaryClipChangedListener listener : onPrimaryClipChangedListeners) {
                listener.onPrimaryClipChanged(clipboardManager);
            }
        }
    }

    private ClipboardUtil(Context context) {
        this.context = context;
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(onPrimaryClipChangedListener);
    }

    /**
     * 单例。暂时不清楚此处传 Activity 的 Context 是否会造成内存泄漏
     * 建议在 Application 的 onCreate 方法中实现
     *
     * @param context
     * @return
     */
    public static ClipboardUtil init(Context context) {
        if (instance == null) {
            instance = new ClipboardUtil(context);
        }
        return instance;
    }

    /**
     * 获取ClipboardUtil实例，记得初始化
     *
     * @return
     */
    public static ClipboardUtil getInstance() {
        return instance;
    }

    public void addOnPrimaryClipChangedListener(OnPrimaryClipChangedListener listener) {
        if (!onPrimaryClipChangedListeners.contains(listener)) {
            onPrimaryClipChangedListeners.add(listener);
        }
    }

    public void removeOnPrimaryClipChangedListener(OnPrimaryClipChangedListener listener) {
        onPrimaryClipChangedListeners.remove(listener);
    }

    /**
     * 判断剪贴板内是否有数据
     *
     * @return
     */
    public boolean hasPrimaryClip() {
        return clipboardManager.hasPrimaryClip();
    }

    /**
     * 获取剪贴板中第一条String
     *
     * @return
     */
    public String getClipText() {
        if (!hasPrimaryClip()) {
            return null;
        }
        ClipData data = clipboardManager.getPrimaryClip();
        if (data != null
                && clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            return data.getItemAt(0).getText().toString();
        }
        return null;
    }

    /**
     * 获取剪贴板中第一条String
     *
     * @param context
     * @return
     */
    public String getClipText(Context context) {
        return getClipText(context, 0);
    }

    /**
     * 获取剪贴板中指定位置item的string
     *
     * @param context
     * @param index
     * @return
     */
    public String getClipText(Context context, int index) {
        if (!hasPrimaryClip()) {
            return null;
        }
        ClipData data = clipboardManager.getPrimaryClip();
        if (data == null) {
            return null;
        }
        if (data.getItemCount() > index) {
            return data.getItemAt(index).coerceToText(context).toString();
        }
        return null;
    }

    /**
     * 将文本拷贝至剪贴板
     *
     * @param text
     */
    public void copyText(String label, String text) {
        ClipData clip = ClipData.newPlainText(label, text);
        clipboardManager.setPrimaryClip(clip);
    }

    /**
     * 将HTML等富文本拷贝至剪贴板
     *
     * @param label
     * @param text
     * @param htmlText
     */
    public void copyHtmlText(String label, String text, String htmlText) {
        ClipData clip = ClipData.newHtmlText(label, text, htmlText);
        clipboardManager.setPrimaryClip(clip);
    }

    /**
     * 将Intent拷贝至剪贴板
     *
     * @param label
     * @param intent
     */
    public void copyIntent(String label, Intent intent) {
        ClipData clip = ClipData.newIntent(label, intent);
        clipboardManager.setPrimaryClip(clip);
    }

    /**
     * 将Uri拷贝至剪贴板
     * If the URI is a content: URI,
     * this will query the content provider for the MIME type of its data and
     * use that as the MIME type.  Otherwise, it will use the MIME type
     * {@link ClipDescription#MIMETYPE_TEXT_URILIST}.
     * 如 uri = "content://contacts/people"，那么返回的MIME type将变成"vnd.android.cursor.dir/person"
     *
     * @param cr    ContentResolver used to get information about the URI.
     * @param label User-visible label for the clip data.
     * @param uri   The URI in the clip.
     */
    public void copyUri(ContentResolver cr, String label, Uri uri) {
        ClipData clip = ClipData.newUri(cr, label, uri);
        clipboardManager.setPrimaryClip(clip);
    }

    /**
     * 将多组数据放入剪贴板中，如选中ListView多个Item，并将Item的数据一起放入剪贴板
     *
     * @param label    User-visible label for the clip data.
     * @param mimeType mimeType is one of them:{@link android.content.ClipDescription#MIMETYPE_TEXT_PLAIN},
     *                 {@link android.content.ClipDescription#MIMETYPE_TEXT_HTML},
     *                 {@link android.content.ClipDescription#MIMETYPE_TEXT_URILIST},
     *                 {@link android.content.ClipDescription#MIMETYPE_TEXT_INTENT}.
     * @param items    放入剪贴板中的数据
     */
    public void copyMultiple(String label, String mimeType, List<ClipData.Item> items) {
        if (items == null) {
            throw new NullPointerException("items is null");
        }
        int size = items.size();
        ClipData clip = new ClipData(label, new String[]{mimeType}, items.get(0));
        for (int i = 1; i < size; i++) {
            clip.addItem(items.get(i));
        }
        clipboardManager.setPrimaryClip(clip);
    }

    public CharSequence coercePrimaryClipToText() {
        if (!hasPrimaryClip()) {
            return null;
        }
        return clipboardManager.getPrimaryClip().getItemAt(0).coerceToText(context);
    }

    public CharSequence coercePrimaryClipToStyledText() {
        if (!hasPrimaryClip()) {
            return null;
        }
        return clipboardManager.getPrimaryClip().getItemAt(0).coerceToStyledText(context);
    }

    public CharSequence coercePrimaryClipToHtmlText() {
        if (!hasPrimaryClip()) {
            return null;
        }
        return clipboardManager.getPrimaryClip().getItemAt(0).coerceToHtmlText(context);
    }

    /**
     * 获取当前剪贴板内容的MimeType
     *
     * @return 当前剪贴板内容的MimeType
     */
    public String getPrimaryClipMimeType() {
        if (!hasPrimaryClip()) {
            return null;
        }
        return clipboardManager.getPrimaryClipDescription().getMimeType(0);
    }

    /**
     * 获取剪贴板内容的MimeType
     *
     * @param clip 剪贴板内容
     * @return 剪贴板内容的MimeType
     */
    public String getClipMimeType(ClipData clip) {
        return clip.getDescription().getMimeType(0);
    }

    /**
     * 获取剪贴板内容的MimeType
     *
     * @param clipDescription 剪贴板内容描述
     * @return 剪贴板内容的MimeType
     */
    public String getClipMimeType(ClipDescription clipDescription) {
        return clipDescription.getMimeType(0);
    }

    /**
     * 清空剪贴板
     */
    public void clearClip() {
        clipboardManager.setPrimaryClip(null);
    }

}

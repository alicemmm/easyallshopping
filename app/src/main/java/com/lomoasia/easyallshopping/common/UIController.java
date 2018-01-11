package com.lomoasia.easyallshopping.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.WebView;

import com.just.agentweb.AgentWebUIControllerImplBase;

/**
 * Created by cenxiaozhong on 2017/12/23.
 */

/**
 * 如果你需要修改某一个AgentWeb 内部的某一个弹窗 ，请看下面的例子
 * 注意写法一定要参照 DefaultUIController 的写法 ，因为UI自由定制，但是回调的方式是固定的，并且一定要回调。
 */
public class UIController extends AgentWebUIControllerImplBase {

    private Activity mActivity;

    public UIController(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void showMessage(String message, String from) {
        super.showMessage(message, from);
        Log.i(TAG, "message:" + message);
    }

    @Override
    public void showChooser(WebView view, String url, String[] ways, Handler.Callback callback) {
        super.showChooser(view, url, ways, callback); //使用默认的UI
    }


    //修改文件选择器的弹窗
//    @Override
//    public void showChooser(WebView view, String url, String[] ways, final Handler.Callback callback) {
//        //super.showChooser(view,url,ways,callback); //这行应该注释或者删除掉
//        final AlertDialog mAlertDialog = new AlertDialog.Builder(mActivity)//
//                .setSingleChoiceItems(ways, -1, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        if (callback != null) {
//                            Message mMessage = Message.obtain();
//                            mMessage.what = which;  //mMessage.what 必须等于ways的index
//                            callback.handleMessage(mMessage); //最后callback一定要回调
//                        }
//
//                    }
//                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        dialog.dismiss();
//                        if (callback != null) {
//                            callback.handleMessage(Message.obtain(null, -1)); //-1表示取消  //最后callback一定要回调
//                        }
//                    }
//                }).create();
//        mAlertDialog.show();
//    }
}

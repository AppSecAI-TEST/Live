package com.tonglu.live.callback;

import android.app.Activity;
import android.app.Dialog;

import com.tonglu.okhttp.request.base.Request;
import com.tonglu.okhttp.utils.OkLogger;

/**
 * ===========================================
 * 作    者：对于网络请求是否需要弹出进度对话框
 * 版    本：1.0
 * 创建日期：2017-7-26.
 * 描    述：DialogCallback
 * ===========================================
 */
public abstract class DialogCallback<T> extends JsonCallback<T> {

    //private ProgressDialog dialog;
    public Dialog mLoading;
    //添加公共请求头Headers
    //int currentUserId = (Integer) SPConfigManager.getInstance().get("userId", 0);
    //String userSign = (String) SPConfigManager.getInstance().get("userSign", "");

    public DialogCallback(Activity activity) {
        super();
        initDialog(activity);
    }

    private void initDialog(Activity activity) {
        if (activity != null) {
            //mLoading = ToastUtils.createLoadingDialog(activity);
        }

        /*dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请求网络中...");*/
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        if (mLoading != null && !mLoading.isShowing()) {
            mLoading.show();
        }

        OkLogger.e("添加公共请求头-----DialogCallback");
        //request.headers("currentUserId", currentUserId + "")
        //        .headers("UserSign", userSign);
    }

    @Override
    public void onFinish() {
        //网络请求结束后关闭对话框
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }
}

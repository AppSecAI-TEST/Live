package com.tonglu.live.callback;

import android.app.Activity;
import android.app.Dialog;

import com.tonglu.live.utils.ToastUtils;
import com.tonglu.okhttp.callback.StringCallback;
import com.tonglu.okhttp.request.base.Request;

/**
 * ===========================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：2017-7-26.
 * 描    述：DialogCallback  --- 返回String
 * ===========================================
 */
public abstract class StringDialogCallback extends StringCallback {

    //private ProgressDialog dialog;
    public Dialog mLoading;

    public StringDialogCallback(Activity activity) {
        /*dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请求网络中...");*/

        if (activity != null) {
            mLoading = ToastUtils.createLoadingDialog(activity);
        }
    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        if (mLoading != null && !mLoading.isShowing()) {
            mLoading.show();
        }
    }

    @Override
    public void onFinish() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }
}

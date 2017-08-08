package com.tonglu.live.activity;


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.tonglu.live.AppAplication;
import com.tonglu.live.R;
import com.tonglu.live.base.BaseActivity;
import com.tonglu.live.base.SystemBarTintManager;
import com.tonglu.live.listener.OnClickFastListener;
import com.tonglu.live.utils.DealViewUtils;
import com.tonglu.live.utils.KeyboardUtils;
import com.tonglu.live.utils.ValidateUtils;
import com.tonglu.live.widgets.SecretTextView;
import com.tonglu.okhttp.OkHttpUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * ===========================================
 * 作    者：zhsh
 * 版    本：1.0
 * 创建日期：2016/11/18.
 * 描    述：登录界面
 * ===========================================
 */

public class VerifyCodeActivity extends BaseActivity {

    //private SVProgressHUD mSVProgressHUD;

    private SystemBarTintManager tintManager;   //单独设置登录界面bar的颜色
    private SecretTextView secretTextView;

    private TextView tv_login;
    private EditText et_phone_number;

    private View ll_login_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        //mSVProgressHUD = new SVProgressHUD(this);

        setContentView(R.layout.activity_code_verify);
        initView();

        //提示网络连接不可用
        if (!ValidateUtils.isNetworkConnected(AppAplication.getInstance())) {
            //mSVProgressHUD.showInfoWithStatus("网络不可用",SVProgressHUD.SVProgressHUDMaskType.None);
            //mSVProgressHUD.showErrorWithStatus("网络未连接",SVProgressHUD.SVProgressHUDMaskType.None);
        }
    }

    @TargetApi(19)
    private void initWindow() {

        //Build.VERSION.SDK_INT  判断当前版本号是否大于19
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            tintManager = new SystemBarTintManager(this);
            //tintManager.setTintColor(Color.parseColor("#444764"));
            tintManager.setStatusBarTintColor(Color.parseColor("#354361"));
            tintManager.setStatusBarTintEnabled(true);
            //tintManager.setNavigationBarTintEnabled(true);
        }
    }


    private void initView() {

        //********************* 界面字体LOGO动画-暂定 *************************
        secretTextView = findView(R.id.secret_tv);
        secretTextView.setDuration(2800);
        //secretTextView.setIsVisible(true);
        secretTextView.show();
        //secretTextView.setOnClickListener(new FastClickListener());

        tv_login = findView(R.id.tv_login);
        tv_login.setOnClickListener(new FastClickListener());
        tv_login.setClickable(false);

        et_phone_number = findView(R.id.et_phone_number);
        et_phone_number.addTextChangedListener(textWatcher);
        et_phone_number.setFocusable(false);

        ll_login_layout = findView(R.id.ll_login_layout);

        //设置一个定时器，处理软键盘上弹和TextView动画冲突问题
        if (secretTextView.getIsVisible()) {
            Timer timer = new Timer();
            timer.schedule(task, 2800);
        }
    }

    //TextView动画显示完，设置输入框的状态和软键盘弹出动态计算高度
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    et_phone_number.setFocusable(true);
                    et_phone_number.setFocusableInTouchMode(true);
                    et_phone_number.requestFocus();

                    //处理弹出软键盘遮挡输入框的问题
                    KeyboardUtils.controlKeyboardLayout(ll_login_layout, et_phone_number);
                }
            });

        }
    };
    /**
     * Edittext监听事件
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(et_phone_number.getText())) {
                    DealViewUtils.buttonState(tv_login, R.drawable.rectangle_27dp_blue_selected, true);
                } else {
                    DealViewUtils.buttonState(tv_login, R.drawable.rectangle_27dp_blue, false);
                }
            }

    };

    private class FastClickListener extends OnClickFastListener {
        @Override
        public void onFastClick(View v) {

            switch (v.getId()) {

                case R.id.tv_login://登录

//                    new AlertView("Eworkpal提示", "账号密码错误请重新输入", null, new String[]{"确定"}, null,VerifyCodeActivity.this,
//                            AlertView.Style.Alert,new ItemClick() {
//                        @Override
//                        public void onItemClick(Object o, int position) {
//
//                            //...点击取消按钮返回 －1，其他按钮从0开始算
//
//                        }
//                    }).show();

                    startActivity(new Intent(VerifyCodeActivity.this, MainActivity.class));
                    finish();
                    break;

            }
        }
    }

    /**
     * 提交登录,返回企业列表(企业Id,企业Name)
     */
    private void loginGo(String phone, String password) {

        /*Map<String, Object> mapList = new HashMap<String, Object>();
        mapList.put("phone", phone);
        mapList.put("password",password);
        mapList.put("type", ResultErrorCode.TYPE_LOGIN_TERMINAL);
        mapList.put("ispass", pass);
        String str = GsonConvertUtil.toJson(mapList);

        UserRequestManager.getInstance().loginGo(this, str, new DialogCallback<BaseResponse<List<UserLoginToModle>>>(this) {
            @Override
            public void onSuccess(BaseResponse<List<UserLoginToModle>> baseResponse, Call call, Response response) {

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                OkLogger.e(e.toString());
            }
        });*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtil.getInstance().cancelTag(this);
    }
}

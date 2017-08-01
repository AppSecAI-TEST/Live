package com.tonglu.live.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;

import com.tonglu.live.R;
import com.tonglu.live.base.BaseTitleActivity;
import com.tonglu.live.callback.StringDialogCallback;
import com.tonglu.live.manager.GenericRequestManager;
import com.tonglu.live.model.LiveListInfo;
import com.tonglu.live.utils.Base64Object;
import com.tonglu.live.utils.Constants;
import com.tonglu.live.utils.GsonConvertUtil;
import com.tonglu.live.utils.MD5Utils;
import com.tonglu.live.utils.ToastUtils;
import com.tonglu.okhttp.model.Response;
import com.tonglu.okhttp.utils.OkLogger;

import java.util.Map;
import java.util.TreeMap;

import static com.tonglu.live.utils.MD5Utils.MD5;

public class MainActivity extends BaseTitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("直播列表");

       /* final String url = "rtmp://live.otofuturestore.com/malls/dianneizhibo?auth_key=1502265257-0-0-d56929ae83d1de1a1c1683e7cd2bc9cb";

        Button mBtn = (Button) findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LivePlayerActivity.class);
                intent.putExtra("url_address", url);
                startActivity(intent);
            }
        });*/

        test();

    }


    private void test() {
        String url = "v/VedioAddress";


        Map<String, String> params = new TreeMap<>();
        params.put("appkey", Constants.APP_KEY);
        params.put("appsecret", Constants.APP_SECRET);

        //MD5加密
        String signTest = "abc" + Constants.APP_KEY + Constants.APP_SECRET + "cba";
        params.put("sign", MD5Utils.MD5(signTest));

        String jsonParams = GsonConvertUtil.toJson(params);

        GenericRequestManager.upJson(url, jsonParams, this, new StringDialogCallback(this) {
            @Override
            public void onSuccess(Response<String> response) {

                if (!TextUtils.isEmpty(response.body())) {
                    LiveListInfo listInfo = GsonConvertUtil.fromJson(response.body(), LiveListInfo.class);
                    //OkLogger.e("------>" + listInfo);
                }

            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                ToastUtils.showLongToastSafe("请确认服务器是否开启");
            }
        });

    }
}

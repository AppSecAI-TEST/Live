package com.tonglu.live;

import android.app.Application;
import android.util.Log;

import com.tencent.rtmp.TXLivePusher;

public class AppApplication extends Application{

    private static AppApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        int[] sdkver = TXLivePusher.getSDKVersion();
        if (sdkver != null && sdkver.length >= 3) {
            Log.d("rtmpsdk", "rtmp sdk version is:" + sdkver[0] + "." + sdkver[1] + "." + sdkver[2]);
        }

    }


    public static AppApplication getInstance() {
        return instance;
    }

}

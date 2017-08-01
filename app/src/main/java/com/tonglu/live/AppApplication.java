package com.tonglu.live;

import android.app.Application;
import android.util.Log;

import com.tencent.rtmp.TXLivePusher;
import com.tonglu.live.manager.GenericRequestManager;

public class AppApplication extends Application {

    private static AppApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        GenericRequestManager.initialize(this);

    }


    public static AppApplication getInstance() {
        return instance;
    }

}

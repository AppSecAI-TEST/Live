package com.tonglu.live;

import android.app.Application;

import com.tonglu.live.manager.GenericRequestManager;

public class AppAplication extends Application {

    private static AppAplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        GenericRequestManager.initialize(this);

    }


    public static AppAplication getInstance() {
        return instance;
    }

}

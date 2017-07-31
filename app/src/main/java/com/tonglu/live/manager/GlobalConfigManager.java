package com.tonglu.live.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.tonglu.okhttp.utils.OkLogger;

import java.io.File;

/**
 * @author gao_chun
 * 读取MetaData中的服务器地址
 */
public class GlobalConfigManager {

    private static final String META_NAME_SERVER = "app.server";
    private static GlobalConfigManager sInstance;
    private Context mApplicationContext;
    private String mServerHost;

    public static void initialize(Context aApplicationContext) {
        // 防止初始化多次
        if (sInstance == null) {
            sInstance = new GlobalConfigManager(aApplicationContext);
        } else {
            throw new IllegalStateException("SystemConfigManager has already been initialized.");
        }
    }

    public static GlobalConfigManager getInstance() {
        // 防止在初始化之前被调用
        if (sInstance != null) {
            return sInstance;
        } else {
            throw new IllegalStateException("SystemConfigManager not initialized.");
        }
    }

    private GlobalConfigManager(Context aApplicationContext) {
        this.mApplicationContext = aApplicationContext;

        // 从AndroidManifest.xml中读取服务器地址ַ
        final ApplicationInfo applicationInfo;
        final PackageManager packageManager = mApplicationContext.getPackageManager();
        String serverHost = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(mApplicationContext.getPackageName(), PackageManager.GET_META_DATA);
            //获取META服务器地址
            serverHost = applicationInfo.metaData.getString(META_NAME_SERVER);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        if (serverHost != null) {
            mServerHost = serverHost;
            OkLogger.e("[meta-data] ServerHost=" + serverHost);
        } else {
            OkLogger.e("Reading server host from AndroidManifest.xml failed.");
        }


    }

    public String getServerHost() {
        return mServerHost;
    }

    public String getUrlPrefix() {
        return mServerHost + File.separator;
    }

}

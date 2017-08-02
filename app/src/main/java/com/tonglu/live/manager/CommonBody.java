package com.tonglu.live.manager;


import com.tonglu.live.utils.Constants;
import com.tonglu.live.utils.MD5Utils;

import java.util.HashMap;

/**
 * ===========================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：2017-6-26.
 * 描    述：公共的请求体
 * ===========================================
 */


public class CommonBody {

    private static String currentTime;

    private static CommonBody sInstance;

    public CommonBody() {

    }

    public static CommonBody getInstance() {
        currentTime = System.currentTimeMillis() + "";
        if (sInstance == null) {
            sInstance = new CommonBody();

        } // else ignored.
        return sInstance;
    }

    public HashMap<String, String> commonBody() {

        HashMap<String, String> mParams = new HashMap<>();
        mParams.put("appKey", Constants.APP_KEY);
        mParams.put("version", Constants.APP_VERSION);
        mParams.put("timeStamp", currentTime);
        mParams.put("sign", MD5Utils.signRequest(currentTime));
        return mParams;
    }

}

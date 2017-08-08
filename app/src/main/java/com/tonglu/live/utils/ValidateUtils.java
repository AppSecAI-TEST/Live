/*******************************************************************************
 * Copyright (c) Weaver Info Tech Co. Ltd
 * <p/>
 * Utils
 * <p/>
 * app.util.Utils.java
 * TODO: File description or class description.
 *
 * @author: gao_chun
 * @since: 2014年9月26日
 * @version: 1.0.0
 * @changeLogs: 1.0.0: First created this class.
 ******************************************************************************/
package com.tonglu.live.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gao_chun
 */
public class ValidateUtils {

    private ValidateUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断手机号码
     *
     * @param mobiles
     * @return
     */
    /* public static boolean isMobileNO(String paramString) {
        return Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$").matcher(paramString).matches();
    }*/
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断手机号码,只判断是否是11位，首位数是1。
     *
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        if (mobile.length() >= 1) {
            if (mobile.startsWith("1") && mobile.length() == 11) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断邮编
     *
     * @param zipString
     * @return
     */
    public static boolean isZipNO(String zipString) {
        String str = "^[1-9][0-9]{5}$";
        return Pattern.compile(str).matcher(zipString).matches();
    }

    /**
     * 打电话
     *
     * @param context
     * @param phone_num
     */
    public static void makePhoneCall(Context context, String phone_num) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (manager.getSimState()) {
            case TelephonyManager.SIM_STATE_READY:
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_num));       //叫出呼叫程序
                //Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_num));     //直接打出去
                context.startActivity(phoneIntent);
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                Toast.makeText(context, "无SIM卡", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "SIM卡被锁定或未知状态", Toast.LENGTH_LONG).show();
                break;
        }
    }


    /**
     * 判断邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * 验证输入密码长度 (6-18位)
     *
     * @param password
     * @return
     */
    public static boolean isPassword(String password) {
        if (null == password || "".equals(password)) return false;
        Pattern p = Pattern.compile("^\\d{6,18}$");
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * 获取WiFi Mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        return m_szWLANMAC;
    }


    /*获取当前应用的版本号*/
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            // 不可能发生.
            // can't reach
            return 0;
        }
    }


    /**
     * 网络连接是否正常
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager == null) {
                return false;
            } else {
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }
        }
        return false;
    }


    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */

    /*public static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    //System.out.println(i + "===网络状态===" + networkInfo[i].getState());
                    //System.out.println(i + "===网络类型===" + networkInfo[i].getTypeName());

                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }*/


    /**
     * 判断当前的网络连接方式是否为WIFI
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str) || "null".equalsIgnoreCase(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }
}

package com.tonglu.live.utils;

import android.text.TextUtils;

import com.tonglu.okhttp.utils.OkLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import static com.tonglu.live.utils.Constants.APP_KEY;

/**
 * ===========================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：2017-6-28.
 * 描    述：MD5加密工具类
 * ===========================================
 */
public class MD5Utils {


    private MD5Utils() {
        //防止初始化
    }

    /**
     * sign排序及MD5加密
     *
     * @return
     */
    public static String signRequest(String currentTime) {

        StringBuilder query = new StringBuilder(Constants.SECRET_REQUEST_BODY);
        Map<String, String> map = new TreeMap<>();
        map.put("AppKey", Constants.APP_KEY);
        map.put("Version", Constants.APP_VERSION);
        map.put("TimeStamp", currentTime);
        // 第一步：把字典按Key的字母顺序排序
        Map<String, String> resultMap = sortMapByKey(map);
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            //OkLogger.e("key= " + entry.getKey() + " and value= " + entry.getValue());
            if (!TextUtils.isEmpty(entry.getKey()) && !TextUtils.isEmpty(entry.getValue())) {
                // 第二步：把所有参数名和参数值串在一起
                query.append(entry.getKey()).append(entry.getValue());
            }
        }
        // 第三步：使用MD5加密,则直接将返回String类型的加密数据
        String encryption = StringUtils.hash(query.toString());
        //OkLogger.e("排序后的MD5加密值-->" + encryption);
        return encryption.toUpperCase();
    }

    /**
     * 使用Map按key进行排序
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * 普通MD5加密
     *
     * @param input
     * @return
     */
    public static String MD5(String input) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "check jdk";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = input.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 比较器类(把字典按Key的字母顺序排序)升序排序
     */
    public static class MapKeyComparator implements Comparator<String> {
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }


    //公共的MD5加密方法
    public static Map<String, String> commonMD5() {

        Map<String, String> params = new TreeMap<>();
        params.put("appkey", Constants.APP_KEY_);
        params.put("appsecret", Constants.APP_SECRET);
        //MD5加密
        String signTest = "abc" + Constants.APP_KEY_ + Constants.APP_SECRET + "cba";
        params.put("sign", MD5Utils.MD5(signTest));

        return params;
    }

    /*public static String md5(String string, String slat) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest((string + slat).getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }*/
}

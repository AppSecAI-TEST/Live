package com.tonglu.live.manager;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import com.tonglu.okhttp.OkHttpUtil;
import com.tonglu.okhttp.cache.CacheEntity;
import com.tonglu.okhttp.cache.CacheMode;
import com.tonglu.okhttp.callback.AbsCallback;
import com.tonglu.okhttp.interceptor.HttpLoggingInterceptor;
import com.tonglu.okhttp.model.HttpParams;
import com.tonglu.okhttp.utils.OkLogger;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * ===========================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：2017-6-26.
 * 描    述：数据通讯类，不涉及认证与授权的数据。
 * 这个类的任务：初始化网络框架配置，提供请求方法。
 * ===========================================
 */
public class GenericRequestManager {

    private static String mServerHost;

    //private static GenericRequestManager sInstance;
    /*public static GenericRequestManager getInstance() {
        if (sInstance == null) {
            OkLogger.e("SPConfigManager.initiate method not called in the application.");
        } // else ignored.
        return sInstance;
    }*/

    public static void initialize(Application applicationContext) {

        //初始化全局网络配置信息（读取MetaData）
        GlobalConfigManager.initialize(applicationContext);
        //读取Server地址
        mServerHost = GlobalConfigManager.getInstance().getUrlPrefix();

        //初始化网络请求对象
        //sInstance = new GenericRequestManager();

        //--缓存模式
        //.setCacheMode(CacheMode.NO_CACHE)               //全局统一设置缓存模式,默认是不使用缓存,可以不传
        //.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //可以全局统一设置缓存时间,默认永不过期

        /************************** 全局公共请求Header *******************************/

        //header不支持中文，不允许有特殊字符
        //HttpHeaders headers = new HttpHeaders();
        //headers.put("AppFrom", Constants.APP_FROM);
        //headers.put("AppVersion",  Constants.APP_VERSION);

        /*****************************************************************************/

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //Log相关设置
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkHttpUtil");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.SEVERE);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加默认debug日志
        //builder.addInterceptor(new LoggerInterceptor("OkHttpUtil", true));


        //超时时间设置，默认30秒
        builder.readTimeout(OkHttpUtil.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkHttpUtil.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkHttpUtil.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(applicationContext)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new DBCookieStore(applicationContext)));            //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，以下几种方案根据需要自己设置
        //方法一：信任所有证书,不安全有风险
        //HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        //方法二：自定义信任规则，校验服务端证书
        //HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        //方法三：使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
        //builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        //builder.hostnameVerifier(new SafeHostnameVerifier());

        // 其他统一的配置
        OkHttpUtil.getInstance().init(applicationContext)                     //必须调用初始化
                .setOkHttpClient(builder.build())               //必须设置OkHttpClient
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                //.addCommonHeaders(headers);                      //全局公共头参数
    }


    /*private GenericRequestManager(String serverHost) {

        // 初始化Handler，用于在主线程中执行任务
        //mHandler = new Handler(Looper.getMainLooper());
        // 初始化服务器地址
        mServerHost = serverHost;
    }*/


    /****************************** GET 请求 *******************************/
    public static <T> void get(String mUrl, Activity mActivity, AbsCallback<T> mCallback) {

        if (TextUtils.isEmpty(mUrl)) {
            OkLogger.e("-----> URL is null.");
            return;
        }
        final String URL = mServerHost + mUrl;    //URL
        //OkLogger.e(URL);
        OkHttpUtil.<T>get(mUrl).tag(mActivity).execute(mCallback);

    }


    public static <T> void get(String mUrl, HttpParams params, Activity mActivity, AbsCallback<T> mCallback) {

        if (TextUtils.isEmpty(mUrl)) {
            OkLogger.e("-----> URL is null.");
            return;
        }
        final String URL = mServerHost + mUrl;    //URL
        OkHttpUtil.<T>get(mUrl).params(params).tag(mActivity).execute(mCallback);

    }


    /****************************** POST 请求 *******************************/

    public static <T> void post(String mUrl, Activity mActivity, AbsCallback<T> mCallback) {

        if (TextUtils.isEmpty(mUrl)) {
            OkLogger.e("-----> URL is null.");
            return;
        }
        final String URL = mServerHost + mUrl;    //URL
        OkHttpUtil.<T>post(mUrl).tag(mActivity).execute(mCallback);

    }

    public static <T> void post(String mUrl, HttpParams params, Activity mActivity, AbsCallback<T> mCallback) {

        if (TextUtils.isEmpty(mUrl)) {
            OkLogger.e("-----> URL is null.");
            return;
        }
        final String URL = mServerHost + mUrl;    //URL
        OkHttpUtil.<T>post(mUrl).params(params).tag(mActivity).execute(mCallback);

    }


    /****************************** POST-(upJson) 请求 *******************************/

    public static <T> void upJson(String mUrl, String json, Activity mActivity, AbsCallback<T> mCallback) {

        if (TextUtils.isEmpty(mUrl)) {
            OkLogger.e("-----> URL is null.");
            return;
        }
        final String URL = mServerHost + mUrl;    //URL
        OkHttpUtil.<T>post(URL).upJson(json).tag(mActivity).execute(mCallback);
    }



    /**
     * 文件下载
     *
     * @param activity  取消请求的Tag
     * @param mUrl      请求地址
     * @param mParams   请求参数（没有可以不填）
     * @param mCallback
     */
    /*public <T> void downloadFile(Activity activity, String mUrl, HttpParams mParams, AbsCallback<T> mCallback) {
        final String URL = mServerHost + File.separator + mUrl;    //URL
        if (mParams != null) {
            OkHttpUtils.get(URL).tag(activity).params(mParams).execute(mCallback);
        } else {
            OkHttpUtils.get(URL).tag(activity).execute(mCallback);
        }
    }*/

}
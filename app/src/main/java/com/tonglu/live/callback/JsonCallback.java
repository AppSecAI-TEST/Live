package com.tonglu.live.callback;

import com.tonglu.okhttp.callback.AbsCallback;
import com.tonglu.okhttp.request.base.Request;
import com.tonglu.okhttp.utils.OkLogger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * ================================================
 * 描    述：默认将返回的数据解析成需要的Bean,
 * 可以是 BaseBean，String，List，Map
 * ================================================
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {

    //添加公共请求头Headers
    //int currentUserId = (Integer) SPConfigManager.getInstance().get("userId", 0);
    //String userSign = (String) SPConfigManager.getInstance().get("userSign", "");

    private Type type;
    private Class<T> clazz;

    public JsonCallback() {
    }

    public JsonCallback(Type type) {
        this.type = type;
    }

    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);

        //OkLogger.e("添加公共请求头-----JsonCallback");
        //request.headers("currentUserId", currentUserId + "")
        //        .headers("UserSign", userSign);

    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {

        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                JsonConvert<T> convert = new JsonConvert<>(clazz);
                return convert.convertResponse(response);
            }
        }

        JsonConvert<T> convert = new JsonConvert<>(type);
        return convert.convertResponse(response);
    }
}

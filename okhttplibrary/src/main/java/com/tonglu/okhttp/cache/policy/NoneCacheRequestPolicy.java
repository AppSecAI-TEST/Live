package com.tonglu.okhttp.cache.policy;

import com.tonglu.okhttp.cache.CacheEntity;
import com.tonglu.okhttp.callback.Callback;
import com.tonglu.okhttp.model.Response;
import com.tonglu.okhttp.request.base.Request;

public class NoneCacheRequestPolicy<T> extends BaseCachePolicy<T> {

    public NoneCacheRequestPolicy(Request<T, ? extends Request> request) {
        super(request);
    }

    @Override
    public void onSuccess(final Response<T> success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess(success);
                mCallback.onFinish();
            }
        });
    }

    @Override
    public void onError(final Response<T> error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onError(error);
                mCallback.onFinish();
            }
        });
    }

    @Override
    public Response<T> requestSync(CacheEntity<T> cacheEntity) {
        try {
            prepareRawCall();
        } catch (Throwable throwable) {
            return Response.error(false, rawCall, null, throwable);
        }
        Response<T> response = null;
        if (cacheEntity != null) {
            response = Response.success(true, cacheEntity.getData(), rawCall, null);
        }
        if (response == null) {
            response = requestNetworkSync();
        }
        return response;
    }

    @Override
    public void requestAsync(final CacheEntity<T> cacheEntity, Callback<T> callback) {
        mCallback = callback;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onStart(request);

                try {
                    prepareRawCall();
                } catch (Throwable throwable) {
                    Response.error(false, rawCall, null, throwable);
                    return;
                }
                if (cacheEntity != null) {
                    Response<T> success = Response.success(true, cacheEntity.getData(), rawCall, null);
                    mCallback.onCacheSuccess(success);
                    mCallback.onFinish();
                    return;
                }
                requestNetworkAsync();
            }
        });
    }
}

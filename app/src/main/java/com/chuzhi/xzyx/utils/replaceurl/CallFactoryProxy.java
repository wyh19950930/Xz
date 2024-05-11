package com.chuzhi.xzyx.utils.replaceurl;

/**
 * @Author : wyh
 * @Time : On 2023/5/31 16:18
 * @Description : CallFactoryProxy
 */

import android.util.Log;

import androidx.annotation.Nullable;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * 创建时间：2019-09-04
 * 编写人：chengxin
 * 功能描述：代理{@link okhttp3.Call.Factory} 拦截{@link #newCall(Request)}方法 用于切换baseurl
 */
public abstract class CallFactoryProxy implements Call.Factory {
    private static final String NAME_BASE_URL = "BaseUrlName";
    private final Call.Factory delegate;
    private String TAG = "CallFactoryProxy";
    public CallFactoryProxy(Call.Factory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call newCall(Request request) {
        String baseUrlName = request.header(NAME_BASE_URL);
        if (baseUrlName != null) {
            HttpUrl newHttpUrl = getNewUrl(baseUrlName, request);
            if (newHttpUrl != null) {
                Request newRequest = request.newBuilder().url(newHttpUrl).build();
                return delegate.newCall(newRequest);
            } else {
                Log.w(TAG, "getNewUrl() return null when baseUrlName==" + baseUrlName);
            }
        }
        return delegate.newCall(request);
    }

    @Nullable
    protected abstract HttpUrl getNewUrl(String baseUrlName, Request request);
}

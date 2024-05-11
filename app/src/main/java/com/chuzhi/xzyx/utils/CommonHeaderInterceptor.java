package com.chuzhi.xzyx.utils;

import android.util.Log;

import com.chuzhi.xzyx.api.ApiRetrofit;
import com.chuzhi.xzyx.app.MyApplication;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加token到请求头
 */
public class CommonHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request(); // 获取旧连接
        //获取老的url
        HttpUrl oldUrl = oldRequest.url();
        Request.Builder requestBuilder = oldRequest.newBuilder(); // 建立新的构建者
        List<String> urlnameList = oldRequest.headers("BaseUrlName");
        if (urlnameList != null && urlnameList.size() > 0) {
            //删除原有配置中的值,就是namesAndValues集合里的值
            requestBuilder.removeHeader("BaseUrlName");
            //获取头信息中配置的value,如：manage或者mdffx
            String urlname = urlnameList.get(0);
            HttpUrl baseURL = null;
            //根据头信息中配置的value,来匹配新的base_url地址
            if ("BASE_RC".equals(urlname)) {
                baseURL = HttpUrl.parse(ApiRetrofit.BASE_RC_SERVER_URL);
            } else if ("BASE_MAP".equals(urlname)) {
                baseURL = HttpUrl.parse(ApiRetrofit.BASE_GET_IP_URL);
            } else {
                baseURL = HttpUrl.parse(ApiRetrofit.BASE_BBS_SERVER_URL);
            }
            //重建新的HttpUrl，需要重新设置的url部分
            HttpUrl newHttpUrl = oldUrl.newBuilder()
                    .scheme(baseURL.scheme())//http协议如：http或者https
                    .host(baseURL.host())//主机地址
                    .port(baseURL.port())//端口
                    .build();
            // 将旧请求的请求方法和请求体设置到新请求中
            requestBuilder.method(oldRequest.method(), oldRequest.body());
            // 获取旧请求的头
            Headers headers = oldRequest.headers();
            if (headers != null) {
                Set<String> names = headers.names();
                for (String name : names) {
                    String value = headers.get(name);
                    // 将旧请求的头设置到新请求中
                    requestBuilder.header(name, value);
                }
            }

            // 建立新请求连接
            if ("BASE_MAP".equals(urlname)) {
                newHttpUrl = HttpUrl.parse(newHttpUrl.url().toString().replace("api/", ""));
                Request newRequest = requestBuilder.url(newHttpUrl).build();
                return chain.proceed(newRequest);
            } else {
            // 添加额外的自定义公共请求头
                String token = SpUtils.getSharedStringData(MyApplication.Companion.getInstance(), "Token");
                if (!token.isEmpty()) {
                    requestBuilder.header("Authorization", "Bearer " + token);
                }
                requestBuilder.header("Content-Type", "application/x-www-form-urlencoded");
                requestBuilder.header("deviceId", DeviceIdUtils.getAndroidId(MyApplication.Companion.getInstance()));
                Log.e("device_Id if",DeviceIdUtils.getAndroidId(MyApplication.Companion.getInstance()));
                Request newRequest = requestBuilder.url(newHttpUrl).build();
                return chain.proceed(newRequest);
            }
        } else {
            // 将旧请求的请求方法和请求体设置到新请求中
            requestBuilder.method(oldRequest.method(), oldRequest.body());
            // 获取旧请求的头
            Headers headers = oldRequest.headers();
            if (headers != null) {
                Set<String> names = headers.names();
                for (String name : names) {
                    String value = headers.get(name);
                    // 将旧请求的头设置到新请求中
                    requestBuilder.header(name, value);
                }
            }
            // 添加额外的自定义公共请求头
            String token = SpUtils.getSharedStringData(MyApplication.Companion.getInstance(), "Token");
            if (!token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }
            requestBuilder.header("Content-Type", "application/x-www-form-urlencoded");
            requestBuilder.header("deviceId", DeviceIdUtils.getAndroidId(MyApplication.Companion.getInstance()));
            Log.e("device_Id else",DeviceIdUtils.getAndroidId(MyApplication.Companion.getInstance()));
            // 建立新请求连接
            Request newRequest = requestBuilder.build();
            return chain.proceed(newRequest);
        }
    }
}


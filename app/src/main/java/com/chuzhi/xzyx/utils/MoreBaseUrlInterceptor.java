package com.chuzhi.xzyx.utils;

import com.chuzhi.xzyx.api.ApiRetrofit;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author : wyh
 * @Time : On 2023/5/31 17:20
 * @Description : MoreBaseUrlInterceptor
 */
public class MoreBaseUrlInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取原始的originalRequest
        Request oldRequest = chain.request();
        //获取老的url
        HttpUrl oldUrl = oldRequest.url();
        //获取oldRequest的创建者builder
        Request.Builder requestBuilder = oldRequest.newBuilder();
        //获取头信息的集合如：manage,mdffx
        List<String> urlnameList = oldRequest.headers("BaseUrlName");
        if (urlnameList != null && urlnameList.size() > 0) {
            //删除原有配置中的值,就是namesAndValues集合里的值
            requestBuilder.removeHeader("BaseUrlName");
            //获取头信息中配置的value,如：manage或者mdffx
            String urlname = urlnameList.get(0);
            HttpUrl baseURL=null;
            //根据头信息中配置的value,来匹配新的base_url地址
            if ("BASE_RC".equals(urlname)) {
                baseURL = HttpUrl.parse(ApiRetrofit.BASE_RC_SERVER_URL);
            } else {
                baseURL = HttpUrl.parse(ApiRetrofit.BASE_BBS_SERVER_URL);
            }
            //重建新的HttpUrl，需要重新设置的url部分
            HttpUrl newHttpUrl = oldUrl.newBuilder()
                    .scheme(baseURL.scheme())//http协议如：http或者https
                    .host(baseURL.host())//主机地址
                    .port(baseURL.port())//端口
                    .build();
            //获取处理后的新newRequest
            Request newRequest = requestBuilder.url(newHttpUrl).build();
            return  chain.proceed(newRequest);
        }else{
            return chain.proceed(oldRequest);
        }

    }
}
package com.chuzhi.xzyx.api;

import android.util.Log;

import androidx.annotation.Nullable;

import com.chuzhi.xzyx.app.MyApplication;
import com.chuzhi.xzyx.utils.CommonHeaderInterceptor;
import com.chuzhi.xzyx.utils.DecodeUnicodeUtil;
import com.chuzhi.xzyx.utils.SpUtils;
import com.chuzhi.xzyx.utils.cookie.LocalCookieStore;
import com.chuzhi.xzyx.utils.cookie.MyCookieJarImp;
import com.chuzhi.xzyx.utils.replaceurl.CallFactoryProxy;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiRetrofit {

    /**
     * BASE_RC_SERVER_URL 远程管控模块
     * BASE_BBS_SERVER_URL 社区论坛模块
     */
//    public static final String BASE_RC_SERVER_URL = "http://control.chuzhi.cn/api/";//生产环境
//    public static final String BASE_BBS_SERVER_URL = "http://www.chuzhi.cn/api/";//生产环境
//    public static final String BASE_GET_VERSION_URL = "http://www.chuzhi.cn/media/android/version";//生产环境
//    public static final String host = "tcp://mqtt.chuzhi.cn:1883";
//
    public static final String BASE_RC_SERVER_URL = "http://4g.chuzhi.cn/api/";//测试环境
    public static final String BASE_BBS_SERVER_URL = "http://testindex.chuzhi.cn/api/";//测试环境
    public static final String BASE_GET_VERSION_URL = "http://testindex.chuzhi.cn/media/android/version";//测试环境
    public static final String host = "tcp://testmqtt.chuzhi.cn";

//    public static final String BASE_BBS_SERVER_URL = "http://192.168.2.42:8000/api/";//本地环境
//    public static final String BASE_RC_SERVER_URL = "http://192.168.2.42:8080/api/";//本地环境
    public static final String BASE_GD_MAP_SERVER_URL = "https://restapi.amap.com/";
    public static final String BASE_GET_IP_URL = "http://whois.pconline.com.cn/";

    private static ApiRetrofit apiRetrofit;
    private Retrofit retrofit;
    private OkHttpClient client;
    private ApiServer apiServer;

    private String TAG = "ApiRetrofit";
    /**
     * 请求访问quest
     * response拦截器
     */
    
    private Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            Response response = chain.proceed(chain.request());
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MediaType mediaType = response.body().contentType();
            String content = response.body().string();
//            String message = URLDecoder.decode(content,"utf-8");
            String message = DecodeUnicodeUtil.UnicodeToCN(content);//因为html代码收到unicode转中文的影响，此代码暂时只是作为log显示
            Log.e(TAG, "----------Request Start----------------");
            Log.e(TAG, "| " + request.toString() + request.headers().toString());
//            if (message.length()<1000){
            Log.e(TAG, "| Response:" + message);
//            }
            Log.e(TAG, "----------Request End:" + duration + "毫秒----------");
            String token = SpUtils.getSharedStringData(MyApplication.Companion.getInstance(), "Token");
            return response.newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build();
        }
    };

    public ApiRetrofit() {
        client = new OkHttpClient.Builder()
                .cookieJar(new MyCookieJarImp(new LocalCookieStore(MyApplication.Companion.getInstance())))
                //添加log拦截器
                .addInterceptor(new CommonHeaderInterceptor())
                .addInterceptor(interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_BBS_SERVER_URL)
                .callFactory(new CallFactoryProxy(client) {
                    @Nullable
                    @Override
                    protected HttpUrl getNewUrl(String baseUrlName, Request request) {
                        if (baseUrlName.equals("BASE_RC")) {
                            String oldUrl = request.url().toString();
                            String newUrl = oldUrl.replace(BASE_BBS_SERVER_URL, BASE_RC_SERVER_URL);
                            return HttpUrl.get(newUrl);
                        }
                        return null;
                    }
                })
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                //支持RxJava2
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        apiServer = retrofit.create(ApiServer.class);
    }

    public static ApiRetrofit getInstance() {
        if (apiRetrofit == null) {
            synchronized (Object.class) {
                if (apiRetrofit == null) {
                    apiRetrofit = new ApiRetrofit();
                }
            }
        }
        return apiRetrofit;
    }

    public ApiServer getApiService() {
        return apiServer;
    }

    private static String getCacheKey(HttpUrl url) {
        return url.host() + ":" + url.port();
    }
}



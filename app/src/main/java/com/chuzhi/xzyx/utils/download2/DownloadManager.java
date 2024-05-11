package com.chuzhi.xzyx.utils.download2;

/**
 * @Author : wyh
 * @Time : On 2023/7/24 15:10
 * @Description : DownloadManager
 */

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 下载管理
 * Created by ${R.js} on 2018/3/22.
 */

public class DownloadManager{

    private DownloadInfo info;
    private ProgressListener progressObserver;

    private Disposable subscribe;
    private DownLoadService service;
    private long currentRead;
    private String fileAbsolutePath;
    private int progress=-1;

    private DownloadManager() {
        info = new DownloadInfo();
    }

    public static DownloadManager getInstance() {
        return Holder.manager;
    }

    public static class Holder {
        private static DownloadManager manager = new DownloadManager();
    }

    /**
     * 开始下载
     *
     * @param url
     */
    public boolean start(String url, String dir, String fileName) {
        Log.e("retrofitdownload ", "start() url = " + url);
        info.setUrl(url);
        if (TextUtils.isEmpty(dir)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        }
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        Log.e("retrofitdownload ", "start() dir = " + dir);
        fileAbsolutePath = dir + fileName;
        info.setSavePath(fileAbsolutePath);
        final DownloadInterceptor interceptor = new DownloadInterceptor(new MyDownloadProgressListener());
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(8, TimeUnit.SECONDS);
        builder.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(getBasUrl(url))
                .build();
        if (service == null) {
            service = retrofit.create(DownLoadService.class);
            info.setService(service);
        } else {
            service = info.getService();
        }

        downLoad();
        return true;
    }

    /**
     * 开始下载
     */
    private void downLoad() {
        info.setContentLength(0l);
        info.setReadLength(0l);
        Log.e("下载：", info.toString());
        subscribe = service.download("bytes=" + info.getReadLength() + "-", info.getUrl())
                /*指定线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenNetworkException())
                /* 读取下载写入文件，并把ResponseBody转成DownInfo */
                .map(new Function<ResponseBody, DownloadInfo>() {
                    @Override
                    public DownloadInfo apply(ResponseBody responseBody) throws Exception {
                        try {
                            //写入文件
                            writeCache(responseBody, new File(info.getSavePath()), info);
                        } catch (IOException e) {
                            Log.e("异常:", e.toString());
                            if (progressObserver!=null){
                                progressObserver.progressError();
                            }
                        }
                        return info;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DownloadInfo>() {
                    @Override
                    public void accept(DownloadInfo downloadInfo) throws Exception {
                        Log.e("retrofitdownload", "onNext");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("retrofitdownload", "onError" + throwable.toString());
                        if (progressObserver!=null){
                            progressObserver.progressError();
                        }
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e("retrofitdownload", "onCompleted");
                    }
                })
        ;
    }

    /**
     * 暂停下载
     */
    public void pause() {
        if (subscribe != null)
            subscribe.dispose();
    }

    /**
     * 继续下载
     */
    public void reStart() {
        downLoad();
    }

    /**
     * 进度监听
     */
    public interface ProgressListener {
        void progressChanged(int progress);

        void progressCompleted(String fileAbsolutePath);

        void progressError();
    }

    public void setProgressListener(ProgressListener progressObserver) {
        this.progressObserver = progressObserver;
    }


    /**
     * 读取baseurl
     *
     * @param url
     * @return
     */
    public static String getBasUrl(String url) {
        String head = "";
        int index = url.indexOf("://");
        if (index != -1) {
            head = url.substring(0, index + 3);
            url = url.substring(index + 3);
        }
        index = url.indexOf("/");
        if (index != -1) {
            url = url.substring(0, index + 1);
        }
        return head + url;
    }

    /**
     * 写入文件
     *
     * @param file
     * @param info
     * @throws IOException
     */
    public static void writeCache(ResponseBody responseBody, File file, DownloadInfo info) throws IOException {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        long allLength;
        if (info.getContentLength() == 0) {
            allLength = responseBody.contentLength();
        } else {
            allLength = info.getContentLength();
        }

        FileChannel channelOut = null;
        RandomAccessFile randomAccessFile = null;
        randomAccessFile = new RandomAccessFile(file, "rwd");
        channelOut = randomAccessFile.getChannel();
        MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE,
                info.getReadLength(), allLength - info.getReadLength());
//        https://www.cnblogs.com/yanduanduan/p/6046235.html
        byte[] buffer = new byte[1024 * 10];
        int len;
        int record = 0;
        while ((len = responseBody.byteStream().read(buffer)) != -1) {
            mappedBuffer.put(buffer, 0, len);
            record += len;
        }
        responseBody.byteStream().close();
        if (channelOut != null) {
            channelOut.close();
        }
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
    }

    /**
     * 下载信息
     * Created by ${R.js} on 2018/3/22.
     */

    class DownloadInfo {

        /* 存储位置 */
        private String savePath;
        /* 文件总长度 */
        private long contentLength;
        /* 下载长度 */
        private long readLength;
        /* 下载该文件的url */
        private String url;
        private DownLoadService service;


        public DownLoadService getService() {
            return service;
        }

        public void setService(DownLoadService service) {
            this.service = service;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSavePath() {
            return savePath;
        }

        public void setSavePath(String savePath) {
            this.savePath = savePath;
        }

        public long getContentLength() {
            return contentLength;
        }

        public void setContentLength(long contentLength) {
            this.contentLength = contentLength;
        }

        public long getReadLength() {
            return readLength;
        }

        public void setReadLength(long readLength) {
            this.readLength = readLength;
        }

        @Override
        public String toString() {
            return "DownloadInfo{" +
                    "savePath='" + savePath + '\'' +
                    ", contentLength=" + contentLength +
                    ", readLength=" + readLength +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
    /**
     * 下载的Service
     * Created by ${R.js} on 2018/3/22.
     */

    interface DownLoadService {
        /**
         * @param start 从某个字节开始下载数据
         * @param url   文件下载的url
         * @return Observable
         * @Streaming 这个注解必须添加，否则文件全部写入内存，文件过大会造成内存溢出
         */
        @Streaming
        @GET
        Observable<ResponseBody> download(@Header("RANGE") String start, @Url String url);
    }
    interface DownloadProgressListener {

        /**
         * @param read 已下载长度
         * @param contentLength 总长度
         * @param done 是否下载完毕
         */
        void progress(long read, long contentLength, boolean done);

    }
    class MyDownloadProgressListener implements DownloadProgressListener {
        @Override
        public void progress(long read, long contentLength,final  boolean done) {
            // 该方法仍然是在子线程，如果想要调用进度回调，需要切换到主线程，否则的话，会在子线程更新UI，直接错误
            // 如果断电续传，重新请求的文件大小是从断点处到最后的大小，不是整个文件的大小，info中的存储的总长度是
            // 整个文件的大小，所以某一时刻总文件的大小可能会大于从某个断点处请求的文件的总大小。此时read的大小为
            // 之前读取的加上现在读取的
            if (info.getContentLength() > contentLength) {
                read = read + (info.getContentLength() - contentLength);
            } else {
                info.setContentLength(contentLength);
            }
            info.setReadLength(read);

            Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Integer value) {
                    if (progressObserver != null) {
                        if (!done) {
                            int progressNew = (int) (100 * info.getReadLength() / info.getContentLength());
                            if (progressNew > progress) {
                                progress=progressNew;
                                progressObserver.progressChanged(progress);
                            }
                        } else {
                            progress=-1;
                            progressObserver.progressCompleted(fileAbsolutePath);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    progressObserver.progressError();
                }

                @Override
                public void onComplete() {

                }
            });
        }
    }

    /**
     * 下载进度拦截器
     * Created by ${R.js} on 2018/3/22.
     */

    class DownloadInterceptor implements Interceptor {

        private DownloadProgressListener listener;

        public DownloadInterceptor(DownloadProgressListener listener) {
            this.listener = listener;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());

            return originalResponse.newBuilder()
                    .body(new DownloadResponseBody(originalResponse.body(), listener))
                    .build();
        }
    }
    class DownloadResponseBody extends ResponseBody {

        private ResponseBody responseBody;
        private DownloadProgressListener listener;
        private BufferedSource bufferedSource;

        public DownloadResponseBody(ResponseBody responseBody, DownloadProgressListener listener) {
            this.responseBody = responseBody;
            this.listener = listener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    if (null != listener) {
                        listener.progress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    }
                    return bytesRead;
                }
            };

        }
    }
    /**
     * retry条件
     */
    class RetryWhenNetworkException implements Function<Observable<? extends Throwable>, Observable<?>> {
        //    retry次数
        private int count = 3;
        //    延迟
        private long delay = 3000;
        //    叠加延迟
        private long increaseDelay = 3000;

        public RetryWhenNetworkException() {

        }

        public RetryWhenNetworkException(int count, long delay) {
            this.count = count;
            this.delay = delay;
        }

        public RetryWhenNetworkException(int count, long delay, long increaseDelay) {
            this.count = count;
            this.delay = delay;
            this.increaseDelay = increaseDelay;
        }
        @Override
        public Observable<?> apply(Observable<? extends Throwable> observable) throws Exception {
            return  observable.zipWith(Observable.range(1, count + 1), new BiFunction<Throwable, Integer, RetryWhenNetworkException.Wrapper>(){

                @Override
                public RetryWhenNetworkException.Wrapper apply(Throwable throwable, Integer integer) throws Exception {
                    //压缩规则 合并后的结果是一个Observable<Wrapper>
                    return new RetryWhenNetworkException.Wrapper(throwable, integer);
                }
            }).flatMap(new Function<RetryWhenNetworkException.Wrapper, ObservableSource<?>>() {
                @Override
                public ObservableSource<?> apply(RetryWhenNetworkException.Wrapper wrapper) throws Exception {
                    //转换规则
                    if ((wrapper.throwable instanceof ConnectException
                            || wrapper.throwable instanceof SocketTimeoutException
                            || wrapper.throwable instanceof TimeoutException)
                            && wrapper.index < count + 1) { //如果超出重试次数也抛出错误，否则默认是会进入onCompleted
                        return Observable.timer(delay + (wrapper.index - 1) * increaseDelay, TimeUnit.MILLISECONDS);

                    }
                    return Observable.error(wrapper.throwable);
                }
            });
        }

        private class Wrapper {
            private int index;
            private Throwable throwable;

            public Wrapper(Throwable throwable, int index) {
                this.index = index;
                this.throwable = throwable;
            }
        }
    }
}

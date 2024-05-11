package com.chuzhi.xzyx.utils.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @Author : wyh
 * @Time : On 2023/7/21 16:34
 * @Description : FileDownloader
 */
public class FileDownloader {
    /**
     * 下载文件法1(使用Handler更新UI)
     *
     * @param observable      下载被观察者
     * @param destDir         下载目录
     * @param fileName        文件名
     * @param progressHandler 进度handler
     */
    public static void downloadFile(Observable<ResponseBody> observable, final String destDir,
                                    final String fileName, final DownloadProgressHandler progressHandler) {
        final DownloadInfo downloadInfo = new DownloadInfo();
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        addDisposable(d);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        InputStream inputStream = null;
                        long total = 0;
                        long responseLength;
                        FileOutputStream fos = null;
                        try {
                            byte[] buf = new byte[1024 * 8];
                            int len;
                            responseLength = responseBody.contentLength();
                            inputStream = responseBody.byteStream();
                            final File file = new File(destDir, fileName);
                            downloadInfo.setFile(file);
                            downloadInfo.setFileSize(responseLength);
                            File dir = new File(destDir);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            fos = new FileOutputStream(file);
                            int progress = 0;
                            int lastProgress=-1;
                            long startTime = System.currentTimeMillis(); // 开始下载时获取开始时间
                            while ((len = inputStream.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                total += len;

                                progress = (int) (total * 100 / responseLength);
                                long curTime = System.currentTimeMillis();
                                long usedTime = (curTime - startTime) / 1000;
                                if (usedTime == 0) {
                                    usedTime = 1;
                                }
                                long speed = (total / usedTime); // 平均每秒下载速度
                                // 如果进度与之前进度相等，则不更新，如果更新太频繁，则会造成界面卡顿
                                if (progress != lastProgress) {
                                    downloadInfo.setSpeed(speed);
                                    downloadInfo.setProgress(progress);
                                    downloadInfo.setCurrentSize(total);
                                    progressHandler.sendMessage(DownloadProgressHandler.DOWNLOAD_PROGRESS, downloadInfo);
                                }
                                lastProgress = progress;
                            }
                            fos.flush();
                            downloadInfo.setFile(file);
                            progressHandler.sendMessage(DownloadProgressHandler.DOWNLOAD_SUCCESS, downloadInfo);

                        } catch (final Exception e) {
                            downloadInfo.setErrorMsg(e);
                            progressHandler.sendMessage(DownloadProgressHandler.DOWNLOAD_FAIL, downloadInfo);
                        } finally {
                            try {
                                if (fos != null) {
                                    fos.close();
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {//new Consumer<Throwable>
                        downloadInfo.setErrorMsg(e);
                        progressHandler.sendMessage(DownloadProgressHandler.DOWNLOAD_FAIL, downloadInfo);

                    }

                    @Override
                    public void onComplete() {// new Action()

                    }
                });
    }
}

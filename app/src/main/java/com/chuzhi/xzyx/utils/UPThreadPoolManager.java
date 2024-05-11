package com.chuzhi.xzyx.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author : wyh
 * @Time : On 2023/7/12 16:59
 * @Description : UPThreadPoolManager 线程池管理
 */

public class UPThreadPoolManager {

    private static UPThreadPoolManager mInstance = new UPThreadPoolManager();

    public static final synchronized UPThreadPoolManager getInstance() {
        if (mInstance == null) {
            mInstance = new UPThreadPoolManager();
        }
        return mInstance;
    }

    private int corePoolSize; // 核心线程池的数量，同时能够执行的线程数量
    private int maximumPoolSize;// 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
    private long keepAliveTime = 3;// 存活时间
    private TimeUnit unit = TimeUnit.MINUTES;
    private ThreadPoolExecutor executor;

    private UPThreadPoolManager() {
        /**
         * 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行
         */
        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        maximumPoolSize = corePoolSize; // 虽然maximumPoolSize用不到，但是需要赋值，否则报错
        executor = new ThreadPoolExecutor(corePoolSize, // 当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                maximumPoolSize, // 5,先corePoolSize,然后new
                // LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                keepAliveTime, // 表示的是maximumPoolSize当中等待任务的存活时间
                unit, new LinkedBlockingQueue<Runnable>(), // 缓冲队列，用于存放等待任务，Linked的先进先出（无界队列）
                Executors.defaultThreadFactory(), // 创建线程的工厂
                new ThreadPoolExecutor.AbortPolicy() // 用来对超出maximumPoolSize的任务的处理策略
        );
        executor.allowCoreThreadTimeOut(true); // keepAliveTime同样作用于核心线程
    }

    /**
     * 执行任务
     */
    public void execute(Runnable runnable) {
        if (runnable == null)
            return;

        executor.execute(runnable);
    }

    /**
     * 从线程池中移除任务
     */
    public void remove(Runnable runnable) {
        if (runnable == null)
            return;

        executor.remove(runnable);
    }
}
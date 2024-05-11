package com.chuzhi.xzyx.utils.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * @Author : wyh
 * @Time : On 2023/9/21 17:07
 * @Description : ScheduledTask
 */

public class ScheduledTask {

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void startCountdown(long seconds,long m, Runnable task) {
        executorService.scheduleAtFixedRate(task, seconds, m,TimeUnit.MILLISECONDS);
    }
    public void stop(){
        if (executorService!=null){
            executorService.shutdownNow();
        }
        executorService = null;
    }
}


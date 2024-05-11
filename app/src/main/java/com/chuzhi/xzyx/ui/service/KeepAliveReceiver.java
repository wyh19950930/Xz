package com.chuzhi.xzyx.ui.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @Author : wyh
 * @Time : On 2023/6/21 18:02
 * @Description : KeepAliveReceiver
 */
public class KeepAliveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, KeepAliveService.class);
        context.startService(service);
    }
}
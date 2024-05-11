package com.chuzhi.xzyx.ui.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @Author : wyh
 * @Time : On 2023/11/15 11:19
 * @Description : NetWorkReceiver
 */
public class NetWorkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NetWoekService.class);
        context.startService(service);
    }
}

package com.chuzhi.xzyx.ui.mqtt;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * @Author : wyh
 * @Time : On 2023/6/28 19:58
 * @Description : MyServiceConnection
 */
public class MyServiceConnection implements ServiceConnection {

    private MQTTService mqttService;
    private IGetMessageCallBack IGetMessageCallBack;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mqttService = ((MQTTService.CustomBinder)iBinder).getService();
        mqttService.setIGetMessageCallBack(IGetMessageCallBack);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public MQTTService getMqttService(){
        return mqttService;
    }

    public void setIGetMessageCallBack(IGetMessageCallBack IGetMessageCallBack){
        this.IGetMessageCallBack = IGetMessageCallBack;
    }
}
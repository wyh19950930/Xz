package com.chuzhi.xzyx.ui.mqtt;

/**
 * @Author : wyh
 * @Time : On 2023/6/28 19:57
 * @Description : IGetMessageCallBack
 */
public interface IGetMessageCallBack {
     void setMessage(String message,int pos,String topic);//设备位置回调
     void setCsq(String message,int pos);//信号强度
     void setBattery(String message,int pos);//设备电量
     void setPowerStatus(String message,int pos);//power键状态
     void setBacklightStatus(String message,int pos);//背光状态
     void setClockStatus(String message,int pos);//锁机状态
     void setStolenStatus(String message,int pos);//被盗状态
     void setComputerStatus(String message,int pos);//设备开关机状态
     void setSsdCommand(String message,int pos);//硬盘状态
     void setSsdCommandSN(String message,int pos);//硬盘SN号
     void setUsbStatus(String message,int pos);//usb端口状态
     void setTypeStatus(String message,int pos);//type-c端口状态
     void setWifiStatus(String message,int pos);//wifi端口状态
     void setBlueToothStatus(String message,int pos);//蓝牙端口状态
}

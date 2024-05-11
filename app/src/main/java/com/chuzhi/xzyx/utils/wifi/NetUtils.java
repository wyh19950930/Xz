package com.chuzhi.xzyx.utils.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * @Author : wyh
 * @Time : On 2023/11/14 19:48
 * @Description : NetUtils
 */

public class NetUtils {
    public static final int NETWORK_NONE = 0; // 没有网络连接
    public static final int NETWORK_WIFI = 1; // wifi连接
    public static final int NETWORK_2G = 2; // 2G
    public static final int NETWORK_3G = 3; // 3G
    public static final int NETWORK_4G = 4; // 4G
    public static final int NETWORK_5G = 5; // 5G
    public static final int NETWORK_MOBILE = 6; // 手机流量

    /**
     * 获取运营商名字
     *
     * @param context context
     * @return int
     */
    public static String getOperatorName(Context context) {
        /*
         * getSimOperatorName()就可以直接获取到运营商的名字
         * 也可以使用IMSI获取，getSimOperator()，然后根据返回值判断，例如"46000"为移动
         * IMSI相关链接：http://baike.baidu.com/item/imsi
         */
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // getSimOperatorName就可以直接获取到运营商的名字
        return telephonyManager.getSimOperatorName();
    }

    /**
     * 获取当前网络连接的类型
     *
     * @param context context
     * @return int
     */
    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // 获取网络服务
        if (null == connManager) { // 为空则认为无网络
            return NETWORK_NONE;
        }
        // 获取网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORK_NONE;
        }
        // 判断是否为WIFI
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORK_WIFI;
                }
            }
        }
        // 若不是WIFI，则去判断是2G、3G、4G网
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") int networkType = telephonyManager.getNetworkType();
        switch (networkType) {
	/*
	   GPRS : 2G(2.5) General Packet Radia Service 114kbps
	   EDGE : 2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
	   UMTS : 3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
	   CDMA : 2G 电信 Code Division Multiple Access 码分多址
	   EVDO_0 : 3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
	   EVDO_A : 3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
	   1xRTT : 2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
	   HSDPA : 3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
	   HSUPA : 3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
	   HSPA : 3G (分HSDPA,HSUPA) High Speed Packet Access
	   IDEN : 2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
	   EVDO_B : 3G EV-DO Rev.B 14.7Mbps 下行 3.5G
	   LTE : 4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
	   EHRPD : 3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
	   HSPAP : 3G HSPAP 比 HSDPA 快些
	   */
            // 2G网络
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_GSM:
                return NETWORK_2G;
            // 3G网络
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return NETWORK_3G;
            // 4G网络
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_4G;
            case TelephonyManager.NETWORK_TYPE_NR:
                return NETWORK_5G;
            default:
                return NETWORK_MOBILE;
        }
    }

    /**
     * 判断网络是否连接
     *
     * @param context context
     * @return true/false
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否wifi连接
     *
     * @param context context
     * @return true/false
     */
    public static synchronized boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                int networkInfoType = networkInfo.getType();
                if (networkInfoType == ConnectivityManager.TYPE_WIFI || networkInfoType == ConnectivityManager.TYPE_ETHERNET) {
                    return networkInfo.isConnected();
                }
            }
        }
        return false;
    }


    public static int getNetworkWifiLevel(Context context) {
        if (!isWifiConnected(context)) {
            return 0;
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //获得信号强度值
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int level = wifiInfo.getRssi();
            //根据获得信号的强度发送信息
            if (level <= 0 && level >= -50) {//最强
                System.out.println("level==========1===========" + level);
                return 1;
            } else if (level < -50 && level >= -70) {//较强
                System.out.println("level===========2==========" + level);
                return 2;
            } else if (level < -70 && level >= -80) {//较弱
                System.out.println("level==========3===========" + level);
                return 3;
            } else if (level < -80 && level >= -100) {//微弱
                System.out.println("level==========4===========" + level);
                return 4;
            } else {
                System.out.println("level==========5===========" + level);
                return 5;
            }
        }

        return 0;
    }


}

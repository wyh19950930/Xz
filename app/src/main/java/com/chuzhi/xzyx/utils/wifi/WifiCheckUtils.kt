package com.chuzhi.xzyx.utils.wifi

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log

/**

 * @Author : wyh

 * @Time : On 2023/11/14 18:38

 * @Description : WifiCheckUtils

 */
/**
 * @ClassName: WifiCheckUtils
 * @Description: 页面切换 检测wifi信号强弱
 * @Author: 有时有晌
 * @Date: 2022/1/26 16:00
 */
object WifiCheckUtils {
    fun checkWifiLevle(context: Context, isConnect:Boolean):Int{
        var wifiType = 3
        try {
            var wifiManager = context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
            var wifiInfo = wifiManager.connectionInfo as WifiInfo
            var wifiLever = wifiInfo.rssi
            if (isConnect){
                when (wifiLever) {
                    in -50..0 -> {//信号最强
                        wifiType = 0
                    }
                    in -70..-50 -> {//较强
                        wifiType = 1
                    }
                    in -80..-70 -> {//较弱
                        wifiType = 2
                    }
                    in -100..-80 -> {//微弱
                        wifiType = 3
                    }
                    else->{
                        wifiType = 3
                    }
                }
//                Log.e("TAG","==-->WiFi信号强度$wifiType")
                setWifiLevel(wifiType)
            }
        } catch (e: Exception) {
        }
        return wifiType
    }
    /**
     * 设置wifi信号等级,这里是设置MainActivity页面的wifiLevel，用来设置wifi强度
     */
    private fun setWifiLevel(wifiLevel:Int){
        try {
//            MainActivity.act.wifiLevel = wifiLevel
        } catch (e: Exception) {
        }
    }
}

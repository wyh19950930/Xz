package com.chuzhi.xzyx.utils.wifi

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**

 * @Author : wyh

 * @Time : On 2023/11/14 18:40

 * @Description : CheckNetStatus

 */
/**
 * @ClassName: CheckNetStatus
 * @Description: 检测网络状态是否通畅
 * @Author: 有时有晌
 * @Date: 2022/1/24 16:47
 */
object CheckNetStatus {
    val TAG="CheckNetStatus"
    fun check(mContext: Activity, netStatusCallBack: NetStatusCallBack){
        var url ="www.baidu.com"
        //TaskManager 这个类是我本地在用的，大家用时候随便开个线程什么的就可以了。
        Thread{
            Log.e(TAG,"==-->WIFI可用检测")
            val stringBuilder = StringBuilder()
            val netState = toCheck(url, stringBuilder)
            Log.e(TAG, "ping ${url} content \n${stringBuilder.toString()}")
            Log.e(TAG, "ping ${url} state:${netState}")
            mContext.runOnUiThread {
                netStatusCallBack.netStatus(netState)
            }
        }.start()
    }
    fun toCheck(ipAddress: String, stringBuilder: StringBuilder): Boolean {
        var result: String? = null
        try {
            var ip = "www.baidu.com" // ping 的地址，可以换成任何一种可靠的外网
            if (!TextUtils.isEmpty(ipAddress)) {
                ip = ipAddress
            }
            val p = Runtime.getRuntime().exec("ping -c 4 -w 100 $ip") // ping网址4次 响应100ms
            // 读取ping的内容，可以不加
            val input = p.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(input))
            var content: String? = ""
            while (bufferedReader.readLine().also { content = it } != null) {
                stringBuilder.append(content)
                stringBuilder.append("\n")
            }
            // ping的状态
            val status = p.waitFor()
            if (status == 0) {
                result = "success"
                return true
            } else {
                result = "failed" //PING www.a.shifen.com (180.101.49.11) 56(84) bytes of data.   //64 bytes from 180.101.49.11: icmp_seq=2 ttl=49 time=53.3 ms  //64 bytes from 180.101.49.11: icmp_seq=3 ttl=49 time=51.3 ms  //64 bytes from 180.101.49.11: icmp_seq=4 ttl=49 time=50.2 ms
            }
        } catch (e: IOException) {
            result = "IOException"
        } catch (e: InterruptedException) {
            result = "InterruptedException"
        } finally {
            Log.e(TAG, "result = $result")
        }
        return false
    }
    interface NetStatusCallBack{
        fun netStatus(isConnect:Boolean)
    }
}

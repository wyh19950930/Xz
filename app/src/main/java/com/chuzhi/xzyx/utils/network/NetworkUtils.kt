package com.chuzhi.xzyx.utils.network


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * @Author: let
 * @date: 2022/11/15 17:31
 * @description:
 */
object NetworkUtils {
    @JvmStatic
    @SuppressLint("MissingPermission")
    fun getNetWorkState(context: Context): NetworkStatus {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mobileNetInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            val wifiNetInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (mobileNetInfo != null && mobileNetInfo.isAvailable) {
                //WIFI和移动网络均未连接
                NetworkStatus.MOBILE
            } else if (wifiNetInfo != null && wifiNetInfo.isAvailable) {
                //WIFI和移动网络均未连接
                NetworkStatus.WIFI
            } else {
                NetworkStatus.NONE
            }
        } else {
            when {
                isMobileConnected(context) -> {
                    NetworkStatus.MOBILE
                }
                isWifiConnected(context) -> {
                    NetworkStatus.WIFI
                }
                else -> {
                    NetworkStatus.NONE
                }
            }
        }

//            //获取所有网络连接的信息
//            Network[] networks = connMgr.getAllNetworks();
//            //通过循环将网络信息逐个取出来
//            for (int i = 0; i < networks.length; i++) {
//                //获取ConnectivityManager对象对应的NetworkInfo对象
//                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
//                if (networkInfo.isConnected()) {
//                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//                        return NetwordStatus.WIFI;
//                    } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//                        return NetwordStatus.MOBILE;
//                    }
//                }
//            }
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    fun isOnline(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return networkInfo != null && networkInfo.isAvailable
        } else {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork)
            if (networkCapabilities != null) {
                return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }
        }
        return false
    }

    @SuppressLint("MissingPermission")
    fun isWifiConnected(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (networkInfo != null) {
                return networkInfo.isAvailable
            }
        } else {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork)
            if (networkCapabilities != null) {
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
        }
        return false
    }

    @SuppressLint("MissingPermission")
    fun isMobileConnected(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (networkInfo != null) {
                return networkInfo.isAvailable
            }
        } else {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork)
            if (networkCapabilities != null) {
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
        }
        return false
    }
}
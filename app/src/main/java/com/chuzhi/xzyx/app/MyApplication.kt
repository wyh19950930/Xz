package com.chuzhi.xzyx.app

import android.app.Application
import androidx.multidex.MultiDex
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.chuzhi.xzyx.utils.network.NetworkListenerHelper
import me.jingbin.smb.BySMB

class MyApplication : Application(){
    companion object{
        var mContext :MyApplication ?=null
        fun getInstance():MyApplication{
            return mContext!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        //加在这里
        MultiDex.install(this)
        // 注册网络状态监听；
        NetworkListenerHelper.init(this).registerNetworkListener()
        if (! Python.isStarted()) {
            Python.start( AndroidPlatform(this));
        }
        //初始化smb
        BySMB.initProperty()
    }
}
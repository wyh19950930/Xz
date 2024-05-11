package com.chuzhi.xzyx.utils;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

import com.chuzhi.xzyx.ui.activity.me.NetWorkSignalActivity;
import com.chuzhi.xzyx.utils.wifi.NetUtils;

/**
 * @Author : wyh
 * @Time : On 2023/11/15 11:30
 * @Description : MyPhoneListener
 */
public class MyPhoneListener extends PhoneStateListener {
    private Context context;

    public MyPhoneListener(Context context) {
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        super.onCallStateChanged(state, phoneNumber);
        Log.e("11","22");
    }

    //获取信号强度
    @Override
    public void onSignalStrengthChanged(int asu) {
        super.onSignalStrengthChanged(asu);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        //获取网络信号强度
        //获取0-4的5种信号级别，越大信号越好,但是api23开始才能用
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int level = signalStrength.getLevel();
            System.out.println("level====" + level);
        }
        int cdmaDbm = signalStrength.getCdmaDbm();
        int evdoDbm = signalStrength.getEvdoDbm();
        System.out.println("cdmaDbm=====" + cdmaDbm);
        System.out.println("evdoDbm=====" + evdoDbm);

        int gsmSignalStrength = signalStrength.getGsmSignalStrength();
        int dbm = -113 + 2 * gsmSignalStrength;
        System.out.println("dbm===========" + dbm);

        //获取网络类型
        int netWorkType = NetUtils.getNetworkState(context);
        switch (netWorkType) {
            case NetUtils.NETWORK_WIFI:
                Log.e("当前网络为wifi,信号强度为：" , gsmSignalStrength+"");
                break;
            case NetUtils.NETWORK_2G:
                Log.e("当前网络为2G移动网络,信号强度为：" , gsmSignalStrength+"");
                break;
            case NetUtils.NETWORK_3G:
                Log.e("当前网络为3G移动网络,信号强度为：" , gsmSignalStrength+"");
                break;
            case NetUtils.NETWORK_4G:
                Log.e("当前网络为4G移动网络,信号强度为：" , gsmSignalStrength+"");
                break;
            case NetUtils.NETWORK_5G:
                Log.e("当前网络为5G移动网络,信号强度为：" , gsmSignalStrength+"");
                break;
            case NetUtils.NETWORK_NONE:
                Log.e("当前没有网络,信号强度为：" , gsmSignalStrength+"");
                break;
            case -1:
                Log.e("当前网络错误,信号强度为：" , gsmSignalStrength+"");
                break;
        }
    }
}

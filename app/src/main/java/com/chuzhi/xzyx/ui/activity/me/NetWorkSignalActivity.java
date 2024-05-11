package com.chuzhi.xzyx.ui.activity.me;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.ui.service.NetWoekService;
import com.chuzhi.xzyx.utils.wifi.NetUtils;

public class NetWorkSignalActivity extends AppCompatActivity {

    private TextView mTextView;
    public TelephonyManager mTelephonyManager;
    public PhoneStatListener mListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work_signal);

        Intent service = new Intent(this, NetWoekService.class);
        startService(service);

        mTextView = findViewById(R.id.tv_network_signal);
        //获取telephonyManager
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //开始监听
        mListener = new PhoneStatListener();
        //监听信号强度
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //用户不在当前页面时，停止监听
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_NONE);
    }

    @SuppressWarnings("deprecation")
    private class PhoneStatListener extends PhoneStateListener {
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
            int netWorkType = NetUtils.getNetworkState(NetWorkSignalActivity.this);
            switch (netWorkType) {
                case NetUtils.NETWORK_WIFI:
                    mTextView.setText("当前网络为wifi,信号强度为：" + gsmSignalStrength);
                    break;
                case NetUtils.NETWORK_2G:
                    mTextView.setText("当前网络为2G移动网络,信号强度为：" + gsmSignalStrength);
                    break;
                case NetUtils.NETWORK_3G:
                    mTextView.setText("当前网络为3G移动网络,信号强度为：" + gsmSignalStrength);
                    break;
                case NetUtils.NETWORK_4G:
                    mTextView.setText("当前网络为4G移动网络,信号强度为：" + gsmSignalStrength);
                    break;
                case NetUtils.NETWORK_5G:
                    mTextView.setText("当前网络为5G移动网络,信号强度为：" + gsmSignalStrength);
                    break;
                case NetUtils.NETWORK_NONE:
                    mTextView.setText("当前没有网络,信号强度为：" + gsmSignalStrength);
                    break;
                case -1:
                    mTextView.setText("当前网络错误,信号强度为：" + gsmSignalStrength);
                    break;
            }
        }
    }
}
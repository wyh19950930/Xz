package com.chuzhi.xzyx.ui.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.ui.activity.me.NetWorkSignalActivity;
import com.chuzhi.xzyx.utils.MyPhoneListener;

/**
 * @Author : wyh
 * @Time : On 2023/11/15 11:16
 * @Description : NetWoekService
 */
public class NetWoekService extends Service {
    private static final String CHANNEL_ID = "MY_CHANNEL_ID";
    private static final String CHANNEL_NAME = "MY_CHANNEL_NAME";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyPhoneListener phoneStateListener = new MyPhoneListener(getApplicationContext());
        TelephonyManager telephonymanager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        telephonymanager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        Intent notificationIntent = new Intent(this, NetWorkSignalActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.text_keep_alive))
                    .setSmallIcon(R.mipmap.app_icon_foreground)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.text_keep_alive))
                    .setSmallIcon(R.mipmap.app_icon_foreground)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .build();
        }

        startForeground(12345, notification);
        // 定时发送广播
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getApplicationContext(), KeepAliveReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, pendingIntent);

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.cancel(12345);
        stopForeground(true);
    }
}

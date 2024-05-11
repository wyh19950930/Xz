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
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.ui.activity.MainActivity;

/**
 * @Author : wyh
 * @Time : On 2023/6/21 18:00
 * @Description : KeepAliveService
 */
public class KeepAliveService extends Service {
    private static final int NOTIFY_ID = 1001;
    private static final String CHANNEL_ID = "MY_CHANNEL_ID";
    private static final String CHANNEL_NAME = "MY_CHANNEL_NAME";
    private static final String TAG = "KeepAliveService";

    private PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

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

        startForeground(NOTIFY_ID, notification);

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

        Log.i(TAG, "onDestroy");

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);

        stopForeground(true);
    }
}
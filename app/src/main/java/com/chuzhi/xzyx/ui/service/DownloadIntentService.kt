package com.chuzhi.xzyx.ui.service

import android.annotation.SuppressLint
import android.app.*
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.ApiRetrofit
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.app.MyApplication
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateNewEntity
import com.chuzhi.xzyx.ui.bean.eventbus.DownLoadEBEntity
import com.chuzhi.xzyx.utils.AlertDialogIos
import com.chuzhi.xzyx.utils.UpdateApkUtils
import com.chuzhi.xzyx.utils.UpdateApkUtils1
import com.chuzhi.xzyx.utils.apputils.AppUtils
import com.chuzhi.xzyx.utils.download2.DownloadManager
import com.chuzhi.xzyx.utils.network.NetworkListenerHelper
import com.chuzhi.xzyx.utils.network.NetworkStatus
import com.chuzhi.xzyx.widget.CircleProgress
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * @Author : wyh
 * @Time : On 2023/12/12 14:38
 * @Description : DownloadIntentService
 */
class DownloadIntentService : IntentService("InitializeService"), NetworkListenerHelper.NetworkConnectedListener  {
    private val progress = 0
    private var remoteViews: RemoteViews? = null
    private var downloadId = 0
    private var downloadUrl = ""
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun stopService(name: Intent): Boolean {
        return super.stopService(name)
    }

    private val CHANNEL_ID = "MY_CHANNEL_ID"
    private val CHANNEL_NAME = "MY_CHANNEL_NAME"

    private var notification: Notification? = null
    private var manager: NotificationManager? = null

    @SuppressLint("RemoteViewLayout")
    override fun onHandleIntent(intent: Intent?) {

        // 网络状态回调；
        NetworkListenerHelper.addListener(this)
        downloadId = intent!!.extras!!.getInt("download_id")
        downloadUrl = intent!!.extras!!.getString("download_url").toString()

        //下载文件
        remoteViews = RemoteViews(packageName, R.layout.notify_download)
        remoteViews!!.setProgressBar(R.id.pb_progress, 100, progress, false)
        remoteViews!!.setTextViewText(R.id.tv_progress, "小志云享更新包已下载$progress%")

        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            manager!!.createNotificationChannel(channel)
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContent(remoteViews)
                .setSmallIcon(R.mipmap.app_icon_desktop)
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
        } else {
            NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContent(remoteViews)
                .setSmallIcon(R.mipmap.app_icon_desktop)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
        }

        if (manager != null) {
            manager!!.notify(downloadId, notification)
        }

        var downLoadEntity = DownLoadEBEntity()
        downloadManager = DownloadManager.getInstance()
        downloadManager!!.setProgressListener(object : DownloadManager.ProgressListener {
            override fun progressChanged(progress: Int) {
                remoteViews!!.setProgressBar(R.id.pb_progress, 100, progress, false)
                remoteViews!!.setTextViewText(R.id.tv_progress, "小志云享更新包已下载$progress%")
                manager!!.notify(downloadId, notification)
                downLoadEntity.progress = progress
                EventBus.getDefault().postSticky(downLoadEntity)
                AppCache.getInstance().apkUploadIng = 1
            }

            override fun progressCompleted(fileAbsolutePath: String?) {
                manager!!.cancel(downloadId)
                downLoadEntity.path = fileAbsolutePath
                EventBus.getDefault().postSticky(downLoadEntity)
                AppCache.getInstance().apkUploadIng = 0
            }

            override fun progressError() {
            }
        })
        downloadManager!!.start(
            downloadUrl,
            "/storage/emulated/0/Download",
            "xzyx.apk"
        )
        Log.e("service", downloadId.toString())
    }


    fun setCompositeDisposable() {
        //暂停下载
        if (!compositeDisposable.isDisposed) {
            if (compositeDisposable.size() != 0) {
                compositeDisposable.clear()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private var downloadManager: DownloadManager? = null

    companion object {
        private const val TAG = "DownloadIntentService"

        //retrofit订阅事件的监听
        var compositeDisposable = CompositeDisposable()
    }

    override fun onNetworkConnected(isConnected: Boolean, networkStatus: NetworkStatus?) {
        if (isConnected) {
            Log.e("当前网络",isConnected.toString())
            if (downloadManager!=null){
                downloadManager!!.reStart()
            }
        }else{
            Log.e("当前网络",isConnected.toString())
        }
    }
}
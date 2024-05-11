package com.chuzhi.xzyx.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.ApiRetrofit.BASE_BBS_SERVER_URL
import com.chuzhi.xzyx.app.MyApplication.Companion.getInstance
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateNewEntity
import com.chuzhi.xzyx.utils.apputils.AppUtils
import com.chuzhi.xzyx.utils.download2.DownloadManager
import com.chuzhi.xzyx.widget.CircleProgress
import com.tbruyelle.rxpermissions2.RxPermissions
import java.io.File


/**

 * @Author : wyh

 * @Time : On 2023/9/6 14:47

 * @Description : UpdateApkUtils

 */
class UpdateApkUtils {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var updatePop: AlertDialogIos? = null
        private var installPop: AlertDialogIos? = null
        private var downloadProgressDialog: AlertDialog? = null
        private var downloadPgs: CircleProgress? = null
        private var downloadContinue: TextView? = null
        public var mContext: Context? = null
        public var mActivity: Activity? = null
        private var rxPermissions: RxPermissions? = null
        private var permissionPop: AlertDialogIos? = null
        public fun updateApkUtils(context: Context, entity: VersionUpdateNewEntity) {
            mContext = context
            showDialog()
            updatePop(entity)
        }

        public fun updateApkUtils(
            activity: Activity,
            context: Context,
            entity: VersionUpdateNewEntity
        ) {
            mActivity = activity
            mContext = context
            rxPermissions = RxPermissions(activity)
            permissionPop()
            showDialog()
            updatePop(entity)
        }

        private var fileName = ""
        private fun updatePop(entity: VersionUpdateNewEntity) {
            fileName = entity.filename
            updatePop = AlertDialogIos(mContext).builder()
                .setTitle("发现新版本：v" + entity.last_version)
                .setMsg(entity.description)
                .setCancelable(entity.isForce_update != "True")
//            .setNegativeButton("取消", R.color.gray,null)
                .setPositiveButtonNoDismiss("立即更新", R.color.text_default, View.OnClickListener {


                    rxPermissions!!.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).subscribe { granted ->

                        if (granted) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                //判断是否有管理外部存储的权限
                                if (!Environment.isExternalStorageManager()) {
                                    Log.e("android11", "需要跳转到权限")
                                    goManagerFileAccess(mContext!!)
                                } else {
                                    val replace = BASE_BBS_SERVER_URL.replace("api/", "")
                                    downloadApk(replace + "media/android/" + fileName)
                                    updatePop!!.dismiss()
                                }
                            } else {
                                val replace = BASE_BBS_SERVER_URL.replace("api/", "")
                                downloadApk(replace + "media/android/" + fileName)
                                updatePop!!.dismiss()
                            }
                        } else {
                            permissionPop!!.show()
                        }
                    }

                })
            if (entity.isForce_update != "True") {
                updatePop!!.setNegativeButton("残忍拒绝", R.color.gray, null)
            }
            updatePop!!.show()
        }

        private fun permissionPop() {
            permissionPop = AlertDialogIos(mContext).builder()
                .setTitle("获取读写权限")
                .setMsg("小志云享需要更新版本以开启读写权限\n请在设置中开启权限")
//            .setNegativeButton("取消", R.color.gray,null)
                .setPositiveButton("设置", R.color.text_default, View.OnClickListener {

                    //跳转应用消息，间接打开应用权限设置-效率高
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri: Uri = Uri.fromParts("package", mContext!!.packageName, null)
                    intent.data = uri
                    mContext!!.startActivity(intent)
                })
        }

        private var downloadManager: DownloadManager? = null
        private fun downloadApk(url: String) {
            downloadManager = DownloadManager.getInstance()
            downloadManager!!.setProgressListener(object : DownloadManager.ProgressListener {
                override fun progressChanged(progress: Int) {
                    Log.e("retrofitdownload", "progress = " + progress)
                    downloadProgressDialog!!.show()
                    downloadPgs!!.SetCurrent(progress)
                    if (progress == 100) {
                        downloadProgressDialog!!.dismiss()
                    }
                }

                override fun progressCompleted(fileAbsolutePath: String?) {
                    installApk(fileAbsolutePath)
                }

                override fun progressError() {
                    downloadContinue!!.visibility = View.VISIBLE
                }
            })
            downloadManager!!.start(
                url,
                "/storage/emulated/0/Download",
                "xzyx.apk"
            )
        }

        //安装apk弹框
        private fun installApk(fileAbsolutePath: String?) {
            installPop = AlertDialogIos(mContext).builder()
                .setTitle("提示")
                .setMsg("安装包更新完毕")
                .setCancelable(false)
                .setPositiveButtonNoDismiss("立即安装", R.color.text_default, View.OnClickListener {
                    AppUtils.installApp(
                        mContext,
                        File(fileAbsolutePath),
                        AppUtils.getAppPackageName()
                    )
                    Log.e(
                        "retrofitdownload",
                        "progressCompleted fileAbsolutePath=" + fileAbsolutePath
                    )
                })
            installPop!!.show()
        }


        /**
         * 进入Android 11或更高版本的文件访问权限页面
         */
        private fun goManagerFileAccess(activity: Context) {
            // Android 11 (Api 30)或更高版本的写文件权限需要特殊申请，需要动态申请管理所有文件的权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val appIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                appIntent.data = Uri.parse("package:" + getInstance().packageName)
                //appIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                try {
                    activity.startActivity(appIntent)
                } catch (ex: ActivityNotFoundException) {
                    ex.printStackTrace()
                    val allFileIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    activity.startActivity(allFileIntent)
                }
            }
        }

        private fun showDialog() {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.pop_download_progress, null, false)
            downloadProgressDialog = AlertDialog.Builder(mContext).setView(view).create()
            downloadProgressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))//设置Dialog背景为透明
            downloadProgressDialog!!.setCanceledOnTouchOutside(false)//设置对话框以外的阴影地方点击不起作用

            downloadPgs = view.findViewById<CircleProgress>(R.id.cp_pop_down_pgs)
            downloadContinue = view.findViewById<TextView>(R.id.tv_pop_down_continue)
            downloadProgressDialog!!.setCancelable(false)
//        downloadProgressDialog!!.show()
            downloadProgressDialog!!.window?.setLayout(//设置对话框的大小
                mContext!!.resources.displayMetrics.widthPixels * 3 / 4,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            downloadContinue!!.setOnClickListener {
                if (downloadManager != null) {
                    downloadManager!!.reStart()
                    downloadContinue!!.visibility = View.GONE
                }
            }
        }
    }
}
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
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.ApiRetrofit.BASE_BBS_SERVER_URL
import com.chuzhi.xzyx.app.MyApplication.Companion.getInstance
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateNewEntity
import com.chuzhi.xzyx.ui.service.DownloadIntentService
import com.chuzhi.xzyx.utils.download2.DownloadManager
import com.chuzhi.xzyx.widget.CircleProgress
import com.tbruyelle.rxpermissions2.RxPermissions


/**

 * @Author : wyh

 * @Time : On 2023/9/6 14:47

 * @Description : UpdateApkUtils

 */
class UpdateApkUtils1 {

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
        public fun updateApkUtils1(context: Context, entity: VersionUpdateNewEntity) {
            mContext = context
//            showDialog()
            updatePop(entity)
        }

        public fun updateApkUtils1(
            activity: Activity,
            context: Context,
            entity: VersionUpdateNewEntity
        ) {
            mActivity = activity
            mContext = context
            rxPermissions = RxPermissions(activity)
            permissionPop()
//            showDialog()
            updatePop(entity)
        }

        private fun updatePop(entity: VersionUpdateNewEntity) {

            updatePop = AlertDialogIos(mContext).builder()
                .setTitle("发现新版本：v" + entity.last_version)
                .setMsg(entity.description)
//                .setCancelable(entity.isForce_update != "True")
            .setNegativeButton("残忍拒绝", R.color.gray,null)
                .setPositiveButtonNoDismiss("立即更新", R.color.text_default, View.OnClickListener {
                    rxPermissions!!.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                    ).subscribe { granted ->

                        if (granted) {
                            try {
                                //打开白名单
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (!FcfrtAppBhUtils.isIgnoringBatteryOptimizations(mContext)) {
                                        //不在白名单中
                                        //打开后台运行权限
                                        FcfrtAppBhUtils.requestIgnoreBatteryOptimizations(mContext)


                                        //判断是哪个厂家，打开对应手机管家设置，手动设置APP为白名单
                                        if (FcfrtAppBhUtils.isXiaomi()) {
                                            //小米手机
                                            FcfrtAppBhUtils.goXiaomiSetting(mContext)
                                        } else if (FcfrtAppBhUtils.isOPPO()) {
                                            //oppo手机
                                            FcfrtAppBhUtils.goOPPOSetting(mContext)
                                        } else if (FcfrtAppBhUtils.isMeizu()) {
                                            //魅族手机
                                            FcfrtAppBhUtils.goMeizuSetting(mContext)
                                        } else if (FcfrtAppBhUtils.isSamsung()) {
                                            //三星手机
                                            FcfrtAppBhUtils.goSamsungSetting(mContext)
                                        } else if (FcfrtAppBhUtils.isLeTV()) {
                                            //乐视手机
                                            FcfrtAppBhUtils.goLetvSetting(mContext)
                                        } else if (FcfrtAppBhUtils.isVIVO()) {
                                            //vivo手机
                                            FcfrtAppBhUtils.goVIVOSetting(mContext)
                                        } else if (FcfrtAppBhUtils.isHuawei()) {
                                            //华为手机
                                            FcfrtAppBhUtils.goHuaweiSetting(mContext)
                                        } else if (FcfrtAppBhUtils.isSmartisan()) {
                                            //锤子手机
                                            FcfrtAppBhUtils.goSmartisanSetting(mContext)
                                        }
                                    }
                                }
                            } catch (exception: Exception) {
                            }


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                //判断是否有管理外部存储的权限
                                if (!Environment.isExternalStorageManager()) {
                                    Log.e("android11", "需要跳转到权限")
                                    goManagerFileAccess(mContext!!)
                                } else {
                                    val replace = BASE_BBS_SERVER_URL.replace("api/", "")
                                    downloadApk(replace + "media/android/" + entity.filename)
                                    updatePop!!.dismiss()
                                }
                            } else {
                                val replace = BASE_BBS_SERVER_URL.replace("api/", "")
                                downloadApk(replace + "media/android/" + entity.filename)
                                updatePop!!.dismiss()
                            }
                        } else {
                            permissionPop!!.show()
                        }
                    }
                })
//            if (entity.isForce_update != "True") {
//                updatePop!!.setNegativeButton("残忍拒绝", R.color.gray, null)
//            }
            updatePop!!.show()
        }

        private fun permissionPop() {
            permissionPop = AlertDialogIos(mContext).builder()
                .setTitle("获取权限")
                .setMsg("小志云享更新版本需要开启“下载\"功能以便下载安装包和监听移动网络变化\n\n请在设置中开启权限")
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

        var DOWNLOADAPK_ID = 100
        private var downloadManager: DownloadManager? = null

        //开启服务去下载更新包apk
        private fun downloadApk(url: String) {
            val intent = Intent(
                mContext,
                DownloadIntentService::class.java
            )
            val bundle = Bundle()
            bundle.putInt("download_id", DOWNLOADAPK_ID)
            bundle.putString("download_url", url)
            intent.putExtras(bundle)
            mContext!!.startService(intent)
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
        public fun popDismiss(){
            if (updatePop!=null){
                updatePop!!.dismiss()
            }
        }
    }
}
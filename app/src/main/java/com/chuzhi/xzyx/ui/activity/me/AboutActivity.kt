package com.chuzhi.xzyx.ui.activity.me

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telephony.PhoneStateListener
import android.telephony.PhoneStateListener.LISTEN_NONE
import android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.ApiRetrofit
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.databinding.ActivityAboutBinding
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateEntity
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateNewEntity
import com.chuzhi.xzyx.ui.bean.eventbus.DownLoadEBEntity
import com.chuzhi.xzyx.ui.presenter.AboutPresenter
import com.chuzhi.xzyx.ui.view.AboutView
import com.chuzhi.xzyx.utils.*
import com.chuzhi.xzyx.utils.apputils.AppUtils
import com.chuzhi.xzyx.utils.wifi.CheckNetStatus
import com.chuzhi.xzyx.utils.wifi.WifiCheckUtils
import com.chuzhi.xzyx.widget.CircleProgress
import com.google.gson.Gson
import com.luck.picture.lib.thread.PictureThreadUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


/**
 * 关于我们
 */
class AboutActivity : BaseActivity<ActivityAboutBinding, AboutPresenter>(), AboutView {
    private var downloadProgressDialog: AlertDialog? = null
    private var downloadPgs: CircleProgress? = null
    private var downloadContinue: TextView? = null
    private var updateTitle: TextView? = null
    private var updateContent: TextView? = null
    private var updateUpload: Button? = null
    private var appPackageName = ""


    override fun createPresenter(): AboutPresenter {
        return AboutPresenter(this)
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        binding.includeActAbout.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActAbout.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.includeActAbout.tvIncludeTitleTitle.text = "关于"
    }



    @SuppressLint("SetTextI18n")
    override fun initData() {
        appPackageName = AppUtils.getAppVersionName()
        binding.tvActAboutVersion.text = "当前版本：$appPackageName"
//        presenter.versionUpdate("android", appPackageName.replace("v", ""))
        if (NetworkUtils.isNetworkAvailable(this)) {
            getHttpFile()//检查更新
        } else {
            ToastUtil.showLong(this, "请检查您的网络!")
        }
        binding.llActAboutUpdate.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                getHttpFile()//检查更新
            } else {
                ToastUtil.showLong(this, "请检查您的网络!")
            }
        }
    }

    //检查更新回调
    override fun versionUpdate(msg: VersionUpdateEntity?) {
        if (msg != null) {
            binding.ivActAboutApkNew.visibility = View.VISIBLE
//            this?.let { UpdateApkUtils.updateApkUtils(it, msg) }
        }
    }

    private fun showDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.pop_download_progress, null, false)
        downloadProgressDialog = AlertDialog.Builder(this).setView(view).create()
        downloadProgressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))//设置Dialog背景为透明
        downloadProgressDialog!!.setCanceledOnTouchOutside(false)//设置对话框以外的阴影地方点击不起作用

        downloadPgs = view.findViewById<CircleProgress>(R.id.cp_pop_down_pgs)
        downloadProgressDialog!!.setCancelable(false)
        downloadProgressDialog!!.window?.setLayout(//设置对话框的大小
            this.resources.displayMetrics.widthPixels * 3 / 4,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }


    //检查更新获取网络资源
    private fun getHttpFile() {
        Thread {
            var url = URL(ApiRetrofit.BASE_GET_VERSION_URL)
//            var url = URL("http://testindex.chuzhi.cn/media/android/version")
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.setRequestMethod("GET")

            val responseCode: Int = conn.getResponseCode()
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val `in` = BufferedReader(InputStreamReader(conn.getInputStream()))
                var line: String?
                val response = StringBuilder()
                while (`in`.readLine().also { line = it } != null) {
                    response.append(line)
                }
                `in`.close()

                // 在这里处理服务器返回的txt文件内容
                val txtContent = response.toString()
                var gson = Gson()
                val fromJson =
                    gson.fromJson<VersionUpdateNewEntity>(
                        txtContent,
                        VersionUpdateNewEntity::class.java
                    )

                if (appPackageName != "") {
                    val nowVersion = appPackageName.replace("v", "")
                    val versionNew =
                        VersionCodeUtils.isVersionNew(fromJson.last_version, nowVersion)
                    if (versionNew) {
                        Thread {
                            runOnUiThread {
                                this?.let {
                                    UpdateApkUtils1.updateApkUtils1(this,
                                        it,
                                        fromJson
                                    )
                                }
                                showDialog()
                            }
                        }.start()
                    } else {
                        Thread {
                            runOnUiThread {
                                ToastUtil.showLong(
                                    this,
                                    "已是最新版本!"
                                )
                            }
                        }.start()
                    }
                }

                Log.e("网络文件", txtContent)
                Log.e("网络文件转json", fromJson.toString())
            } else {
                // 处理网络请求失败的情况
                // 处理网络请求失败的情况
                PictureThreadUtils.runOnUiThread {
                    ToastUtil.showShort(this, "更新接口出错")
                }
            }
        }.start()

    }
    //eventbus安装包更新进度通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getApkProgress(entity: DownLoadEBEntity) {
        Log.e("正在下载", entity.progress.toString())
        if (downloadProgressDialog != null) {
            if (entity.progress == 0) {
                downloadProgressDialog!!.show()
            }
            downloadPgs!!.SetCurrent(entity.progress)
            if (entity.progress == 100) {
                downloadProgressDialog!!.dismiss()
                installApk(entity.path)
            }
            if (entity.path!=""){
                installApk(entity.path)
                Log.e("apk下载完毕，路径", entity.path)
            }
        }
    }
    //安装apk弹框
    private fun installApk(fileAbsolutePath: String?) {
        val view =
            LayoutInflater.from(this).inflate(R.layout.pop_apk_install, null, false)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))//设置Dialog背景为透明
        val tvInstall = view.findViewById<TextView>(R.id.tv_pop_apk_install)
        dialog.setCancelable(false)
        dialog.window?.setLayout(//设置对话框的大小
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tvInstall.setOnClickListener {
            AppUtils.installApp(
                this,
                File(fileAbsolutePath),
                AppUtils.getAppPackageName()
            )
        }
        dialog.show()
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showLong(this, msg)
        if (msg == "已是最新版本") {
            binding.ivActAboutApkNew.visibility = View.GONE
        }
    }

}
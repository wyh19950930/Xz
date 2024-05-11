  package com.chuzhi.xzyx.ui.fragment.homepage

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.*
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.*
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.AMapException
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeResult
import com.chaquo.python.Python
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.ApiRetrofit
import com.chuzhi.xzyx.api.ApiRetrofit.BASE_GET_VERSION_URL
import com.chuzhi.xzyx.api.ApiServer
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.app.MyApplication
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseFragment
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.FragmentHPDeviceBinding
import com.chuzhi.xzyx.ui.activity.homepage.FenceListActivity
import com.chuzhi.xzyx.ui.activity.homepage.HPAddDeviceActivity
import com.chuzhi.xzyx.ui.activity.homepage.LocationTrackingActivity
import com.chuzhi.xzyx.ui.activity.homepage.PortManagementActivity
import com.chuzhi.xzyx.ui.adapter.DeviceMapInfoWinAdapter
import com.chuzhi.xzyx.ui.adapter.HPDeviceBannerVPAdapter
import com.chuzhi.xzyx.ui.bean.UserIpEntity
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateNewEntity
import com.chuzhi.xzyx.ui.bean.eventbus.DownLoadEBEntity
import com.chuzhi.xzyx.ui.bean.mqtt.HomePageMQTTMessage
import com.chuzhi.xzyx.ui.bean.mqtt.MQTTMessage
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity
import com.chuzhi.xzyx.ui.bean.rc.DynamicPasswordEntity
import com.chuzhi.xzyx.ui.mqtt.IGetMessageCallBack
import com.chuzhi.xzyx.ui.mqtt.MQTTService
import com.chuzhi.xzyx.ui.mqtt.MyServiceConnection
import com.chuzhi.xzyx.ui.presenter.HPDevicePresenter
import com.chuzhi.xzyx.ui.view.HPDeviceView
import com.chuzhi.xzyx.utils.*
import com.chuzhi.xzyx.utils.apputils.AppUtils
import com.chuzhi.xzyx.utils.dynamicpassword.TotpUtil
import com.chuzhi.xzyx.utils.network.NetworkListenerHelper
import com.chuzhi.xzyx.utils.network.NetworkStatus
import com.chuzhi.xzyx.utils.thread.AppExecutors
import com.chuzhi.xzyx.utils.thread.ScheduledTask
import com.chuzhi.xzyx.utils.wifi.WifiCheckUtils
import com.chuzhi.xzyx.widget.CircleProgress
import com.chuzhi.xzyx.widget.CustomTickView
import com.google.gson.Gson
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 设备页面（本项目的核心）
 */
class HPDeviceFragment : BaseFragment<FragmentHPDeviceBinding, HPDevicePresenter>(),
    HPDeviceView, GeocodeSearch.OnGeocodeSearchListener,
    IGetMessageCallBack, NetworkListenerHelper.NetworkConnectedListener {
    private var param1: String? = null
    private var param2: String? = null

    private var settingPop: AlertDialogIos? = null
    private var subMenuPop: CommenPop? = null


    private var serviceConnection: MyServiceConnection? = null
    private var mqttService: MQTTService? = null
    private var aMap: AMap? = null
    private var geocodeSearch: GeocodeSearch? = null
    private var mMarkers: ArrayList<Marker>? = null


    private val MSG_DISMISS_DIALOG = 0//连接成功后
    private val MSG_FAIL_DISMISS_DIALOG = 1//连接失败后
    private val MQTT_DATA_DESTORY = 2//数据销毁后
    private val MQTT_COMPUTER_STATUS = 3//查询开关机状态
    private var reFreshFlag = 0//20231106因为禁止刚进来加载动画弹框，所以这个字段替换之前判断弹框是否初始化的逻辑

    class MyHandler(activity: HPDeviceFragment) : Handler(Looper.getMainLooper()) {
        var weakReference = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                weakReference.get()!!.MSG_DISMISS_DIALOG -> {//连接成功
                    hpDBannerVPAdapter!!.notifyItemChanged(
                        devicePos,
                        "连接," + 1
                    )
                    AppCache.getInstance().riskOperations = 0//0的时候设备管理可以解绑设备
                    if (null != mqttSuccessPop) {
                        if (mqttSuccessPop!!.isShowing) {
                            mqttSuccessPop!!.dismiss()
                            mqttSuccessPop = null
                        }
                        if (popTimer != null) {
                            popTimer!!.cancel()
                            popTimer = null
                        }
                    }
                }
                weakReference.get()!!.MSG_FAIL_DISMISS_DIALOG -> {//连接失败
                    hpDBannerVPAdapter!!.notifyItemChanged(
                        devicePos,
                        "连接," + 0
                    )
                    AppCache.getInstance().riskOperations = 0//0的时候设备管理可以解绑设备
                    if (popTimer != null) {
                        popTimer!!.cancel()
                        popTimer = null
                    }

                    if (null != mqttSuccessPop) {
                        if (mqttSuccessPop!!.isShowing) {
                            mqttSuccessPop!!.dismiss()
                            mqttSuccessPop = null
                            ToastUtil.showLong(weakReference.get()!!.context, "设备连接已断开，请重试！")
                            if (hpDBannerVPAdapter != null && cumputerList.size > 0) {
                                weakReference.get()!!.binding.llFragHPTop.setBackgroundResource(R.mipmap.home_gj_bg)
                                cumputerList[devicePos].computer_status = 0
                                hpDBannerVPAdapter!!.notifyItemChanged(devicePos, "开关机," + 0)
                            }
                        }
                    }
                }
                weakReference.get()!!.MQTT_DATA_DESTORY -> {//数据销毁后
                    ToastUtil.showLong(weakReference.get()!!.context, "数据已销毁，关机中...")
                    MQTTService.publish(cumputerList[devicePos].sn, devicePos, "关机")
                    weakReference.get()!!.mHandler.sendEmptyMessageDelayed(
                        weakReference.get()!!.MQTT_COMPUTER_STATUS,
                        500
                    )
                }
                weakReference.get()!!.MQTT_COMPUTER_STATUS -> {//数据销毁后查询开机状态
                    MQTTService.publish(cumputerList[devicePos].sn, devicePos, "设备开机状态")
                }
            }
        }
    }

    private val mHandler = MyHandler(this)

    companion object {
        var mqttSuccessPop: CommenPop? = null
        var popTimer: CountDownTimer? = null

        @SuppressLint("StaticFieldLeak")
        var hpDBannerVPAdapter: HPDeviceBannerVPAdapter? = null
        var cumputerList = ArrayList<ComputerListEntity.ComputerListDTO>()
        var devicePos = 0

        @JvmStatic
        fun newInstance() =
            HPDeviceFragment()
    }

    override fun createPresenter(): HPDevicePresenter {
        return HPDevicePresenter(this)
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        // 网络状态回调；
        NetworkListenerHelper.addListener(this)
        AMapLocationClient.updatePrivacyAgree(activity, true);
        AMapLocationClient.updatePrivacyShow(activity, true, true);
//        binding.mapFragHpDevice.onCreate(requireActivity().intent.extras)
//        initAMap()

        serviceConnection = MyServiceConnection()
        serviceConnection!!.setIGetMessageCallBack(this)



        createBannerVPAdapter()
        subMenuPop()

        presenter.userComputerList()
        settingPop = AlertDialogIos(activity).builder()//初始化设置弹框
        if (NetworkUtils.isNetworkAvailable(activity)) {
            getHttpFile()//检查更新
        } else {
            binding.tvFragHPNetworkType.text = "当前无网络"
            binding.tvFragHPNetworkType.visibility = View.VISIBLE

            val sharedList = SpUtils.getSharedList(
                activity,
                "userComputerList",
                ComputerListEntity.ComputerListDTO::class.java
            )
            if (sharedList != null && sharedList.size > 0) {
                networkMqttRefresh = true
                cumputerList.clear()
                cumputerList.addAll(sharedList)
                binding.bannerVpFragHpDevice.refreshData(cumputerList)
                Log.e("无网络状态下读取缓存数据", cumputerList.toString())

                hpDBannerVPAdapter!!.setOnItemClickListener { v, position ->
                    when (v.id) {
                        R.id.tv_item_hp_device_banner_kl_hq -> {//获取口令(远程版)
                            getDynamic(cumputerList[position].ssd_sn)
//                            presenter.getTotp(cumputerList[position].id.toString(), position)

                        }
                        R.id.tv_item_hp_device_banner_kl_djb -> {//获取口令(单机版)
                            getDynamic(cumputerList[position].ssd_sn)
//                            presenter.getTotp(cumputerList[position].id.toString(), position)
                        }
                    }
                }

            } else {
                cumputerList.clear()
                devicePos = 0
                if (mIsBound) {
                    Log.e("true", "当前用户未绑定设备")
                    requireActivity().unbindService(serviceConnection!!)
                    MQTTService.exitLogin()
                    mIsBound = false
                }
                //无设备显示去绑定界面
                binding.llFragHpYbd.visibility = View.GONE
                binding.includeFragWbd.rltIncludeHpBinding.visibility = View.VISIBLE
            }
        }
    }


    override fun initData() {
        binding.includeFragWbd.btnFragBindingBd.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(activity)) {
                startActivity(
                    Intent(
                        activity,
                        HPAddDeviceActivity::class.java
                    )
                )
            } else {
                ToastUtil.showLong(activity, "请检查您的网络")
            }

        }
//        getUserAddress("")//获取用户ip归属
//        val logPath =
//            Environment.getExternalStorageDirectory().absolutePath + "/Android/data/" + AppUtils.getAppPackageName() + "/files/logs/log.txt"
//        Log.e("logoutPath", logPath)
//        try {
//            val logFile = File(logPath)
//            val exportDir = File(Environment.getExternalStorageDirectory().absolutePath + "/logs/")
//            if (!exportDir.exists()) {
//                exportDir.mkdirs()
//            }
//            val exportFile = File(exportDir, "log.txt")
//            FileUtils.copyFile(logFile, exportFile)
//            Log.e("日志已导出到：", exportFile.absolutePath)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
    }

    /**
     * 初始化AMap对象
     */
    private fun initAMap() {
        mMarkers = ArrayList()
        if (aMap == null) {
            aMap = binding.mapFragHpDevice.map
        }
        //双击缩放
        aMap!!.uiSettings.isZoomGesturesEnabled = false
        //拖动
        aMap!!.uiSettings.isScrollGesturesEnabled = false
        //下移高德LOG到屏幕外
        aMap!!.uiSettings.setLogoBottomMargin(-100);
        // 去掉高德地图右下角隐藏的缩放按钮
        aMap!!.uiSettings.isZoomControlsEnabled = false;
        //地理搜索类
        geocodeSearch = GeocodeSearch(activity);
        geocodeSearch!!.setOnGeocodeSearchListener(this);
    }

    private var llDestory: LinearLayout? = null
    private var ivStolen: LinearLayout? = null
    private var ivClock: LinearLayout? = null
    private var ivClockSp: LinearLayout? = null
    private var ivClockSpan: LinearLayout? = null
    private var ivClockDestroy: ImageView? = null
    private var stolenStatus = 2//被盗状态 0（解除被盗模式）2（开启被盗模式）
    private var clock_status = 2//锁机状态 0（解锁）2（锁机） Power键状态==>: 2（启用） 0（禁用）
    private var backlightStatus = 2//锁屏状态 2（开启背光）0（关闭背光）
    private var ssdCommandStatus = 2//锁盘状态
    private fun subMenuPop() {
        subMenuPop = CommenPop.getNormalPopu(
            activity,
            R.layout.pop_device_submenu,
            binding.bannerFragHpDevice
        )
        val contentView = subMenuPop!!.contentView
        subMenuPop!!.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        val llAdd = contentView.findViewById<LinearLayout>(R.id.ll_pop_device_submenu_add)
        llDestory = contentView.findViewById<LinearLayout>(R.id.ll_pop_device_submenu_destory)
        val llStolen = contentView.findViewById<LinearLayout>(R.id.ll_pop_device_submenu_stolen)
        val llClock = contentView.findViewById<LinearLayout>(R.id.ll_pop_device_submenu_clock)
        val llClockSp = contentView.findViewById<LinearLayout>(R.id.ll_pop_device_submenu_clock_sp)
        val llClockSpan =
            contentView.findViewById<LinearLayout>(R.id.ll_pop_device_submenu_clock_span)
        ivStolen = contentView.findViewById<LinearLayout>(R.id.iv_pop_device_submenu_stolen)
        ivClock = contentView.findViewById<LinearLayout>(R.id.iv_pop_device_submenu_clock)
        ivClockSp = contentView.findViewById<LinearLayout>(R.id.iv_pop_device_submenu_clock_sp)
        ivClockSpan = contentView.findViewById<LinearLayout>(R.id.iv_pop_device_submenu_clock_span)
        ivClockDestroy = contentView.findViewById<ImageView>(R.id.iv_pop_device_submenu_destory)
        subMenuPop!!.isOutsideTouchable = true
        subMenuPop!!.isFocusable = true
        llAdd.setOnClickListener {//添加设备
            if (cumputerList.size < 3) {
                subMenuPop!!.dismiss()
                var intent = Intent(activity, HPAddDeviceActivity::class.java)
                startActivity(intent)
            } else {
                ToastUtil.showLong(activity, "最多绑定三台设备！")
            }

        }
        //被盗模式
        llStolen!!.setOnClickListener {
            if (stolenStatus == 2) {//被盗状态 0（解除被盗模式）2（开启被盗模式）
                settingPopup("关闭被盗模式", snType, "steal")
            } else {
                settingPopup("开启被盗模式", snType, "unsteal")
            }
        }
        //锁机模式
        llClock!!.setOnClickListener {
            if (clock_status == 2) {//锁机状态 0（解锁）2（锁机） Power键状态==>: 2（启用） 0（禁用）
                settingPopup("关闭锁机模式", snType, "unlock")
            } else {
                settingPopup("开启锁机模式", snType, "clock")
            }
        }
        //锁屏模式
        llClockSp!!.setOnClickListener {
            if (backlightStatus == 2) {//锁屏状态 2（开启背光）0（关闭背光）
                settingPopup("开启锁屏模式", snType, "openBacklight")
            } else {
                settingPopup("关闭锁屏模式", snType, "closeBacklight")
            }
        }
        //锁盘模式
        llClockSpan!!.setOnClickListener {
            if (ssdCommandStatus == 2) {
                settingPopup("开启锁盘模式", snType, "openBacklight")
            } else {// if (ssdCommandStatus == 1)
                settingPopup("关闭锁盘模式", snType, "openBacklight")
            }
        }
        //数据销毁
        llDestory!!.setOnClickListener {
            settingPopup("数据销毁", snType, "openBacklight")
        }
        subMenuPop!!.setOnDismissListener {
            CommenPop.backgroundAlpha(1f, activity)
            binding.cvwFragHpDevice.radius = 30f
        }
    }

    //连接成功弹框
    private var customTickView: CustomTickView? = null
    private var mqttSuccessPopLl: LinearLayout? = null
    private var mqttSuccessPopImg: ImageView? = null
    private var mqttSuccessAnimation: Animation? = null
    private fun mqttSuccessPop() {
        mqttSuccessPop = CommenPop.getNormalPopu(
            activity,
            R.layout.pop_device_mqtt_success,
            binding.bannerFragHpDevice
        )

        val contentView = mqttSuccessPop!!.contentView
        mqttSuccessPop!!.width = ViewGroup.LayoutParams.WRAP_CONTENT
        customTickView = contentView.findViewById<CustomTickView>(R.id.pop_device_mqtt_success_ctv)
        mqttSuccessPopLl = contentView.findViewById<LinearLayout>(R.id.pop_device_mqtt_success_ll)
        mqttSuccessPopImg =
            contentView.findViewById<ImageView>(R.id.pop_device_mqtt_success_dialog_img)
        mqttSuccessAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation)
        mqttSuccessAnimation!!.interpolator = LinearInterpolator()
        mqttSuccessPopImg!!.startAnimation(mqttSuccessAnimation)
        CommenPop.backgroundAlpha(0.5f, activity)
        mqttSuccessPop!!.showAtLocation(binding.bannerFragHpDevice, Gravity.CENTER, 0, 0)
        mqttSuccessPop!!.isOutsideTouchable = false
        mqttSuccessPop!!.isFocusable = false
        binding.cvwFragHpDevice.radius = 0f
        mqttSuccessPop!!.setOnDismissListener {
            CommenPop.backgroundAlpha(1f, activity)
            binding.cvwFragHpDevice.radius = 30f
        }
        popTimer = object : CountDownTimer(9000, 10) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                if (mqttSuccessPop != null) {
                    if (mqttSuccessPop!!.isShowing) {
                        reFreshFlag = 0
                        AppCache.getInstance().riskOperations = 0
                        mHandler.sendEmptyMessageDelayed(MSG_FAIL_DISMISS_DIALOG, 1000)
                    }
                }

            }
        }
        popTimer!!.start()
    }

    private fun createBannerVPAdapter() {
        hpDBannerVPAdapter = HPDeviceBannerVPAdapter(activity, cumputerList)
        binding.bannerVpFragHpDevice.setLifecycleRegistry(lifecycle)
            .setAdapter(hpDBannerVPAdapter)
            .setCanLoop(false)
            .setIndicatorVisibility(View.GONE)
            .create()
//        binding.bannerVpFragHpDevice.setCanLoop(false).setIndicatorVisibility(View.GONE)

        bannerVpChange()

    }

    var mIsBound = false //是否注册服务
    var mqttUnBindType = 0
    private fun bannerVpChange() {
        binding.bannerVpFragHpDevice.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("vpBanner==>", position.toString())
                devicePos = position
//                binding.bannerIdvFragHpDevice.changeIndicator(devicePos)

                /*binding.btnFragHpDeviceAq.setOnClickListener {//安全日志
                    val intent = Intent(activity, SecurityLogActivity::class.java)
                    intent.putExtra("portSn", cumputerList[devicePos].sn)
                    startActivity(intent)
                }
                binding.btnFragHpDeviceDk.setOnClickListener {//端口管理
                    val intent = Intent(activity, PortManagementActivity::class.java)
                    intent.putExtra("portSn", cumputerList[devicePos].sn)
                    startActivity(intent)
                }*/

                if (mIsBound && cumputerList.size > 1) {
                    requireActivity().unbindService(serviceConnection!!)
                    MQTTService.exitLogin()
                    mIsBound = false
                    Log.e("vp注销服务", mIsBound.toString())
                }
                val intent = Intent(MyApplication.getInstance(), MQTTService::class.java)
                intent.putExtra("snType", cumputerList[devicePos].sn)
                intent.putExtra("snPwd", "!_#@" + cumputerList[devicePos].ssd_sn)
                requireActivity().bindService(
                    intent,
                    serviceConnection!!,
                    Context.BIND_AUTO_CREATE
                )
                mIsBound = true
                Log.e("vp注册服务", mIsBound.toString())
                if (scheduledTask != null) {
                    scheduledTask!!.stop()
                    delaytime = 0
                    delaytime1 = 0
                    delaytime2 = 0
                }
                mqttPublish()
//                setMapInfoWinData(devicePos)//新版首页不显示地图 20231117
            }

            @SuppressLint("LongLogTag")
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                when (state) {
                    SCROLL_STATE_IDLE -> {
                        Log.e("滑动状态" + "SCROLL_STATE_IDLE", state.toString())
                        val timer1 = Timer() //实例化Timer类
                        timer1.schedule(object : TimerTask() {
                            override fun run() {
                                binding.bannerVpFragHpDevice.setUserInputEnabled(true)
                                this.cancel()
                            }
                        }, 100)

                    }
                    SCROLL_STATE_DRAGGING -> {
                        Log.e("滑动状态" + "SCROLL_STATE_DRAGGING", state.toString())
                    }
                    SCROLL_STATE_SETTLING -> {
                        Log.e("滑动状态" + "SCROLL_STATE_SETTLING", state.toString())
                        binding.bannerVpFragHpDevice.setUserInputEnabled(false)
                    }

                }

            }
        })
    }


    //设备信息列表
    override fun userComputerList(msg: ComputerListEntity?) {

        if (schedule != null) {
            schedule!!.cancel(false)
        }
        if (msg!!.computer_list != null && msg.computer_list.size > 0) {
            Log.e("设备信息展示", "==>")
            //请求数据缓存到本地
            SpUtils.setSharedList(activity, "userComputerList", msg!!.computer_list)

            //有设备显示设备界面
            binding.llFragHpYbd.visibility = View.VISIBLE
            binding.includeFragWbd.rltIncludeHpBinding.visibility = View.GONE

            cumputerList.clear()
            cumputerList.addAll(msg.computer_list)
//            bannerVpChange()
//            hpDeviceBannerAdapter!!.setData(cumputerList)
            if (reFreshFlag == 0) {//mqttSuccessPop == null  20231106修改刚进来或者切换到此页面不加载弹框动画
//                Log.e("mqttSuccessPop====》","mqttSuccessPop为空，创建")
//                mqttSuccessPop()//初始化连接成功弹框
                reFreshFlag = 1
                binding.bannerVpFragHpDevice.refreshData(cumputerList)
                hpDBannerVPAdapter!!.setOnItemClickListener { v, position ->
                    when (v.id) {
                        R.id.iv_item_hp_device_banner_status -> {//开关机
                            snType = cumputerList[position].sn
                            if (computerStatus == "关机") settingPopup("开机", snType, "bootup")
                            else settingPopup("关机", snType, "shutdown")
                        }
                        R.id.ll_item_hp_device_banner -> {//二级菜单箭头
//                        subMenuPop(cumputerList[position])
                            if (cumputerList[position].isDestroy_permissions) {
                                llDestory!!.visibility = View.VISIBLE
                            } else {
                                llDestory!!.visibility = View.INVISIBLE
                            }
                            snType = cumputerList[position].sn
                            CommenPop.backgroundAlpha(0.5f, activity)
                            subMenuPop!!.showAtLocation(
                                binding.bannerFragHpDevice,
                                Gravity.TOP,
                                100,
                                200
                            )
                            binding.cvwFragHpDevice.radius = 0f
                        }
                        R.id.tv_item_hp_device_banner_kl_hq -> {//获取口令(远程版)
                            getDynamic(cumputerList[position].ssd_sn)
//                            presenter.getTotp(cumputerList[position].id.toString(), position)

                        }
                        R.id.tv_item_hp_device_banner_kl_djb -> {//获取口令(单机版)
                            getDynamic(cumputerList[position].ssd_sn)
//                            presenter.getTotp(cumputerList[position].id.toString(), position)
                        }
                        R.id.iv_item_hp_device_banner_refresh -> {//刷新设备
                            if (mqttSuccessPop == null) {
                                mqttSuccessPop()//初始化连接成功弹框
                            }
                            requireActivity().unbindService(serviceConnection!!)
                            MQTTService.exitLogin()
                            val intent =
                                Intent(MyApplication.getInstance(), MQTTService::class.java)

                            intent.putExtra("snType", cumputerList[position].sn)
                            intent.putExtra("snPwd", "!_#@" + cumputerList[position].ssd_sn)
                            requireActivity().bindService(
                                intent,
                                serviceConnection!!,
                                Context.BIND_AUTO_CREATE
                            )
                            if (scheduledTask != null) {
                                scheduledTask!!.stop()
                                delaytime = 0
                                delaytime1 = 0
                                delaytime2 = 0
                            }
                            mqttPublish()
                        }
                        R.id.ll_item_hp_device_banner_spm -> {//锁定屏幕
                            if (backlightStatus == 2) {//锁屏状态 2（开启背光）0（关闭背光）
                                settingPopup("开启锁屏模式", cumputerList[position].sn, "openBacklight")
                            } else {
                                settingPopup("关闭锁屏模式", cumputerList[position].sn, "closeBacklight")
                            }
                        }
                        R.id.ll_item_hp_device_banner_syp -> {//锁定硬盘
                            if (instructYpSetType == "") {
                                if (ssdCommandStatus == 2) {
                                    settingPopup(
                                        "开启锁盘模式",
                                        cumputerList[position].sn,
                                        "openBacklight"
                                    )
                                } else {// if (ssdCommandStatus == 1)
                                    settingPopup(
                                        "关闭锁盘模式",
                                        cumputerList[position].sn,
                                        "openBacklight"
                                    )
                                }
                            } else {
                                ToastUtil.showLong(activity, "正在执行中，请稍后!")
                            }

                        }
                        R.id.ll_item_hp_device_banner_sdn -> {//锁定电脑
                            if (clock_status == 2) {//锁机状态 0（解锁）2（锁机） Power键状态==>: 2（启用） 0（禁用）
                                settingPopup("关闭锁机模式", cumputerList[position].sn, "unlock")
                            } else {
                                settingPopup("开启锁机模式", cumputerList[position].sn, "clock")
                            }
                        }
                        R.id.ll_item_hp_device_banner_bd -> {//被盗模式
                            if (stolenStatus == 2) {//被盗状态 0（解除被盗模式）2（开启被盗模式）
                                settingPopup("关闭被盗模式", cumputerList[position].sn, "steal")
                            } else {
                                settingPopup("开启被盗模式", cumputerList[position].sn, "unsteal")
                            }
                        }
                        R.id.ll_item_hp_device_banner_xh -> {//数据销毁
                            settingPopup("数据销毁", cumputerList[position].sn, "openBacklight")
                        }
                        R.id.ll_item_hp_device_banner_dk -> {//端口管理
                            val intent = Intent(activity, PortManagementActivity::class.java)
                            intent.putExtra("portSn", cumputerList[position].sn)
                            startActivity(intent)
                        }
                        R.id.ll_item_hp_device_banner_yj_dw -> {//一键定位
                            val intent = Intent(activity, LocationTrackingActivity::class.java)
                            intent.putExtra("deviceData", cumputerList[position])
                            intent.putExtra("deviceType", 1)
                            startActivity(intent)
                        }
                        R.id.ll_item_hp_device_banner_dz_wl -> {//电子围栏
                            var intent = Intent(activity, FenceListActivity::class.java)
                            intent.putExtra("computerData", cumputerList[position])
                            startActivity(intent)
                        }

                    }
                }
//                binding.bannerIdvFragHpDevice.initIndicatorCount(cumputerList.size)
//                binding.bannerIdvFragHpDevice.changeIndicator(devicePos)

                if (isViewShown == true || onResumeType != 0) {
                    if (cumputerList.size > 0) {
                        if (mIsBound && cumputerList.size > 1) {
                            requireActivity().unbindService(serviceConnection!!)
                            MQTTService.exitLogin()
                            mIsBound = false
                            Log.e("注销服务", mIsBound.toString())
                        } else if (networkMqttRefresh) {
                            networkMqttRefresh = false
                            requireActivity().unbindService(serviceConnection!!)
                            MQTTService.exitLogin()
                            mIsBound = false
                            Log.e("注销服务", mIsBound.toString())
                        }
//                    val timer1 = Timer() //实例化Timer类
//                    timer1.schedule(object : TimerTask() {
//                        override fun run() {
                        val intent =
                            Intent(MyApplication.getInstance(), MQTTService::class.java)
                        if (devicePos >= cumputerList.size) {
                            devicePos = cumputerList.size - 1
                        }
                        intent.putExtra("snType", cumputerList[devicePos].sn)
                        intent.putExtra("snPwd", "!_#@" + cumputerList[devicePos].ssd_sn)
                        requireActivity().bindService(
                            intent,
                            serviceConnection!!,
                            Context.BIND_AUTO_CREATE
                        )
                        mIsBound = true
                        Log.e("注册服务", mIsBound.toString())
                        if (scheduledTask != null) {
                            scheduledTask!!.stop()
                            delaytime = 0
                            delaytime1 = 0
                            delaytime2 = 0
                        }
                        mqttPublish()
//                            this.cancel()
//                        }
//                    }, 50)


                    }
                }
            } else {
//                Log.e("mqttSuccessPop====》","mqttSuccessPop不为空，不创建")
                reFreshFlag = 0
                binding.bannerVpFragHpDevice.refreshData(cumputerList)
            }


        } else {
            Log.e("false", "当前用户未绑定设备")
            cumputerList.clear()
            devicePos = 0
            if (mIsBound) {
                Log.e("true", "当前用户未绑定设备")
                requireActivity().unbindService(serviceConnection!!)
                MQTTService.exitLogin()
                mIsBound = false
            }
            //无设备显示去绑定界面
            binding.llFragHpYbd.visibility = View.GONE
            binding.includeFragWbd.rltIncludeHpBinding.visibility = View.VISIBLE
        }
    }

    private var snType = ""

    override fun showHomeInformation(msg: ComputerListEntity?) {
    }

    //获取动态口令(调用本地python文件)
    private fun getDynamic(ssdSn: String) {
        val python = Python.getInstance()
        val pyObject = python.getModule("totp").callAttr("generateTOTP", ssdSn)
        if (hpDBannerVPAdapter != null) {
            hpDBannerVPAdapter!!.notifyItemChanged(devicePos, "动态口令," + pyObject.toString())
        }
        Log.e("打印动态口令", "第" + devicePos + "个" + pyObject.toString())
    }

    private fun getScrollYDistance(recyclerView: RecyclerView): Int? {
        kotlin.runCatching {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val position = layoutManager.findFirstVisibleItemPosition()
            val firstVisibleChildView = layoutManager.findViewByPosition(position)
            val itemWith = firstVisibleChildView!!.width
            return position * itemWith - firstVisibleChildView.left
        }
        return null
    }

    private var iPos = 0 //初始化进度条的进度值
    private var progressDialog //声明进度条对话框的对象
            : ProgressDialog? = null
    private var computerStatus = "-1"
    private var instructType = ""//判断是哪个指令
    private var instructYpSetType = ""//对硬盘操作，比如解锁盘或者硬盘数据销毁的时候存一下状态，然后在查询硬盘sn回调的时候区分

    //操作弹框
    private fun settingPopup(msg: String?, sn: String, operation_type: String) {
//        computerStatus = msg!!
        settingPop!!.setTitle("提示")
            .setMsg("是否$msg?")
            .setNegativeButton("取消", R.color.gray, null)
            .setPositiveButton("确定", R.color.text_default, View.OnClickListener {

                when (msg) {
                    "开机" -> {
                        MQTTService.publish(sn, devicePos, "开机")
                        if (hpDBannerVPAdapter != null) {
                            hpDBannerVPAdapter!!.notifyItemChanged(devicePos, "开关机," + 3)
                        }
                        Thread {
                            while (100 - iPos > 0) {
                                iPos++
//                            progressDialog!!.setProgress(iPos)
                                if (iPos == 99) {//大概十多秒的时候请求下开机状态
                                    MQTTService.publish(sn, devicePos, "设备开机状态")
                                }
                                try {
                                    Thread.sleep(250)
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                }
                            }
                            //如果超过30s开机还是失败的话就执行关机操作
//                            if (computerStatus == "-1") {
//                                MQTTService.publish(sn, devicePos, "关机")
//                                return@Thread
//                            }
                            iPos = 0
                        }.start()
                    }
                    "关机" -> {
                        MQTTService.publish(sn, devicePos, "关机")
                        if (hpDBannerVPAdapter != null) {
                            hpDBannerVPAdapter!!.notifyItemChanged(devicePos, "开关机," + 4)
                        }
                        //这里让他延时0.5秒请求
                        val timer = Timer() //实例化Timer类
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                MQTTService.publish(sn, devicePos, "设备开机状态")
                                this.cancel()
                            }
                        }, 500)
                    }
                    "开启被盗模式" -> {
                        MQTTService.publish(sn, devicePos, "开启被盗模式")
                        val timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                MQTTService.publish(sn, devicePos, "被盗状态")
                                this.cancel()
                            }
                        }, 500)

//                        var sTime = 0
//                        var schedulers = ScheduledTask()
//                        try {
//                            schedulers.startCountdown(200L, 500L) {
//                                sTime++
//                                if (sTime<4){
//                                    when(sTime){
//                                        1->{
//                                            MQTTService.publish(sn, devicePos, "被盗状态")
//                                        }
//                                        2->{
//                                            MQTTService.publish(sn, devicePos, "锁机")
//                                        }
//                                        3->{
//                                            MQTTService.publish(sn, devicePos, "被盗状态")
//                                        }
//                                    }
//                                }else{
//                                    schedulers.stop()
//                                    sTime = 0
//                                }
//                            }
//                        } catch (e: Exception) {
//                        }

                    }
                    "关闭被盗模式" -> {
                        instructType = "关闭被盗模式"
                        MQTTService.publish(sn, devicePos, "解除被盗模式")
                        val timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                MQTTService.publish(sn, devicePos, "被盗状态")
                                MQTTService.publish(sn, devicePos, "设备开机状态")
                                this.cancel()
                            }
                        }, 500)
                    }
                    "开启锁机模式" -> {

                        var sTime = 0
                        var schedulers = ScheduledTask()
                        try {
                            schedulers.startCountdown(200L, 2000L) {
                                sTime++
                                if (sTime < 3) {
                                    when (sTime) {
                                        1 -> {
                                            MQTTService.publish(sn, devicePos, "锁机")
                                            MQTTService.publish(sn, devicePos, "禁用power键")
                                        }
                                        2 -> {
                                            MQTTService.publish(sn, devicePos, "锁机状态")
                                            MQTTService.publish(sn, devicePos, "power键状态")
                                        }
                                    }
                                } else {
                                    schedulers.stop()
                                    sTime = 0
                                }
                            }
                        } catch (e: Exception) {
                        }

                    }
                    "关闭锁机模式" -> {
                        instructType = "关闭锁机模式"

                        var sTime = 0
                        var schedulers = ScheduledTask()
                        try {
                            schedulers.startCountdown(200L, 2000L) {
                                sTime++
                                if (sTime < 3) {
                                    when (sTime) {
                                        1 -> {
                                            MQTTService.publish(sn, devicePos, "解锁")
                                            MQTTService.publish(sn, devicePos, "启用power键")
                                        }
                                        2 -> {
                                            MQTTService.publish(sn, devicePos, "锁机状态")
                                            MQTTService.publish(sn, devicePos, "power键状态")
                                            MQTTService.publish(sn, devicePos, "设备开机状态")
                                        }
                                    }
                                } else {
                                    schedulers.stop()
                                    sTime = 0
                                }
                            }
                        } catch (e: Exception) {
                        }


                    }
                    "开启锁屏模式" -> {
                        MQTTService.publish(sn, devicePos, "关闭背光")
                        val timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                MQTTService.publish(sn, devicePos, "背光状态")
                                this.cancel()
                            }
                        }, 500)
                    }
                    "关闭锁屏模式" -> {
                        MQTTService.publish(sn, devicePos, "开启背光")
                        val timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                MQTTService.publish(sn, devicePos, "背光状态")
                                this.cancel()
                            }
                        }, 500)
                    }
                    "开启锁盘模式" -> {
                        instructYpSetType = "开启锁盘模式"
                        MQTTService.publish(
                            cumputerList[devicePos].sn,
                            devicePos,
                            "硬盘SN号"
                        )
                    }
                    "关闭锁盘模式" -> {
                        instructYpSetType = "关闭锁盘模式"
                        MQTTService.publish(
                            cumputerList[devicePos].sn,
                            devicePos,
                            "硬盘SN号"
                        )
                    }
                    "数据销毁" -> {
                        instructYpSetType = "数据销毁"
                        MQTTService.publish(
                            cumputerList[devicePos].sn,
                            devicePos,
                            "硬盘SN号"
                        )
                    }
                }

//                if (msg.contains("锁机")) {
//                    presenter.clockComputer(sn, operation_type)
//                } else {
//                    presenter.riskOperations(sn, operation_type, riskType)
//                }
                AppCache.getInstance().riskOperations = 1
            }).show()
    }


    //动态口令
    override fun getTotp(msg: DynamicPasswordEntity?, position: Int) {
        if (hpDBannerVPAdapter != null) {
            hpDBannerVPAdapter!!.notifyItemChanged(position, "动态口令," + msg!!.dynamic_password)
        }
    }

    //更新轨迹
    override fun updateTrack(msg: BaseModel<String>?) {

    }

    //检查更新
    override fun versionUpdate(msg: VersionUpdateNewEntity?) {
        if (msg != null) {
//            activity?.let { updateApkUtils(it, msg) }
        }
    }

    private fun setMapInfoWinData(position: Int) {
        aMap!!.clear()
//        val split =
//            cumputerList[position].current_coordinate.split(",".toRegex()).toTypedArray()
//        val marker1 = LatLng(split[1].toDouble(), split[0].toDouble())
        val marker1 = LatLng(39.916527, 116.397128)
        aMap!!.moveCamera(CameraUpdateFactory.changeLatLng(marker1))
        aMap!!.moveCamera(CameraUpdateFactory.zoomTo(14f))
        var adapter: DeviceMapInfoWinAdapter? = null
        try {
            adapter = DeviceMapInfoWinAdapter(mContext, cumputerList[position])
        } catch (e: AMapException) {
            e.printStackTrace()
        }
        aMap!!.setInfoWindowAdapter(adapter)

        //绘制marker  实际使用时会循环创建marker并填入数据
        val marker = aMap!!.addMarker(
            MarkerOptions()
                .anchor(0.5f, 0.5f)
                .position(marker1)
                .title("标题数据")
                .snippet("消息数据")
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory
                            .decodeResource(
                                mContext.resources,
                                R.mipmap.home_icon_ddw_default
                            )
                    )
                )
        ) //点位图标

        mMarkers!!.add(marker)
        marker.showInfoWindow()
    }

    override fun getMapImage(msg: Bitmap?) {

    }

    override fun showError(msg: String?) {
        super.showError(msg)
        if (msg != "已是最新版本") {
            ToastUtil.showShort(activity, msg)
        }
        if (msg != "存在设备位置未更新") {
            if (msg == "当前用户未绑定设备!") {
                cumputerList.clear()
                SpUtils.setSharedStringData(activity, "HomePageBinding", "")
                binding.llFragHpYbd.visibility = View.GONE
                binding.includeFragWbd.rltIncludeHpBinding.visibility = View.VISIBLE
            } else {
//                binding.includeFragHpDevice.llIncludeNoneData.visibility = View.VISIBLE
            }
        }
    }

    private var inetntMsg: HomePageMQTTMessage? = null

    //mqtt回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getMqttMessage(entity: MQTTMessage) {
        if (cumputerList != null && cumputerList.size > 0) {
            inetntMsg = HomePageMQTTMessage()

            when (entity.type) {
                "开机状态" -> {
                    if (hpDBannerVPAdapter != null) {
                        computerStatus = when (entity.message) {
                            "0" -> {
                                binding.llFragHPTop.setBackgroundResource(R.mipmap.home_gj_bg)
                                cumputerList[entity.position].computer_status = 0
                                hpDBannerVPAdapter!!.notifyItemChanged(entity.position, "开关机," + 0)
                                "关机"

                            }
                            "16" -> {
                                binding.llFragHPTop.setBackgroundResource(R.mipmap.home_kj_bg)
                                cumputerList[entity.position].computer_status = 1
                                hpDBannerVPAdapter!!.notifyItemChanged(entity.position, "开关机," + 1)
                                "开机"
                            }
                            else -> {
                                hpDBannerVPAdapter!!.notifyItemChanged(entity.position, "开关机," + 4)
                                "-1"
                            }

                        }
                        if (entity.message == "0") {
                            if (instructType == "关闭被盗模式" ||
                                instructType == "关闭锁机模式" ||
                                instructType == "关闭锁盘模式"
                            ) {
                                MQTTService.publish(
                                    cumputerList[devicePos].sn,
                                    devicePos,
                                    "关机"
                                )
                            }
                            instructType = ""
                        }
                    }
                }
                "信号强度" -> {
                    cumputerList[entity.position].csq = entity.message
                    if (hpDBannerVPAdapter != null) {
                        hpDBannerVPAdapter!!.notifyItemChanged(
                            entity.position,
                            "信号强度," + entity.message
                        )
                        Log.d("信号强度回调adapter=", "信号强度," + entity.message)
                        reFreshFlag = 0
                        AppCache.getInstance().riskOperations = 0
                        if (mqttSuccessPop != null) {
                            if (mqttSuccessPop!!.isShowing) {
                                mqttSuccessPopLl!!.visibility = View.GONE
                                customTickView!!.visibility = View.VISIBLE
                                customTickView!!.setDown()
                                mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, 2000)
                            }
                        }
                    }
                }
                "设备电量" -> {
                    inetntMsg!!.dl = entity.message
                    EventBus.getDefault().postSticky(inetntMsg)
                    cumputerList[entity.position].battery = entity.message
                    if (hpDBannerVPAdapter != null) {
                        hpDBannerVPAdapter!!.notifyItemChanged(
                            entity.position,
                            "设备电量," + entity.message.toInt()
                        )
                    }
                }
                "Power键状态" -> {

                }
                "背光状态" -> {
                    if (hpDBannerVPAdapter != null) {
//                        Log.e("背光状态position",entity.position.toString())
                        when (entity.message) {
                            "0" -> {
                                ivClockSp!!.setBackgroundResource(R.drawable.btn_blue_oval_background)
                                backlightStatus = 0
                                hpDBannerVPAdapter!!.notifyItemChanged(
                                    entity.position,
                                    "锁定屏幕," + entity.message.toInt()
                                )
                            }
                            "2" -> {
                                ivClockSp!!.setBackgroundResource(R.drawable.btn_gray_oval_background)
                                backlightStatus = 2
                                hpDBannerVPAdapter!!.notifyItemChanged(
                                    entity.position,
                                    "锁定屏幕," + entity.message.toInt()
                                )
                            }
                            else -> {
                                ivClockSp!!.setBackgroundResource(R.drawable.btn_gray_oval_background)
                                backlightStatus = -1
                            }
                        }
                    }
                }
                "锁机状态" -> {
                    if (hpDBannerVPAdapter != null) {
                        when (entity.message) {
                            "0" -> {
                                ivClock!!.setBackgroundResource(R.drawable.btn_gray_oval_background)
                                clock_status = 0
                                hpDBannerVPAdapter!!.notifyItemChanged(
                                    entity.position,
                                    "锁定电脑," + entity.message.toInt()
                                )
                            }
                            "2" -> {
                                ivClock!!.setBackgroundResource(R.drawable.btn_blue_oval_background)
                                clock_status = 2
                                hpDBannerVPAdapter!!.notifyItemChanged(
                                    entity.position,
                                    "锁定电脑," + entity.message.toInt()
                                )
                            }
                            else -> {
                                ivClock!!.setBackgroundResource(R.drawable.btn_gray_oval_background)
                                clock_status = -1
                            }
                        }
                    }
                }
                "被盗状态" -> {
                    if (hpDBannerVPAdapter != null) {
                        when (entity.message) {
                            "0" -> {
                                ivStolen!!.setBackgroundResource(R.drawable.btn_gray_oval_background)
                                stolenStatus = 0
                                hpDBannerVPAdapter!!.notifyItemChanged(
                                    entity.position,
                                    "被盗模式," + entity.message.toInt()
                                )
                            }
                            "2" -> {
                                ivStolen!!.setBackgroundResource(R.drawable.btn_blue_oval_background)
                                stolenStatus = 2
                                hpDBannerVPAdapter!!.notifyItemChanged(
                                    entity.position,
                                    "被盗模式," + entity.message.toInt()
                                )
                            }
                            else -> {
                                ivStolen!!.setBackgroundResource(R.drawable.btn_gray_oval_background)
                                stolenStatus = -1
                            }
                        }
                    }
                }
                "设备硬盘SN号" -> {
                    val split = entity.message!!.split("-")
                    if (split.size >= 4) {
                        ypSn = split[0].substring(
                            split[0].length - 4,
                            split[0].length
                        ) + "-" + split[1] + "-" + split[2] + "-" + split[3].substring(0, 4)
                        Log.e("硬盘SN号回调", ypSn)
                    }
                    when (instructYpSetType) {
                        "开启锁盘模式" -> {
//                            instructYpSetType = ""  //1127
                            Log.e(
                                "锁盘前硬盘sn对比 position=" + devicePos.toString(),
                                "mqtt:" + ypSn + "--" + cumputerList[devicePos].ssd_sn
                            )
                            if (ypSn != "" && ypSn == cumputerList[devicePos].ssd_sn) {
                                MQTTService.publish(cumputerList[devicePos].sn, devicePos, "锁定硬盘")
//                                val timer = Timer()
//                                timer.schedule(object : TimerTask() {
//                                    override fun run() {
//                                        MQTTService.publish(
//                                            cumputerList[devicePos].sn,
//                                            devicePos,
//                                            "硬盘状态"
//                                        )
//                                        this.cancel()
//                                    }
//                                }, 2000)
                            } else {
                                ToastUtil.showLong(activity, "检测到未授权的硬盘,禁止操作!")
                            }
                        }
                        "关闭锁盘模式" -> {
//                            instructYpSetType = ""  //1127
                            Log.e(
                                "解除锁盘前硬盘sn对比 position=" + devicePos.toString(),
                                "mqtt:" + ypSn + "--" + cumputerList[devicePos].ssd_sn
                            )
                            if (ypSn != "" && ypSn == cumputerList[devicePos].ssd_sn) {
                                Log.e("解除锁盘前查询硬盘sn", ypSn)
                                instructType = "关闭锁盘模式"
                                MQTTService.publish(cumputerList[devicePos].sn, devicePos, "解锁硬盘")
//                                var sTime = 0
//                                var schedulers = ScheduledTask()
//                                try {
//                                    schedulers.startCountdown(2000L, 1000L) {
//                                        sTime++
//                                        if (sTime < 3) {
//                                            when (sTime) {
//                                                1 -> {
//                                                    MQTTService.publish(
//                                                        cumputerList[devicePos].sn,
//                                                        devicePos,
//                                                        "硬盘状态"
//                                                    )
//                                                }
//                                                2 -> {
//                                                    MQTTService.publish(
//                                                        cumputerList[devicePos].sn,
//                                                        devicePos,
//                                                        "设备开机状态"
//                                                    )
//                                                }
//                                            }
//                                        } else {
//                                            schedulers.stop()
//                                            sTime = 0
//                                        }
//                                    }
//                                } catch (e: Exception) {
//                                }

                            } else {
                                ToastUtil.showLong(activity, "检测到未授权的硬盘,禁止操作!")
                            }
                        }
                        "数据销毁" -> {
                            instructYpSetType = ""
                            Log.e(
                                "数据销毁前硬盘sn对比 position=" + devicePos.toString(),
                                "mqtt:" + ypSn + "--" + cumputerList[devicePos].ssd_sn
                            )
                            if (ypSn != "" && ypSn == cumputerList[devicePos].ssd_sn) {
                                ToastUtil.showLong(activity, "数据销毁中...")
                                MQTTService.publish(cumputerList[devicePos].sn, devicePos, "销毁")
                                mHandler.sendEmptyMessageDelayed(MQTT_DATA_DESTORY, 5000)
                            } else {
                                ToastUtil.showLong(activity, "检测到未授权的硬盘,禁止操作!")
                            }
                        }
                    }
                }
                "设备硬盘状态" -> {
                    Log.e("硬盘状态type", instructYpSetType)
                    val replace = entity.message.replace(" b'FBRAM\\n\\r'", "")
                    val split = replace.split("\\")
                    if (hpDBannerVPAdapter != null) {
                        if (!replace.contains("FBRAM")
                            && !replace.contains("b'\\r'")
                            && !replace.contains("b'\\n\\r'")
                            && !replace.contains("b'AM\\n\\r'")
                            && !replace.contains("b'M\\n\\r'")
                        ) {
                            when (instructYpSetType) {
                                "开启锁盘模式" -> {
                                    if (replace == "b'\\xab\\xba\\x04\\x00\\xaa\\x01\\xaf\\x00'") {//成功
                                        Log.e("开启锁盘-正确应答", replace)
                                        instructYpSetType = ""
                                        MQTTService.publish(
                                            cumputerList[devicePos].sn,
                                            devicePos,
                                            "硬盘状态"
                                        )
                                    } else {//失败，继续锁定硬盘
                                        Log.e("开启锁盘-失败应答", replace)
                                        MQTTService.publish(
                                            cumputerList[devicePos].sn,
                                            devicePos,
                                            "锁定硬盘"
                                        )
                                    }
                                }
                                "关闭锁盘模式" -> {
                                    if (replace == "b'\\xab\\xba\\x04\\x00\\xaa\\x01\\xaf\\x00'") {//成功
                                        var sTime = 0
                                        var schedulers = ScheduledTask()
                                        try {
                                            schedulers.startCountdown(2000L, 1000L) {
                                                sTime++
                                                if (sTime < 3) {
                                                    when (sTime) {
                                                        1 -> {
                                                            Log.e("关闭锁盘-正确应答", replace)
                                                            instructYpSetType = ""
                                                            MQTTService.publish(
                                                                cumputerList[devicePos].sn,
                                                                devicePos,
                                                                "硬盘状态"
                                                            )
                                                        }
                                                        2 -> {
                                                            MQTTService.publish(
                                                                cumputerList[devicePos].sn,
                                                                devicePos,
                                                                "设备开机状态"
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    schedulers.stop()
                                                    sTime = 0
                                                }
                                            }
                                        } catch (e: Exception) {
                                        }
                                    } else {//失败，继续解锁硬盘
                                        Log.e("关闭锁盘-失败应答", replace)
                                        MQTTService.publish(
                                            cumputerList[devicePos].sn,
                                            devicePos,
                                            "解锁硬盘"
                                        )
                                    }
                                }
                            }
                            if (split.size > 9) {//x00解锁 xf0锁定
                                Log.e("锁盘截取l=", split[8])
                                if (split[8] == "x00") {
                                    instructYpSetType = ""
                                    ivClockSpan!!.setBackgroundResource(R.drawable.btn_gray_oval_background)
                                    ssdCommandStatus = 2
                                    hpDBannerVPAdapter!!.notifyItemChanged(
                                        entity.position,
                                        "锁定硬盘,x00"
                                    )
                                } else if (split[8] == "xf0") {
                                    instructYpSetType = ""
                                    ivClockSpan!!.setBackgroundResource(R.drawable.btn_blue_oval_background)
                                    ssdCommandStatus = 1
                                    hpDBannerVPAdapter!!.notifyItemChanged(
                                        entity.position,
                                        "锁定硬盘,xf0"
                                    )
                                }
                            }
                        }


                        /*if (!replace.contains("FBRAM")
                            && !replace.contains("b'\\r'")
                            && !replace.contains("b'\\n\\r'")
                            && !replace.contains("b'AM\\n\\r'")
                            && !replace.contains("b'M\\n\\r'")
    //                        && !replace.contains("b'\\xab\\xba\\x04\\x00\\xaa\\x01\\xaf\\x00'")
                        ) {
                            Log.e("锁盘状态回调", entity.message.toString())
                        }

                        if (split.size > 9) {//x00解锁 xf0锁定
                            Log.e("锁盘截取l=", split[8])
                            if (split[8] == "x00") {
                                ivClockSpan!!.setBackgroundResource(R.drawable.btn_gray_oval_background)
                                ssdCommandStatus = 2
                                hpDBannerVPAdapter!!.notifyItemChanged(entity.position, "锁定硬盘,x00")
                            } else if (split[8] == "xf0") {
                                ivClockSpan!!.setBackgroundResource(R.drawable.btn_blue_oval_background)
                                ssdCommandStatus = 1
                                hpDBannerVPAdapter!!.notifyItemChanged(entity.position, "锁定硬盘,xf0")
                            }
                        }*/

                    }
                }
                "设备坐标" -> {
                    if (hpDBannerVPAdapter != null) {
                        inetntMsg!!.point = entity.message
                        EventBus.getDefault().postSticky(inetntMsg)
                        cumputerList[entity.position].current_coordinate = entity.message
                        hpDBannerVPAdapter!!.notifyItemChanged(
                            entity.position,
                            "设备坐标_" + entity.message
                        )
                    }
//                    runOnUiThread {
//                        mqttDeviceCenter(entity)
//                    }
                }
                "连接失败" -> {
//                    if (hpDBannerVPAdapter != null) {
//                        cumputerList[entity.position].computer_status = 0
//                        hpDBannerVPAdapter!!.notifyItemChanged(entity.position, "开关机," + 0)
//                    }
                    if (cumputerList.size > 0) {
                        if (cumputerList[devicePos].c_version == 1) {
                            ToastUtil.showLong(activity, "设备不在线！")
                            //设备连接失败后弹出获取网络状态监听权限
//                            rxPermissions = RxPermissions(requireActivity())
//                            rxPermissions!!.request(
//                                Manifest.permission.ACCESS_NETWORK_STATE
//                            ).subscribe { granted ->
//                                if (granted) {
//                                    //获取telephonyManager
//                                    mTelephonyManager =
//                                        requireActivity().getSystemService(BaseActivity.TELEPHONY_SERVICE) as TelephonyManager
//                                    //开始监听
//                                    mListener = PhoneStatListener()
//                                    //监听信号强度
//                                    mTelephonyManager!!.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
//                                } else {
//
//                                }
//                            }
                        }
                    }

                }
            }
        }

    }

    //坐标回调 新版首页不加载地图
    private fun mqttDeviceCenter(entity: MQTTMessage) {
        if (entity.message!!.contains(",")) {
            aMap!!.clear()
            inetntMsg!!.point = entity.message
            EventBus.getDefault().postSticky(inetntMsg)
            cumputerList[entity.position].current_coordinate = entity.message

            val split = entity.message!!.split(",".toRegex()).toTypedArray()
            val marker1 = LatLng(split[1].toDouble(), split[0].toDouble())
            aMap!!.moveCamera(CameraUpdateFactory.changeLatLng(marker1))
            aMap!!.moveCamera(CameraUpdateFactory.zoomTo(14f))
            var adapter: DeviceMapInfoWinAdapter? = null
            try {
                adapter =
                    DeviceMapInfoWinAdapter(mContext, cumputerList[entity.position])
            } catch (e: AMapException) {
                e.printStackTrace()
            }
            aMap!!.setInfoWindowAdapter(adapter)

            //绘制marker  实际使用时会循环创建marker并填入数据
            val marker = aMap!!.addMarker(
                MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(marker1)
                    .title("标题数据")
                    .snippet("消息数据")
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory
                                .decodeResource(
                                    mContext.resources,
                                    R.mipmap.home_icon_ddw_default
                                )
                        )
                    )
            ) //点位图标
            mMarkers!!.add(marker)
            marker.showInfoWindow()

            if (hpDBannerVPAdapter != null) {
                hpDBannerVPAdapter!!.notifyDataSetChanged()
            }
        } else {
            aMap!!.clear()
            val marker1 = LatLng(39.916527, 116.397128)
            aMap!!.moveCamera(CameraUpdateFactory.changeLatLng(marker1))
            aMap!!.moveCamera(CameraUpdateFactory.zoomTo(10f))
            var adapter: DeviceMapInfoWinAdapter? = null
            try {
                adapter =
                    DeviceMapInfoWinAdapter(mContext, cumputerList[entity.position])
            } catch (e: AMapException) {
                e.printStackTrace()
            }
            aMap!!.setInfoWindowAdapter(adapter)

            //绘制marker  实际使用时会循环创建marker并填入数据
            val marker = aMap!!.addMarker(
                MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(marker1)
                    .title("标题数据")
                    .snippet("消息数据")
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory
                                .decodeResource(
                                    mContext.resources,
                                    R.mipmap.home_icon_ddw_default
                                )
                        )
                    )
            ) //点位图标
            mMarkers!!.add(marker)
            marker.showInfoWindow()

            if (hpDBannerVPAdapter != null) {
                hpDBannerVPAdapter!!.notifyDataSetChanged()
            }
        }
    }

    //eventbus退出登录通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getLoginType(data: String) {
        if (data == "退出登录") {
            Log.e("设备收到通知：", "退出登录")
            if (mIsBound) {
                requireActivity().unbindService(serviceConnection!!)
                MQTTService.exitLogin()
                mIsBound = false
            } else if (cumputerList.size > 0) {
                requireActivity().unbindService(serviceConnection!!)
                MQTTService.exitLogin()
            }
        }
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
//                installApk(entity.path)
            }
            if (entity.path != "") {
                installApk(entity.path)
                Log.e("apk下载完毕，路径", entity.path)
            }
        }
    }


    private var schedule: ScheduledFuture<*>? = null
    private var isViewShown = false
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (view != null && isVisibleToUser) {
            reFreshFlag = 0
            isViewShown = true
            if (cumputerList.size > 0) {
//                mIsBound = true //20231009
            }

            schedule = AppExecutors.getInstance().scheduledExecutor().schedule({
                presenter.userComputerList()
            }, 1, TimeUnit.SECONDS)
//            scheduleAtFixedRate!!.cancel(false)
            Log.e("设备首页生命周期Hint1", "isViewShown=$isViewShown")
        } else if (isVisibleToUser) {
            isViewShown = true
            Log.e("设备首页生命周期Hint2", "isViewShown=$isViewShown")
        } else {
            isViewShown = false
//            mIsBound = false
            Log.e("设备首页生命周期Hint3", "isViewShown=$isViewShown")
            if (schedule != null) {
                schedule!!.cancel(false)
            }
            if (scheduledTask != null) {
                scheduledTask!!.stop()
            }
        }
    }

    private var onResumeType = 0
    override fun onResume() {

        super.onResume()
        if (onResumeType != 0 && isViewShown) {
            presenter.userComputerList()
//            onResumeType = 0
        }
        binding.mapFragHpDevice!!.onResume()
        Log.e("设备首页生命周期onResume==》", "onResume =$onResumeType,isViewShown=$isViewShown")

    }


    private var scheduledTask: ScheduledTask? = null
    private var delaytime = 0
    private var delaytime1 = 0//防止线程池没走1
    private var delaytime2 = 0//防止线程池没走2
    private fun mqttPublish() {
        delaytime = 0
        delaytime1 = 0
        delaytime2 = 0
        if (cumputerList.size > 0) {
            scheduledTask = ScheduledTask()
            try {
                scheduledTask!!.startCountdown(1500L, 500L) {
                    delaytime++
                    if (delaytime < 12) {
                        when (delaytime) {
                            1 -> {
                                delaytime1 = 1
                                mqttUnBindType = 1
                                MQTTService.publish(
                                    cumputerList[devicePos].sn,
                                    devicePos,
                                    "设备坐标"
                                )
                                Log.e("delaytime", delaytime.toString())
                            }
                            2 -> {
                                delaytime2 = 2
                                MQTTService.publish(
                                    cumputerList[devicePos].sn,
                                    devicePos,
                                    "设备开机状态"
                                )
                                Log.e("delaytime", delaytime.toString())
                            }
                            3 -> {
                                MQTTService.publish(
                                    cumputerList[devicePos].sn,
                                    devicePos,
                                    "设备电量"
                                )
                                Log.e("delaytime", delaytime.toString())
                            }
                            4 -> {
                                MQTTService.publish(
                                    cumputerList[devicePos].sn,
                                    devicePos,
                                    "信号强度"
                                )
                                Log.e("delaytime", delaytime.toString())
                            }
                            5 -> {
                                MQTTService.publish(
                                    cumputerList[devicePos].sn,
                                    devicePos,
                                    "锁机状态"
                                )
                                Log.e("delaytime", delaytime.toString())
                            }
                            6 -> {
                                MQTTService.publish(
                                    cumputerList[devicePos].sn,
                                    devicePos,
                                    "背光状态"
                                )
                                Log.e("delaytime", delaytime.toString())
                            }
                            7 -> {
                                MQTTService.publish(
                                    cumputerList[devicePos].sn,
                                    devicePos,
                                    "硬盘SN号"
                                )
                                Log.e("delaytime", delaytime.toString())
                            }
                            8 -> {
                                MQTTService.publish(
                                    cumputerList[devicePos].sn,
                                    devicePos,
                                    "硬盘状态"
                                )
                                Log.e("delaytime", delaytime.toString())
                            }
                            9 -> {
                                MQTTService.publish(
                                    cumputerList[devicePos].sn,
                                    devicePos,
                                    "被盗状态"
                                )
                                Log.e("delaytime", delaytime.toString())
                            }
                            10 -> {
                                if (delaytime1 == 0) {
                                    MQTTService.publish(
                                        cumputerList[devicePos].sn,
                                        devicePos,
                                        "设备坐标"
                                    )
                                    Log.e("delaytime", "请求硬盘SN号")
                                }
                            }
                            11 -> {
                                if (delaytime2 == 0) {
                                    MQTTService.publish(
                                        cumputerList[devicePos].sn,
                                        devicePos,
                                        "设备开机状态"
                                    )
                                    Log.e("delaytime", "再次请求设备开机状态")
                                }
                            }
                        }
                    } else {
                        scheduledTask!!.stop()
                        Log.e("delaytime", "停止")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (binding != null) {
            if (mIsBound) {
                requireActivity().unbindService(serviceConnection!!)
                mIsBound = false
            }
            binding.mapFragHpDevice!!.onDestroy()
            mHandler.removeCallbacksAndMessages(null)
            NetworkListenerHelper.removeListener(this)
            Log.e("设备首页生命周期onDestroy==》", "清除")
        } else {
            Log.e("设备首页生命周期onDestroy==》", "不清除")
        }
        UpdateApkUtils1.popDismiss()

    }


    override fun onPause() {
        super.onPause()
        onResumeType = 1
//        mIsBound = false
        binding.mapFragHpDevice!!.onPause()
        Log.e("设备首页生命周期onPause==》", "onPause")

    }

    private var downloadProgressDialog: AlertDialog? = null
    private var downloadPgs: CircleProgress? = null
    private var downloadContinue: TextView? = null

    //检查更新获取网络资源
    private fun getHttpFile() {
        Thread {
            var url = URL(BASE_GET_VERSION_URL)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            val responseCode: Int = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val `in` = BufferedReader(InputStreamReader(conn.inputStream))
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
                    gson.fromJson(txtContent, VersionUpdateNewEntity::class.java)

                if (AppUtils.getAppVersionName().replace("v", "") != "") {
                    val nowVersion = AppUtils.getAppVersionName().replace("v", "").replace("v", "")
                    val versionNew =
                        VersionCodeUtils.isVersionNew(fromJson.last_version, nowVersion)
                    if (versionNew) {
                        Thread {
                            runOnUiThread {
                                activity?.let {
                                    UpdateApkUtils1.updateApkUtils1(
                                        requireActivity(),
                                        it,
                                        fromJson
                                    )
                                }
                                downloadProgressDialog()
                            }
                        }.start()
                    } else {
//                        Thread{
//                            runOnUiThread { ToastUtil.showLong(activity,"已是最新版本!")}
//                        }.start()
                    }
                }

                Log.e("网络文件", txtContent)
                Log.e("网络文件转json", fromJson.toString())
            } else {

                // 处理网络请求失败的情况
                runOnUiThread {
                    ToastUtil.showShort(activity, "更新接口出错")
                }
            }
        }.start()

    }

    //apk下载进度dialog
    private fun downloadProgressDialog() {
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

    }

    //安装apk弹框
    private fun installApk(fileAbsolutePath: String?) {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.pop_apk_install, null, false)
        val dialog = AlertDialog.Builder(mContext).setView(view).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))//设置Dialog背景为透明
        val tvInstall = view.findViewById<TextView>(R.id.tv_pop_apk_install)
        dialog.setCancelable(false)
        dialog.window?.setLayout(//设置对话框的大小
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tvInstall.setOnClickListener {
            AppUtils.installApp(
                mContext,
                File(fileAbsolutePath),
                AppUtils.getAppPackageName()
            )
        }
        dialog.show()
    }

    private val NETWORKTYPE_WIFI = 1
    private val NETWORKTYPE_2G = 2
    private val NETWORKTYPE_3G = 3
    private val NETWORKTYPE_4G = 4
    private val NETWORKTYPE_5G = 5
    private val NETWORKTYPE_NONE = 0
    var mTelephonyManager: TelephonyManager? = null
    var mListener: PhoneStatListener? = null

    private var rxPermissions: RxPermissions? = null

    @SuppressWarnings("deprecation")
    inner class PhoneStatListener : PhoneStateListener() {
        //获取信号强度
        @SuppressLint("CheckResult")
        override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
            super.onSignalStrengthsChanged(signalStrength)
            //获取网络类型
            val netWorkType: Int? = activity?.let { getNetWorkType(it) }
            when (netWorkType) {
                NETWORKTYPE_WIFI -> {
//                Log.e("当前网络为wifi,信号强度为", gsmSignalStrength.toString())
                }
                NETWORKTYPE_2G -> {
                    binding.tvFragHPNetworkType.visibility = View.VISIBLE
                    binding.tvFragHPNetworkType.text = "当前网络较差"
//                    Log.e("当前网络为2G移动网络,信号强度为", gsmSignalStrength.toString())
                }
                NETWORKTYPE_3G -> {
                    binding.tvFragHPNetworkType.visibility = View.VISIBLE
                    binding.tvFragHPNetworkType.text = "当前网络较差"
//                    Log.e("当前网络为3G移动网络,信号强度为", gsmSignalStrength.toString())
                }
                NETWORKTYPE_4G -> {
                    binding.tvFragHPNetworkType.visibility = View.GONE
//                    Log.e("当前网络为4G移动网络,信号强度为", gsmSignalStrength.toString())
                }
                NETWORKTYPE_5G -> {
                    binding.tvFragHPNetworkType.visibility = View.GONE
//                    Log.e("当前网络为5G移动网络,信号强度为", gsmSignalStrength.toString())
                }
                NETWORKTYPE_NONE -> {
                    binding.tvFragHPNetworkType.visibility = View.VISIBLE
                    binding.tvFragHPNetworkType.text = "当前无网络"
//                    Log.e("当前没有网络,信号强度为", gsmSignalStrength.toString())
                }
                -1 -> {
                    binding.tvFragHPNetworkType.visibility = View.VISIBLE
                    binding.tvFragHPNetworkType.text = "当前无网络"
//                    Log.e("当前网络错误,信号强度为", gsmSignalStrength.toString())
                }
            }

        }
    }

    /**
     * wifi信号level
     * @param wifiLevel
     */
    fun setWifiLevel(wifiLevel: Int) {
        when (wifiLevel) {
            0 -> {
                binding.tvFragHPNetworkType.visibility = View.GONE
//                Log.e("信号强度:", "信号最强")
            }
            1 -> {
                binding.tvFragHPNetworkType.visibility = View.GONE
//                Log.e("信号强度:", "较强")
            }
            2 -> {
                binding.tvFragHPNetworkType.visibility = View.VISIBLE
                binding.tvFragHPNetworkType.text = "当前网络较差"
//                Log.e("信号强度:", "较弱")
            }
            else -> {
                binding.tvFragHPNetworkType.visibility = View.VISIBLE
                binding.tvFragHPNetworkType.text = "当前网络较差"
//                Log.e("信号强度:", "微弱")
            }
        }

    }

    fun getNetWorkType(context: Context): Int {
        var mNetWorkType = -1
        val manager =
            context.getSystemService(BaseActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            val type = networkInfo.typeName
            if (type.equals("WIFI", ignoreCase = true)) {
                mNetWorkType = NETWORKTYPE_WIFI
//                Log.e("当前网络：","wifi")
                setWifiLevel(WifiCheckUtils.checkWifiLevle(requireActivity(), true))

            } else if (type.equals("MOBILE", ignoreCase = true)) {
//                Log.e("当前网络：","流量")
                when (isFastMobileNetwork(context)) {
                    0 -> {
                        return NETWORKTYPE_NONE
                    }
                    2 -> {
                        return NETWORKTYPE_2G
                    }
                    3 -> {
                        return NETWORKTYPE_3G
                    }
                    4 -> {
                        return NETWORKTYPE_4G
                    }
                    5 -> {
                        return NETWORKTYPE_5G
                    }
                }
            }
        } else {
            mNetWorkType = NETWORKTYPE_NONE //没有网络
        }
        return mNetWorkType
    }


    /**判断网络类型 */
    @SuppressLint("MissingPermission")
    private fun isFastMobileNetwork(context: Context): Int {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        when (telephonyManager.dataNetworkType) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN,
            TelephonyManager.NETWORK_TYPE_GSM
            -> return 2
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_TD_SCDMA
            -> return 3
            TelephonyManager.NETWORK_TYPE_LTE
            -> return 4
            TelephonyManager.NETWORK_TYPE_NR
            -> return 5
            else -> return 0
        }
    }

    var networkMqttRefresh = false

    //网络状态监听回调
    override fun onNetworkConnected(isConnected: Boolean, networkStatus: NetworkStatus?) {
        Log.e("监听网络状态", isConnected.toString() + networkStatus!!.desc)
        runOnUiThread {
            if (!isConnected) {
                binding.tvFragHPNetworkType.text = "当前无网络"
                binding.tvFragHPNetworkType.visibility = View.VISIBLE

            } else {
                binding.tvFragHPNetworkType.visibility = View.GONE
                reFreshFlag = 0
                presenter.userComputerList()
            }
        }

        Thread{
            if (AppCache.getInstance().apkUploadIng == 0) {
//                getHttpFile()//检查更新
            }
        }.start()

//        Thread {
//            if (!isConnected) {
//                val sharedList = SpUtils.getSharedList(
//                    activity,
//                    "userComputerList",
//                    ComputerListEntity.ComputerListDTO::class.java
//                )
//                if (sharedList != null && sharedList.size > 0) {
//                    networkMqttRefresh = true
//                    cumputerList.clear()
//                    cumputerList.addAll(sharedList)
//                    runOnUiThread {
//                        binding.bannerVpFragHpDevice.refreshData(cumputerList)
//                    }
//                    Log.e("无网络状态下读取缓存数据", cumputerList.toString())
//                    hpDBannerVPAdapter!!.setOnItemClickListener { v, position ->
//                        when (v.id) {
//                            R.id.tv_item_hp_device_banner_kl_hq -> {//获取口令(远程版)
//                                getDynamic(cumputerList[position].ssd_sn)
//                            }
//                            R.id.tv_item_hp_device_banner_kl_djb -> {//获取口令(单机版)
//                                getDynamic(cumputerList[position].ssd_sn)
//                            }
//                        }
//                    }
//                } else {
//                    cumputerList.clear()
//                    devicePos = 0
//                    if (mIsBound) {
//                        Log.e("true", "当前用户未绑定设备")
//                        requireActivity().unbindService(serviceConnection!!)
//                        MQTTService.exitLogin()
//                        mIsBound = false
//                    }
//                }
//            } else {
//                reFreshFlag = 0
//                presenter.userComputerList()
//                if (AppCache.getInstance().apkUploadIng == 0) {
//                    getHttpFile()//检查更新
//                }
//            }
//        }.start()

    }

    //获取归属地
    private fun getUserAddress(ip: String) {
        Thread {
            //OK设置请求超时时间，读取超时时间
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
            val retrofit: Retrofit = Retrofit.Builder().baseUrl(ApiRetrofit.BASE_GET_IP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            val apiService: ApiServer = retrofit.create(ApiServer::class.java)
            val fieldMap: MutableMap<String, Any> = HashMap()
            fieldMap["ip"] = "223.70.179.71"
            fieldMap["json"] = true
            val observable: Observable<UserIpEntity> = apiService.getUserIp(fieldMap)
            observable.subscribeOn(Schedulers.io())
                .subscribe(object : Observer<UserIpEntity?> {
                    override fun onNext(value: UserIpEntity) {

                        try {

                            runOnUiThread(Runnable
                            //开启主线程更新UI
                            {
                                try { //调用saveFile方法
                                    Log.e("city===========", value.toString())
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            })
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onComplete() {

                    }
                })
        }.start()
    }


    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {

    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
    }

    //mqtt位置回调
    override fun setMessage(message: String?, pos: Int, type: String) {
    }

    //mqtt信号强度回调
    override fun setCsq(message: String?, pos: Int) {
    }

    //mqtt设备电量回调
    override fun setBattery(message: String?, pos: Int) {
    }

    //mqtt设备power键状态回调
    override fun setPowerStatus(message: String?, pos: Int) {
    }

    //mqtt设备背光状态回调
    override fun setBacklightStatus(message: String?, pos: Int) {
    }

    //mqtt设备锁机状态回调
    override fun setClockStatus(message: String?, pos: Int) {
    }

    //mqtt设备被盗状态回调
    override fun setStolenStatus(message: String?, pos: Int) {
    }

    //mqtt设备开关机状态回调
    override fun setComputerStatus(message: String?, pos: Int) {
    }

    //mqtt硬盘状态回调
    override fun setSsdCommand(message: String?, pos: Int) {
    }

    //mqtt硬盘sn号回调
    var ypSn = ""
    override fun setSsdCommandSN(message: String?, pos: Int) {
    }

    //mqttUsb端口状态回调
    override fun setUsbStatus(message: String?, pos: Int) {
    }

    //mqttType-c端口状态回调
    override fun setTypeStatus(message: String?, pos: Int) {
    }

    //mqttWifi端口状态回调
    override fun setWifiStatus(message: String?, pos: Int) {
    }

    //mqtt蓝牙端口状态回调
    override fun setBlueToothStatus(message: String?, pos: Int) {
    }


    /*private fun downLoadImg(location:String,zoom:String,size:String,markers:String,key:String) {
        Thread {
            //OK设置请求超时时间，读取超时时间
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
            val retrofit: Retrofit = Retrofit.Builder().baseUrl(ApiRetrofit.BASE_GD_MAP_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            val apiService: ApiServer = retrofit.create(ApiServer::class.java)
            val fieldMap: MutableMap<String, Any> = HashMap()
            fieldMap["location"] = location
            fieldMap["zoom"] = zoom
            fieldMap["size"] = size
            fieldMap["markers"] = markers
            fieldMap["key"] = key
            val observable: Observable<ResponseBody> = apiService.getMapImage(fieldMap)
            observable.subscribeOn(Schedulers.io())
                .subscribe(object : Observer<ResponseBody?> {
                    override fun onNext(value: ResponseBody) {
                        var bys = ByteArray(0)
                        try {
                            bys = value.bytes() //注意：把byte[]转换为bitmap时，也是耗时操作，也必须在子线程
                            bitmap = BitmapFactory.decodeByteArray(bys, 0, bys.size)
                            bitmap = BitmapFactory.decodeByteArray(bys, 0, bys.size)
                            runOnUiThread(Runnable
                            //开启主线程更新UI
                            {
//                                binding.homepageImage.setImageBitmap(bitmap)
//                                img.setImageBitmap(bitmap)
                                try { //调用saveFile方法

                                    Log.e(
                                        "TAG",
                                        Environment.getExternalStorageDirectory()
                                            .toString() + "/imgpic/"
                                    )
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            })
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onComplete() {

                    }
                })
        }.start()
    }*/

}
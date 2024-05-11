package com.chuzhi.xzyx.ui.activity.homepage

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.databinding.ActivityLocationTrackingBinding
import com.chuzhi.xzyx.ui.adapter.LocationTrackingListAdapter
import com.chuzhi.xzyx.ui.bean.mqtt.HomePageMQTTMessage
import com.chuzhi.xzyx.ui.bean.mqtt.MQTTMessage
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity
import com.chuzhi.xzyx.ui.bean.rc.GeoFenceCountEntity
import com.chuzhi.xzyx.ui.presenter.LocationTrackingPresenter
import com.chuzhi.xzyx.ui.view.LocationTrackingView
import com.chuzhi.xzyx.utils.AlertDialogIos
import com.chuzhi.xzyx.utils.CommenPop
import com.chuzhi.xzyx.utils.GpsUtil
import com.chuzhi.xzyx.utils.ToastUtil
import com.chuzhi.xzyx.utils.apputils.AppUtils
import com.chuzhi.xzyx.utils.map.MapNavigationUtils
import com.loopeer.cardstack.CardStackView
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference


/**
 * 终端定位
 */
class LocationTrackingActivity :
    BaseActivity<ActivityLocationTrackingBinding, LocationTrackingPresenter>(),
    AMapLocationListener, LocationTrackingView, CardStackView.ItemExpendListener ,
    LocationSource,GeocodeSearch.OnGeocodeSearchListener{

    private var lTListAdapter: LocationTrackingListAdapter? = null
    private var aMap: AMap? = null
    private var addMarker: Marker? = null
    private var computerData: ComputerListEntity.ComputerListDTO? = null
    private var geocodeSearch: GeocodeSearch? = null
    private var rxPermissions: RxPermissions?=null
    private var permissionPop : AlertDialogIos? = null
    private var mListener: LocationSource.OnLocationChangedListener? = null
    //声明AMapLocationClient类对象
    var mapLocationClient: AMapLocationClient? = null
    //声明AMapLocationClientOption对象
    var mapLocationClientOption: AMapLocationClientOption? = null
    private var deviceType = 0
    private var tipPop: CommenPop? = null
    private class MyHandler(activity: LocationTrackingActivity?) : Handler() {
        private var mActivity: WeakReference<LocationTrackingActivity>? = null
        override fun handleMessage(msg: Message) {
            val activity: LocationTrackingActivity = mActivity!!.get()!!
            if (activity != null) {

            }
        }

        init {
            mActivity = WeakReference<LocationTrackingActivity>(activity)
        }
    }

    private val mHandler = MyHandler(this)

    /**
     * Instances of anonymous classes do not hold an implicit
     * reference to their outer class when they are "static".
     */
    private val sRunnable = Runnable { /* ... */ }

    override fun createPresenter(): LocationTrackingPresenter {
        return LocationTrackingPresenter(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        EventBus.getDefault().register(this)
        binding.includeActLT.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActLT.tvIncludeTitleTitle.text = "终端定位"
        binding.includeActLT.ivIncludeTitleBack.setOnClickListener { finish() }
        rxPermissions = RxPermissions(this)
        AMapLocationClient.updatePrivacyAgree(this, true);
        AMapLocationClient.updatePrivacyShow(this, true, true);
        binding.mapActLT.onCreate(this!!.intent.extras)
        permissionPop()
        deviceType = intent.getIntExtra("deviceType", 0)
        if (deviceType == 1) {
            computerData =
                intent.getSerializableExtra("deviceData") as ComputerListEntity.ComputerListDTO
        } else {
            val notificationmanager: NotificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationmanager.cancel(1001)
        }
        initAMap()

        if (computerData!=null){
            binding.tvActLTName.text = computerData!!.name
            binding.tvActLTDl.text = "剩余电量"+computerData!!.battery+"%"
            binding.dvActLTDl.setProgress(computerData!!.battery.toInt())
            binding.tvActLTYjNum.text = "围栏预警"+computerData!!.geogence_warning_count.toString()+"次"
//            presenter.getGeofenceCount(computerData!!.sn)
        }


        //设置围栏
        binding.btnActLTSetFence.setOnClickListener {
            if (computerData!=null){
                var intent = Intent(this, FenceListActivity::class.java)
                intent.putExtra("computerData", computerData)
                startActivity(intent)
            }else{
                ToastUtil.showLong(this,"位置信息获取失败！")
            }

        }

    }




    var list = ArrayList<String>()
    override fun initData() {
        list.add("1")
//        list.add("2")
//        list.add("3")
//        binding.csvActLT.itemExpendListener = this
//        lTListAdapter = LocationTrackingListAdapter(this)
//        binding.csvActLT.setAdapter(lTListAdapter)
//        mHandler.postDelayed(Runnable { lTListAdapter!!.updateData(list) }, 200)
//
//        lTListAdapter!!.setOnClickListener { view, position ->
//            when(view.id){
//                R.id.text_list_card_gj->{
//                    if (computerData!=null){
//                        var intent = Intent(this,TrackDetailsActivity::class.java)
//                        intent.putExtra("deviceData",computerData)
//                        startActivity(intent)
//                    }
//                }
//                R.id.text_list_card_jb->{
//                    ToastUtil.showShort(this,"警报")
//                }
//                R.id.text_list_card_zz->{
//
//                    var intent = Intent(this,TrackNavigationActivity::class.java)
//                    intent.putExtra("deviceData",computerData)
//                    startActivity(intent)
//                }
//            }
//        }

        //轨迹
        binding.tvActLTGj.setOnClickListener {
            if (computerData != null) {
                var intent = Intent(this, TrackListActivity::class.java) //TrackDetailsActivity
                intent.putExtra("computerSn",computerData!!.sn)
                intent.putExtra("computerName",computerData!!.name)
                startActivity(intent)
            }else{
                ToastUtil.showLong(this,"位置信息获取失败！")
            }
        }
        //追踪
        binding.tvActLTZz.setOnClickListener {
            permissionData()
        }
    }

    /**
     * 初始化AMap对象
     */
    private fun initAMap() {
        if (aMap == null) {
            aMap = binding.mapActLT.map
        }
        aMap!!.isTrafficEnabled = true
        /*aMap!!.setLocationSource(this);// 设置定位监听
        aMap!!.uiSettings.isMyLocationButtonEnabled = true;// 设置默认定位按钮是否显示
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap!!.isMyLocationEnabled = true;
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap!!.setMyLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);*/
        //地理搜索类
        geocodeSearch = GeocodeSearch(this);
        geocodeSearch!!.setOnGeocodeSearchListener(this);
        if (computerData != null) {
            if (computerData!!.current_coordinate!=null){
                val center = computerData!!.current_coordinate
                val split = center.split(",")
                setMapCenter(LatLng(split[1].toDouble(), split[0].toDouble()))
                getAddressByLatlng(LatLng(split[1].toDouble(), split[0].toDouble()))
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getMqttPoint(msg: HomePageMQTTMessage) {
        Log.e("首页传来的坐标点",msg.toString())
        if (msg!=null){
            if (deviceType==1){
                if (msg.point!=null&&msg.point.contains(",")){
                    if (computerData!=null){
                        computerData!!.current_coordinate = msg.point
                    }
                    val split = msg.point.split(",")
                    setMapCenter(LatLng(split[1].toDouble(), split[0].toDouble()))
                    getAddressByLatlng(LatLng(split[1].toDouble(), split[0].toDouble()))
                }
                if (msg.dl!=null){
                    binding.tvActLTDl.text = "剩余电量"+msg.dl+"%"
                    binding.dvActLTDl.setProgress(msg.dl.toInt())
                }
            }


        }
    }
    private fun getAddressByLatlng(latLng: LatLng?) {
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        val latLonPoint = LatLonPoint(latLng!!.latitude, latLng.longitude)
        val query = RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP)
        //异步查询
        geocodeSearch!!.getFromLocationAsyn(query)

    }

    /**
     * 设置地图中心点
     * @param latLng
     */
    private fun setMapCenter(latLng: LatLng) {
        aMap!!.moveCamera(CameraUpdateFactory.changeLatLng(latLng))
        aMap!!.moveCamera(CameraUpdateFactory.zoomTo(16f))

        drawMarker(latLng)
    }

    /**
     * 绘制自定义marker（图标）
     */
    fun drawMarker(latLng: LatLng?) {
        if (addMarker != null) {
            addMarker!!.remove()
        }
        val markerOption = MarkerOptions()
        markerOption.position(latLng)
        markerOption.draggable(false) //设置Marker true可拖动 false不可拖动
        markerOption.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory
                    .decodeResource(resources, R.mipmap.track_icon_position)
            )
        )
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//        markerOption.setFlat(false) //设置marker平贴地图效果
        markerOption.anchor(0.5f, 0.5f) //设置marker偏移量
        addMarker = aMap!!.addMarker(markerOption)
    }

    override fun onResume() {
        super.onResume()
        binding.mapActLT!!.onResume()
        if (computerData!=null){
//            presenter.getGeofenceCount(computerData!!.sn)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapActLT!!.onDestroy()
        if (mapLocationClient != null) {
            mapLocationClient!!.onDestroy()
        }
        if (tipPop!=null){
            if (tipPop!!.isShowing){
                tipPop!!.dismiss()
            }
        }
        EventBus.getDefault().unregister(this)
    }

    override fun onPause() {
        super.onPause()
        binding.mapActLT!!.onPause()
    }
    override fun onStop() {
        super.onStop()
        if (mapLocationClient != null) {
            mapLocationClient!!.stopLocation()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapActLT!!.onSaveInstanceState(outState)

    }
    private fun permissionPop(){
        permissionPop = AlertDialogIos(this).builder()
            .setTitle("无法访问位置")
            .setMsg("小志云享需要位置信息以开启“定位追踪\"功能\n请在设置中开启权限")
//            .setNegativeButton("取消", R.color.gray,null)
            .setPositiveButton("设置", R.color.text_default,View.OnClickListener {
                if (tipPop!=null){
                    if (tipPop!!.isShowing){
                        tipPop!!.dismiss()
                    }
                }
                //跳转应用消息，间接打开应用权限设置-效率高
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            })
    }
    @SuppressLint("CheckResult")
    private fun permissionData(){
        tipPopup()
        rxPermissions!!.request(
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).subscribe { granted ->

            if (granted) {
                if (tipPop!=null){
                    if (tipPop!!.isShowing){
                        tipPop!!.dismiss()
                    }
                }
                if (GpsUtil.isOPen(this)) {
                    if (computerData!=null){
//                        var intent = Intent(this, TrackNavigationActivity::class.java)
//                        intent.putExtra("deviceData", computerData)
//                        startActivity(intent)
                        trackNavigationPop()
                    }else{
                        ToastUtil.showLong(this,"位置信息获取失败！")
                    }

                } else {
                    GpsUtil.openGPS(this)
                }
            } else {
                permissionPop!!.show()
            }
        }
    }
    private fun tipPopup(){
        tipPop = CommenPop.getNormalPopu(this,R.layout.pop_tip,binding.llActLTTop)
        val contentView = tipPop!!.contentView
        val tvTip = contentView.findViewById<TextView>(R.id.tv_pop_tip)
        tvTip.text = "定位权限使用说明：\n提供位置后便于导航追踪设备"
        tipPop!!.isOutsideTouchable = true
        tipPop!!.isFocusable = true
        CommenPop.backgroundAlpha(0.5f, this)
        tipPop!!.showAtLocation(
            binding.llActLTTop,
            Gravity.TOP,
            100,
            0
        )
    }
    private var trackNavigationPop:CommenPop?=null
    private fun trackNavigationPop(){
        trackNavigationPop = CommenPop.getNormalPopu(this,R.layout.pop_track_navigation,binding.llActLTTop)
        val contentView = trackNavigationPop!!.contentView
        val tvGd = contentView.findViewById<TextView>(R.id.tv_pop_t_n_gd)
        val tvBd = contentView.findViewById<TextView>(R.id.tv_pop_t_n_bd)
        val tvTx = contentView.findViewById<TextView>(R.id.tv_pop_t_n_tx)
        val btClose = contentView.findViewById<Button>(R.id.btn_pop_t_n_tx_close)
        trackNavigationPop!!.isFocusable = true
        CommenPop.backgroundAlpha(0.5f, this)
        trackNavigationPop!!.showAtLocation(binding.llActLTTop, Gravity.BOTTOM, 0, 0)

        tvGd.setOnClickListener {
            if (computerData != null) {
                if (computerData!!.current_coordinate!=null){
                    val center = computerData!!.current_coordinate
                    val split = center.split(",")
//                    setMapCenter(LatLng(split[1].toDouble(), split[0].toDouble()))
                    getAddressByLatlng(LatLng(split[1].toDouble(), split[0].toDouble()))
                    MapNavigationUtils.openGaodeNavigation(this,"设备位置",
                        computerData!!.current_coordinate.split(",")[1].toDouble(),
                        computerData!!.current_coordinate.split(",")[0].toDouble())
                    trackNavigationPop!!.dismiss()
                }
            }
        }
        tvBd.setOnClickListener {
            if (computerData != null) {
                if (computerData!!.current_coordinate!=null){
                    val center = computerData!!.current_coordinate
                    val split = center.split(",")
//                    setMapCenter(LatLng(split[1].toDouble(), split[0].toDouble()))
                    getAddressByLatlng(LatLng(split[1].toDouble(), split[0].toDouble()))
                    MapNavigationUtils.openBaiduNavigation(this,"设备位置",
                        computerData!!.current_coordinate.split(",")[1].toDouble(),
                        computerData!!.current_coordinate.split(",")[0].toDouble())
                    trackNavigationPop!!.dismiss()
                }
            }
        }
        tvTx.setOnClickListener {
            if (computerData != null) {
                if (computerData!!.current_coordinate!=null){
                    val center = computerData!!.current_coordinate
                    val split = center.split(",")
//                    setMapCenter(LatLng(split[1].toDouble(), split[0].toDouble()))
                    getAddressByLatlng(LatLng(split[1].toDouble(), split[0].toDouble()))
                    MapNavigationUtils.openTencentNavigation(this,"设备位置",
                        computerData!!.current_coordinate.split(",")[1].toDouble(),
                        computerData!!.current_coordinate.split(",")[0].toDouble())
                    trackNavigationPop!!.dismiss()
                }
            }
        }
        btClose.setOnClickListener {
            trackNavigationPop!!.dismiss()
        }
    }



    //点击卡片显示上一条下一条
    override fun onItemExpend(expend: Boolean) {
//        binding.buttonContainer.visibility = if (expend) View.VISIBLE else View.GONE
    }

    fun onPreClick(view: android.view.View) {
//        binding.csvActLT.pre()
    }

    fun onNextClick(view: android.view.View) {
//        binding.csvActLT.next()
    }

    /**
     *  得到逆地理编码异步查询结果
     */
    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {
        val regeocodeAddress = p0!!.regeocodeAddress
        val district = regeocodeAddress.district
        val formatAddress = regeocodeAddress.formatAddress
        binding.tvActLTCity.text = district
        binding.tvActLTAddress.text = formatAddress
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
    }

    //越过围栏总次数回调
    @SuppressLint("SetTextI18n")
    override fun getGeofenceCount(msg: GeoFenceCountEntity?) {
        binding.tvActLTYjNum.text = "围栏预警"+msg!!.warning_count.toString()+"次"
    }

    private var startLatLonPoint = ""
    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (aMapLocation != null) {
            if (aMapLocation.errorCode === 0) {
//                mListener!!.onLocationChanged(aMapLocation) // 显示系统小蓝点
                val stringBuilder = StringBuilder()
                //定位成功回调信息，设置相关消息
                val type: Int = aMapLocation.locationType
                val address: String = aMapLocation.address
                stringBuilder.append(type.toString() + address)
                startLatLonPoint = aMapLocation.latitude.toString()+","+aMapLocation.longitude.toString()
                Log.e("定位成功",startLatLonPoint)
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见下方错误码表。
                Log.i(
                    "定位失败：",
                    aMapLocation.errorCode.toString() + "---" + aMapLocation.errorInfo
                )
            }
        }
    }

    //激活定位
    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener?) {
        mListener = onLocationChangedListener
        if (mapLocationClient == null) {
            //初始化AMapLocationClient，并绑定监听
            mapLocationClient = AMapLocationClient(this)

            //初始化定位参数
            mapLocationClientOption = AMapLocationClientOption()
            //设置定位精度
            mapLocationClientOption!!.locationMode =
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //是否返回地址信息
            mapLocationClientOption!!.isNeedAddress = true
            //是否只定位一次
            mapLocationClientOption!!.isOnceLocation = false
            //设置是否强制刷新WIFI，默认为强制刷新
            mapLocationClientOption!!.isWifiActiveScan = true
            //是否允许模拟位置
            mapLocationClientOption!!.isMockEnable = false
            //定位时间间隔
            mapLocationClientOption!!.interval = 2000
            //给定位客户端对象设置定位参数
            mapLocationClient!!.setLocationOption(mapLocationClientOption)
            //绑定监听
            mapLocationClient!!.setLocationListener(this)
            //开启定位
            mapLocationClient!!.startLocation()

        }
    }

    //停止定位
    override fun deactivate() {
        mListener = null
        if (mapLocationClient != null) {
            mapLocationClient!!.stopLocation()
            mapLocationClient!!.onDestroy()
        }
        mapLocationClient = null;
    }


}
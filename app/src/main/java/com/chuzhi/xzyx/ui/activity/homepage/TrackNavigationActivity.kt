package com.chuzhi.xzyx.ui.activity.homepage

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.*
import com.amap.api.navi.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.*
import com.amap.api.services.route.*
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.app.MyApplication
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.base.BaseView
import com.chuzhi.xzyx.databinding.ActivityTrackNavigationBinding
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity
import com.chuzhi.xzyx.utils.TimeUtils
import com.chuzhi.xzyx.utils.ToastUtil
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * 追踪导航页面
 */
class TrackNavigationActivity : BaseActivity<ActivityTrackNavigationBinding, BasePresenter<*>>(),
    BaseView, LocationSource, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener,
    DistanceSearch.OnDistanceSearchListener,RouteSearch.OnRouteSearchListener  {

    private var aMap: AMap? = null
    private var addMarker: Marker? = null
    private var geocodeSearch: GeocodeSearch? = null
    private var mListener: LocationSource.OnLocationChangedListener? = null

    //声明AMapLocationClient类对象
    var mapLocationClient: AMapLocationClient? = null

    //声明AMapLocationClientOption对象
    var mapLocationClientOption: AMapLocationClientOption? = null
    //测距
    private var distanceQuery: DistanceSearch.DistanceQuery? = null
    private var distanceSearch: DistanceSearch? = null
    //骑行
    private var mRouteSearch : RouteSearch?=null
    private var computerListDTO:ComputerListEntity.ComputerListDTO?=null
    private var currentCoordinate = "0.0,0.0"
    private var navigationType = -1
    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }

    override fun initView() {
        binding.includeActTrackNav.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActTrackNav.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.includeActTrackNav.tvIncludeTitleTitle.text = "定位追踪"
        AMapLocationClient.updatePrivacyAgree(this, true);
        AMapLocationClient.updatePrivacyShow(this, true, true);
        binding.mapActTrackNav.onCreate(this!!.intent.extras)

        computerListDTO =
            intent.getSerializableExtra("deviceData") as ComputerListEntity.ComputerListDTO
        if (computerListDTO!=null){
            currentCoordinate = computerListDTO!!.current_coordinate
            binding.tvActTrackNavName.text = computerListDTO!!.name
            binding.tvActTrackNavDl.text =  "剩余电量"+computerListDTO!!.battery+"%"
        }
        initAMap()
    }

    var rbtType = 1
    override fun initData() {
        binding.rgpActTrackNav.setOnCheckedChangeListener { group, checkedId ->
            binding.btnActTrackNav.visibility = View.VISIBLE
            binding.tvActTrackNavDistance.visibility = View.VISIBLE
            when (checkedId) {
                R.id.rbt_act_track_nav_walk -> {//步行
                    rbtType = 1
//                    //设置测量方式，支持直线和驾车
//                    distanceQuery!!.type = DistanceSearch.TYPE_WALK_DISTANCE
//                    //发送请求
//                    distanceSearch!!.calculateRouteDistanceAsyn(distanceQuery)
                    if (currentCoordinate!="0.0,0.0"&&startLatLonPoint!=""){
                        val splitStart = startLatLonPoint.split(",")
                        val splitEnd = currentCoordinate.split(",")
                        val fromAndTo = RouteSearch.FromAndTo(LatLonPoint(splitStart[0].toDouble(), splitStart[1].toDouble()),
                            LatLonPoint(splitEnd[1].toDouble(), splitEnd[0].toDouble()))
                        val query = RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WALK_DEFAULT)
                        mRouteSearch!!.calculateWalkRouteAsyn(query)
                    }

                }
                R.id.rbt_act_track_nav_ride -> {//骑车
                    rbtType = 2
                    if (currentCoordinate!="0.0,0.0"&&startLatLonPoint!=""){
                        val splitStart = startLatLonPoint.split(",")
                        val splitEnd = currentCoordinate.split(",")
                        val fromAndTo = RouteSearch.FromAndTo(LatLonPoint(splitStart[0].toDouble(), splitStart[1].toDouble()),
                            LatLonPoint(splitEnd[1].toDouble(), splitEnd[0].toDouble()))
                        val query = RouteSearch.RideRouteQuery(fromAndTo, RouteSearch.RidingDefault)
                        mRouteSearch!!.calculateRideRouteAsyn(query)
                    }

                }
                R.id.rbt_act_track_nav_drive -> {//驾车
                    rbtType = 3
                    if (currentCoordinate!="0.0,0.0"&&startLatLonPoint!=""){
                        val splitStart = startLatLonPoint.split(",")
                        val splitEnd = currentCoordinate.split(",")
                        val fromAndTo = RouteSearch.FromAndTo(LatLonPoint(splitStart[0].toDouble(), splitStart[1].toDouble()),
                            LatLonPoint(splitEnd[1].toDouble(), splitEnd[0].toDouble()))
                        val query = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT,null,null,"")
                        mRouteSearch!!.calculateDriveRouteAsyn(query)
                    }
                }
            }
        }

        binding.btnActTrackNav.setOnClickListener {
            if (currentCoordinate!="0.0,0.0"){
                navigationType = 1
                val splitEnd = currentCoordinate.split(",")
                getAddressByLatlng(LatLng(splitEnd[1].toDouble(), splitEnd[0].toDouble()))//终点逆地理编码（坐标转地址）
            }else{
                ToastUtil.showLong(this,"位置信息获取失败！")
            }
        }
    }

    private fun initAMap() {
        if (aMap == null) {
            aMap = binding.mapActTrackNav.map
        }
        aMap!!.isTrafficEnabled = true
        aMap!!.setLocationSource(this);// 设置定位监听
        aMap!!.uiSettings.isMyLocationButtonEnabled = true;// 设置默认定位按钮是否显示
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap!!.isMyLocationEnabled = true;
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap!!.setMyLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        //地理搜索类
        geocodeSearch = GeocodeSearch(this);
        geocodeSearch!!.setOnGeocodeSearchListener(this);



        //测量距离和时间
//        distanceQuery = DistanceSearch.DistanceQuery()
//        distanceSearch = DistanceSearch(this)
//        val start =
//            LatLonPoint(39.899672, 116.4273404)
//        val dest = LatLonPoint(39.709771, 116.38339)
//        val latLonPoints = ArrayList<LatLonPoint>()
//
//        latLonPoints.add(start)
//        distanceQuery!!.origins = latLonPoints
//        distanceQuery!!.destination = dest
        //设置测量方式，支持直线和驾车
//        distanceQuery!!.type = DistanceSearch.TYPE_WALK_DISTANCE
//        //发送请求
//        distanceSearch!!.calculateRouteDistanceAsyn(distanceQuery)
//        distanceSearch!!.setDistanceSearchListener(this)
        //骑行
        mRouteSearch = RouteSearch(this)
        mRouteSearch!!.setRouteSearchListener(this)

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

    override fun onResume() {
        super.onResume()
        binding.mapActTrackNav!!.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapActTrackNav!!.onDestroy()
        if (mapLocationClient != null) {
            mapLocationClient!!.onDestroy()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mapLocationClient != null) {
            mapLocationClient!!.stopLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapActTrackNav!!.onPause()
    }

    //定位回调
    var startAddress = ""
    var startLatLonPoint = ""
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
                Log.e("定位==》", address)
                Log.e("定位坐标==》", startLatLonPoint)
                val city = aMapLocation.city
                val district = aMapLocation.district
                val road = aMapLocation.road
                startAddress = address
                if (startLatLonPoint!=""){
                    if (computerListDTO!=null){
                        if (computerListDTO!!.current_coordinate!=null){
                            val center = computerListDTO!!.current_coordinate
                            val splitEnd = center.split(",")
                            getAddressByLatlng(LatLng(splitEnd[1].toDouble(), splitEnd[0].toDouble()))
                            //绘制起点终点marker
                            val splitStart = startLatLonPoint.split(",")
                            val latList = ArrayList<LatLng>()
                            latList.add(LatLng(splitStart[0].toDouble(), splitStart[1].toDouble()))
                            latList.add(LatLng(splitEnd[1].toDouble(), splitEnd[0].toDouble()))
                            AMapUtils.calculateLineDistance(latList[0], latList[latList.size - 1])
                            //绘制轨迹线
//                            var mPolylineOptions = PolylineOptions()
//                            mPolylineOptions!!.addAll(latList)
//                            mPolylineOptions!!.color(Color.argb(255, 26, 37, 55))
//                            aMap!!.addPolyline(mPolylineOptions.zIndex(0f))
                            aMap!!.clear()
                            val mInflater = LayoutInflater.from(this)
                            val view: View = mInflater.inflate(R.layout.item_image_marker, null)
                            val ivItemMarker = view.findViewById<ImageView>(R.id.iv_item_image_marker)
                            ivItemMarker.setImageResource(R.mipmap.track_icon_d)
                            var bitmap = convertViewToBitmap(view)
                            aMap?.addMarker(MarkerOptions().position(latList[0]))!!
                                .setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))

                            val mInflater1 = LayoutInflater.from(this)
                            val view1: View = mInflater1.inflate(R.layout.item_image_marker, null)
                            val ivItemMarker1 = view1.findViewById<ImageView>(R.id.iv_item_image_marker)
                            ivItemMarker1.setImageResource(R.mipmap.track_icon_position)
                            var bitmap1 = convertViewToBitmap(view1)
                            aMap?.addMarker(MarkerOptions().position(latList[latList.size - 1]))!!.setIcon(
                                BitmapDescriptorFactory.fromBitmap(bitmap1)
                            )
                            val builder = LatLngBounds.Builder()
                            builder.include(LatLng(splitStart[0].toDouble(), splitStart[1].toDouble()))
                            builder.include(LatLng(splitEnd[1].toDouble(), splitEnd[0].toDouble()))
                            aMap!!.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(builder.build(),
                                300));//第二个参数为四周留空宽度.

                        }
                    }
                }
//                drawMarker(latLng)
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见下方错误码表。
                Log.i(
                    "定位失败：",
                    aMapLocation.errorCode.toString() + "---" + aMapLocation.errorInfo
                )
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

    fun convertViewToBitmap(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        return view.drawingCache
    }

    /**
     *  得到逆地理编码异步查询结果
     */
    override fun onRegeocodeSearched(regeocodeResult: RegeocodeResult?, p1: Int) {
        val regeocodeAddress: RegeocodeAddress = regeocodeResult!!.regeocodeAddress
        val city = regeocodeAddress.city
        val district = regeocodeAddress.district
        val formatAddress = regeocodeAddress.formatAddress
        var simpleAddress = formatAddress.substring(9)
        binding.tvActTrackNavCity.text = district
        binding.tvActTrackNavAddress.text = formatAddress
        if (currentCoordinate!="0.0,0.0") {
            if (startAddress != ""){
                val splitStart = startLatLonPoint.split(",")
                val splitEnd = currentCoordinate.split(",")
                var start =
                    Poi(startAddress, LatLng(splitStart[1].toDouble(), splitStart[0].toDouble()), null)
                var end =
                    Poi(formatAddress, LatLng(splitEnd[1].toDouble(), splitEnd[0].toDouble()), null)
                var params :AmapNaviParams?=null
                when(rbtType){
                    1->{
                        params = AmapNaviParams(start, null, end, AmapNaviType.WALK, AmapPageType.ROUTE)
                    }
                    2->{
                        params = AmapNaviParams(start, null, end, AmapNaviType.RIDE, AmapPageType.ROUTE)
                    }
                    3->{
                        params = AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE)
                    }
                }
                if (navigationType == 1){
                    //导航页面
                    AmapNaviPage.getInstance()
                        .showRouteActivity(MyApplication.getInstance().applicationContext, params, null)
                }
            }

        }else{
            ToastUtil.showLong(this,"位置信息获取失败！")
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
    }

    //测距离回调
    override fun onDistanceSearched(distanceResult: DistanceResult?, p1: Int) {
//        val time = distanceResult!!.distanceResults[0].duration.toInt() / 60
//        when(rbtType){
//            1->{
//                binding.rbtActTrackNavWalk.text = time.toString() + "分钟"
//            }
//
//            3->{
//                binding.rbtActTrackNavDrive.text = time.toString() + "分钟"
//            }
//        }

    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {

    }
    //驾车回调 可获取驾车时间
    @SuppressLint("SetTextI18n")
    override fun onDriveRouteSearched(driveRouteResult: DriveRouteResult?, p1: Int) {
        val time =  driveRouteResult!!.paths[0].duration
        val formatDateTime = TimeUtils.formatDateTime(time)
        binding.rbtActTrackNavDrive.text = formatDateTime
        val distance = driveRouteResult.paths[0].distance / 1000
        binding.tvActTrackNavDistance.text ="共计"+getFloatNoMoreThanTwoDigits(distance)+"公里"
    }

    //步行回调 可获取步行时间
    @SuppressLint("SetTextI18n")
    override fun onWalkRouteSearched(walkRouteResult: WalkRouteResult?, p1: Int) {
        val time =  walkRouteResult!!.paths[0].duration
        val formatDateTime = TimeUtils.formatDateTime(time)
        binding.rbtActTrackNavWalk.text = formatDateTime
        val distance = walkRouteResult.paths[0].distance / 1000
        binding.tvActTrackNavDistance.text ="共计"+getFloatNoMoreThanTwoDigits(distance)+"公里"
    }

    //骑行回调 可获取骑行时间
    @SuppressLint("SetTextI18n")
    override fun onRideRouteSearched(rideRouteResult: RideRouteResult?, p1: Int) {
        val time =  rideRouteResult!!.paths[0].duration
        val formatDateTime = TimeUtils.formatDateTime(time)
        binding.rbtActTrackNavRide.text = formatDateTime
        val distance = rideRouteResult.paths[0].distance / 1000
        binding.tvActTrackNavDistance.text ="共计"+getFloatNoMoreThanTwoDigits(distance)+"公里"
    }

    fun getFloatNoMoreThanTwoDigits(number: Float): String {
        val format = DecimalFormat("#.#")
        //舍弃规则，RoundingMode.FLOOR表示直接舍弃。
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }

}
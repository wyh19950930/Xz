package com.chuzhi.xzyx.ui.activity.homepage

import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.CoordinateConverter
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.*
import com.amap.api.maps.model.MyLocationStyle.LOCATION_TYPE_LOCATE
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.*
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.api.AppCache
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BaseModel
import com.chuzhi.xzyx.databinding.ActivityFenceSettingBinding
import com.chuzhi.xzyx.ui.bean.mqtt.HomePageMQTTMessage
import com.chuzhi.xzyx.ui.bean.rc.GeofenceListEntity
import com.chuzhi.xzyx.ui.bean.rc.SearchPoiBean
import com.chuzhi.xzyx.ui.presenter.FenceSettingPresenter
import com.chuzhi.xzyx.ui.view.FenceSettingView
import com.chuzhi.xzyx.utils.SpaceFilter
import com.chuzhi.xzyx.utils.ToastUtil
import com.chuzhi.xzyx.utils.map.AMapUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 添加、修改围栏activity
 */
class FenceSettingActivity : BaseActivity<ActivityFenceSettingBinding, FenceSettingPresenter>(),
    FenceSettingView, LocationSource, AMapLocationListener, Inputtips.InputtipsListener,
    AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener {
    private var aMap: AMap? = null
    private var mListener: LocationSource.OnLocationChangedListener? = null

    //声明AMapLocationClient类对象
    var mapLocationClient: AMapLocationClient? = null

    //声明AMapLocationClientOption对象
    var mapLocationClientOption: AMapLocationClientOption? = null
    var adapterInt = 0
    var adapterStr = ""
    var addressType = ""
    var isFirstLoc = true
    var mix_m = 200
    var max_m = 1000
    var type_m = 200
    var latLngType: LatLng? = null
    var alarmType = 1 //进出预警类型
    var timeJgType = 10 //定位时间间隔类型

    private var addMarker: Marker? = null
    private var pointStr = ""
    private var geocodeSearch: GeocodeSearch? = null
    private var computerSn = ""
    private var computerCenter = ""
    private var computerData: GeofenceListEntity.GeofenceListDTO? = null
    private var jumpType = 0
    override fun createPresenter(): FenceSettingPresenter {
        return FenceSettingPresenter(this)
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        binding.includeActFenceSet.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActFenceSet.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.includeActFenceSet.tvIncludeTitleTitle.text = "设置围栏"

        AMapLocationClient.updatePrivacyAgree(this, true);
        AMapLocationClient.updatePrivacyShow(this, true, true);
        binding.mapActFenceList.onCreate(this!!.intent.extras)
        initAMap()
        jumpType = intent.getIntExtra("jumpType", 0)
        computerSn = intent.getStringExtra("computerSn").toString()
        if (jumpType == 2) {
            computerData =
                intent.getSerializableExtra("computerData") as GeofenceListEntity.GeofenceListDTO
        } else {
            computerCenter = intent.getStringExtra("computerCenter").toString()
        }

        binding.etActFenceSetWlName.filters =
            arrayOf<InputFilter>(SpaceFilter(), InputFilter.LengthFilter(20))
//        requestPermission()
//        binding.rgpActFenceSetJg.isEnabled = AppCache.getInstance().fenceType == 0
        if (AppCache.getInstance().fenceType != 0){
            Log.e("不可点击==",AppCache.getInstance().fenceType.toString())
            disableRadioButton()
        }else{
            Log.e("可点击==",AppCache.getInstance().fenceType.toString())
            effectiveRadioButton()
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getMqttPoint(msg: HomePageMQTTMessage) {
        Log.e("首页传来的坐标点",msg.toString())
        if (msg!=null){
            if (msg.point!=null&&msg.point.contains(",")){
                if (jumpType==1){
                    val split = msg.point.split(",")
                    latLngType = LatLng(split[1].toDouble(), split[0].toDouble())
                    setMapCenter(LatLng(split[1].toDouble(), split[0].toDouble()), type_m.toDouble())
                    getAddressByLatlng(latLngType);
                }
            }
        }
    }
    private fun effectiveRadioButton() {
        for (i in 0 until binding.rgpActFenceSetJg.childCount) {
            (binding.rgpActFenceSetJg.getChildAt(i) as RadioButton).isEnabled = true
        }
    }
    private fun disableRadioButton() {
        for (i in 0 until binding.rgpActFenceSetJg.childCount) {
            (binding.rgpActFenceSetJg.getChildAt(i) as RadioButton).isEnabled = false
        }
    }
    override fun initData() {
        if (computerData != null) {//不为空就是列表页进来的
            binding.etActFenceSetWlName.setText(computerData!!.name)
            binding.etActFenceSetWlAddress.setText(computerData!!.address)
            if (computerData!!.type == "出去报警"){
                binding.rbtActFenceSetWlOut.isChecked = true
                alarmType = 1
            }
            else{
                binding.rbtActFenceSetWlIn.isChecked = true
                alarmType = 0
            }
            when(computerData!!.every){
                10->{
                    binding.rbtActFenceSetJg10.isChecked = true
                    timeJgType = 10
                }
                5->{
                    binding.rbtActFenceSetJg5.isChecked = true
                    timeJgType = 5
                }
                1->{
                    binding.rbtActFenceSetJg1.isChecked = true
                    timeJgType = 1
                }
            }

            val split = computerData!!.center.split(",")
            aMap!!.clear()
            latLngType = LatLng(split[1].toDouble(), split[0].toDouble())
            if (!computerData!!.radius.contains(".")){
                type_m = computerData!!.radius.toInt()
            }else{
                type_m = computerData!!.radius.toDouble().toInt()
            }
            setMapCenter(
                LatLng(split[1].toDouble(), split[0].toDouble()),
                computerData!!.radius.toDouble()
            )
            if (type_m <= mix_m) {
                binding.seekActFenceSet.progress = 0
            } else {
                binding.seekActFenceSet.progress = (type_m / 200) - 1
            }
        } else {
            if (computerCenter != null) {
                val split = computerCenter.split(",")
//                aMap!!.clear()
                latLngType = LatLng(split[1].toDouble(), split[0].toDouble())
                setMapCenter(LatLng(split[1].toDouble(), split[0].toDouble()), type_m.toDouble())
                getAddressByLatlng(latLngType);
            }
            if (AppCache.getInstance().fenceType != 0){
                when(AppCache.getInstance().fenceType){
                    10->{
                        binding.rbtActFenceSetJg10.isChecked = true
                        timeJgType = 10
                    }
                    5->{
                        binding.rbtActFenceSetJg5.isChecked = true
                        timeJgType = 5
                    }
                    1->{
                        binding.rbtActFenceSetJg1.isChecked = true
                        timeJgType = 1
                    }
                }
            }
        }
        //输入框监听
        binding.etActFenceSetSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString().trim { it <= ' ' }
                if (!AMapUtil.IsEmptyOrNullString(newText)) {
                    binding.rltActFenceSetSelectSearch.visibility = View.VISIBLE
                    binding.llActFenceSetWlMsg.visibility = View.GONE
                    val inputquery =
                        InputtipsQuery(newText, binding.etActFenceSetSearch.getText().toString())
                    val inputTips = Inputtips(this@FenceSettingActivity, inputquery)
                    inputTips.setInputtipsListener(this@FenceSettingActivity)
                    inputTips.requestInputtipsAsyn()
                } else {
                    binding.rltActFenceSetSelectSearch.visibility = View.GONE
                    binding.llActFenceSetWlMsg.visibility = View.VISIBLE
                    adapterInt = 0
                    val inputquery = InputtipsQuery(addressType, "")
                    val inputTips = Inputtips(this@FenceSettingActivity, inputquery)
                    inputTips.setInputtipsListener(this@FenceSettingActivity)
                    inputTips.requestInputtipsAsyn()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        //确定选择该地址
        binding.btnActFenceSetSelectSearch.setOnClickListener {
            binding.rltActFenceSetSelectSearch.visibility = View.GONE
            binding.llActFenceSetWlMsg.visibility = View.VISIBLE
        }
        //围栏“-”
        binding.btnActFenceSetReduce.setOnClickListener {
            if (type_m <= mix_m) {
                type_m = mix_m
                binding.seekActFenceSet.progress = 0
            } else {
                type_m -= 200
                binding.seekActFenceSet.progress = (type_m / 200) - 1
            }
            aMap!!.clear()
            setMapCenter(latLngType!!, type_m.toDouble())

        }
        //围栏“+”
        binding.btnActFenceSetAdd.setOnClickListener {
            if (type_m >= max_m) {
                type_m = max_m
                binding.seekActFenceSet.progress = 4
            } else {
                type_m += 200
                binding.seekActFenceSet.progress = (type_m / 200) - 1
            }
            aMap!!.clear()
            setMapCenter(latLngType!!, type_m.toDouble())
        }
        //围栏进度条
        binding.seekActFenceSet.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                aMap!!.clear()
                type_m = ((progress + 1) * 200)
                setMapCenter(latLngType!!, ((progress + 1) * 200).toDouble())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        //告警类型radioGroup
        binding.rgpActFenceSetWl.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.rbt_act_fence_set_wl_out -> {//出去预警
                    alarmType = 1
                }
                R.id.rbt_act_fence_set_wl_in -> {//进入预警
                    alarmType = 0
                }
            }
        }
        //定位间隔radioGroup
        binding.rgpActFenceSetJg.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.rbt_act_fence_set_jg_10 -> {//10分钟
                    timeJgType = 10
                }
                R.id.rbt_act_fence_set_jg_5 -> {//5分钟
                    timeJgType = 5
                }
                R.id.rbt_act_fence_set_jg_1 -> {//1分钟
                    timeJgType = 1
                }
            }
        }
        //添加or修改围栏
        binding.btnActFenceSetWlSave.setOnClickListener {
            val name = binding.etActFenceSetWlName.text.toString().trim()
            val address = binding.etActFenceSetWlAddress.text.toString().trim()
            if (name == "") {
                ToastUtil.showShort(this, "请输入名称")
            } else if (address == "") {
                ToastUtil.showShort(this, "位置出错")
            } else {
                if (computerData != null) {//修改
                    presenter.alterGeofence(
                        computerData!!.id,
                        name,
                        computerSn,
                        latLngType,
                        address,
                        type_m.toString(),
                        alarmType
                    )
                } else {//添加
                    presenter.addGeofence(
                        name,
                        computerSn,
                        latLngType,
                        address,
                        type_m.toString(),
                        alarmType,
                        timeJgType
                    )
                }

            }
        }

        //定位到初始位置
        binding.btnActFenceSetWlPoint.setOnClickListener {
            if (computerData != null) {//不为空就是列表页进来的
                binding.etActFenceSetWlName.setText(computerData!!.name)
                binding.etActFenceSetWlAddress.setText(computerData!!.address)
                if (computerData!!.type == "出去报警"){
                    binding.rbtActFenceSetWlOut.isChecked = true
                    alarmType = 1
                }
                else{
                    binding.rbtActFenceSetWlIn.isChecked = true
                    alarmType = 0
                }
                when(computerData!!.every){
                    10->{
                        binding.rbtActFenceSetJg10.isChecked = true
                        timeJgType = 10
                    }
                    5->{
                        binding.rbtActFenceSetJg5.isChecked = true
                        timeJgType = 5
                    }
                    1->{
                        binding.rbtActFenceSetJg1.isChecked = true
                        timeJgType = 1
                    }
                }

                val split = computerData!!.center.split(",")
                aMap!!.clear()
                latLngType = LatLng(split[1].toDouble(), split[0].toDouble())
                if (!computerData!!.radius.contains(".")){
                    type_m = computerData!!.radius.toInt()
                }else{
                    type_m = computerData!!.radius.toDouble().toInt()
                }
                setMapCenter(
                    LatLng(split[1].toDouble(), split[0].toDouble()),
                    computerData!!.radius.toDouble()
                )
                if (type_m <= mix_m) {
                    binding.seekActFenceSet.progress = 0
                } else {
                    binding.seekActFenceSet.progress = (type_m / 200) - 1
                }
            } else {
                if (computerCenter != null) {
                    val split = computerCenter.split(",")
                    aMap!!.clear()
                    latLngType = LatLng(split[1].toDouble(), split[0].toDouble())
                    setMapCenter(
                        LatLng(split[1].toDouble(), split[0].toDouble()),
                        type_m.toDouble()
                    )
                    getAddressByLatlng(latLngType);
                }
                if (AppCache.getInstance().fenceType != 0){
                    when(AppCache.getInstance().fenceType){
                        10->{
                            binding.rbtActFenceSetJg10.isChecked = true
                            timeJgType = 10
                        }
                        5->{
                            binding.rbtActFenceSetJg5.isChecked = true
                            timeJgType = 5
                        }
                        1->{
                            binding.rbtActFenceSetJg1.isChecked = true
                            timeJgType = 1
                        }
                    }
                }
            }
        }

    }

    /**
     * 初始化AMap对象
     */
    private fun initAMap() {
        if (aMap == null) {
            aMap = binding.mapActFenceList.map
        }
        aMap!!.setLocationSource(this);// 设置定位监听
        aMap!!.uiSettings.isMyLocationButtonEnabled = false;// 设置默认定位按钮是否显示
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap!!.isMyLocationEnabled = true;
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap!!.setMyLocationType(LOCATION_TYPE_LOCATE);

        //地理搜索类
        geocodeSearch = GeocodeSearch(this);
        geocodeSearch!!.setOnGeocodeSearchListener(this);
        // 定义 Marker拖拽的监听
        val markerDragListener: AMap.OnMarkerDragListener = object : AMap.OnMarkerDragListener {
            // 当marker开始被拖动时回调此方法, 这个marker的位置可以通过getPosition()方法返回。
            // 这个位置可能与拖动的之前的marker位置不一样。
            // marker 被拖动的marker对象。
            override fun onMarkerDragStart(arg0: Marker) {
                Log.d("拖拽开始==》", arg0.position.latitude.toString())
            }

            // 在marker拖动完成后回调此方法, 这个marker的位置可以通过getPosition()方法返回。
            // 这个位置可能与拖动的之前的marker位置不一样。
            // marker 被拖动的marker对象。
            override fun onMarkerDragEnd(arg0: Marker) {
                Log.d("拖拽结束==》", arg0.position.latitude.toString())
                latLngType = arg0.position
                aMap!!.clear()
                setMapCenter(arg0.position, type_m.toDouble())
                getAddressByLatlng(arg0.position);
//                drawMarker(arg0.position)
//                drawCircle(arg0.position)
            }

            // 在marker拖动过程中回调此方法, 这个marker的位置可以通过getPosition()方法返回。
            // 这个位置可能与拖动的之前的marker位置不一样。
            // marker 被拖动的marker对象。
            override fun onMarkerDrag(arg0: Marker) {
                Log.d("拖拽中==》", arg0.position.latitude.toString())
                latLngType = arg0.position
                aMap!!.clear()
                setMapCenter(arg0.position, type_m.toDouble())
//                drawMarker(arg0.position)
//                drawCircle(arg0.position)
            }
        }
// 绑定marker拖拽事件
        aMap!!.setOnMarkerDragListener(markerDragListener)

    }

    private fun getAddressByLatlng(latLng: LatLng?) {
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        val latLonPoint = LatLonPoint(latLng!!.latitude, latLng.longitude)
        val query = RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP)
        //异步查询
        geocodeSearch!!.getFromLocationAsyn(query)

    }


    override fun onResume() {
        super.onResume()
        binding.mapActFenceList!!.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapActFenceList!!.onDestroy()
        if (mapLocationClient != null) {
            mapLocationClient!!.onDestroy()
        }
        EventBus.getDefault().unregister(this)
    }

    override fun onStop() {
        super.onStop()
        if (mapLocationClient != null) {
            mapLocationClient!!.stopLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapActFenceList!!.onPause()
    }

    //激活定位
    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener?) {
        mListener = onLocationChangedListener
        if (mapLocationClient == null) {
            //初始化AMapLocationClient，并绑定监听
            mapLocationClient = AMapLocationClient(applicationContext)

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
//            if (computerData==null){
//                mapLocationClient!!.startLocation()
//            }
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

    //定位回调
    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (aMapLocation != null) {
            if (aMapLocation.errorCode === 0) {

//                val latLng = LatLng(aMapLocation.latitude, aMapLocation.longitude)
//                latLngType = LatLng(aMapLocation.latitude, aMapLocation.longitude)
//                drawMarker(latLng)
                mListener!!.onLocationChanged(aMapLocation) // 显示系统小蓝点
                val stringBuilder = StringBuilder()
                //定位成功回调信息，设置相关消息
                val type: Int = aMapLocation.locationType
                val address: String = aMapLocation.address
                stringBuilder.append(type.toString() + address)
                Log.e("定位==》", stringBuilder.toString())
//                drawMarker(latLng)
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见下方错误码表。
                Log.i(
                    "erro info：",
                    aMapLocation.errorCode.toString() + "---" + aMapLocation.errorInfo
                )
            }
        }
    }

    //搜索回调
    var searchPoiBeans = ArrayList<SearchPoiBean>()
    var dzAdapter: BaseQuickAdapter<SearchPoiBean, BaseViewHolder>? = null
    override fun onGetInputtips(p0: MutableList<Tip>?, p1: Int) {
        if (p1 == AMapException.CODE_AMAP_SUCCESS && p0 != null) {// 正确返回
            val listString = java.util.ArrayList<String>()
            searchPoiBeans.clear()
            for (i in p0!!.indices) {
                listString.add(p0.get(i).getName())
                searchPoiBeans.add(
                    SearchPoiBean(
                        searchPoiBeans.size,
                        p0.get(i).name,
                        p0.get(i).district,
                        p0.get(i).address,
                        "POINT(" + p0.get(i).point.longitude + " " + p0.get(i).point.latitude + ")",
                        false,
                        false
                    )
                )
            }
            binding.rlvActFenceSetSelectSearch.layoutManager = LinearLayoutManager(this)
            dzAdapter = object : BaseQuickAdapter<SearchPoiBean, BaseViewHolder>(
                R.layout.item_search_dz,
                searchPoiBeans
            ) {
                override fun convert(helper: BaseViewHolder?, item: SearchPoiBean?) {
                    helper!!.setText(R.id.tv_itme_serach_dz_name, item!!.name)
                        .setText(R.id.tv_itme_serach_dz_detail, item!!.detailDz)
                    val ivItmeSerachDzSure = helper.getView<ImageView>(R.id.iv_itme_serach_dz_sure)
                    val ll_itme_search_dz = helper.getView<LinearLayout>(R.id.ll_itme_search_dz)

                    if (helper.adapterPosition == adapterInt) {
                        item.isCheck = true
                        adapterInt = -1
                        adapterStr = item.district + item.name
                    }

                    if (item.isCheck) {
                        ivItmeSerachDzSure.visibility = View.VISIBLE
                    } else {
                        ivItmeSerachDzSure.visibility = View.GONE
                    }

                    helper.itemView.setOnClickListener {
                        //                        if (item.isFwn){
                        for (i in searchPoiBeans) {
                            i.isCheck = false
                            if (i.id == item.id) {
                                item.isCheck = true
                            }
                        }
                        if (item.isCheck) {
                            adapterStr = item.district + item.name
                            if (addMarker != null) {
                                addMarker!!.remove()
                            }
                            val markerOptions = MarkerOptions()
                            val center = getCenter(item.pointStr)
                            //清除圆圈和marker
                            aMap!!.clear();
                            setMapCenter(center, type_m.toDouble())
                            latLngType = center
                            binding.etActFenceSetWlAddress.setText(item.district + item.name)
                            //绘制圆圈
//                            drawCircle(center);
                            //绘制marker
//                            drawMarker(center);
//                            addMarker = aMap!!.addMarker(markerOptions.position(center))
//                            addMarker!!.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.home_icon_ddw_default))
//                            aMap?.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(center, 16f, 0f, 0f)))
                            notifyDataSetChanged()
                        }

                    }


                }

            }
            binding.rlvActFenceSetSelectSearch.adapter = dzAdapter

        } else {
//            ToastUtil.showShort(this,""+p1)
        }
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
        markerOption.draggable(true) //设置Marker可拖动
        markerOption.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory
                    .decodeResource(resources, R.mipmap.home_icon_ddw_default)
            )
        )
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//        markerOption.setFlat(false) //设置marker平贴地图效果
        markerOption.anchor(0.5f, 0.5f) //设置marker偏移量
        addMarker = aMap!!.addMarker(markerOption)
    }

    /**
     * 设置地图中心点
     * @param latLng
     */
    private fun setMapCenter(latLng: LatLng, radius: Double) {
        aMap!!.moveCamera(CameraUpdateFactory.changeLatLng(latLng))
        aMap!!.moveCamera(CameraUpdateFactory.zoomTo(16f))

        drawMarker(latLng)
        drawCircle(latLng, radius)
    }

    /**
     * 绘制圆圈
     *
     * @param latLng
     */
    var circle: Circle? = null
    fun drawCircle(latLng: LatLng?, radius: Double) {
        val color = "#26b637"
        val sb = java.lang.StringBuilder(color) // 构造一个StringBuilder对象
        sb.insert(1, "50") // 在指定的位置10，插入指定的字符串
        if (circle != null) {
            circle = null
        }
        circle = aMap!!.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(radius)
                .fillColor(Color.parseColor(sb.toString()))
                .strokeColor(Color.parseColor(color))
                .strokeWidth(5f)
        )
    }

    fun getCenter(center: String): LatLng {
        if (center != null) {
            if (!center.equals("")) {
                val points = center.substring(6, center.length - 1).split(" ").toTypedArray()
                return if (points != null && points.size > 1) {
                    val converter = CoordinateConverter(this)
                    // CoordType.GPS 待转换坐标类型
                    converter.from(CoordinateConverter.CoordType.GPS)
                    val sl = LatLng(points[1].toDouble(), points[0].toDouble())
                    // sourceLatLng待转换坐标点 LatLng类型
                    converter.coord(sl)
                    val latLng = converter.convert()
                    sl
                } else {
                    LatLng(0.0, 0.0)
                }
            } else {
                LatLng(0.0, 0.0)
            }
        }
        return LatLng(0.0, 0.0)
    }

    override fun onMapClick(p0: LatLng?) {
        if (addMarker != null) {
            addMarker!!.remove()
        }
        val markerOptions = MarkerOptions()
        addMarker = aMap!!.addMarker(markerOptions.position(p0))
        pointStr = "POINT(" + (p0?.longitude) + " " + p0?.latitude + ")"
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
        Log.d("地址==》", "$city-$district-$simpleAddress")
        binding.etActFenceSetWlAddress.setText(formatAddress)
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
    }

    //添加围栏回调
    override fun addGeofence(msg: BaseModel<String>?) {
        ToastUtil.showShort(this, msg!!.msg)
        finish()
    }

    //修改围栏回调
    override fun alterGeofence(msg: BaseModel<String>?) {
        ToastUtil.showShort(this, msg!!.msg)
        finish()
    }

    override fun showError(msg: String?) {
        super.showError(msg)
        ToastUtil.showShort(this, msg)
    }

}
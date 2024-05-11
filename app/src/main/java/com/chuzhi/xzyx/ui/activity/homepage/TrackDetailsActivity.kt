package com.chuzhi.xzyx.ui.activity.homepage

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.chuzhi.xzyx.R
import com.chuzhi.xzyx.base.BaseActivity
import com.chuzhi.xzyx.base.BasePresenter
import com.chuzhi.xzyx.base.BaseView
import com.chuzhi.xzyx.databinding.ActivityTrackDetailsBinding
import com.chuzhi.xzyx.ui.bean.rc.TrackInfoListEntity

/**
 * 轨迹详情activity
 */
class TrackDetailsActivity : BaseActivity<ActivityTrackDetailsBinding, BasePresenter<*>>(),
    BaseView, GeocodeSearch.OnGeocodeSearchListener {
    private var aMap: AMap? = null
    private var addMarker: Marker? = null
    private var trackDictDTO: TrackInfoListEntity.TrackDictDTO? = null
    private var geocodeSearch: GeocodeSearch? = null

    override fun createPresenter(): BasePresenter<*> {
        return BasePresenter(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.includeActTrackDetails.ivIncludeTitleBack.visibility = View.VISIBLE
        binding.includeActTrackDetails.ivIncludeTitleBack.setOnClickListener { finish() }
        binding.includeActTrackDetails.tvIncludeTitleTitle.text = "轨迹详情"
        AMapLocationClient.updatePrivacyAgree(this, true);
        AMapLocationClient.updatePrivacyShow(this, true, true);
        binding.mapActTrackDetails.onCreate(this!!.intent.extras)
        trackDictDTO = intent.getSerializableExtra("trackData") as TrackInfoListEntity.TrackDictDTO
        val trackName = intent.getStringExtra("trackName")
        initAMap()
        if (trackName != "") {
            binding.tvActTrackDetailsName.text = trackName
        }
        if (trackDictDTO != null) {
            binding.tvActTrackDetailsTime.text = trackDictDTO!!.start_time.substring(0, 10)
            if (trackDictDTO!!.distance == "0") {
                binding.tvActTrackDetailsDistance.text = "设备未被移动"
            } else {
                binding.tvActTrackDetailsDistance.text = "共计" + trackDictDTO!!.distance + "公里"
            }
            binding.tvActTrackDetailsStartTime.text = "开始时间：" + trackDictDTO!!.start_time
            binding.tvActTrackDetailsEndTime.text = "结束时间：" + trackDictDTO!!.end_time
            val splitStart = trackDictDTO!!.start_address.split(",")
            getAddressByLatlng(LatLng(splitStart[1].toDouble(), splitStart[0].toDouble()))

        }

    }

    override fun initData() {

    }

    private fun initAMap() {
        if (aMap == null) {
            aMap = binding.mapActTrackDetails.map
        }
        aMap!!.isTrafficEnabled = true
        //地理搜索类
        geocodeSearch = GeocodeSearch(this);
        geocodeSearch!!.setOnGeocodeSearchListener(this);
        if (trackDictDTO != null) {
            if (trackDictDTO!!.coordinate != null && trackDictDTO!!.coordinate.size > 0) {
                val latList = ArrayList<LatLng>()
                val center = trackDictDTO!!.coordinate
//            val substring = center.substring(2, center.length - 2)
//            val splitList = substring.split("', '")
//            for (i in splitList.indices) {
//                val split = splitList[i].split(",")
//                latList.add(LatLng(split[1].replace("'","").toDouble(), split[0].replace("'","").toDouble()))
//            }
//            Log.e("当前坐标列表==》", substring)

                for (i in center.indices) {
                    val split = center[i].split(",")
                    latList.add(LatLng(split[1].toDouble(), split[0].toDouble()))
                }

                if (latList.size == 1) {
                    setMapCenter(latList[0])
                }

                AMapUtils.calculateLineDistance(latList[0], latList[latList.size - 1])
                //绘制轨迹线
                var mPolylineOptions = PolylineOptions()
                mPolylineOptions!!.addAll(latList)
                mPolylineOptions!!.color(Color.argb(255, 1, 102, 255))
                aMap!!.addPolyline(mPolylineOptions.zIndex(0f))

                if (latList.size == 1) {
                    val mInflater1 = LayoutInflater.from(this)
                    val view1: View = mInflater1.inflate(R.layout.item_image_marker, null)
                    val ivItemMarker1 = view1.findViewById<ImageView>(R.id.iv_item_image_marker)
                    ivItemMarker1.setImageResource(R.mipmap.track_icon_position)
                    var bitmap1 = convertViewToBitmap(view1)
                    aMap?.addMarker(MarkerOptions().position(latList[latList.size - 1]))!!
                        .setIcon(BitmapDescriptorFactory.fromBitmap(bitmap1))

                } else {
                    val mInflater = LayoutInflater.from(this)
                    val view: View = mInflater.inflate(R.layout.item_image_marker, null)
                    val ivItemMarker = view.findViewById<ImageView>(R.id.iv_item_image_marker)
                    ivItemMarker.setImageResource(R.mipmap.trajectory_icon_rise)
                    var bitmap = convertViewToBitmap(view)
                    aMap?.addMarker(MarkerOptions().position(latList[0]))!!
                        .setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))

                    val mInflater1 = LayoutInflater.from(this)
                    val view1: View = mInflater1.inflate(R.layout.item_image_marker, null)
                    val ivItemMarker1 = view1.findViewById<ImageView>(R.id.iv_item_image_marker)
                    ivItemMarker1.setImageResource(R.mipmap.trajectory_icon_end)
                    var bitmap1 = convertViewToBitmap(view1)
                    aMap?.addMarker(MarkerOptions().position(latList[latList.size - 1]))!!
                        .setIcon(BitmapDescriptorFactory.fromBitmap(bitmap1))

                    val builder = LatLngBounds.Builder()
                    for (i in 0 until latList.size) {
                        builder.include(LatLng(latList[i].latitude, latList[i].longitude))
                    }
                    aMap!!.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            builder.build(),
                            300
                        )
                    );//第二个参数为四周留空宽度.
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

//        drawMarker(latLng)
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

    fun convertViewToBitmap(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        return view.drawingCache
    }

    private var addressType = 1

    @SuppressLint("SetTextI18n")
    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {
        val regeocodeAddress = p0!!.regeocodeAddress
        val formatAddress = regeocodeAddress.formatAddress
        if (addressType == 1) {
            addressType = 2
            val splitEnd = trackDictDTO!!.end_address.split(",")
            getAddressByLatlng(LatLng(splitEnd[1].toDouble(), splitEnd[0].toDouble()))
            binding.tvActTrackDetailsStartAddress.text = "开始地点：$formatAddress"
        } else {
            binding.tvActTrackDetailsEndAddress.text = "结束地点：$formatAddress"
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
    }

    override fun onResume() {
        super.onResume()
        binding.mapActTrackDetails!!.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapActTrackDetails!!.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        binding.mapActTrackDetails!!.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapActTrackDetails!!.onSaveInstanceState(outState)

    }
}
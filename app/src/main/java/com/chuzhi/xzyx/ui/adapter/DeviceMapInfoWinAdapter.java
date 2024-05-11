package com.chuzhi.xzyx.ui.adapter;

/**
 * @Author : wyh
 * @Time : On 2023/6/8 11:06
 * @Description : DeviceMapInfoWinAdapter
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.ui.activity.homepage.LocationTrackingActivity;
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *自定义地图弹框adapter
 * @author hk
 */
public class DeviceMapInfoWinAdapter implements AMap.InfoWindowAdapter, View.OnClickListener
 , GeocodeSearch.OnGeocodeSearchListener {
    private Context mContext;
    private LatLng latLng;
    private LinearLayout mLlParent;
    private TextView mTvCity,mTvDistance,mTvAddress,mTvTime;
    private String mSnippet,mTitle;
    private ComputerListEntity.ComputerListDTO mData;
    private GeocodeSearch geocodeSearch;

    @Override
    public View getInfoWindow(Marker marker) {
        try {
            initData(marker);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        View view = initView(mData);
        return view;
    }
    @Override
    public View getInfoContents(Marker marker) {
        return null; //因为是自定义的布局，返回null
    }
    public DeviceMapInfoWinAdapter(Context context, ComputerListEntity.ComputerListDTO data) throws AMapException {
        mContext = context;
        mData = data;

    }
    private void getAddressByLatlng(LatLng latLng) {
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);

    }
    private void initData(Marker marker) throws AMapException {
        //当前点位经纬度
        latLng = marker.getPosition();
        //当前点位带的消息信息  也可通过这个传输数据把数据转成json
        mSnippet = marker.getSnippet();
        //当前点位带的标题信息
        mTitle = marker.getTitle();
        geocodeSearch = new GeocodeSearch(mContext);
        geocodeSearch.setOnGeocodeSearchListener(this);
        String[] split = mData.getCurrent_coordinate().split(",");
        LatLng latLng = new LatLng(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
        getAddressByLatlng(latLng);
    }
    @NonNull
    private View initView(ComputerListEntity.ComputerListDTO mData) {
        //获取自定义的布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_map_infowindow, null);
        mLlParent = (LinearLayout) view.findViewById(R.id.ll_view_device_map_window);
        mTvCity = (TextView) view.findViewById(R.id.tv_view_device_map_window_city);
        mTvDistance= (TextView) view.findViewById(R.id.tv_view_device_map_window_distance);
        mTvAddress= (TextView) view.findViewById(R.id.tv_view_device_map_window_address);
        mTvTime= (TextView) view.findViewById(R.id.tv_view_device_map_window_time);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"); //设置时间格式

        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")); //设置时区

        Date curDate = new Date(System.currentTimeMillis()); //获取当前时间

        String createDate = formatter.format(curDate);   //格式转换
        mTvTime.setText(createDate);
        mLlParent.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_view_device_map_window:
                Intent intent = new Intent(mContext, LocationTrackingActivity.class);
                intent.putExtra("deviceData",mData);
                intent.putExtra("deviceType",1);
                mContext.startActivity(intent);
                break;
        }

    }
    /**
     *  得到逆地理编码异步查询结果
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        String city = regeocodeAddress.getCity();
        String district = regeocodeAddress.getDistrict();
        String formatAddress = regeocodeAddress.getFormatAddress();
        mTvCity.setText(district);
        if (formatAddress.length()>30){
            mTvAddress.setText(formatAddress.subSequence(0,29)+"\n"+formatAddress.substring(29,formatAddress.length()));
        }else {
            mTvAddress.setText(formatAddress);
        }
//        Log.e("首页地址=》",city+"-"+district+"-"+formatAddress);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
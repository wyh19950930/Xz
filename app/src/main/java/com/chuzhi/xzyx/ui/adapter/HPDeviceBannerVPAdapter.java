package com.chuzhi.xzyx.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bumptech.glide.Glide;
import com.chuzhi.xzyx.utils.AndroidBarUtils;
import com.chuzhi.xzyx.widget.BannerIndicatorView;
import com.chuzhi.xzyx.widget.DrawView;
import com.chuzhi.xzyx.widget.OnOffView;
import com.duangs.signalv.SignalView;
import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;
import com.chuzhi.xzyx.utils.SpUtils;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;
import com.zhpan.bannerview.utils.BannerUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/2 10:33
 * @Description : HPDeviceBannerAdapter 使用中
 */
public class HPDeviceBannerVPAdapter extends BaseBannerAdapter<ComputerListEntity.ComputerListDTO>
        implements View.OnClickListener , GeocodeSearch.OnGeocodeSearchListener{
    private Context mContext;
    private List<ComputerListEntity.ComputerListDTO> list;
    private CountDownTimer timer;
    private GeocodeSearch geocodeSearch;
    private TextView mTvYjdw;
    private Animation animation;


    public HPDeviceBannerVPAdapter(Context mContext,List<ComputerListEntity.ComputerListDTO> list) throws AMapException {
        this.mContext = mContext;
        this.list = list;
        geocodeSearch = new GeocodeSearch(mContext);
        geocodeSearch.setOnGeocodeSearchListener(this);
        animation = AnimationUtils.loadAnimation(mContext, R.anim.loading_animation);
        animation.setInterpolator(new LinearInterpolator());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindData(BaseViewHolder<ComputerListEntity.ComputerListDTO> holder, ComputerListEntity.ComputerListDTO data, int position, int pageSize) {

        int statusBarHeight = AndroidBarUtils.getStatusBarHeight((Activity) mContext);
        LinearLayout llTitle= holder.findViewById(R.id.ll_item__hp_db_d_title);
        LinearLayout llTitleDjb= holder.findViewById(R.id.ll_item__hp_db_d_title_djb);
        llTitle.setPadding(0,statusBarHeight,0,0);
        llTitleDjb.setPadding(0,statusBarHeight,0,0);
        TextView name = holder.findViewById(R.id.tv_item_hp_db_d_name);
        TextView name_djb = holder.findViewById(R.id.tv_item_hp_db_d_name_djb);
        name.setText(data.getName());//设备名称
        name_djb.setText(data.getName());//设备名称

        ProgressBar pgbItemHpDeviceBanner = holder.findViewById(R.id.pgb_item_hp_device_banner);
        TextView tvItemHpDeviceBannerDl = holder.findViewById(R.id.tv_item_hp_device_banner_dl);
        DrawView dvDl = holder.findViewById(R.id.dv_item_hp_device_banner_dl);
        if (data.getBattery().equals("0")) {
            pgbItemHpDeviceBanner.setProgress(0);//电量
            tvItemHpDeviceBannerDl.setText("**%");//电量
            dvDl.setProgress(0);
        } else {
            pgbItemHpDeviceBanner.setProgress(Integer.parseInt(data.getBattery().replace(" ", "")));//电量
            tvItemHpDeviceBannerDl.setText(data.getBattery() + "%");//电量
            dvDl.setProgress(Integer.parseInt(data.getBattery()));
        }

        BannerIndicatorView banner_idv_item = holder.findViewById(R.id.banner_idv_item_hp_device_banner);
        banner_idv_item.initIndicatorCount(list.size());
        banner_idv_item.changeIndicator(position);
        //二级菜单指示器
//        AnimationDrawable drawable = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.device_arrow_animation);
//        LinearLayout llItemHpDeviceBanner = holder.findViewById(R.id.ll_item_hp_device_banner);
//        ImageView ivItemHpDeviceBanner = holder.findViewById(R.id.iv_item_hp_device_banner);
//        ivItemHpDeviceBanner.setBackground(drawable);
//        drawable.start();

        ImageView iv_item_hp_device_banner_status = holder.findViewById(R.id.iv_item_hp_device_banner_status);
        TextView tvItemHpDeviceBannerKlHq = holder.findViewById(R.id.tv_item_hp_device_banner_kl_hq);
        ImageView ivItemHpDeviceBannerRefresh = holder.findViewById(R.id.iv_item_hp_device_banner_refresh);
        LinearLayout ll_spm = holder.findViewById(R.id.ll_item_hp_device_banner_spm);
        LinearLayout ll_syp = holder.findViewById(R.id.ll_item_hp_device_banner_syp);
        LinearLayout ll_sdn = holder.findViewById(R.id.ll_item_hp_device_banner_sdn);
        LinearLayout ll_dk = holder.findViewById(R.id.ll_item_hp_device_banner_dk);
        LinearLayout ll_xh = holder.findViewById(R.id.ll_item_hp_device_banner_xh);
        LinearLayout ll_bd = holder.findViewById(R.id.ll_item_hp_device_banner_bd);
        LinearLayout ll_yj_dw = holder.findViewById(R.id.ll_item_hp_device_banner_yj_dw);
        LinearLayout ll_dz_wl = holder.findViewById(R.id.ll_item_hp_device_banner_dz_wl);
        mTvYjdw = holder.findViewById(R.id.tv_item_hp_device_banner_yj_dw);
        TextView mTvDzWl = holder.findViewById(R.id.tv_item_hp_device_banner_dz_wl);

        LinearLayout llt_ycb = holder.findViewById(R.id.ll_item_hp_device_banner_ycb);
        LinearLayout rlt_djb = holder.findViewById(R.id.rlt_item_hp_device_banner_djb);
        TextView bt_kl_djb = holder.findViewById(R.id.tv_item_hp_device_banner_kl_djb);

        ImageView zwt = holder.findViewById(R.id.iv_item_device_banner_djb_zwt);
        Glide.with(mContext).load(R.drawable.bdsb_dn).placeholder(R.drawable.bdsb_dn).into(zwt);

        if (data.getC_version()==1){//远程
            llt_ycb.setVisibility(View.VISIBLE);
            rlt_djb.setVisibility(View.GONE);
        }else if (data.getC_version() == 0){//单机
            llt_ycb.setVisibility(View.GONE);
            rlt_djb.setVisibility(View.VISIBLE);
        }

        if (data.getGeogence_count()==0){
            mTvDzWl.setText("暂无围栏");
        }else {
            mTvDzWl.setText("预警"+data.getGeogence_warning_count()+"次");
        }
        int adapterPosition = holder.getAdapterPosition();
        int realPosition = BannerUtils.getRealPosition(adapterPosition, mList.size());
        iv_item_hp_device_banner_status.setOnClickListener(v -> {//开关机
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
//        llItemHpDeviceBanner.setOnClickListener(v -> {//二级菜单
//            if (mOnItemClickListener != null) {
//                mOnItemClickListener.onItemClick(v, realPosition);
//            }
//        });
        tvItemHpDeviceBannerKlHq.setOnClickListener(v -> {//获取口令
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
        bt_kl_djb.setOnClickListener(v -> {//单机版获取口令
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
        ivItemHpDeviceBannerRefresh.setOnClickListener(v -> {//右上角刷新
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
        ll_spm.setOnClickListener(v -> {//锁屏幕
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
        ll_syp.setOnClickListener(v -> {//锁硬盘
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
        ll_sdn.setOnClickListener(v -> {//锁电脑
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
        ll_dk.setOnClickListener(v -> {//端口管理
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
        ll_xh.setOnClickListener(v -> {//数据销毁
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
        ll_bd.setOnClickListener(v -> {//被盗模式
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
        ll_yj_dw.setOnClickListener(v -> {//一键定位
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
        ll_dz_wl.setOnClickListener(v -> {//电子围栏
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_hp_device_banner;
    }

    private int positionType = 0;
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull @NotNull BaseViewHolder<ComputerListEntity.ComputerListDTO> holder, int position, @NonNull @NotNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            positionType = position;
            String type = (String) payloads.get(0);
            if (type.contains("开关机")) {
                TextView tv_refresh = holder.findViewById(R.id.tv_item_hp_device_banner_refresh);
                tv_refresh.setText("已连接");
                tv_refresh.setTextColor(Color.parseColor("#0066ff"));
                String[] split = type.split(",");
                int status = Integer.parseInt(split[1]);
                TextView kj_type = holder.findViewById(R.id.tv_item_hp_device_banner_kj_type);
                ImageView iv_device_status = holder.findViewById(R.id.iv_item_device_banner_device_status);
                ImageView ll_status = holder.findViewById(R.id.iv_item_hp_device_banner_status);
                ImageView iv_status_loading = holder.findViewById(R.id.iv_item_hp_device_banner_status_loading);
                if (status == 0) {
                    iv_status_loading.setVisibility(View.GONE);
                    ll_status.setVisibility(View.VISIBLE);
                    iv_status_loading.clearAnimation();
                    kj_type.setText("");
                    Glide.with(mContext).load(R.mipmap.home_icon_kjbj_default).placeholder(R.mipmap.home_icon_kjbj_default).into(ll_status);
                    Glide.with(mContext).load(R.mipmap.home_gj_wbd).placeholder(R.mipmap.home_gj_wbd).into(iv_device_status);
                } else if (status == 1) {
                    iv_status_loading.setVisibility(View.GONE);
                    ll_status.setVisibility(View.VISIBLE);
                    iv_status_loading.clearAnimation();
                    kj_type.setText("");
                    Glide.with(mContext).load(R.mipmap.home_icon_kjbj_click).placeholder(R.mipmap.home_icon_kjbj_click).into(ll_status);
                    Glide.with(mContext).load(R.mipmap.home_kj_wbd).placeholder(R.mipmap.home_kj_wbd).into(iv_device_status);
                } else if (status == 2) {
                    iv_status_loading.setVisibility(View.GONE);
                    ll_status.setVisibility(View.VISIBLE);
                    iv_status_loading.clearAnimation();
                    kj_type.setText("操作失败！");
                    Glide.with(mContext).load(R.mipmap.home_icon_kjbj_default).placeholder(R.mipmap.home_icon_kjbj_default).into(ll_status);
                } else if (status == 3) {
                    iv_status_loading.setVisibility(View.VISIBLE);
                    ll_status.setVisibility(View.GONE);
                    iv_status_loading.startAnimation(animation);
                    kj_type.setText("开机中...");
                } else if (status == 4) {
                    iv_status_loading.setVisibility(View.VISIBLE);
                    ll_status.setVisibility(View.GONE);
                    iv_status_loading.startAnimation(animation);
                    kj_type.setText("关机中...");
                }
            }else if(type.contains("网络信号")){
                Log.e("网络信号adapter",type);
                TextView tv_network = holder.findViewById(R.id.tv_item_device_banner_network_status);
                String[] split = type.split(",");
                if (split[1].contains("良好")){
                    tv_network.setVisibility(View.GONE);
                }else {
                    tv_network.setVisibility(View.VISIBLE);
                    tv_network.setText(split[1]);
                }

            }else if(type.contains("设备坐标")){
                mTvYjdw = holder.findViewById(R.id.tv_item_hp_device_banner_yj_dw);
                String[] split = type.split("_");
                if (split[1].contains(",")){
                    String[] strings = split[1].split(",");
                    LatLng latLng = new LatLng(Double.parseDouble(strings[1]), Double.parseDouble(strings[0]));
                    getAddressByLatlng(latLng);
                }else {
                    mTvYjdw.setText("定位中");
                }
            }
            else if (type.contains("信号强度")) {
                String[] split = type.split(",");
                SignalView svItemHpDeviceBanner = holder.findViewById(R.id.sv_item_hp_device_banner);
                int csq = Integer.parseInt(split[1].replace(" ", ""));//信号
                if (csq < 10) {
                    svItemHpDeviceBanner.setSignalLevel(1);
                } else if (csq >= 10 && csq < 15) {
                    svItemHpDeviceBanner.setSignalLevel(2);
                } else if (csq >= 15 && csq < 20) {
                    svItemHpDeviceBanner.setSignalLevel(3);
                } else if (csq >= 20) {
                    svItemHpDeviceBanner.setSignalLevel(4);
                }
            } else if (type.contains("设备电量")) {
                String[] split = type.split(",");
                int i = Integer.parseInt(split[1]);
                ProgressBar pgbItemHpDeviceBanner = holder.findViewById(R.id.pgb_item_hp_device_banner);
                TextView tvItemHpDeviceBannerDl = holder.findViewById(R.id.tv_item_hp_device_banner_dl);
                DrawView dvDl = holder.findViewById(R.id.dv_item_hp_device_banner_dl);
//                pgbItemHpDeviceBanner.setProgress(i);//电量
                tvItemHpDeviceBannerDl.setText(i + "%");//电量
                dvDl.setProgress(i);
            } else if (type.contains("动态口令")) {
                int i = Calendar.getInstance().get(Calendar.SECOND);
                String[] split = type.split(",");
                TextView tv_kl = holder.findViewById(R.id.tv_item_hp_device_banner_kl);
                TextView tv_kl_djb = holder.findViewById(R.id.tv_item_hp_device_banner_kl_djb);
                tv_kl.setText(split[1]);
                tv_kl_djb.setText(split[1]);
                countDownTimer(holder, i);
            }else if (type.contains("锁定屏幕")){
                String[] split = type.split(",");
                TextView tv = holder.findViewById(R.id.tv_item_hp_device_banner_spm);
                OnOffView oov = holder.findViewById(R.id.oov_item_hp_device_banner_spm);
                if (split[1].equals("0")){
                    tv.setCompoundDrawablesWithIntrinsicBounds(null,
                            mContext.getResources().getDrawable(R.mipmap.eject_icon_spms_click), null, null);
                    oov.setDefOff(true);
                }else if (split[1].equals("2")){
                    tv.setCompoundDrawablesWithIntrinsicBounds(null,
                            mContext.getResources().getDrawable(R.mipmap.eject_icon_spms_default), null, null);
                    oov.setDefOff(false);
                }
            }else if (type.contains("锁定硬盘")){
                String[] split = type.split(",");
                TextView tv = holder.findViewById(R.id.tv_item_hp_device_banner_syp);
                OnOffView oov = holder.findViewById(R.id.oov_item_hp_device_banner_syp);
                if (split[1].equals("xf0")){
                    tv.setCompoundDrawablesWithIntrinsicBounds(null,
                            mContext.getResources().getDrawable(R.mipmap.eject_icon_sypms_click), null, null);
                    oov.setDefOff(true);
                }else if (split[1].equals("x00")){
                    tv.setCompoundDrawablesWithIntrinsicBounds(null,
                            mContext.getResources().getDrawable(R.mipmap.eject_icon_sypms_default), null, null);
                    oov.setDefOff(false);
                }
            }else if (type.contains("锁定电脑")){
                String[] split = type.split(",");
                TextView tv = holder.findViewById(R.id.tv_item_hp_device_banner_sdn);
                OnOffView oov = holder.findViewById(R.id.oov_item_hp_device_banner_sdn);
                if (split[1].equals("2")){
                    tv.setCompoundDrawablesWithIntrinsicBounds(null,
                            mContext.getResources().getDrawable(R.mipmap.eject_icon_sjms_click), null, null);
                    oov.setDefOff(true);
                }else if (split[1].equals("0")){
                    tv.setCompoundDrawablesWithIntrinsicBounds(null,
                            mContext.getResources().getDrawable(R.mipmap.eject_icon_sjms_default), null, null);
                    oov.setDefOff(false);
                }
            }else if (type.contains("被盗模式")){
                String[] split = type.split(",");
                TextView tv = holder.findViewById(R.id.tv_item_hp_device_banner_bd);
                OnOffView oov = holder.findViewById(R.id.oov_item_hp_device_banner_bd);
                if (split[1].equals("2")){
                    tv.setCompoundDrawablesWithIntrinsicBounds(null,
                            mContext.getResources().getDrawable(R.mipmap.eject_icon_bdms_click), null, null);
                    oov.setDefOff(true);
                }else if (split[1].equals("0")){
                    tv.setCompoundDrawablesWithIntrinsicBounds(null,
                            mContext.getResources().getDrawable(R.mipmap.eject_icon_bdms_default), null, null);
                    oov.setDefOff(false);
                }
            }else if (type.contains("连接")){
                String[] split = type.split(",");
                TextView tv_refresh = holder.findViewById(R.id.tv_item_hp_device_banner_refresh);
                if (split[1].equals("1")){
                    tv_refresh.setText("已连接");
                    tv_refresh.setTextColor(Color.parseColor("#0066ff"));
                }else if (split[1].equals("0")){
                    tv_refresh.setText("已断开");
                    tv_refresh.setTextColor(Color.parseColor("#999999"));
                }
            }
        }
    }

    public void countDownTimer(@NonNull @NotNull BaseViewHolder<ComputerListEntity.ComputerListDTO> holder, int s) {
        Log.e("动态口令剩余有效时间",s+"s");
        int num = 60 - s;
        TextView tvItemHpDeviceBannerKlHq = holder.findViewById(R.id.tv_item_hp_device_banner_kl_hq);
        TextView tv_item_hp_device_banner_kl_djs = holder.findViewById(R.id.tv_item_hp_device_banner_kl_djs);
        TextView tv_item_hp_device_banner_kl_djs_djb = holder.findViewById(R.id.tv_item_hp_device_banner_kl_djs_djb);
        TextView tv_item_hp_device_banner_kl = holder.findViewById(R.id.tv_item_hp_device_banner_kl);
        TextView tv_item_hp_device_banner_kl_djb = holder.findViewById(R.id.tv_item_hp_device_banner_kl_djb);
        tvItemHpDeviceBannerKlHq.setVisibility(View.GONE);
        tv_item_hp_device_banner_kl_djs.setVisibility(View.VISIBLE);
        tv_item_hp_device_banner_kl_djs_djb.setVisibility(View.VISIBLE);

        timer = new CountDownTimer(num * 1000L, 1000L) {
            public void onTick(long millisUntilFinished) {
                tv_item_hp_device_banner_kl_djs.setText(millisUntilFinished / 1000 + "s");
                tv_item_hp_device_banner_kl_djs_djb.setText(millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                timer = null;
                tvItemHpDeviceBannerKlHq.setVisibility(View.VISIBLE);
                tv_item_hp_device_banner_kl_djs.setVisibility(View.GONE);
                tv_item_hp_device_banner_kl_djs_djb.setVisibility(View.GONE);
                tv_item_hp_device_banner_kl.setText("******");
                tv_item_hp_device_banner_kl_djb.setText("获 取 口 令");

            }
        }.start();
    }

    @Override
    public void onClick(View v) {

    }
    private void getAddressByLatlng(LatLng latLng) {
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);

    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        String city = regeocodeAddress.getCity();
        String district = regeocodeAddress.getDistrict();
        mTvYjdw.setText(city+" "+district);
        Log.e("adapter逆地理编码",city+" "+district);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }


    //第一步：自定义一个回调接口来实现Click和LongClick事件
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public OnItemClickListener mOnItemClickListener;//第二步：声明自定义的接口

    //第三步：定义方法并暴露给外面的调用者
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


}

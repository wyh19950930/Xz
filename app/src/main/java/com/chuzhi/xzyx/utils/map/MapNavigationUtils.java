package com.chuzhi.xzyx.utils.map;

/**
 * @Author : wyh
 * @Time : On 2023/11/1 19:10
 * @Description : MapNavigationUtils
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.chuzhi.xzyx.utils.ToastUtil;

/**
 * Date: 2022-11-23
 * Author: lanzi
 * 调用第三方地图app导航
 */
public class MapNavigationUtils {
    public static final String PN_GAODE_MAP = "com.autonavi.minimap";// 高德地图包名
    public static final String PN_BAIDU_MAP = "com.baidu.BaiduMap"; // 百度地图包名
    public static final String PN_TENCENT_MAP = "com.tencent.map"; // 腾讯地图包名

    /**
     * 检查地图应用是否安装
     */
    public static boolean isGaodeMapInstall(Context context) {
        return isInstallPackage(context, PN_GAODE_MAP);
    }

    public static boolean isBaiduMapInstall(Context context) {
        return isInstallPackage(context, PN_BAIDU_MAP);
    }

    public static boolean isTencentMapInstall(Context context) {
        return isInstallPackage(context, PN_TENCENT_MAP);
    }

    private static boolean isInstallPackage(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            if (info != null) {
                return true;
            }
        } catch (Exception e) {
            // loge(e);
        }
        return false;
    }

    /**
     * 打开高德地图导航（https://lbs.amap.com/api/amap-mobile/guide/android/route）
     *
     * @param context   context
     * @param address   终点名称
     * @param latitude  终点纬度
     * @param longitude 终点经度
     */
    public static void openGaodeNavigation(Context context, String address, double latitude, double longitude) {
        if (isGaodeMapInstall(context)) {
            Uri uri = Uri.parse("amapuri://route/plan/?dlat=" + latitude
                    + "&dlon=" + longitude
                    + "&dname=" + address
                    + "&dev=0&t=0");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } else {
            ToastUtil.showLong(context,"未安装高德地图");
//            Toast.makeText(context, "未安装高德地图", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 打开百度地图导航（https://lbsyun.baidu.com/index.php?title=uri/api/android#service-page-anchor9）
     *
     * @param context   context
     * @param address   终点名称
     * @param latitude  终点纬度
     * @param longitude 终点经度
     */
    public static void openBaiduNavigation(Context context, String address, double latitude, double longitude) {
        if (isBaiduMapInstall(context)) {
            String dLatlng = latitude + "," + longitude; // 目的地坐标（注意：坐标先纬度，后经度）
            Uri uri = Uri.parse("baidumap://map/direction?origin=我的位置"
                    + "&destination=name:" + address
                    + "|latlng:" + dLatlng
                    + "&coord_type=gcj02&mode=driving&src=andr.baidu.openAPIdemo");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } else {
            ToastUtil.showLong(context,"未安装百度地图");
//            Toast.makeText(context, "未安装百度地图", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 打开腾讯地图导航（https://lbs.qq.com/webApi/uriV1/uriGuide/uriMobileRoute）
     *
     * @param context   context
     * @param address   终点名称
     * @param latitude  终点纬度
     * @param longitude 终点经度
     */
    public static void openTencentNavigation(Context context, String address, double latitude, double longitude) {
        if (isTencentMapInstall(context)) {
            String dLatlng = latitude + "," + longitude;
            Uri uri = Uri.parse("qqmap://map/routeplan?type=drive&from=我的位置&fromcoord=CurrentLocation"
                    + "&to=" + address
                    + "&tocoord=" + dLatlng
                    + "&referer=key"); // key:填写你自己的腾讯地图开发者key
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } else {
            ToastUtil.showLong(context,"未安装腾讯地图");
//            Toast.makeText(context, "未安装腾讯地图", Toast.LENGTH_LONG).show();
        }
    }
}

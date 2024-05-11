package com.chuzhi.xzyx.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.View;

import com.chuzhi.xzyx.R;


public class GpsUtil {
    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps|| network) {
            return true;
        }

        return false;
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
   /* public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }*/
    private static AlertDialogIos permissionPop;
    public static final void openGPS(Activity context) {
//        new AlertDialog.Builder(context)
//                .setIcon(android.R.drawable.ic_dialog_info)
//                .setTitle("请打开GPS")
//                .setMessage("开启定位")
//                .setNegativeButton("取消",null)
//                .setPositiveButton("打开", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        context.startActivityForResult(intent,887);
//                        dialogInterface.dismiss();
//                    }
//                })
//                .show();

        permissionPop = new AlertDialogIos(context).builder()
                .setTitle("需要打开系统定位开关")
                .setMsg("用于提供精确的定位及导航类服务")
            .setNegativeButton("暂不开启", R.color.gray,null)
                .setPositiveButton("去设置", R.color.text_default, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivityForResult(intent, 887);
                        permissionPop.dismiss();
                    }
                });
        permissionPop.show();
    }
}

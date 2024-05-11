package com.chuzhi.xzyx.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.chuzhi.xzyx.app.MyApplication;

public class GetFileUtil {
    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
    public static String getSDCardPath() {
        Context context = MyApplication.Companion.getInstance();

        String path = "";
        if (Build.VERSION.SDK_INT < 29) {
            path = Environment.getExternalStorageDirectory() + "/file/";
        } else {
            //10以后
            path = context.getExternalFilesDir("").getAbsolutePath() + "/file/";
        }
        return path;
    }

}

package com.chuzhi.xzyx.utils;

import android.content.Context;

/**
 * @Author : wyh
 * @Time : On 2023/11/22 13:46
 * @Description : DpPxUtils
 */
public class DpPxUtils {

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static int pxToDp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(px / density);
    }
}

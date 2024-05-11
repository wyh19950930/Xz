package com.chuzhi.xzyx.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;

/**
 * @Author : wyh
 * @Time : On 2023/5/24 15:34
 * @Description : DensityUtils
 */
public class DensityUtils {
    public static float dp2px(@NonNull Context context, @Dimension(unit = Dimension.DP) int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
    public static int dp2px(@NonNull Context context,final float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}

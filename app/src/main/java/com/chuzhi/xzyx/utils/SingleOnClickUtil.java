package com.chuzhi.xzyx.utils;

public class SingleOnClickUtil {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1500;//1000
    private static long lastClickTime;

    public static boolean isFastClick() {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            return true;
        }
        return false;
    }


}

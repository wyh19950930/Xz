package com.chuzhi.xzyx.utils;

/**
 * @Author : wyh
 * @Time : On 2023/7/7 10:20
 * @Description : SpaceFilter
 */

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 禁止输入空格
 */
public class SpaceFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // 判断是否是空格
        if (source.equals(" "))
            return "";
        return null;
    }
}
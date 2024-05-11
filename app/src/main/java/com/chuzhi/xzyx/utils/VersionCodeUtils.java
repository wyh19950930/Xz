package com.chuzhi.xzyx.utils;

import android.text.TextUtils;

/**
 * @Author : wyh
 * @Time : On 2023/11/6 14:53
 * @Description : VersionCodeUtils 对比版本
 */
public class VersionCodeUtils {
    public static boolean isVersionNew(String newVer, String lastVer) {
        if (TextUtils.isEmpty(newVer) || TextUtils.isEmpty(lastVer)) {
            return false;
        }
        String[] newVerSplit = newVer.split("\\.");
        String[] lastVerSplit = lastVer.split("\\.");

        int maxLen = newVerSplit.length > lastVerSplit.length ? newVerSplit.length : lastVerSplit.length;
        for (int i = 0; i < maxLen; i++) {
            int newVerNum = strToInt(i < newVerSplit.length ? newVerSplit[i] : "0");
            int lastVerNum = strToInt(i < lastVerSplit.length ? lastVerSplit[i] : "0");
            if (newVerNum > lastVerNum) {
                return true;
            } else if (newVerNum < lastVerNum) {
                return false;
            }
        }
        return false;
    }

    private static int strToInt(String numStr) {
        try {
            return Integer.parseInt(numStr);
        } catch (Exception e) {
            return 0;
        }
    }
}

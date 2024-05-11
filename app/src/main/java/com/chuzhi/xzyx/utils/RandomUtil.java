package com.chuzhi.xzyx.utils;

import java.util.Random;

/**
 * @Author : wyh
 * @Time : On 2023/7/5 15:15
 * @Description : RandomUtil
 */
public class RandomUtil {
    /**
     * 随机产生6位数字：例：456562
     */
    public static final int sexNumber() {
        int numCode = (int) ((Math.random() * 9 + 1) * 100000);
        return numCode;
    }
    /**
     * 随机产生几位数字：例：maxLength=3,则结果可能是 012
     */
    public static final int produceNumber(int maxLength) {
        Random random = new Random();
        return random.nextInt(maxLength);
    }


    /**
     * 随机产生区间数字：例：minNumber=1,maxNumber=2,则结果可能是 1、2,包括首尾。
     */
    public static int produceRegionNumber(int minNumber, int maxNumber) {
        return minNumber + produceNumber(maxNumber);
    }

    /**
     * 随机产生几位字符串：例：maxLength=3,则结果可能是 aAz
     *
     * @param maxLength 传入数必须是正数。
     */
    public static String produceString(int maxLength) {
        String source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return doProduce(maxLength, source);
    }

    /**
     * 随机产生随机数字+字母：例：maxLength=3,则结果可能是 1Az
     *
     * @param maxLength 传入数必须是正数。
     */
    public static String produceStringAndNumber(int maxLength) {
//        String source = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String source = "bcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
        return doProduce(maxLength, source);
    }
    //0-9随机数
    public static int produceNumber(){
        int random=(int)(Math.random()*10);
        return random;
    }

    /**
     * 自定义随机产生结果
     */
    public static String produceResultByCustom(String customString, int maxLength) {
        return doProduce(maxLength, customString);
    }

    /**
     * 生产结果
     */
    private static String doProduce(int maxLength, String source) {
        StringBuffer sb = new StringBuffer(100);
        for (int i = 0; i < maxLength; i++) {
            final int number = produceNumber(source.length());
            sb.append(source.charAt(number));
        }
        return sb.toString();
    }
}

package com.chuzhi.xzyx.utils.dynamicpassword;

import android.util.Log;

import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>ClassName: TOTP</p>
 * <p>Description: TOTP = HOTP(K, T) // T is an integer
 * and represents the number of time steps between the initial counter
 * time T0 and the current Unix time
 * <p>
 * More specifically, T = (Current Unix time - T0) / X, where the
 * default floor function is used in the computation.</p>
 *
 * @author wangqian
 * @date 2018-04-03 11:44
 */
public class TOTP {

    public  void main(String[] args) {
        try {

            for (int j = 0; j < 10; j++) {
                String totp = generateMyTOTP("account01");
                System.out.println(String.format("加密后: %s", totp));
                Thread.sleep(1000);
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 共享密钥
     */
    private  final String SECRET_KEY = "zhangx2023_!070812345678900987654321qazwsxedcrfvtgbyhnujmik,ol.678912344566699944435109637fgvrcdgHHBFHMJHJBFFV";

    /**
     * 时间步长 单位:毫秒 作为口令变化的时间周期
     */
    private  final long STEP = 60000;

    /**
     * 转码位数 [1-8]
     */
    private  final int CODE_DIGITS = 6;

    /**
     * 初始化时间
     */
    private  final long INITIAL_TIME = 0;

    /**
     * 柔性时间回溯
     */
    private  final long FLEXIBILIT_TIME = 5000;

    /**
     * 数子量级
     */
    private  final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};



    public TOTP() {
    }

    /**
     * 生成一次性密码
     *
     * @param code 账户
     * @param
     * @return String
     */
    public  String generateMyTOTP(String code) {
        if (code.isEmpty()) {
            throw new RuntimeException("账户密码不许为空");
        }
//        long now = System.currentTimeMillis();
        long now = new Date().getTime();
//        long now = 1701413310;
        String time = Long.toHexString(timeFactor(now)).toUpperCase();
        Log.e("口令时间",time);
        return generateTOTP512(code + SECRET_KEY, time);
    }

    /**
     * 刚性口令验证
     *
     * @param code 账户
     * @param
     * @param totp 待验证的口令
     * @return boolean
     */
    public  boolean verifyTOTPRigidity(String code, String totp) {
        return generateMyTOTP(code).equals(totp);
    }

    /**
     * 柔性口令验证
     *
     * @param code 账户
     * @param
     * @param totp 待验证的口令
     * @return boolean
     */
    public  boolean verifyTOTPFlexibility(String code, String totp) {
        long now = new Date().getTime();
        String time = Long.toHexString(timeFactor(now)).toUpperCase();
        String tempTotp = generateTOTP(code  + SECRET_KEY, time);
        if (tempTotp.equals(totp)) {
            return true;
        }
        String time2 = Long.toHexString(timeFactor(now - FLEXIBILIT_TIME)).toUpperCase();
        String tempTotp2 = generateTOTP(code + SECRET_KEY, time2);
        return tempTotp2.equals(totp);
    }

    /**
     * 获取动态因子
     *
     * @param targetTime 指定时间
     * @return long
     */
    private  long timeFactor(long targetTime) {
        return (targetTime - INITIAL_TIME) / STEP;
    }

    /**
     * 哈希加密
     *
     * @param crypto   加密算法
     * @param keyBytes 密钥数组
     * @param text     加密内容
     * @return byte[]
     */
    private  byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text) {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey = new SecretKeySpec(keyBytes, "AES");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    private  byte[] hexStr2Bytes(String hex) {
        byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
        byte[] ret = new byte[bArray.length - 1];
        System.arraycopy(bArray, 1, ret, 0, ret.length);
        return ret;
    }

    private  String generateTOTP(String key, String time) {
        return generateTOTP(key, time, "HmacSHA1");
    }


    private  String generateTOTP256(String key, String time) {
        return generateTOTP(key, time, "HmacSHA256");
    }

    private  String generateTOTP512(String key, String time) {
        return generateTOTP(key, time, "HmacSHA512");
    }

    private  String generateTOTP(String key, String time, String crypto) {
        StringBuilder timeBuilder = new StringBuilder(time);
        while (timeBuilder.length() < 16)
            timeBuilder.insert(0, "0");
        time = timeBuilder.toString();

        byte[] msg = hexStr2Bytes(time);
        byte[] k = key.getBytes();
        byte[] hash = hmac_sha(crypto, k, msg);
//        Log.e("msg",Arrays.toString(msg));
//        Log.e("k",Arrays.toString(k));
//        Log.e("hash",Arrays.toString(hash));
        return truncate(hash);
    }

    /**
     * 截断函数
     *
     * @param target 20字节的字符串
     * @return String
     */
    private  String truncate(byte[] target) {
        StringBuilder result;
        int offset = target[target.length - 1] & 0xf;
        int binary = ((target[offset] & 0x7f) << 24)
                | ((target[offset + 1] & 0xff) << 16)
                | ((target[offset + 2] & 0xff) << 8) | (target[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[CODE_DIGITS];
        result = new StringBuilder(Integer.toString(otp));
        while (result.length() < CODE_DIGITS) {
            result.insert(0, "0");
        }
        return result.toString();
    }
}

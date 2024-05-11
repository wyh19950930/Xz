package com.chuzhi.xzyx.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecodeUnicodeUtil {
    /**
     * 汉字转化为Unicode编码
     *
     * @param CN 待转化的中文
     * @return 返回unicode编码
     */
    public static String CNToUnicode(String CN) {

        try {
            StringBuffer out = new StringBuffer("");
            //直接获取字符串的unicode二进制
            byte[] bytes = CN.getBytes("unicode");
            //然后将其byte转换成对应的16进制表示即可
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * unicode编码转换为中文
     *
     * @param unicodeStr 待转化的编码
     * @return 返回中文
     */
    public static String UnicodeToCN(String unicodeStr) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(unicodeStr);
        char ch;
        while (matcher.find()) {
            String group = matcher.group(2);
            ch = (char) Integer.parseInt(group, 16);
            String group1 = matcher.group(1);
            unicodeStr = unicodeStr.replace(group1, ch + "");
        }

        return unicodeStr.replace("\\", "").trim();
    }

    public static void main(String[] args) throws Exception {
        String str = "{\"msg\": \"\\u6570\\u636e\\u7c7b\\u578b\\u6709\\u8bef, \\u5b57\\u6bb5\\u540d\\u4e3a:\\u5206\\u9694\\u7ebf\\u989c\\u8272\", \"header_list\": [\"\\u8868\\u540d\", \"\\u5143\\u7d20ID\", \"\\u5b57\\u6bb5\\u540d\", \"\\u5b57\\u6bb5\\u503c\"], \"data_list\": [\"\\u8f66\\u9053\\u5206\\u9694\\u7ebf\", 65374, \"\\u5206\\u9694\\u7ebf\\u989c\\u8272\", 999]}";

//        String rs = "\\u6570\\u636e\\u7c7b\\u578b\\u6709\\u8bef, \\u5b57\\u6bb5\\u540d\\u4e3a:\\u5206\\u9694\\u7ebf\\u989c\\u8272";

        str = UnicodeToCN(str);
        System.out.println(str);

    }

}

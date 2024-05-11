package com.chuzhi.xzyx.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.chuzhi.xzyx.app.MyApplication;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


/**
 * 对SharedPreference文件中的各种类型的数据进行存取操作
 *
 */
public class SpUtils {

    private static SharedPreferences sp;

    private static void init(Context context) {
        if (sp == null) {
            sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.Companion.getInstance());//BaseApplication.getAppContext()
        }
    }

    public static void setSharedIntData(Context context, String key, int value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putInt(key, value).commit();
    }

    public static int getSharedIntData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getInt(key, 0);
    }

    public static void setSharedlongData(Context context, String key, long value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putLong(key, value).commit();
    }

    public static long getSharedlongData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getLong(key, 0l);
    }

    public static void setSharedFloatData(Context context, String key,
                                          float value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putFloat(key, value).commit();
    }

    public static Float getSharedFloatData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getFloat(key, 0f);
    }

    public static void setSharedBooleanData(Context context, String key,
                                            boolean value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    public static Boolean getSharedBooleanData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getBoolean(key, false);
    }

    public static void setSharedStringData(Context context, String key, String value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putString(key, value).commit();
    }

    public static String getSharedStringData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getString(key, "");
    }

    /**
     * 使用SharedPreferences保存List
     * 支持类型：List<String>，List<JavaBean>
     *
     * @param context  上下文
     * @param key      储存的key
     * @param dataList 存储数据
     * @param <T>      泛型
     */
    public static <T> void setSharedList(Context context, String key, List<T> dataList) {
        if (sp == null) {
            init(context);
        }
        if (null == dataList || dataList.size() < 0) {
            return;
        }
        Gson gson = new Gson();
        //转换成json数据，再保存
        String value = gson.toJson(dataList);
        sp.edit().putString(key, value).commit();
    }

    /**
     * 获取SharedPreferences保存的List
     *
     * @param context 上下文
     * @param key     储存的key
     * @param <T>     泛型
     * @return 存储List<T>数据
     */
    public static <T> List<T> getSharedList(Context context, String key, Class<T> cls) {
        if (sp == null) {
            init(context);
        }
        List<T> dataList = new ArrayList<T>();
        String strJson = sp.getString(key, null);
        if (null == strJson) {
            return dataList;
        }

        Gson gson = new Gson();

        //使用泛型解析数据会出错，返回的数据类型是LinkedTreeMap
//        dataList = gson.fromJson(strJson, new TypeToken<List<T>>() {
//        }.getType());

        //这样写，太死
//        dataList = gson.fromJson(strJson, new TypeToken<List<UserModel>>() {
//        }.getType());

        JsonArray arry = new JsonParser().parse(strJson).getAsJsonArray();
        for (JsonElement jsonElement : arry) {
            dataList.add(gson.fromJson(jsonElement, cls));
        }

        return dataList;
    }

}
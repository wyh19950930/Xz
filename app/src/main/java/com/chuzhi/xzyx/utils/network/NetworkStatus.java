package com.chuzhi.xzyx.utils.network;


/**
 * @Author: let
 * @date: 2022/11/15 17:30
 * @description: 网络连接状态的枚举
 */
public enum NetworkStatus {

    /**
     * ；
     */
    NONE(-1, "无网络连接"),
    /**
     * 解析数据内容失败
     */
    MOBILE(0, "移动网络连接"),
    /**
     * 网络问题
     */
    WIFI(1, "WIFI连接");

    private int status;
    private String desc;

    NetworkStatus(int code, String msg) {
        this.status = code;
        this.desc = msg;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "NetwordStatus{" +
                "status=" + status +
                ", desc='" + desc + '\'' +
                "} " + super.toString();
    }
}
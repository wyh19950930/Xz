package com.chuzhi.xzyx.api;


/**
 * App内存缓存
 */
public class AppCache {
    private volatile static AppCache instance;
    private String cardPath = "";
    private int RiskOperations = 0;//风险操作中 0可其他操作 1禁止其他操作
    private int LogInAgain = 1;//账号已在其他设备登录，请重新登录or你还未登录/登录超时!-1重新登录 0重新登录 1正常
    private int deviceSn;//当前设备sn号
    private int bannerPosition;//存储banner下标
    private int fenceType = 0;//围栏定位时间间隔判断 0可以选择间隔条件 1不可
    private int apkUploadIng = 0;//0没下载，1下载中，判断apk文件是否在更新中，用于判断下载中网络切换的问题
    private AppCache() {
    }
    public static AppCache getInstance() {
        if (null == instance) {
            synchronized (AppCache.class) {
                if (instance == null) {
                    instance = new AppCache();
                }
            }
        }
        return instance;
    }


    public String getCardPath() {
        return cardPath==null?"":cardPath;
    }

    public void setCardPath(String cardPath) {
        this.cardPath = cardPath;
    }

    public int getRiskOperations() {
        return RiskOperations;
    }

    public void setRiskOperations(int riskOperations) {
        RiskOperations = riskOperations;
    }

    public int getLogInAgain() {
        return LogInAgain;
    }

    public void setLogInAgain(int logInAgain) {
        LogInAgain = logInAgain;
    }

    public int getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(int deviceSn) {
        this.deviceSn = deviceSn;
    }

    public int getBannerPosition() {
        return bannerPosition;
    }

    public void setBannerPosition(int bannerPosition) {
        this.bannerPosition = bannerPosition;
    }

    public int getFenceType() {
        return fenceType;
    }

    public void setFenceType(int fenceType) {
        this.fenceType = fenceType;
    }

    public int getApkUploadIng() {
        return apkUploadIng;
    }

    public void setApkUploadIng(int apkUploadIng) {
        this.apkUploadIng = apkUploadIng;
    }
}

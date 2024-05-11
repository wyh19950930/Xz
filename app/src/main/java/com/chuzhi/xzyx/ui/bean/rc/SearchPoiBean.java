package com.chuzhi.xzyx.ui.bean.rc;

public class SearchPoiBean {

    private int id;
    private String name;
    private String detailDz;
    private String pointStr;
    private boolean isCheck = false;
    private boolean isFwn = false;
    private String zhen;
    private String cun;
    private String code;
    private String district;

    public SearchPoiBean(int id, String name, String district, String detailDz, String pointStr, boolean isCheck, boolean isFwn) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.detailDz = detailDz;
        this.pointStr = pointStr;
        this.isCheck = isCheck;
        this.isFwn = isFwn;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getZhen() {
        return zhen==null?"":zhen;
    }

    public void setZhen(String zhen) {
        this.zhen = zhen;
    }

    public String getCun() {
        return cun==null?"":cun;
    }

    public void setCun(String cun) {
        this.cun = cun;
    }

    public String getCode() {
        return code==null?"":code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFwn() {
        return isFwn;
    }

    public void setFwn(boolean fwn) {
        isFwn = fwn;
    }

    public String getName() {
        return name==null?"":name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetailDz() {
        return detailDz==null?"":detailDz;
    }

    public void setDetailDz(String detailDz) {
        this.detailDz = detailDz;
    }

    public String getPointStr() {
        return pointStr==null?"":pointStr;
    }

    public void setPointStr(String pointStr) {
        this.pointStr = pointStr;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}

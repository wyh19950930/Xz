package com.chuzhi.xzyx.ui.bean.rc;

/**
 * @Author : wyh
 * @Time : On 2023/6/27 15:38
 * @Description : PortStatusEntity
 */
public class PortStatusEntity {

    private int id;
    private String sn;
    private int status;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

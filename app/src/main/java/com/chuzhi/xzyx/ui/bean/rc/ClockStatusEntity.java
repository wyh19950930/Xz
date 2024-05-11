package com.chuzhi.xzyx.ui.bean.rc;

/**
 * @Author : wyh
 * @Time : On 2023/7/17 14:42
 * @Description : ClockStatusEntity 锁机结果查询
 */
public class ClockStatusEntity {

    private int id;
    private String sn;
    private int power_button;
    private int clock_status;
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

    public int getPower_button() {
        return power_button;
    }

    public void setPower_button(int power_button) {
        this.power_button = power_button;
    }

    public int getClock_status() {
        return clock_status;
    }

    public void setClock_status(int clock_status) {
        this.clock_status = clock_status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

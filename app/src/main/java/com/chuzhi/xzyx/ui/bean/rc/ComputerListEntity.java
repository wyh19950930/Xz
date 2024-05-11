package com.chuzhi.xzyx.ui.bean.rc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/7 11:11
 * @Description : ComputerListEntity
 */
public class ComputerListEntity implements Serializable{

    private List<ComputerListDTO> computer_list;

    public List<ComputerListDTO> getComputer_list() {
        if (computer_list == null){
            computer_list = new ArrayList<>();
        }
        return computer_list;
    }

    public void setComputer_list(List<ComputerListDTO> computer_list) {
        this.computer_list = computer_list;
    }

    public static class ComputerListDTO implements Serializable{
        private int id;
        private String sn;
        private String ssd_sn;
        private String name;
        private Object os;
        private Object cpu;
        private Object mainboard;
        private Object ram;
        private Object ssd;
        private Object graphics;
        private Object displayer;
        private String current_coordinate;
        private String battery;//电池电量
        private String csq;//信号值
        private int computer_status;//设备开机状态
        private int clock_status;//锁机状态
        private int stolen_status;//被盗模式状态
        private int backlight_status;//背光状态
        private long totalTime;//倒计时
        private int warning_count;//设备单围栏预警次数
        private boolean destroy_permissions;//true 可以销毁，false不可以销毁
        private int geogence_warning_count;//设备全围栏预警次数
        private int geogence_count;//设备围栏数
        private int c_version;//1远程版0单机版
        private String mainboard_sn;//主板sn，只在我的-设备管理-列表用

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

        public String getSsd_sn() {
            return ssd_sn;
        }

        public void setSsd_sn(String ssd_sn) {
            this.ssd_sn = ssd_sn;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getOs() {
            return os;
        }

        public void setOs(Object os) {
            this.os = os;
        }

        public Object getCpu() {
            return cpu;
        }

        public void setCpu(Object cpu) {
            this.cpu = cpu;
        }

        public Object getMainboard() {
            return mainboard;
        }

        public void setMainboard(Object mainboard) {
            this.mainboard = mainboard;
        }

        public Object getRam() {
            return ram;
        }

        public void setRam(Object ram) {
            this.ram = ram;
        }

        public Object getSsd() {
            return ssd;
        }

        public void setSsd(Object ssd) {
            this.ssd = ssd;
        }

        public Object getGraphics() {
            return graphics;
        }

        public void setGraphics(Object graphics) {
            this.graphics = graphics;
        }

        public Object getDisplayer() {
            return displayer;
        }

        public void setDisplayer(Object displayer) {
            this.displayer = displayer;
        }

        public String getCurrent_coordinate() {
            return current_coordinate == null?"0.0,0.0":current_coordinate;//116.2482640189582,40 .07321507074024
        }

        public void setCurrent_coordinate(String current_coordinate) {
            this.current_coordinate = current_coordinate;
        }

        public String getBattery() {
            return battery == null?"0":battery;
        }

        public void setBattery(String battery) {
            this.battery = battery;
        }

        public String getCsq() {
            return csq == null?"0":csq;
        }

        public void setCsq(String csq) {
            this.csq = csq;
        }

        public int getComputer_status() {
            return computer_status;
        }

        public void setComputer_status(int computer_status) {
            this.computer_status = computer_status;
        }

        public int getClock_status() {
            return clock_status;
        }

        public void setClock_status(int clock_status) {
            this.clock_status = clock_status;
        }

        public int getStolen_status() {
            return stolen_status;
        }

        public void setStolen_status(int stolen_status) {
            this.stolen_status = stolen_status;
        }

        public int getBacklight_status() {
            return backlight_status;
        }

        public void setBacklight_status(int backlight_status) {
            this.backlight_status = backlight_status;
        }

        public long getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(long totalTime) {
            this.totalTime = totalTime;
        }

        public int getWarning_count() {
            return warning_count;
        }

        public void setWarning_count(int warning_count) {
            this.warning_count = warning_count;
        }

        public boolean isDestroy_permissions() {
            return destroy_permissions;
        }

        public void setDestroy_permissions(boolean destroy_permissions) {
            this.destroy_permissions = destroy_permissions;
        }

        public int getGeogence_warning_count() {
            return geogence_warning_count;
        }

        public void setGeogence_warning_count(int geogence_warning_count) {
            this.geogence_warning_count = geogence_warning_count;
        }

        public int getGeogence_count() {
            return geogence_count;
        }

        public void setGeogence_count(int geogence_count) {
            this.geogence_count = geogence_count;
        }

        public int getC_version() {
            return c_version;
        }

        public void setC_version(int c_version) {
            this.c_version = c_version;
        }

        public String getMainboard_sn() {
            return mainboard_sn;
        }

        public void setMainboard_sn(String mainboard_sn) {
            this.mainboard_sn = mainboard_sn;
        }
    }
}

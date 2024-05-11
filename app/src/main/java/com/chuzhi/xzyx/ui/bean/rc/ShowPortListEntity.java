package com.chuzhi.xzyx.ui.bean.rc;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/27 13:42
 * @Description : ShowPortListEntity设备端口列表信息展示实体
 */
public class ShowPortListEntity {

    private List<PortListDTO> port_list;

    public List<PortListDTO> getPort_list() {
        return port_list;
    }

    public void setPort_list(List<PortListDTO> port_list) {
        this.port_list = port_list;
    }

    public static class PortListDTO {
        private int id;
        private String sn;
        private int usb_status;
        private int type_status;
        private int wifi_status;
        private int bluetooth_status;

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

        public int getUsb_status() {
            return usb_status;
        }

        public void setUsb_status(int usb_status) {
            this.usb_status = usb_status;
        }

        public int getType_status() {
            return type_status;
        }

        public void setType_status(int type_status) {
            this.type_status = type_status;
        }

        public int getWifi_status() {
            return wifi_status;
        }

        public void setWifi_status(int wifi_status) {
            this.wifi_status = wifi_status;
        }

        public int getBluetooth_status() {
            return bluetooth_status;
        }

        public void setBluetooth_status(int bluetooth_status) {
            this.bluetooth_status = bluetooth_status;
        }
    }
}

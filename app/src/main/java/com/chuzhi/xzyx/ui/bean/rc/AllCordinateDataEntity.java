package com.chuzhi.xzyx.ui.bean.rc;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/28 18:48
 * @Description : AllCordinateDataEntity 首页所有设备位置信息
 */
public class AllCordinateDataEntity {

    private List<ComputerListDTO> computer_list;

    public List<ComputerListDTO> getComputer_list() {
        return computer_list;
    }

    public void setComputer_list(List<ComputerListDTO> computer_list) {
        this.computer_list = computer_list;
    }

    public static class ComputerListDTO {
        private int id;
        private String sn;
        private String name;
        private String current_coordinate;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCurrent_coordinate() {
            return current_coordinate==null?"0.0,0.0":current_coordinate;
        }

        public void setCurrent_coordinate(String current_coordinate) {
            this.current_coordinate = current_coordinate;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}

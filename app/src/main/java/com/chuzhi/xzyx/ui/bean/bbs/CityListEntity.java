package com.chuzhi.xzyx.ui.bean.bbs;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/16 16:18
 * @Description : CityListEntity 城市列表
 */
public class CityListEntity {

    private List<CityListDTO> city_list;
    private String province_id;

    public List<CityListDTO> getCity_list() {
        return city_list;
    }

    public void setCity_list(List<CityListDTO> city_list) {
        this.city_list = city_list;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public static class CityListDTO {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

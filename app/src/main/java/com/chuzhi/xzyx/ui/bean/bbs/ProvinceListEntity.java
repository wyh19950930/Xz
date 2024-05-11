package com.chuzhi.xzyx.ui.bean.bbs;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/16 16:10
 * @Description : ProvinceListEntity 省份列表
 */
public class ProvinceListEntity {

    private List<ProvenceListDTO> provence_list;

    public List<ProvenceListDTO> getProvence_list() {
        return provence_list;
    }

    public void setProvence_list(List<ProvenceListDTO> provence_list) {
        this.provence_list = provence_list;
    }

    public static class ProvenceListDTO {
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

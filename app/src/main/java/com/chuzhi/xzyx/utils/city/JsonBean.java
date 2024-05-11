package com.chuzhi.xzyx.utils.city;

/**
 * @Author : wyh
 * @Time : On 2023/6/16 15:02
 * @Description : JsonBean
 */
import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

public class JsonBean implements IPickerViewData {

    /**
     * name : 省份
     * city : [{"name":"北京市","area":["东城区","西城区","崇文区","宣武区","朝阳区"]}]
     */

    public String name;
    public List<CityBean> city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityBean> getCityList() {
        return city;
    }

    public void setCityList(List<CityBean> city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "JsonBean{" +
                "name='" + name + '\'' +
                ", city=" + city +
                '}';
    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return name;
    }


    public static class CityBean {
        /**
         * name : 城市
         * area : ["东城区","西城区","崇文区","昌平区"]
         */

        private String name;
        private List<String> area;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getArea() {
            return area;
        }

        public void setArea(List<String> area) {
            this.area = area;
        }
    }
}

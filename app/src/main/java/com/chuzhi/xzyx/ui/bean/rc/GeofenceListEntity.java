package com.chuzhi.xzyx.ui.bean.rc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/12 16:42
 * @Description : GeofenceListEntity
 */
public class GeofenceListEntity implements Serializable {

    private List<GeofenceListDTO> geofence_list;

    public List<GeofenceListDTO> getGeofence_list() {
        if (geofence_list == null) {
            geofence_list = new ArrayList<>();
        }
        return geofence_list;
    }

    public void setGeofence_list(List<GeofenceListDTO> geofence_list) {
        this.geofence_list = geofence_list;
    }

    public static class GeofenceListDTO implements Serializable {
        private int id;
        private String name;
        private String center;
        private String radius;
        private String address;
        private String create_time;
        private String type;//进出预警
        private int every;//定位时间间隔

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name == null ? "" : name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCenter() {
            return center == null ? "" : center;
        }

        public void setCenter(String center) {
            this.center = center;
        }

        public String getRadius() {
            return radius;
        }

        public void setRadius(String radius) {
            this.radius = radius;
        }

        public String getAddress() {
            return address==null?"":address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCreate_time() {
            return create_time == null ? "" : create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getEvery() {
            return every;
        }

        public void setEvery(int every) {
            this.every = every;
        }
    }
}

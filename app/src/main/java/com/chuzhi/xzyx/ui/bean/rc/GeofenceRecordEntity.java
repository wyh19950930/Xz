package com.chuzhi.xzyx.ui.bean.rc;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/7/4 18:00
 * @Description : GeofenceRecordEntity 安全日志实体
 */
public class GeofenceRecordEntity {

    private int page_count;
    private int record_model_count;
    private List<RecordListDTO> record_list;

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public int getRecord_model_count() {
        return record_model_count;
    }

    public void setRecord_model_count(int record_model_count) {
        this.record_model_count = record_model_count;
    }

    public List<RecordListDTO> getRecord_list() {
        return record_list;
    }

    public void setRecord_list(List<RecordListDTO> record_list) {
        this.record_list = record_list;
    }

    public static class RecordListDTO {
        private String geofence_name;
        private String create_time;
        private String position;
        private String record_status;
        private String address;

        public String getGeofence_name() {
            return geofence_name;
        }

        public void setGeofence_name(String geofence_name) {
            this.geofence_name = geofence_name;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getPosition() {
            return position == null?"暂未获取到位置":position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getRecord_status() {
            return record_status;
        }

        public void setRecord_status(String record_status) {
            this.record_status = record_status;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}

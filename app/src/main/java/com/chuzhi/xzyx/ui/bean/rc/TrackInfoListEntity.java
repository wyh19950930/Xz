package com.chuzhi.xzyx.ui.bean.rc;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/29 15:08
 * @Description : TrackInfoListEntity 历史轨迹列表
 */
public class TrackInfoListEntity implements Serializable {

    private int count;
    private int page_count;
    private List<TrackDictDTO> track_dict;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public List<TrackDictDTO> getTrack_dict() {
        return track_dict;
    }

    public void setTrack_dict(List<TrackDictDTO> track_dict) {
        this.track_dict = track_dict;
    }

    public static class TrackDictDTO implements Serializable{
        private int id;
        private String start_address;
        private String start_time;
        private String end_address;
        private String end_time;
        private String distance;
        private List<String> coordinate;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStart_address() {
            return start_address;
        }

        public void setStart_address(String start_address) {
            this.start_address = start_address;
        }

        public String getStart_time() {
            return start_time == null?"2000-00-00":start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_address() {
            return end_address;
        }

        public void setEnd_address(String end_address) {
            this.end_address = end_address;
        }

        public String getEnd_time() {
            return end_time == null?"2000-00-00":end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public List<String> getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(List<String> coordinate) {
            this.coordinate = coordinate;
        }
    }
}

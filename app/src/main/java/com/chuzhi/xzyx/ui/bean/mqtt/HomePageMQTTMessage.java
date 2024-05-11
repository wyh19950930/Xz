package com.chuzhi.xzyx.ui.bean.mqtt;

/**
 * @Author : wyh
 * @Time : On 2023/9/22 9:11
 * @Description : HomePageMQTTMessage
 */
public class HomePageMQTTMessage {
    private String dl;
    private String point;

    public String getDl() {
        return dl;
    }

    public void setDl(String dl) {
        this.dl = dl;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "HomePageMQTTMessage{" +
                "dl='" + dl + '\'' +
                ", point='" + point + '\'' +
                '}';
    }
}

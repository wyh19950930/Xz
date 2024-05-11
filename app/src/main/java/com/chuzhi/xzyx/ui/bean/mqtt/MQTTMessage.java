package com.chuzhi.xzyx.ui.bean.mqtt;

/**
 * @Author : wyh
 * @Time : On 2023/7/27 15:36
 * @Description : MQTTConMessage
 */
public class MQTTMessage {
    private String type;
    private String message;
    private int position;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

package com.chuzhi.xzyx.ui.bean.rc;

/**
 * @Author : wyh
 * @Time : On 2023/6/8 15:48
 * @Description : ComputerInfoEntity
 */
public class ComputerInfoEntity {
    private String code;

    public String getCode() {
        return code == null ? "未知设备" : code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

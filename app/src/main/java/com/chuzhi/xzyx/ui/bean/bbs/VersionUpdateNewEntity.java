package com.chuzhi.xzyx.ui.bean.bbs;

import java.io.Serializable;

/**
 * @Author : wyh
 * @Time : On 2023/11/6 14:58
 * @Description : VersionUpdateNewEntity
 */
public class VersionUpdateNewEntity implements Serializable {

    private String last_version;
    private String filename;
    private int size;
    private String force_update;
    private String description;

    public String getLast_version() {
        return last_version;
    }

    public void setLast_version(String last_version) {
        this.last_version = last_version;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String isForce_update() {
        return force_update;
    }

    public void setForce_update(String force_update) {
        this.force_update = force_update;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "VersionUpdateNewEntity{" +
                "last_version='" + last_version + '\'' +
                ", filename='" + filename + '\'' +
                ", size=" + size +
                ", force_update=" + force_update +
                ", description='" + description + '\'' +
                '}';
    }
}

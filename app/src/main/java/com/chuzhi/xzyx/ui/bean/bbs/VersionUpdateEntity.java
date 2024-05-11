package com.chuzhi.xzyx.ui.bean.bbs;

/**
 * @Author : wyh
 * @Time : On 2023/7/20 19:47
 * @Description : VersionUpdateEntity版本更新实体
 */
public class VersionUpdateEntity {

    private String version;
    private String version_note;
    private boolean version_force_update;
    private String version_size;
    private String version_url;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion_note() {
        return version_note;
    }

    public void setVersion_note(String version_note) {
        this.version_note = version_note;
    }

    public boolean isVersion_force_update() {
        return version_force_update;
    }

    public void setVersion_force_update(boolean version_force_update) {
        this.version_force_update = version_force_update;
    }

    public String getVersion_size() {
        return version_size;
    }

    public void setVersion_size(String version_size) {
        this.version_size = version_size;
    }

    public String getVersion_url() {
        return version_url;
    }

    public void setVersion_url(String version_url) {
        this.version_url = version_url;
    }

    @Override
    public String toString() {
        return "VersionUpdateEntity{" +
                "version='" + version + '\'' +
                ", version_note='" + version_note + '\'' +
                ", version_force_update=" + version_force_update +
                ", version_size='" + version_size + '\'' +
                ", version_url='" + version_url + '\'' +
                '}';
    }
}

package com.chuzhi.xzyx.ui.bean.eventbus;

/**
 * @Author : wyh
 * @Time : On 2023/12/13 11:17
 * @Description : DownLoadEBEntity
 */
public class DownLoadEBEntity {
    private int progress;
    private String path;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getPath() {
        return path == null?"":path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

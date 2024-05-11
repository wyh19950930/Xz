package com.chuzhi.xzyx.ui.bean.bbs;

/**
 * 文章分类实体
 */
public class ArticleCategoryEntity {

    private int id;
    private String name;
    private int flag;

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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}

package com.chuzhi.xzyx.ui.bean.bbs;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/5/24 10:20
 * @Description : 文章详情页实体
 */
public class ArticleDetailsEntity {

    private String user_name;
    private String title;
    private String avatar;
    private String contents;
    private int answers_count;//评论数
    private int read_count;//浏览量
    private String create_time;
    private int praise_count;//点赞数
    private boolean praise_status;
    private boolean follow_status;
    private List<String> image_name;//此字段如果不为空，则是app端上传的内容，需判断显示
    private int sourced;//1 为移动端，0 为网页端


    public String getUser_name() {
        return user_name == null ? "" : user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar == null ? "" : avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContents() {
        return contents == null ? "" : contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getAnswers_count() {
        return answers_count;
    }

    public void setAnswers_count(int answers_count) {
        this.answers_count = answers_count;
    }

    public int getRead_count() {
        return read_count;
    }

    public void setRead_count(int read_count) {
        this.read_count = read_count;
    }

    public String getCreate_time() {
        return create_time == null ? "" : create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getPraise_count() {
        return praise_count;
    }

    public void setPraise_count(int praise_count) {
        this.praise_count = praise_count;
    }

    public boolean isPraise_status() {
        return praise_status;
    }

    public void setPraise_status(boolean praise_status) {
        this.praise_status = praise_status;
    }

    public boolean isFollow_status() {
        return follow_status;
    }

    public void setFollow_status(boolean follow_status) {
        this.follow_status = follow_status;
    }

    public List<String> getImage_name() {
        return image_name;
    }

    public void setImage_name(List<String> image_name) {
        this.image_name = image_name;
    }

    public int getSourced() {
        return sourced;
    }

    public void setSourced(int sourced) {
        this.sourced = sourced;
    }
}

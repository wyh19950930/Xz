package com.chuzhi.xzyx.ui.bean.bbs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章列表实体
 */
public class ArticleListEntity implements Serializable {

    private List<ArticleListDTO> article_list;

    public List<ArticleListDTO> getArticle_list() {
        if (article_list == null) {
            article_list = new ArrayList<>();
        }
        return article_list;
    }

    public void setArticle_list(List<ArticleListDTO> article_list) {
        this.article_list = article_list;
    }

    public static class ArticleListDTO implements Serializable {
        private int id;
        private String author_name;
        private String avatar;
        private String title;
        private String contents;
        private int answers_count;//评论量
        private int article_praise_count;//点赞量
        private int read_count;//浏览量
        private String create_time;
        private String img;
        private String article_img;//app上传图片
        private boolean followed = false;//true已关注false未关注
        private boolean praise_status = false;//true已赞false未赞

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAuthor_name() {
            return author_name == null ? "" : author_name;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public String getAvatar() {
            return avatar == null ? "" : avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getTitle() {
            return title == null ? "" : title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public int getArticle_praise_count() {
            return article_praise_count;
        }

        public void setArticle_praise_count(int article_praise_count) {
            this.article_praise_count = article_praise_count;
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

        public String getImg() {
            return img == null ? "" : img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public boolean isFollowed() {
            return followed;
        }

        public void setFollowed(boolean followed) {
            this.followed = followed;
        }

        public boolean isPraise_status() {
            return praise_status;
        }

        public void setPraise_status(boolean praise_status) {
            this.praise_status = praise_status;
        }

        public String getArticle_img() {
            return article_img == null?"":article_img;
        }

        public void setArticle_img(String article_img) {
            this.article_img = article_img;
        }
    }
}

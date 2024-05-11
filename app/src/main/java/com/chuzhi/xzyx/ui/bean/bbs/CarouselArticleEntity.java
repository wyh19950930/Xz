package com.chuzhi.xzyx.ui.bean.bbs;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/7/20 18:53
 * @Description : CarouselArticleEntity 轮播图实体
 */
public class CarouselArticleEntity {

    private List<ArticleListDTO> article_list;

    public List<ArticleListDTO> getArticle_list() {
        return article_list;
    }

    public void setArticle_list(List<ArticleListDTO> article_list) {
        this.article_list = article_list;
    }

    public static class ArticleListDTO {
        private int id;
        private int article_category;
        private String title;
        private String contents;
        private int read_count;
        private int top;
        private int transmit;
        private int sourced;
        private String author_name;
        private Object avatar;
        private String create_time;
        private int answers_count;
        private int praise_count;
        private boolean praise_status;
        private String img;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getArticle_category() {
            return article_category;
        }

        public void setArticle_category(int article_category) {
            this.article_category = article_category;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }

        public int getRead_count() {
            return read_count;
        }

        public void setRead_count(int read_count) {
            this.read_count = read_count;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getTransmit() {
            return transmit;
        }

        public void setTransmit(int transmit) {
            this.transmit = transmit;
        }

        public int getSourced() {
            return sourced;
        }

        public void setSourced(int sourced) {
            this.sourced = sourced;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public Object getAvatar() {
            return avatar;
        }

        public void setAvatar(Object avatar) {
            this.avatar = avatar;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getAnswers_count() {
            return answers_count;
        }

        public void setAnswers_count(int answers_count) {
            this.answers_count = answers_count;
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

        public String getArticle_img() {
            return img;
        }

        public void setArticle_img(String article_img) {
            this.img = article_img;
        }
    }
}

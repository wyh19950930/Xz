package com.chuzhi.xzyx.ui.bean.bbs;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/19 15:05
 * @Description : UserArticleListEntity 我的发布文章实体
 */
public class UserArticleListEntity {

    private List<ArticleListDTO> article_list;
    private int article_count;

    public List<ArticleListDTO> getArticle_list() {
        if (article_list == null) {
            article_list = new ArrayList<>();
        }
        return article_list;
    }

    public void setArticle_list(List<ArticleListDTO> article_list) {
        this.article_list = article_list;
    }

    public int getArticle_count() {
        return article_count;
    }

    public void setArticle_count(int article_count) {
        this.article_count = article_count;
    }

    public static class ArticleListDTO {
        private int id;
        private String title;
        private Object image_name;
        private String contents;
        private int praise_count;
        private int transmit;
        private String create_time;
        private int audit_status;//0待审核1已审核2不通过
        private int answer_number;
        private int read_count;
        private boolean praise_status = false;//是否点赞

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Object getImage_name() {
            return image_name;
        }

        public void setImage_name(Object image_name) {
            this.image_name = image_name;
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }

        public int getPraise_count() {
            return praise_count;
        }

        public void setPraise_count(int praise_count) {
            this.praise_count = praise_count;
        }

        public int getTransmit() {
            return transmit;
        }

        public void setTransmit(int transmit) {
            this.transmit = transmit;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getAudit_status() {
            return audit_status;
        }

        public void setAudit_status(int audit_status) {
            this.audit_status = audit_status;
        }

        public int getAnswer_number() {
            return answer_number;
        }

        public void setAnswer_number(int answer_number) {
            this.answer_number = answer_number;
        }

        public int getRead_count() {
            return read_count;
        }

        public void setRead_count(int read_count) {
            this.read_count = read_count;
        }

        public boolean isPraise_status() {
            return praise_status;
        }

        public void setPraise_status(boolean praise_status) {
            this.praise_status = praise_status;
        }
    }
}

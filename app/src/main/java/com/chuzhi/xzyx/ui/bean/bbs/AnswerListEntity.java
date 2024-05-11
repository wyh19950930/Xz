package com.chuzhi.xzyx.ui.bean.bbs;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/20 16:33
 * @Description : AnswerListEntity 我的回答列表实体
 */
public class AnswerListEntity {

    private int answer_count;
    private List<AnswerListDTO> answer_list;

    public int getAnswer_count() {
        return answer_count;
    }

    public void setAnswer_count(int answer_count) {
        this.answer_count = answer_count;
    }

    public List<AnswerListDTO> getAnswer_list() {
        return answer_list;
    }

    public void setAnswer_list(List<AnswerListDTO> answer_list) {
        this.answer_list = answer_list;
    }

    public static class AnswerListDTO {
        private int article_id;
        private String title;
        private String content;
        private String push_time;
        private int praise_count;
        private boolean praise_status;
        private int sourced;//0 web 1 app
        private String content_text;//web评论

        public int getArticle_id() {
            return article_id;
        }

        public void setArticle_id(int article_id) {
            this.article_id = article_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPush_time() {
            return push_time;
        }

        public void setPush_time(String push_time) {
            this.push_time = push_time;
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

        public int getSourced() {
            return sourced;
        }

        public void setSourced(int sourced) {
            this.sourced = sourced;
        }

        public String getContent_text() {
            return content_text;
        }

        public void setContent_text(String content_text) {
            this.content_text = content_text;
        }
    }
}

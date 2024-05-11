package com.chuzhi.xzyx.ui.bean.bbs;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/5/30 10:43
 * @Description : ArticleAnswersEntity 文章详情页评论列表
 */
public class ArticleAnswersEntity{

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
        private int id;
        private int user_id;
        private String user_name;
        private String avatar;
        private String content;
        private Object relevance_answer;
        private String push_time;
        private int praise_count;
        private boolean praise_status;
        private int sourced;//0 web 1 app
        private String content_text;//web评论



        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name == null ? "匿名用户" : user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getAvatar() {
            return avatar == null ? "" : avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getContent() {
            return content == null ? "" : content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Object getRelevance_answer() {
            return relevance_answer;
        }

        public void setRelevance_answer(Object relevance_answer) {
            this.relevance_answer = relevance_answer;
        }

        public String getPush_time() {
            return push_time == null ? "" : push_time;
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

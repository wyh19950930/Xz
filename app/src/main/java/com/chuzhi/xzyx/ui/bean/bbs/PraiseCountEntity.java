package com.chuzhi.xzyx.ui.bean.bbs;

/**
 * @Author : wyh
 * @Time : On 2023/6/20 13:57
 * @Description : PraiseCountEntity 获赞/点赞/粉丝数统计
 */
public class PraiseCountEntity {

    private int praise_count;
    private int follow_count;
    private int fan_count;
    private int artcle_count;
    private int answer_count;

    public int getPraise_count() {
        return praise_count;
    }

    public void setPraise_count(int praise_count) {
        this.praise_count = praise_count;
    }

    public int getFollow_count() {
        return follow_count;
    }

    public void setFollow_count(int follow_count) {
        this.follow_count = follow_count;
    }

    public int getFan_count() {
        return fan_count;
    }

    public void setFan_count(int fan_count) {
        this.fan_count = fan_count;
    }

    public int getArtcle_count() {
        return artcle_count;
    }

    public void setArtcle_count(int artcle_count) {
        this.artcle_count = artcle_count;
    }

    public int getAnswer_count() {
        return answer_count;
    }

    public void setAnswer_count(int answer_count) {
        this.answer_count = answer_count;
    }
}

package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleAnswersEntity;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleDetailsEntity;

public interface FindDetailsView extends BaseView {
    void articleDetails(ArticleDetailsEntity msg);//文章详情
    void articleFollower(BaseModel<String> msg);//关注用户or取消关注
    void articlePraise(BaseModel<String> msg);//文章点赞 取消点赞
    void articleAnswers(ArticleAnswersEntity msg);//文章详情评论列表
    void answerPraise(BaseModel<String> msg);//评论点赞 取消点赞
    void addAnswer(BaseModel<String> msg);//发送评论
}

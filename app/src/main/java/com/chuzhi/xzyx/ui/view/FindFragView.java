package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleCategoryEntity1;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.CarouselArticleEntity;

public interface FindFragView extends BaseView {
    void carouselArticle(CarouselArticleEntity msg);//轮播图
    void articleCategoryList(ArticleCategoryEntity1 msg);//文章分类tab
    void articleList(ArticleListEntity msg);//文章列表
    void articleFollower(BaseModel<String> msg);//关注用户or取消关注
    void articlePraise(BaseModel<String> msg);//点赞 取消点赞
}

package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.UserArticleListEntity;

public interface MyReleaseView extends BaseView {
    void userArticleList(UserArticleListEntity msg);//我的发布列表
    void articlePraise(BaseModel<String> msg);//点赞 取消点赞
}

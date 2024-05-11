package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.OauthSetUserInfo;
import com.chuzhi.xzyx.ui.bean.bbs.PraiseCountEntity;

public interface MeFragView extends BaseView {
    void bbsOutToken(BaseModel<String> msg);//退出登录
    void praiseCount(PraiseCountEntity msg);//获赞/点赞/粉丝数统计
    void oauthSetUserInfo(OauthSetUserInfo msg);//查看个人信息
}

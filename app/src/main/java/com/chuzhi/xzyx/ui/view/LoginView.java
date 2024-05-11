package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity;

public interface LoginView extends BaseView {
    void loginSuccess(BbsUserEntity msg);//bbs登录
    void loginRcSuccess(BaseModel<String> msg);//远程管控登录
}

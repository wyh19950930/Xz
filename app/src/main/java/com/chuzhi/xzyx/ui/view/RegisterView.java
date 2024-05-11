package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity;

public interface RegisterView extends BaseView {
    void codeSuccess(BaseModel<String> msg);//获取验证码
    void registerUserSuccess(BbsUserEntity msg);//注册账号
}

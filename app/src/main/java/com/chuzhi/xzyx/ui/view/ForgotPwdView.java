package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;

public interface ForgotPwdView extends BaseView {
    void codeSuccess(BaseModel<String> msg);//获取验证码
    void oauthRetrievePassword(BaseModel<String> msg);//找回密码
}

package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;

public interface LogOffCodeView extends BaseView {
    void codeSuccess(BaseModel<String> msg);//获取验证码
    void logOffPhone(BaseModel<String> msg);//注销账号
}

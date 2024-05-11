package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;

public interface ChangePwdView extends BaseView {
    void oauthAlterPassword(BaseModel<String> msg);//修改密码
}

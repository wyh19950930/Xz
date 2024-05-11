package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity;

public interface CodeView extends BaseView {
    void codeSuccess(BaseModel<String> msg);

    void registerUserSuccess(BbsUserEntity msg);

    void loginSuccess(BbsUserEntity msg);

}

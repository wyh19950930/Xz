package com.chuzhi.xzyx.ui.view;

import android.graphics.Bitmap;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateEntity;
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;
import com.chuzhi.xzyx.ui.bean.rc.DynamicPasswordEntity;

public interface WelcomeView extends BaseView {
    void userComputerList(ComputerListEntity msg);
}

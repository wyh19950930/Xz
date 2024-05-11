package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;

public interface FenceSettingView extends BaseView {
    void addGeofence(BaseModel<String> msg);//添加围栏
    void alterGeofence(BaseModel<String> msg);//修改围栏
}

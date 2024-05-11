package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.rc.ComputerInfoEntity;

public interface HPAddDeviceView extends BaseView {
    void computerInfo(ComputerInfoEntity msg);//设备信息
    void bindComputer(BaseModel<String> msg);//绑定设备
}

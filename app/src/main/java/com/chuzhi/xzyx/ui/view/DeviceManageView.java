package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;

public interface DeviceManageView extends BaseView {
    void userComputerList(ComputerListEntity msg);//设备列表
    void unbindComputer(BaseModel<String> msg);//解绑设备
    void alterBindInfo(BaseModel<String> msg);//修改设备名称
}

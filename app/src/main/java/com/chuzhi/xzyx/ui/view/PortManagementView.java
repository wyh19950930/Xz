package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.rc.PortStatusEntity;
import com.chuzhi.xzyx.ui.bean.rc.ShowPortListEntity;

public interface PortManagementView extends BaseView {
    void inquirePortList(BaseModel<String> msg);//设备端口列表信息查询①
    void showPortList(ShowPortListEntity msg);//设备端口列表信息展示②
    void portOperations(BaseModel<String> msg,int type);//设备端口操作①
    void getOperationsResult(BaseModel<String> msg);//设备端口查询操作结果②
    void getOperationsStatus(PortStatusEntity msg);//设备端口查询最终状态③
}

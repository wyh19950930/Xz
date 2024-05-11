package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.rc.GeofenceRecordEntity;

public interface SecurityLogView extends BaseView {
    void getGeofenceRecord(GeofenceRecordEntity msg);//安全日志列表
}

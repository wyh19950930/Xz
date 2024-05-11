package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.rc.GeofenceListEntity;

public interface FenceListView extends BaseView {
    void geofenceList(GeofenceListEntity msg);//围栏列表
    void deleteGeofence(BaseModel<String> msg);//删除围栏
}

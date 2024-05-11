package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.rc.GeoFenceCountEntity;

public interface LocationTrackingView extends BaseView {
    void getGeofenceCount(GeoFenceCountEntity msg);//越过围栏总次数
}

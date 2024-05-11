package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.rc.GeoFenceCountEntity;
import com.chuzhi.xzyx.ui.view.LocationTrackingView;

import java.util.HashMap;
import java.util.Map;

public class LocationTrackingPresenter extends BasePresenter<LocationTrackingView> {
    public LocationTrackingPresenter(LocationTrackingView baseView) {
        super(baseView);
    }

    //越过围栏总次数
    public void getGeofenceCount(String sn) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn", sn);
        addDisposable(apiServer.getGeofenceCount(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<GeoFenceCountEntity> body = (BaseModel<GeoFenceCountEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.getGeofenceCount(body.getData());
                    } else {
                        baseView.showError(body.getMsg());
                    }
                }
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }
}

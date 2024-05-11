package com.chuzhi.xzyx.ui.presenter;

import com.amap.api.maps.model.LatLng;
import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.view.FenceSettingView;

import java.util.HashMap;
import java.util.Map;

public class FenceSettingPresenter extends BasePresenter<FenceSettingView> {
    public FenceSettingPresenter(FenceSettingView baseView) {
        super(baseView);
    }

    //添加围栏
    public void addGeofence(String name, String sn, LatLng latLng, String address,
                            String radius,int type,int every) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("name", name);
        fieldMap.put("sn", sn);
        fieldMap.put("center", latLng.longitude+","+latLng.latitude);
        fieldMap.put("address", address);
        fieldMap.put("radius", radius);
        fieldMap.put("type", type);
        fieldMap.put("every", every);
        addDisposable(apiServer.addGeofence(fieldMap), new BaseObserver(baseView,true) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.addGeofence(body);
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
    //修改围栏
    public void alterGeofence(int id,String name, String sn, LatLng latLng,String address,
                              String radius,int type) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("name", name);
        fieldMap.put("sn", sn);
        fieldMap.put("center", latLng.longitude+","+latLng.latitude);
        fieldMap.put("address", address);
        fieldMap.put("radius", radius);
        fieldMap.put("type", type);
        addDisposable(apiServer.alterGeofence(id,fieldMap), new BaseObserver(baseView,true) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.alterGeofence(body);
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

package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.rc.GeofenceListEntity;
import com.chuzhi.xzyx.ui.view.FenceListView;

import java.util.HashMap;
import java.util.Map;

public class FenceListPresenter extends BasePresenter<FenceListView> {
    public FenceListPresenter(FenceListView baseView) {
        super(baseView);
    }

    //围栏信息列表
    public void geofenceList(String sn) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn",sn);
        addDisposable(apiServer.geofenceList(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<GeofenceListEntity> body = (BaseModel<GeofenceListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                        baseView.geofenceList(body.getData());
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
    //删除围栏
    public void deleteGeofence(int id,String sn) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("id",id);
        fieldMap.put("sn",sn);
        addDisposable(apiServer.deleteGeofence(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                            baseView.deleteGeofence(body);
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

package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.rc.GeofenceRecordEntity;
import com.chuzhi.xzyx.ui.view.SecurityLogView;

import java.util.HashMap;
import java.util.Map;

public class SecurityLogPresenter extends BasePresenter<SecurityLogView> {
    public SecurityLogPresenter(SecurityLogView baseView) {
        super(baseView);
    }


    public void getGeofenceRecord(int page,String sn) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn", sn);
        addDisposable(apiServer.getGeofenceRecord(page,fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<GeofenceRecordEntity> body = (BaseModel<GeofenceRecordEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null&&body.getData().getRecord_list()!=null
                                &&body.getData().getRecord_list().size()>0){
                            baseView.getGeofenceRecord(body.getData());
                        }else {
                            baseView.showError(body.getMsg());
                        }
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

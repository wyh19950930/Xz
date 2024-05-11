package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.rc.ComputerInfoEntity;
import com.chuzhi.xzyx.ui.view.HPAddDeviceView;

import java.util.HashMap;
import java.util.Map;

public class HPAddDevicePresenter extends BasePresenter<HPAddDeviceView> {
    public HPAddDevicePresenter(HPAddDeviceView baseView) {
        super(baseView);
    }
    //根据sn号搜索设备
    public void computerInfo(String sn1,String sn2) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn1", sn1);
        fieldMap.put("sn2", sn2);
        addDisposable(apiServer.computerInfo(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<ComputerInfoEntity> body = (BaseModel<ComputerInfoEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                        baseView.computerInfo(body.getData());
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
    //绑定设备
    public void bindComputer(String sn1,String sn2,String computer_name) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn1", sn1);
        fieldMap.put("sn2", sn2);
        fieldMap.put("computer_name", computer_name);
        addDisposable(apiServer.bindComputer(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.bindComputer(body);
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

package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;
import com.chuzhi.xzyx.ui.view.DeviceManageView;

import java.util.HashMap;
import java.util.Map;

public class DeviceManagePresenter extends BasePresenter<DeviceManageView> {
    public DeviceManagePresenter(DeviceManageView baseView) {
        super(baseView);
    }

    //设备信息列表
    public void userComputerList() {
        addDisposable(apiServer.userComputerList(), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<ComputerListEntity> body = (BaseModel<ComputerListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getMsg().contains("未绑定")) {
                            ComputerListEntity entity = new ComputerListEntity();
                            baseView.userComputerList(entity);
                        } else {
                            if (body.getData() != null)
                                baseView.userComputerList(body.getData());
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
    //解绑设备
    public void unbindComputer(String sn) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("id", sn);
        addDisposable(apiServer.unbindComputer(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.unbindComputer(body);
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


    //修改设备名称
    public void alterBindInfo(int id,String computer_name) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("computer_name", computer_name);
        addDisposable(apiServer.alterBindInfo(id,fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.alterBindInfo(body);
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

package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.rc.PortStatusEntity;
import com.chuzhi.xzyx.ui.bean.rc.ShowPortListEntity;
import com.chuzhi.xzyx.ui.view.PortManagementView;

import java.util.HashMap;
import java.util.Map;

public class PortManagementPresenter extends BasePresenter<PortManagementView> {
    public PortManagementPresenter(PortManagementView baseView) {
        super(baseView);
    }

    //设备端口列表信息查询①
    public void inquirePortList(String sn) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn", sn);
        addDisposable(apiServer.inquirePortList(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.inquirePortList(body);
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
    //设备端口列表信息展示②
    public void showPortList(String sn) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn", sn);
        addDisposable(apiServer.showPortList(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<ShowPortListEntity> body = (BaseModel<ShowPortListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                        baseView.showPortList(body.getData());
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
    //设备端口操作①
    public void portOperations(String sn,String operation_type,int type) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn", sn);
        fieldMap.put("operation_type", operation_type);
        addDisposable(apiServer.portOperations(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                            baseView.portOperations(body,type);
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
    //设备端口查询操作结果②
    public void getOperationsResult(String sn,String operation_type) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn", sn);
        fieldMap.put("operation_type", operation_type);
        addDisposable(apiServer.getOperationsResult(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.getOperationsResult(body);
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
    //设备端口查询最终状态③
    public void getOperationsStatus(String sn,String operation_type) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn", sn);
        fieldMap.put("operation_type", operation_type);
        addDisposable(apiServer.getOperationsStatus(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<PortStatusEntity> body = (BaseModel<PortStatusEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                        baseView.getOperationsStatus(body.getData());
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

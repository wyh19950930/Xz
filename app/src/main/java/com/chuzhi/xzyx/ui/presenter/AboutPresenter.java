package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateEntity;
import com.chuzhi.xzyx.ui.view.AboutView;

import java.util.HashMap;
import java.util.Map;

public class AboutPresenter extends BasePresenter<AboutView> {
    public AboutPresenter(AboutView baseView) {
        super(baseView);
    }


    public void versionUpdate(String system_style,String version) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("system_style", system_style);
        fieldMap.put("version", version);
        addDisposable(apiServer.versionUpdate(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<VersionUpdateEntity> body = (BaseModel<VersionUpdateEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null){
                            baseView.versionUpdate(body.getData());
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

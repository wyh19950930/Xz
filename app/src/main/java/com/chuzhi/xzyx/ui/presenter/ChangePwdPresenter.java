package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.view.ChangePwdView;

import java.util.HashMap;
import java.util.Map;

public class ChangePwdPresenter extends BasePresenter<ChangePwdView> {
    public ChangePwdPresenter(ChangePwdView baseView) {
        super(baseView);
    }

    //修改密码
    public void oauthAlterPassword(String old_password,String new_password,String repeat_password) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("old_password", old_password);
        fieldMap.put("new_password", new_password);
        fieldMap.put("repeat_password", repeat_password);
        addDisposable(apiServer.oauthAlterPassword(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.oauthAlterPassword(body);
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

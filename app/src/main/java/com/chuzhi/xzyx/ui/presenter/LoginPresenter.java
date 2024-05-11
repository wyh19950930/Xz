package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity;
import com.chuzhi.xzyx.ui.view.LoginView;

import java.util.HashMap;
import java.util.Map;

public class LoginPresenter extends BasePresenter<LoginView> {
    public LoginPresenter(LoginView baseView) {
        super(baseView);
    }

    //bbs登录
    public void bbsGetToken(String username, String password) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("username", username);
//        fieldMap.put("captcha_code", captcha_code);
        fieldMap.put("password", password);
        fieldMap.put("method", 1213);//密码为1213
        addDisposable(apiServer.bbsGetToken(fieldMap), new BaseObserver(baseView,true) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<BbsUserEntity> body = (BaseModel<BbsUserEntity>) o;
                if (body.getCode() == 200) {
                    if (body.getData() != null)
                        baseView.loginSuccess(body.getData());
                } else {
                    baseView.showError(body.getMsg());
                }
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }
    //远程管控登录
    public void rcGetToken(String username, String password) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("username", username);
//        fieldMap.put("captcha_code", captcha_code);
        fieldMap.put("password", password);
        fieldMap.put("method", 1213);//密码为1213
        addDisposable(apiServer.rcGetToken(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body.getCode() == 200) {
//                    if (body.getData() != null)
                        baseView.loginRcSuccess(body);
                } else {
                    baseView.showError(body.getMsg());
                }
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }
}

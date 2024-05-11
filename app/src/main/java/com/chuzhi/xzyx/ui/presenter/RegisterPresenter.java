package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity;
import com.chuzhi.xzyx.ui.view.RegisterView;

import java.util.HashMap;
import java.util.Map;

public class RegisterPresenter extends BasePresenter<RegisterView> {
    public RegisterPresenter(RegisterView baseView) {
        super(baseView);
    }

    //bbs获取验证码
    public void smsCaptcha(String phone) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("phone", phone);
        addDisposable(apiServer.smsCaptcha(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.codeSuccess(body);
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

    //注册账号
    public void registerUserCode(String phone, String password,String repeat_password,String captcha_code) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("phone", phone);
        fieldMap.put("password", password);
        fieldMap.put("repeat_password", repeat_password);
        fieldMap.put("captcha_code", captcha_code);
        fieldMap.put("register_type", "password_registration");

        addDisposable(apiServer.registerUserCode(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<BbsUserEntity> body = (BaseModel<BbsUserEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.registerUserSuccess(body.getData());
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

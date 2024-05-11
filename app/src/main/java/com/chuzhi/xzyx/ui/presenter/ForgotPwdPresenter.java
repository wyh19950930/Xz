package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.view.ForgotPwdView;

import java.util.HashMap;
import java.util.Map;

public class ForgotPwdPresenter extends BasePresenter<ForgotPwdView> {
    public ForgotPwdPresenter(ForgotPwdView baseView) {
        super(baseView);
    }

    //bbs获取验证码
    public void smsCaptcha(String phone) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("phone", phone);
        fieldMap.put("feature_id", 1);
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

    //找回密码
    public void oauthRetrievePassword(String phone, String new_password,String repeat_password,String captcha_code) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("phone", phone);
        fieldMap.put("new_password", new_password);
        fieldMap.put("repeat_password", repeat_password);
        fieldMap.put("captcha_code", captcha_code);

        addDisposable(apiServer.oauthRetrievePassword(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.oauthRetrievePassword(body);
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

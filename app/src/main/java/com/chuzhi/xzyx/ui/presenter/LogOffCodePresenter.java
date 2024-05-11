package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.view.ForgotPwdView;
import com.chuzhi.xzyx.ui.view.LogOffCodeView;

import java.util.HashMap;
import java.util.Map;

public class LogOffCodePresenter extends BasePresenter<LogOffCodeView> {
    public LogOffCodePresenter(LogOffCodeView baseView) {
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
    public void logOffPhone(String phone,String captcha_code) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("phone", phone);
        fieldMap.put("captcha_code", captcha_code);

        addDisposable(apiServer.logOff(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.logOffPhone(body);
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

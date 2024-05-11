package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.app.MyApplication;
import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity;
import com.chuzhi.xzyx.ui.view.CodeView;
import com.chuzhi.xzyx.utils.RandomUtil;
import com.chuzhi.xzyx.utils.SpUtils;

import java.util.HashMap;
import java.util.Map;

public class CodePresenter extends BasePresenter<CodeView> {
    public CodePresenter(CodeView baseView) {
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

    //bbs验证码注册
    public void registerUserCode(String phone, String captcha_code) {
        String string = RandomUtil.produceStringAndNumber(5);
        int number = RandomUtil.produceNumber();
        SpUtils.setSharedStringData(MyApplication.Companion.getInstance(),"Password",string+number);
        String password = SpUtils.getSharedStringData(MyApplication.Companion.getInstance(), "Password");
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("phone", phone);
        fieldMap.put("captcha_code", captcha_code);
        fieldMap.put("password", password);
        fieldMap.put("repeat_password", password);
        fieldMap.put("register_type", "SMS_registration");

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

    //bbs密码登录
    public void bbsGetToken(String username, String captcha_code) {
        String password = SpUtils.getSharedStringData(MyApplication.Companion.getInstance(), "Password");
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("username", username);
//        fieldMap.put("captcha_code", captcha_code);
        fieldMap.put("password", password);
        fieldMap.put("method", 1213);//密码为1213
        addDisposable(apiServer.bbsGetToken(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<BbsUserEntity> body = (BaseModel<BbsUserEntity>) o;
                if (body.getCode() == 200) {
                    /*Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                    String json = gson.toJson(body.getData());
                    BbsUserEntity bbsUserEntity = gson.fromJson(json, BbsUserEntity.class);*/
                    if (body.getData()!=null)
                    baseView.loginSuccess(body.getData());
                } else {
                    if (body.getMsg().contains("未注册")) {
                        registerUserCode(username, captcha_code);
                    }else {
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

    //短信验证码登录
    public void codeLogin(String phone, String captcha_code) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("username", phone);
        fieldMap.put("captcha_code", captcha_code);
//        fieldMap.put("password", username+"ABC");
        fieldMap.put("method", 1212);//密码为1213
        addDisposable(apiServer.bbsGetToken(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<BbsUserEntity> body = (BaseModel<BbsUserEntity>) o;
                if (body.getCode() == 200) {
//                    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
//                    String json = gson.toJson(body.getData());
//                    BbsUserEntity bbsUserEntity = gson.fromJson(json, BbsUserEntity.class);
                    if (body.getData()!=null)
                    baseView.loginSuccess(body.getData());
                } else {
                    if (body.getMsg().contains("用户不存在")) {
                        registerUserCode(phone, captcha_code);
                    }else {
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

    //用户名密码登录
    public void adminLogin(String name, String pwd) {
        addDisposable(apiServer.LoginByRx(name, pwd), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
//                baseView.loginSuccess((String) o);

            }

            @Override
            public void onError(String msg) {

            }
        });
    }

}

package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.OauthSetUserInfo;
import com.chuzhi.xzyx.ui.bean.bbs.PraiseCountEntity;
import com.chuzhi.xzyx.ui.view.MeFragView;

import java.util.HashMap;
import java.util.Map;

public class MeFragPresenter extends BasePresenter<MeFragView> {
    public MeFragPresenter(MeFragView baseView) {
        super(baseView);
    }

    //退出登录
    public void bbsOutToken(String token) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("token", token);
        addDisposable(apiServer.bbsOutToken(fieldMap), new BaseObserver(baseView,true) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.bbsOutToken(body);
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
    //获赞/点赞/粉丝数统计
    public void praiseCount() {
        addDisposable(apiServer.praiseCount(), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<PraiseCountEntity> body = (BaseModel<PraiseCountEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                        baseView.praiseCount(body.getData());
                    } else {
                        if (!body.getMsg().equals("账号已在其他设备登录，请重新登录")&&
                                !body.getMsg().equals("你还未登录/登录超时!")){
                            baseView.showError(body.getMsg());
                        }
                    }
                }
            }

            @Override
            public void onError(String msg) {
                if (!msg.equals("账号已在其他设备登录，请重新登录")&&
                        !msg.equals("你还未登录/登录超时!")){
                    baseView.showError(msg);
                }
            }
        });
    }
    //查看个人信息
    public void oauthSetUserInfo() {
        addDisposable(apiServer.oauthSetUserInfo(), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<OauthSetUserInfo> body = (BaseModel<OauthSetUserInfo>) o;
//                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
//                String json = gson.toJson(body.getData());
//                BbsUserEntity bbsUserEntity = gson.fromJson(json, BbsUserEntity.class);
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                            baseView.oauthSetUserInfo(body.getData());
                    } else {
                        baseView.showError(body.getMsg());
                    }
                }
            }

            @Override
            public void onError(String msg) {
                if (!msg.equals("账号已在其他设备登录，请重新登录")&&
                        !msg.equals("你还未登录/登录超时!")){
                    baseView.showError(msg);
                }
            }
        });
    }
}

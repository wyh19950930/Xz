package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.BbsUserEntity;
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;
import com.chuzhi.xzyx.ui.view.LoginView;
import com.chuzhi.xzyx.ui.view.WelcomeView;

import java.util.HashMap;
import java.util.Map;

public class WelcomePresenter extends BasePresenter<WelcomeView> {
    public WelcomePresenter(WelcomeView baseView) {
        super(baseView);
    }

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
                        if (!body.getMsg().equals("账号已在其他设备登录，请重新登录")&&
                                !body.getMsg().equals("你还未登录/登录超时!")){
                            baseView.showError(body.getMsg());
                        }
                    }
                }
            }

            @Override
            public void onError(String msg) {
                if (!msg.equals("账号已在其他设备登录，请重新登录")){
                    baseView.showError(msg);
                }
            }
        });
    }
}

package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.AnswerListEntity;
import com.chuzhi.xzyx.ui.view.MyReplyView;

public class MyReplyPresenter extends BasePresenter<MyReplyView> {
    public MyReplyPresenter(MyReplyView baseView) {
        super(baseView);
    }

    //我的回复列表
    public void answerList(int page) {
        addDisposable(apiServer.answerList(page), new BaseObserver(baseView,true) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<AnswerListEntity> body = (BaseModel<AnswerListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null&&body.getData().getAnswer_list()!=null&&
                                body.getData().getAnswer_list().size()>0){
                            baseView.answerList(body.getData());
                        }else {
                            baseView.showError(body.getMsg());
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

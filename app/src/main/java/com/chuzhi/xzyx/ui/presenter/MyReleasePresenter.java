package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.UserArticleListEntity;
import com.chuzhi.xzyx.ui.view.MyReleaseView;

import java.util.HashMap;
import java.util.Map;

public class MyReleasePresenter extends BasePresenter<MyReleaseView> {
    public MyReleasePresenter(MyReleaseView baseView) {
        super(baseView);
    }

    //我的发布文章列表
    public void userArticleList(int page) {
        addDisposable(apiServer.userArticleList(page), new BaseObserver(baseView,true) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<UserArticleListEntity> body = (BaseModel<UserArticleListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null&&body.getData().getArticle_list()!=null
                                &&body.getData().getArticle_list().size()>0){
                            baseView.userArticleList(body.getData());
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
    //文章点赞
    public void articlePraise(int article_id) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("article_id", article_id);
        addDisposable(apiServer.articlePraise(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.articlePraise(body);
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

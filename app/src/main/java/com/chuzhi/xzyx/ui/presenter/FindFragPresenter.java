package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleCategoryEntity1;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.CarouselArticleEntity;
import com.chuzhi.xzyx.ui.view.FindFragView;

import java.util.HashMap;
import java.util.Map;

public class FindFragPresenter extends BasePresenter<FindFragView> {
    public FindFragPresenter(FindFragView baseView) {
        super(baseView);
    }

    //轮播图
    public void carouselArticle(){
        addDisposable(apiServer.CarouselArticle(), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<CarouselArticleEntity> body = (BaseModel<CarouselArticleEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                            baseView.carouselArticle(body.getData());
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

    //文章分类tab
    public void articleCategoryList() {
        addDisposable(apiServer.articleCategoryList(), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<ArticleCategoryEntity1> body = (BaseModel< ArticleCategoryEntity1>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                        baseView.articleCategoryList(body.getData());
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

    //文章列表
    public void articleList(int page,String article_category) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("article_category", article_category);
        addDisposable(apiServer.articleList(page,fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<ArticleListEntity> body = (BaseModel<ArticleListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null/*&&body.getData().getArticle_list().size()>0*/){
                            baseView.articleList(body.getData());
                        }/*else {
                            baseView.showError(body.getMsg());
                        }*/
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
    //关注用户
    public void addFollow(int article_id) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("article_id", article_id);
        addDisposable(apiServer.addFollow(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                            baseView.articleFollower(body);
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
    //取消关注
    public void deleteFollow(int article_id) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("article_id", article_id);
        addDisposable(apiServer.deleteFollow(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.articleFollower(body);
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

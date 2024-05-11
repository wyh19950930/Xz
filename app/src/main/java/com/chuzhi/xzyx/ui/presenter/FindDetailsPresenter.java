package com.chuzhi.xzyx.ui.presenter;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleAnswersEntity;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleDetailsEntity;
import com.chuzhi.xzyx.ui.view.FindDetailsView;

import java.util.HashMap;
import java.util.Map;

public class FindDetailsPresenter extends BasePresenter<FindDetailsView> {
    public FindDetailsPresenter(FindDetailsView baseView) {
        super(baseView);
    }

    //文章详情
    public void articleDetails(String article_id) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("article_id", article_id);
        addDisposable(apiServer.articleDetails(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<ArticleDetailsEntity> body = (BaseModel<ArticleDetailsEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body!=null){
//                            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
//                            String json = gson.toJson(body.getData());
//                            ArticleDetailsEntity entity = gson.fromJson(json, ArticleDetailsEntity.class);
                            if (body.getData()!=null)
                            baseView.articleDetails(body.getData());
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
    //文章详情评论列表
    public void articleAnswers(int page,String article_id) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("article_id", article_id);
        addDisposable(apiServer.articleAnswers(page,fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<ArticleAnswersEntity> body = (BaseModel<ArticleAnswersEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null&&body.getData().getAnswer_list()!=null
                                &&body.getData().getAnswer_list().size()>0){
                            baseView.articleAnswers(body.getData());
                        }else {
//                            baseView.showError(body.getMsg());
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
    //评论点赞
    public void answerPraise(int article_id) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("answer_id", article_id);
        addDisposable(apiServer.answerPraise(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.answerPraise(body);
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
    //发送评论
    public void addAnswer(int article_id,String content) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("article_id", article_id);
        fieldMap.put("content", content);
        fieldMap.put("sourced", 1);
        addDisposable(apiServer.addAnswer(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.addAnswer(body);
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

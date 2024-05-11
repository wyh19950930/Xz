package com.chuzhi.xzyx.ui.presenter;

import android.util.Log;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleCategoryEntity1;
import com.chuzhi.xzyx.ui.bean.bbs.UploadFileImgEntity;
import com.chuzhi.xzyx.ui.view.ReleaseMessageView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;

public class ReleaseMessagePresenter extends BasePresenter<ReleaseMessageView> {
    public ReleaseMessagePresenter(ReleaseMessageView baseView) {
        super(baseView);
    }

    //上传图片
    public void uploadImg(File file, MultipartBody.Part part){
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("username", file.getName());
        addDisposable(apiServer.uploadImg(fieldMap,part), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<UploadFileImgEntity> body = (BaseModel<UploadFileImgEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                        baseView.uploadImg(body.getData());
                    } else {
                        baseView.showError(body.getMsg());
                    }
                }
            }

            @Override
            public void onNext(Object o) {
                super.onNext(o);
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
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
    //发布文章
    public void publishArticle(String title,String contents,List<String> image_name,int article_category) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("title", title);
        fieldMap.put("contents", contents);
        if (image_name.size()>0){
            fieldMap.put("image_name", image_name);
        }
        fieldMap.put("article_category", article_category);
        fieldMap.put("sourced", 1);
        Log.e("image_name type", String.valueOf(image_name instanceof List));
        addDisposable(apiServer.publishArticle(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        baseView.publishArticle(body);
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

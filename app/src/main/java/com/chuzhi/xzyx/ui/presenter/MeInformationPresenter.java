package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.bbs.CityListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.OauthSetUserInfo;
import com.chuzhi.xzyx.ui.bean.bbs.ProvinceListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.UploadFileImgEntity;
import com.chuzhi.xzyx.ui.view.MeInformationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;

public class MeInformationPresenter extends BasePresenter<MeInformationView> {
    public MeInformationPresenter(MeInformationView baseView) {
        super(baseView);
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
                baseView.showError(msg);
            }
        });
    }
    //修改个人信息
    public void updateUserInfo(String nickname,int gender,String birthday,int city_id) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("nickname", nickname);
        fieldMap.put("gender", gender);
        fieldMap.put("birthday", birthday);
        fieldMap.put("city_id", city_id);
        addDisposable(apiServer.updateUserInfo(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<String> body = (BaseModel<String>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                            baseView.updateUserInfo(body);
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
    //修改头像
    public void uploadAvatar(List<MultipartBody.Part> part){

         addDisposable(apiServer.updateAvatar(part), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<UploadFileImgEntity> body = (BaseModel<UploadFileImgEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                            baseView.uploadAvatar(body.getData());
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
    //省份列表
    public void provinceList() {
        addDisposable(apiServer.provinceList(), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<ProvinceListEntity> body = (BaseModel<ProvinceListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                            baseView.provinceList(body.getData());
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
    //城市列表
    public void cityList(int province_id) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("province_id", province_id);
        addDisposable(apiServer.cityList(fieldMap), new BaseObserver(baseView) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<CityListEntity> body = (BaseModel<CityListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null)
                            baseView.cityList(body.getData());
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

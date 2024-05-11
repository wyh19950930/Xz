package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.CityListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.OauthSetUserInfo;
import com.chuzhi.xzyx.ui.bean.bbs.ProvinceListEntity;
import com.chuzhi.xzyx.ui.bean.bbs.UploadFileImgEntity;

public interface MeInformationView extends BaseView {
    void oauthSetUserInfo(OauthSetUserInfo msg);//查看个人信息
    void updateUserInfo(BaseModel<String> msg);//修改个人信息
    void uploadAvatar(UploadFileImgEntity msg);//上传头像
    void provinceList(ProvinceListEntity msg);//省份列表
    void cityList(CityListEntity msg);//城市列表
}

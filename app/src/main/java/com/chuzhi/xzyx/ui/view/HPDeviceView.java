package com.chuzhi.xzyx.ui.view;

import android.graphics.Bitmap;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateEntity;
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateNewEntity;
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;
import com.chuzhi.xzyx.ui.bean.rc.DynamicPasswordEntity;

public interface HPDeviceView extends BaseView {
    void userComputerList(ComputerListEntity msg);
    void showHomeInformation(ComputerListEntity msg);//设备信息展示
    void getTotp(DynamicPasswordEntity msg,int position);//获取动态口令
    void updateTrack(BaseModel<String> msg);//更新轨迹
    void versionUpdate(VersionUpdateNewEntity msg);//检查更新
    void getMapImage(Bitmap msg);
}

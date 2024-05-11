package com.chuzhi.xzyx.ui.presenter;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseObserver;
import com.chuzhi.xzyx.base.BasePresenter;
import com.chuzhi.xzyx.ui.bean.rc.TrackInfoListEntity;
import com.chuzhi.xzyx.ui.view.TrackListView;

import java.util.HashMap;
import java.util.Map;

public class TrackListPresenter extends BasePresenter<TrackListView> {
    public TrackListPresenter(TrackListView baseView) {
        super(baseView);
    }

    //历史轨迹
    public void trackInfo(int page,String sn) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("sn", sn);
        addDisposable(apiServer.trackInfo(page,fieldMap), new BaseObserver(baseView,true) {
            @Override
            public void onSuccess(Object o) {
                BaseModel<TrackInfoListEntity> body = (BaseModel<TrackInfoListEntity>) o;
                if (body != null) {
                    if (body.getCode() == 200) {
                        if (body.getData()!=null&&body.getData().getTrack_dict()!=null
                                &&body.getData().getTrack_dict().size()>0){
                            baseView.trackInfo(body.getData());
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

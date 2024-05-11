package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.rc.TrackInfoListEntity;

public interface TrackListView extends BaseView {
    void trackInfo(TrackInfoListEntity msg);//历史轨迹列表
}

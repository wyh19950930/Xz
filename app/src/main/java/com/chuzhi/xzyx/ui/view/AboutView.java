package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.VersionUpdateEntity;

public interface AboutView extends BaseView {
    void versionUpdate(VersionUpdateEntity msg);//检查更新
}

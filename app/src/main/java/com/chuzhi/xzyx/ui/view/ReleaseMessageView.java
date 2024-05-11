package com.chuzhi.xzyx.ui.view;

import com.chuzhi.xzyx.base.BaseModel;
import com.chuzhi.xzyx.base.BaseView;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleCategoryEntity1;
import com.chuzhi.xzyx.ui.bean.bbs.UploadFileImgEntity;

public interface ReleaseMessageView extends BaseView {
    void articleCategoryList(ArticleCategoryEntity1 msg);//文章分类tab
    void uploadImg(UploadFileImgEntity msg);//上传图片
    void publishArticle(BaseModel<String> msg);//发布文章
}

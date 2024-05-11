package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.ui.bean.bbs.CarouselArticleEntity;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;
import com.zhpan.bannerview.utils.BannerUtils;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/6/2 10:33
 * @Description : FindBannerVPAdapter
 */
public class FindBannerVPAdapter extends BaseBannerAdapter<CarouselArticleEntity.ArticleListDTO> {
    private Context mContext;
    private List<CarouselArticleEntity.ArticleListDTO> list;


    public FindBannerVPAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void bindData(BaseViewHolder<CarouselArticleEntity.ArticleListDTO> holder, CarouselArticleEntity.ArticleListDTO data, int position, int pageSize) {
        ImageView banner_img = holder.findViewById(R.id.iv_item_find_banner_img);
//        int sourced = data.getSourced();
//        if (sourced == 1){
//            Glide.with(mContext).load(data.getImage_name()).placeholder(R.drawable.banner_zwt_img)
//                    .error(R.drawable.banner_zwt_img)
//                    .fallback(R.drawable.banner_zwt_img)
//                    .into(banner_img);
//        }else {
            Glide.with(mContext).load(data.getArticle_img()).placeholder(R.drawable.banner_zwt_img)
                    .error(R.drawable.banner_zwt_img)
                    .fallback(R.drawable.banner_zwt_img)
                    .into(banner_img);
//        }
        int adapterPosition = holder.getAdapterPosition();
        int realPosition = BannerUtils.getRealPosition(adapterPosition, mList.size());
        banner_img.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, realPosition);
            }
        });


    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_find_banner_img;
    }

    //第一步：自定义一个回调接口来实现Click和LongClick事件
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public OnItemClickListener mOnItemClickListener;//第二步：声明自定义的接口

    //第三步：定义方法并暴露给外面的调用者
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


}

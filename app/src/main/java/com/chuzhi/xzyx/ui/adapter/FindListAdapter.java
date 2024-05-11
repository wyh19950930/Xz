package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.databinding.ItemFindListBinding;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleListEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 发现列表
 */
public class FindListAdapter extends RecyclerView.Adapter<FindListAdapter.FindListViewHolder> {

    private Context mContext;
    private List<ArticleListEntity.ArticleListDTO> list;
    private OnClickFindListListener findListOnClickListener;


    public void setData(List<ArticleListEntity.ArticleListDTO> data) {
        this.list = data;
    }

    public FindListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public FindListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemFindListBinding binding = ItemFindListBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new FindListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FindListViewHolder holder, int position) {
        ArticleListEntity.ArticleListDTO data = list.get(position);
        //大图
        Glide.with(mContext).load(data.getImg())
                    .placeholder(R.drawable.find_zwt_img)
                    .error(R.drawable.find_zwt_img)
                    .into(holder.binding.ivItemFindFrag);

        //头像
        Glide.with(mContext).load(data.getAvatar())
                .placeholder(R.drawable.my_img_portrait_default)
                .error(R.drawable.my_img_portrait_default)
                .fallback(R.drawable.my_img_portrait_default)
                .transform(new CenterCrop(),
                        new CircleCrop())
                .into(holder.binding.ivItemFindHead);
        holder.binding.tvItemFindName.setText(data.getAuthor_name());
        holder.binding.tvItemFindTitle.setText(data.getTitle());
        holder.binding.tvItemFindArticlePraiseCount.setText(data.getArticle_praise_count() + "");
        holder.binding.tvItemFindAnswersCount.setText(data.getAnswers_count() + "");
        holder.binding.tvItemFindReadCount.setText(data.getRead_count() + "");
        if (data.isFollowed()) {
            holder.binding.btnItemFindFollower.setText("已关注");
            holder.binding.btnItemFindFollower.setTextColor(Color.parseColor("#999999"));
            holder.binding.btnItemFindFollower.setBackgroundResource(R.drawable.btn_eeeeee_round_30_background);
        } else {
            holder.binding.btnItemFindFollower.setText("关注");
            holder.binding.btnItemFindFollower.setTextColor(Color.parseColor("#1a2537"));
            holder.binding.btnItemFindFollower.setBackgroundResource(R.drawable.btn_default_ring_30_background);
        }
        Drawable drawableLeft;
        if (!data.isPraise_status()){
            drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_default);
        }else {
            drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_highlight);
        }
        drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
        holder.binding.tvItemFindArticlePraiseCount.setCompoundDrawables(drawableLeft,null,null,null);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findListOnClickListener.onClickListener(data);
//                Log.e("FindListAdapter===>",position+"");
            }
        });
        //关注取消关注
        holder.binding.btnItemFindFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findListOnClickListener.onClickFollowerListener(data, position, holder.binding.btnItemFindFollower);
            }
        });
        //点赞取消点赞
        holder.binding.tvItemFindArticlePraiseCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findListOnClickListener.onClickLikeListener(data, position);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FindListViewHolder holder, int position, List<Object> payloads) {
        ArticleListEntity.ArticleListDTO data = list.get(position);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            String type = (String) payloads.get(0);
            if (type.equals("follower")) {
                String isFollower = holder.binding.btnItemFindFollower.getText().toString();
                if (isFollower.equals("关注")) {
                    holder.binding.btnItemFindFollower.setText("已关注");
                    holder.binding.btnItemFindFollower.setTextColor(Color.parseColor("#999999"));
                    holder.binding.btnItemFindFollower.setBackgroundResource(R.drawable.btn_eeeeee_round_30_background);
                } else {
                    holder.binding.btnItemFindFollower.setText("关注");
                    holder.binding.btnItemFindFollower.setTextColor(Color.parseColor("#1a2537"));
                    holder.binding.btnItemFindFollower.setBackgroundResource(R.drawable.btn_default_ring_30_background);
                }
            }else if (type.contains("like")){
                if (type.contains("取消")){
                    Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_default);
                    drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                    holder.binding.tvItemFindArticlePraiseCount.setCompoundDrawables(drawableLeft,null,null,null);
                    holder.binding.tvItemFindArticlePraiseCount.setText(
                            Integer.parseInt(holder.binding.tvItemFindArticlePraiseCount.getText().toString())-1 + "");
                }else {
                    Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_highlight);
                    drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                    holder.binding.tvItemFindArticlePraiseCount.setCompoundDrawables(drawableLeft,null,null,null);
                    holder.binding.tvItemFindArticlePraiseCount.setText(
                            Integer.parseInt(holder.binding.tvItemFindArticlePraiseCount.getText().toString())+1 + "");
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class FindListViewHolder extends RecyclerView.ViewHolder {
        ItemFindListBinding binding;

        public FindListViewHolder(@NonNull ItemFindListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public void setOnClickFindListListener(OnClickFindListListener findListOnClickListeners) {
        this.findListOnClickListener = findListOnClickListeners;
    }

    public interface OnClickFindListListener {
        void onClickListener(ArticleListEntity.ArticleListDTO data);

        void onClickFollowerListener(ArticleListEntity.ArticleListDTO data, int position, Button view);

        void onClickLikeListener(ArticleListEntity.ArticleListDTO data, int position);

    }
}

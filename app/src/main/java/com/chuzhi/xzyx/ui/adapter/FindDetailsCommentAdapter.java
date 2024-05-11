package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.databinding.ItemFindDetailsCommentBinding;
import com.chuzhi.xzyx.ui.bean.bbs.ArticleAnswersEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 文章评论列表
 */
public class FindDetailsCommentAdapter extends RecyclerView.Adapter<FindDetailsCommentAdapter.FindDetailsCommentViewHolder> {

    private Context mContext;
    private List<ArticleAnswersEntity.AnswerListDTO> list;
    private OnClickFindDetailsCommentListener findDetailsCommentOnClickListener;


    public void setData(List<ArticleAnswersEntity.AnswerListDTO> data){
        this.list = data;
    }

    public FindDetailsCommentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public FindDetailsCommentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemFindDetailsCommentBinding binding = ItemFindDetailsCommentBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new FindDetailsCommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FindDetailsCommentViewHolder holder, int position) {
        ArticleAnswersEntity.AnswerListDTO data = list.get(position);
        //头像
        Glide.with(mContext).load(data.getAvatar())
                .placeholder(R.drawable.my_img_portrait_default)
                .error(R.drawable.my_img_portrait_default)
                .fallback(R.drawable.my_img_portrait_default)
                .transform(new CenterCrop(),
                        new CircleCrop())
                .into(holder.binding.ivItemFindDetailsCommentHead);

        holder.binding.tvItemFindDetailsCommentName.setText(data.getUser_name());

        holder.binding.tvItemFindDetailsCommentTime.setText(data.getPush_time());
        if (data.getSourced()==0){
            holder.binding.tvItemFindDetailsCommentMsg.setText(data.getContent_text());
        }else {
            holder.binding.tvItemFindDetailsCommentMsg.setText(data.getContent());
        }
        holder.binding.ivItemFindDetailsCommentLike.setText(data.getPraise_count()+"");
        Drawable drawableLeft;
        if (!data.isPraise_status()){
            drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_default);
        }else {
            drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_highlight);
        }
        drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
        holder.binding.ivItemFindDetailsCommentLike.setCompoundDrawables(drawableLeft,null,null,null);
        holder.binding.ivItemFindDetailsCommentLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDetailsCommentOnClickListener.onClickLikeListener(data,position);
            }
        });

    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull FindDetailsCommentViewHolder holder, int position,List<Object> payloads) {
        ArticleAnswersEntity.AnswerListDTO data = list.get(position);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            String type = (String) payloads.get(0);
            if (type.contains("like")){
                if (type.contains("取消")){
                    Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_default);
                    drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                    holder.binding.ivItemFindDetailsCommentLike.setCompoundDrawables(drawableLeft,null,null,null);
                    holder.binding.ivItemFindDetailsCommentLike.setText(
                            Integer.parseInt(holder.binding.ivItemFindDetailsCommentLike.getText().toString())-1 + "");
                }else {
                    Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_highlight);
                    drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                    holder.binding.ivItemFindDetailsCommentLike.setCompoundDrawables(drawableLeft,null,null,null);
                    holder.binding.ivItemFindDetailsCommentLike.setText(
                            Integer.parseInt(holder.binding.ivItemFindDetailsCommentLike.getText().toString())+1 + "");
                }
            }
        }

    }
    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class FindDetailsCommentViewHolder extends RecyclerView.ViewHolder {
        ItemFindDetailsCommentBinding binding;

        public FindDetailsCommentViewHolder(@NonNull ItemFindDetailsCommentBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
    public void setOnClickFindDetailsCommentListener(OnClickFindDetailsCommentListener onClickFindDetailsCommentListener){
        this.findDetailsCommentOnClickListener = onClickFindDetailsCommentListener;
    }

    public interface OnClickFindDetailsCommentListener{
        void onClickLikeListener(ArticleAnswersEntity.AnswerListDTO data,int position);
    }
}

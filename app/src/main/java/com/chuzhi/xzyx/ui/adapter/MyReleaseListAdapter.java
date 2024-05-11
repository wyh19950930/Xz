package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.databinding.ItemMyReleaseListBinding;
import com.chuzhi.xzyx.ui.bean.bbs.UserArticleListEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 发现列表
 */
public class MyReleaseListAdapter extends RecyclerView.Adapter<MyReleaseListAdapter.FindListViewHolder> {

    private Context mContext;
    private List<UserArticleListEntity.ArticleListDTO> list;
    private OnClickFindListListener findListOnClickListener;


    public void setData(List<UserArticleListEntity.ArticleListDTO> data) {
        this.list = data;
    }

    public MyReleaseListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public FindListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMyReleaseListBinding binding = ItemMyReleaseListBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new FindListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FindListViewHolder holder, int position) {
        UserArticleListEntity.ArticleListDTO data = list.get(position);
        holder.binding.tvItemMyReleaseTitle.setText(data.getTitle());
        holder.binding.tvItemMyReleaseTime.setText(data.getCreate_time());
        holder.binding.tvItemMyReleaseContent.setText(data.getContents());
        holder.binding.tvItemMyReleasePraiseCount.setText(data.getPraise_count() + "");
        holder.binding.tvItemMyReleaseAnswersCount.setText(data.getAnswer_number() + "");
        holder.binding.tvItemMyReleaseReadCount.setText(data.getRead_count() + "");
        //0待审核1已审核2不通过
        if (data.getAudit_status() == 0){
            holder.binding.ivItemMyReleaseAudit.setVisibility(View.VISIBLE);
            holder.binding.ivItemMyReleaseAudit.setBackgroundResource(R.mipmap.my_release_dsh);
        }else if (data.getAudit_status() == 1){
            holder.binding.ivItemMyReleaseAudit.setVisibility(View.GONE);
        }else {
            holder.binding.ivItemMyReleaseAudit.setVisibility(View.VISIBLE);
            holder.binding.ivItemMyReleaseAudit.setBackgroundResource(R.mipmap.my_release_wtg);
        }
        Drawable drawableLeft;
        if (!data.isPraise_status()){
            drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_default);
        }else {
            drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_highlight);
        }
        drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
        holder.binding.tvItemMyReleasePraiseCount.setCompoundDrawables(drawableLeft,null,null,null);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findListOnClickListener.onClickListener(data);
//                Log.e("FindListAdapter===>",position+"");
            }
        });

        //点赞取消点赞
        holder.binding.tvItemMyReleasePraiseCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findListOnClickListener.onClickLikeListener(data, position);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FindListViewHolder holder, int position, List<Object> payloads) {
        UserArticleListEntity.ArticleListDTO data = list.get(position);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            String type = (String) payloads.get(0);
            if (type.contains("like")){
                if (type.contains("取消")){
                    Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_default);
                    drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                    holder.binding.tvItemMyReleasePraiseCount.setCompoundDrawables(drawableLeft,null,null,null);
                    holder.binding.tvItemMyReleasePraiseCount.setText(
                            Integer.parseInt(holder.binding.tvItemMyReleasePraiseCount.getText().toString())-1 + "");
                }else {
                    Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.detail_icon_praise_highlight);
                    drawableLeft.setBounds(0,0,drawableLeft.getMinimumWidth(),drawableLeft.getMinimumHeight());
                    holder.binding.tvItemMyReleasePraiseCount.setCompoundDrawables(drawableLeft,null,null,null);
                    holder.binding.tvItemMyReleasePraiseCount.setText(
                            Integer.parseInt(holder.binding.tvItemMyReleasePraiseCount.getText().toString())+1 + "");
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class FindListViewHolder extends RecyclerView.ViewHolder {
        ItemMyReleaseListBinding binding;

        public FindListViewHolder(@NonNull ItemMyReleaseListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public void setOnClickFindListListener(OnClickFindListListener findListOnClickListeners) {
        this.findListOnClickListener = findListOnClickListeners;
    }

    public interface OnClickFindListListener {
        void onClickListener(UserArticleListEntity.ArticleListDTO data);

        void onClickLikeListener(UserArticleListEntity.ArticleListDTO data, int position);

    }
}

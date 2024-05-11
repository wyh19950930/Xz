package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chuzhi.xzyx.databinding.ItemMyReplyListBinding;
import com.chuzhi.xzyx.ui.bean.bbs.AnswerListEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 设备管理列表
 */
public class MyReplyAdapter extends RecyclerView.Adapter<MyReplyAdapter.MyReplyViewHolder> {

    private Context mContext;
    private List<AnswerListEntity.AnswerListDTO> list;
    private OnClickListener mOnClickListener;


    public void setData(List<AnswerListEntity.AnswerListDTO> data){
        this.list = data;
    }

    public MyReplyAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public MyReplyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMyReplyListBinding binding = ItemMyReplyListBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new MyReplyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyReplyViewHolder holder, int position) {
        AnswerListEntity.AnswerListDTO data = list.get(position);
        holder.binding.tvItemMyReplyTitle.setText(data.getTitle());
        holder.binding.tvItemMyReplyTime.setText(data.getPush_time());
        if (data.getSourced()==0){
            holder.binding.tvItemMyReplyContent.setText(data.getContent_text());
        }else {
            holder.binding.tvItemMyReplyContent.setText(data.getContent());
        }

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class MyReplyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ItemMyReplyListBinding binding;

        public MyReplyViewHolder(@NonNull ItemMyReplyListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener((View.OnClickListener) this);
            binding.llItemFence.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onClickListener(v,getAdapterPosition());
        }
    }
    public void setOnClickListener(OnClickListener onClickListener){
        this.mOnClickListener = onClickListener;
    }

    public interface OnClickListener{
        void onClickListener(View view,int position);
    }
}

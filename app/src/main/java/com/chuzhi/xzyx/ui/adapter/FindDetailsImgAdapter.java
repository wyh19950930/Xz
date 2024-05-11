package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuzhi.xzyx.R;
import com.chuzhi.xzyx.databinding.ItemFindDetailsImgBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 文章详情图片列表
 */
public class FindDetailsImgAdapter extends RecyclerView.Adapter<FindDetailsImgAdapter.FindDetailsImgViewHolder> {

    private Context mContext;
    private List<String> list;
    private OnClickFindDetailsImgListener findDetailsImgOnClickListener;


    public void setData(List<String> data){
        this.list = data;
    }

    public FindDetailsImgAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public FindDetailsImgViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemFindDetailsImgBinding binding = ItemFindDetailsImgBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new FindDetailsImgViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FindDetailsImgViewHolder holder, int position) {
        String data = list.get(position);
        String trim = data.trim();
        //大图
        Glide.with(mContext).load(trim)
                .placeholder(R.drawable.banner_zwt_img)
                .error(R.drawable.banner_zwt_img)
                .into(holder.binding.ivItemFindDetailsImg);

        holder.binding.ivItemFindDetailsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDetailsImgOnClickListener.onClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class FindDetailsImgViewHolder extends RecyclerView.ViewHolder {
        ItemFindDetailsImgBinding binding;

        public FindDetailsImgViewHolder(@NonNull ItemFindDetailsImgBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
    public void setOnClickFindDetailsImgListener(OnClickFindDetailsImgListener onClickFindDetailsImgListener){
        this.findDetailsImgOnClickListener = onClickFindDetailsImgListener;
    }

    public interface OnClickFindDetailsImgListener{
        void onClickListener(int position);
    }
}

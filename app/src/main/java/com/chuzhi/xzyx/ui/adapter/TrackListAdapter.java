package com.chuzhi.xzyx.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chuzhi.xzyx.databinding.ItemTrackListBinding;
import com.chuzhi.xzyx.ui.bean.rc.TrackInfoListEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 设备管理列表
 */
public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder> {

    private Context mContext;
    private List<TrackInfoListEntity.TrackDictDTO> list;
    private OnClickListener mOnClickListener;


    public void setData(List<TrackInfoListEntity.TrackDictDTO> data){
        this.list = data;
    }

    public TrackListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public TrackListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemTrackListBinding binding = ItemTrackListBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new TrackListViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull TrackListViewHolder holder, int position) {
        TrackInfoListEntity.TrackDictDTO data = list.get(position);
        holder.binding.tvItemTrackStartTime.setText("开始时间："+data.getStart_time());
        holder.binding.tvItemTrackEndTime.setText("结束时间："+data.getEnd_time());
        if (data.getDistance().equals("0")){
            holder.binding.tvItemTrackDistance.setText("设备未被移动");
        }else {
            holder.binding.tvItemTrackDistance.setText("共计："+data.getDistance()+"公里");
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class TrackListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ItemTrackListBinding binding;

        public TrackListViewHolder(@NonNull ItemTrackListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener((View.OnClickListener) this);
            binding.llItemTrack.setOnClickListener(this::onClick);
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

package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chuzhi.xzyx.databinding.ItemSecurityLogListBinding;
import com.chuzhi.xzyx.ui.bean.rc.GeofenceRecordEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 安全日志列表
 */
public class SecurityLogListAdapter extends RecyclerView.Adapter<SecurityLogListAdapter.SecurityLogViewHolder> {

    private Context mContext;
    private List<GeofenceRecordEntity.RecordListDTO> list;
    private OnClickListener mOnClickListener;


    public void setData(List<GeofenceRecordEntity.RecordListDTO> data){
        this.list = data;
    }

    public SecurityLogListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public SecurityLogViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemSecurityLogListBinding binding = ItemSecurityLogListBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new SecurityLogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SecurityLogViewHolder holder, int position) {
        GeofenceRecordEntity.RecordListDTO data = list.get(position);
        holder.binding.tvItemSecurityLogName.setText(data.getGeofence_name());
        holder.binding.tvItemSecurityLogType.setText(data.getRecord_status());
        holder.binding.tvItemSecurityLogAddress.setText("围栏地址："+data.getAddress());
        holder.binding.tvItemSecurityLogTime.setText("设置时间："+data.getCreate_time());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class SecurityLogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ItemSecurityLogListBinding binding;

        public SecurityLogViewHolder(@NonNull ItemSecurityLogListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener((View.OnClickListener) this);
            binding.llItemSecurityLog.setOnClickListener(this::onClick);
            binding.llItemSecurityLog.setOnLongClickListener(this::onLongClick);
        }

        @Override
        public void onClick(View v) {
//            mOnClickListener.onClickListener(v,getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
//            mOnClickListener.onLongClickListener(v,getAdapterPosition());
            return true;
        }
    }
    public void setOnClickListener(OnClickListener onClickListener){
        this.mOnClickListener = onClickListener;
    }

    public interface OnClickListener{
        void onClickListener(View view,int position);
        void onLongClickListener(View view,int position);
    }
}

package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chuzhi.xzyx.databinding.ItemFenceListBinding;
import com.chuzhi.xzyx.ui.bean.rc.GeofenceListEntity;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 设备管理列表
 */
public class FenceListAdapter extends RecyclerView.Adapter<FenceListAdapter.DeviceManageViewHolder> {

    private Context mContext;
    private List<GeofenceListEntity.GeofenceListDTO> list;
    private OnClickListener mOnClickListener;


    public void setData(List<GeofenceListEntity.GeofenceListDTO> data){
        this.list = data;
    }

    public FenceListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public DeviceManageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemFenceListBinding binding = ItemFenceListBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new DeviceManageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DeviceManageViewHolder holder, int position) {
        GeofenceListEntity.GeofenceListDTO data = list.get(position);
        holder.binding.tvItemFenceName.setText(data.getName());
        holder.binding.tvItemFenceRadius.setText("围栏范围"+data.getRadius()+"m");
        holder.binding.tvItemFenceAddress.setText("围栏地址："+data.getAddress());
        holder.binding.tvItemFenceTime.setText("设置时间："+data.getCreate_time());
        holder.binding.tvItemFenceType.setText("围栏类型："+data.getType());
        holder.binding.tvItemFenceDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onDetClickListener(position,holder.binding.swtItemDeviceManage);
            }
        });
        holder.binding.llItemFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClickListener(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class DeviceManageViewHolder extends RecyclerView.ViewHolder  {
        ItemFenceListBinding binding;

        public DeviceManageViewHolder(@NonNull ItemFenceListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }


    }
    public void setOnClickListener(OnClickListener onClickListener){
        this.mOnClickListener = onClickListener;
    }

    public interface OnClickListener{
        void onClickListener(int position);
        void onDetClickListener(int position,SwipeMenuLayout swipeMenuLayout);
        void onLongClickListener(View view,int position);
    }
}

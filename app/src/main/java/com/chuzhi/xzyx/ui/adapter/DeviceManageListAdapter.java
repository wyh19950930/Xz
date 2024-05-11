package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chuzhi.xzyx.databinding.ItemDeviceManageListBinding;
import com.chuzhi.xzyx.ui.bean.rc.ComputerListEntity;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 设备管理列表
 */
public class DeviceManageListAdapter extends RecyclerView.Adapter<DeviceManageListAdapter.DeviceManageViewHolder> {

    private Context mContext;
    private List<ComputerListEntity.ComputerListDTO> list;
    private OnClickListener mOnClickListener;

    public void setData(List<ComputerListEntity.ComputerListDTO> data){
        this.list = data;
    }

    public DeviceManageListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public DeviceManageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemDeviceManageListBinding binding = ItemDeviceManageListBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new DeviceManageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DeviceManageViewHolder holder, int position) {
        ComputerListEntity.ComputerListDTO data = list.get(position);
        holder.binding.tvItemDeviceManageName.setText("名称："+data.getName());
        holder.binding.tvItemDeviceManageCode.setText("S  N:"+data.getMainboard_sn());
        holder.binding.tvItemDeviceManageDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onDetClickListener(position,holder.binding.swtItemDeviceManage);
            }
        });
        if (data.getC_version() == 1){

            holder.binding.llItemDeviceManageAqRz.setVisibility(View.VISIBLE);
        }else {
            holder.binding.llItemDeviceManageAqRz.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class DeviceManageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ItemDeviceManageListBinding binding;

        public DeviceManageViewHolder(@NonNull ItemDeviceManageListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener((View.OnClickListener) this);
//            binding.llItemDeviceManage.setOnLongClickListener(this::onLongClick);
            binding.btnItemDeviceManage.setOnClickListener(this::onClick);
            binding.llItemDeviceManageAqRz.setOnClickListener(this::onClick);

        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onClickListener(v,getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mOnClickListener.onLongClickListener(v,getAdapterPosition());
            return true;
        }
    }
    public void setOnClickListener(OnClickListener onClickListener){
        this.mOnClickListener = onClickListener;
    }
//    public void setOnLongClickListener(OnClickListener onClickListener){
//        this.mOnClickListener = onClickListener;
//    }
    public interface OnClickListener{
        void onClickListener(View view,int position);
        void onDetClickListener(int position,SwipeMenuLayout swipeMenuLayout);
        void onLongClickListener(View view,int position);
    }
}

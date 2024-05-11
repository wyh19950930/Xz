package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuzhi.xzyx.R;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.utils.AndroidLifecycleUtils;


/**
 * Created by donglua on 15/5/31.
 */
//图片九宫格展示
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

  private ArrayList<String> photoPaths = new ArrayList<String>();
  private LayoutInflater inflater;

  private Context mContext;
  
  public final static int TYPE_ADD = 1;
  public final static int TYPE_PHOTO = 2;

  public final static int MAX = 9;//9

  public PhotoAdapter(Context mContext, ArrayList<String> photoPaths) {
    this.photoPaths = photoPaths;
    this.mContext = mContext;
    inflater = LayoutInflater.from(mContext);
  }
  public void clearPics(){
    photoPaths.clear();
    notifyDataSetChanged();
  }

  public void setNewData(ArrayList<String> photoPaths) {
    this.photoPaths = photoPaths;
    notifyDataSetChanged();
  }

  @Override
  public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = null;
    switch (viewType) {
      case TYPE_ADD:
        itemView = inflater.inflate(R.layout.item_add, parent, false);
        break;
      case TYPE_PHOTO:
        itemView = inflater.inflate(R.layout.__picker_item_photo, parent, false);
        break;
    }
    return new PhotoViewHolder(itemView);
  }


  @Override
  public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

    if (getItemViewType(position) == TYPE_PHOTO) {
      Uri uri = Uri.fromFile(new File(photoPaths.get(position)));

      boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());

      if (canLoadImage) {
        Glide.with(mContext).load(uri).thumbnail(0.1f)
                /*.placeholder(R.drawable.loading)
                .error(R.drawable.loading_fail)*/
                .into(holder.ivPhoto);
      }
    }
  }


  @Override
  public int getItemCount() {
    int count = photoPaths.size() + 1;
    if (count > MAX) {
      count = MAX;
    }
    return count;
  }

  @Override
  public int getItemViewType(int position) {
    return (position == photoPaths.size() && position != MAX) ? TYPE_ADD : TYPE_PHOTO;
  }

  public static class PhotoViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivPhoto;
    private View vSelected;
    public PhotoViewHolder(View itemView) {
      super(itemView);
      ivPhoto   = (ImageView) itemView.findViewById(R.id.iv_photo);
      vSelected = itemView.findViewById(R.id.v_selected);
      if (vSelected != null) vSelected.setVisibility(View.GONE);
    }
  }

}

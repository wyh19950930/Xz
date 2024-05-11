package me.iwf.photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.iwf.photopicker.R;
import me.iwf.photopicker.utils.AndroidLifecycleUtils;

/**
 * Created by donglua on 15/6/21.
 */
public class PhotoPagerAdapter extends PagerAdapter {

  private List<String> paths = new ArrayList<>();
  private RequestManager mGlide;

  public PhotoPagerAdapter(RequestManager glide, List<String> paths) {
    this.paths = paths;
    this.mGlide = glide;
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    final Context context = container.getContext();
    final String path = paths.get(position);
    final Uri uri;
    if (path.startsWith("http")) {
      uri = Uri.parse(path);
    } else {
      uri = Uri.fromFile(new File(path));
    }
    View itemView=null;

    if (path.endsWith(".png")||path.endsWith(".jpg")||path.endsWith(".jpeg") || path.endsWith(".BMP")
            || path.endsWith(".GIF")|| path.endsWith(".webp")){
      itemView = LayoutInflater.from(context)
              .inflate(R.layout.__picker_picker_item_pager, container, false);

      final ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_pager);



      boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(context);

      if (canLoadImage) {
        final RequestOptions options = new RequestOptions();
        options.dontAnimate()
                .dontTransform()
                .override(800, 800)
                .placeholder(R.drawable.__picker_ic_photo_black_48dp)
                .error(R.drawable.__picker_ic_broken_image_black_48dp);
        mGlide.setDefaultRequestOptions(options).load(uri)
                .thumbnail(0.1f)
                .into(imageView);
      }
      imageView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (context instanceof Activity) {
            if (!((Activity) context).isFinishing()) {
              ((Activity) context).onBackPressed();
            }
          }
        }
      });
      container.addView(itemView);
    }else {
      itemView = LayoutInflater.from(context)
              .inflate(R.layout.__picker_picker_item_video_pager, container, false);

      final VideoView imageView = (VideoView) itemView.findViewById(R.id.iv_pager_video);
      final TextView tv_if_dk = (TextView) itemView.findViewById(R.id.tv_if_dk);
      tv_if_dk.setVisibility(View.GONE);
      /*tv_if_dk.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
      });*/
      imageView.setVideoPath(path);
      //创建MediaController对象
      MediaController mediaController = new MediaController(context);

      //VideoView与MediaController建立关联
      imageView.setMediaController(mediaController);

      //让VideoView获取焦点
      imageView.requestFocus();
      imageView.start();


      container.addView(itemView);
    }






    return itemView;
  }


  @Override public int getCount() {
    return paths.size();
  }


  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }


  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
    mGlide.clear((View) object);
  }

  @Override
  public int getItemPosition (Object object) { return POSITION_NONE; }

}

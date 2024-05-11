package com.chuzhi.xzyx.utils;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.chuzhi.xzyx.R;


/**
 * Created by Administrator on 2017/7/11.
 */

public class CommenPop extends PopupWindow {//弹出框

    public static CommenPop getNormalPopu(final Activity context, int layout, View outsideView){
        final CommenPop commenNromalPop=new CommenPop();
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mContentView = LayoutInflater.from(context).inflate(layout, null);
//        View mContentView = layoutInflater.inflate(layout, null);
        commenNromalPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);//WRAP_CONTENT
        commenNromalPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        commenNromalPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        commenNromalPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //点击空白处时，隐藏掉pop窗口
        commenNromalPop.setBackgroundDrawable(new BitmapDrawable());//new BitmapDrawable()
        commenNromalPop.setOutsideTouchable(false);// 设置popupwindow外部可点击
//        commenNromalPop.setFocusable(true);// 获取焦点
//        commenNromalPop.setTouchable(true);// 设置popupwindow可点击z

//        backgroundAlpha(0.5f,context);
        /*outsideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commenNromalPop.dismiss();
            }
        });*/
        //添加pop窗口关闭事件
        commenNromalPop.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f,context);
            }
        });
        commenNromalPop.setContentView(mContentView);
        commenNromalPop.setAnimationStyle(R.style.anim_popu_zoom);
        return commenNromalPop;
    }

    public static CommenPop getFullScreenPopu(final Activity context, int layout, View outsideView){
        final CommenPop commenFullScreenPop=new CommenPop();
        View mContentView = LayoutInflater.from(context).inflate(layout, null);
        commenFullScreenPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        commenFullScreenPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        commenFullScreenPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        commenFullScreenPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //点击空白处时，隐藏掉pop窗口
        commenFullScreenPop.setBackgroundDrawable(new BitmapDrawable());
        commenFullScreenPop.setOutsideTouchable(false);
        commenFullScreenPop.setFocusable(true);
        commenFullScreenPop.setTouchable(true);
//        backgroundAlpha(0.5f,context);
        outsideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commenFullScreenPop.dismiss();
            }
        });
        //添加pop窗口关闭事件
        commenFullScreenPop.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
//                backgroundAlpha(1f,context);
            }
        });
        commenFullScreenPop.setContentView(mContentView);
        commenFullScreenPop.setAnimationStyle(R.style.anim_popu_zoom);
        return commenFullScreenPop;
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public static void backgroundAlpha(float bgAlpha, Activity activity)
    {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }

}

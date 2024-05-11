package com.chuzhi.xzyx.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.chuzhi.xzyx.R;

import io.reactivex.annotations.NonNull;

/**
 * @Author : wyh
 * @Time : On 2023/7/31 11:55
 * @Description : LoadingDialog1 解决二次加载动画失效问题
 */
public class LoadingDialog1 extends Dialog {
    private ImageView iv_ing;
    private TextView title;
    private AnimationSet animationSet;

    private static LoadingDialog1 instance;

    public static LoadingDialog1 getInstance(Context context) {
        if(instance == null) {
            instance = new LoadingDialog1(context);
        }

        return instance;
    }

    private LoadingDialog1(@NonNull Context context) {
        super(context);
    }

    private LoadingDialog1(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    private LoadingDialog1(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //背景透明处理
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getWindow().setDimAmount(0f);

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha=0.8f;
        getWindow().setAttributes(attributes);

        this.setContentView(R.layout.dialog_loading);

        //设置dialog属性
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        iv_ing = findViewById(R.id.loading_dialog_img);
        title = findViewById(R.id.tv_loading_tx);

        //加载动画
        loadIng();
    }

    @Override
    protected void onStart() {
        super.onStart();
        iv_ing.startAnimation(animationSet);//开始播放
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //加载动画
    private void loadIng() {
        animationSet = new AnimationSet(true);
        RotateAnimation animation_rotate = new RotateAnimation(0, +359,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        //第一个参数fromDegrees为动画起始时的旋转角度 //第二个参数toDegrees为动画旋转到的角度
        //第三个参数pivotXType为动画在X轴相对于物件位置类型 //第四个参数pivotXValue为动画相对于物件的X坐标的开始位置
        //第五个参数pivotXType为动画在Y轴相对于物件位置类型 //第六个参数pivotYValue为动画相对于物件的Y坐标的开始位置
        animation_rotate.setRepeatCount(-1);
        animation_rotate.setStartOffset(0);
        animation_rotate.setDuration(1000);
        LinearInterpolator lir = new LinearInterpolator();
        animationSet.setInterpolator(lir);
        animationSet.addAnimation(animation_rotate);
    }
    public void setTitleMsg(String msg){
        title.setText(msg);
    }
}

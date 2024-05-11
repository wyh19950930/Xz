package com.chuzhi.xzyx.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuzhi.xzyx.R;

/**
 * @Author : wyh
 * @Time : On 2023/7/7 16:59
 * @Description : LoadingDialog 第二次加载动画失效
 */
public class LoadingDialog extends Dialog {

    private TextView title;
    private ImageView img;
    private Animation animation;

    public LoadingDialog(Context context) {
        super(context, R.style.loading_dialog);

        initView();
    }

    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if(LoadingDialog.this.isShowing())
                    LoadingDialog.this.dismiss();
                break;
        }
        return true;
    }

    private void initView(){
        setContentView(R.layout.dialog_loading);
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_animation);
        animation.setInterpolator(new LinearInterpolator());
        img = findViewById(R.id.loading_dialog_img);
        title = findViewById(R.id.tv_loading_tx);
        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha=0.8f;
        getWindow().setAttributes(attributes);
        setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        img.startAnimation(animation);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void setTitleMsg(String msg){
        title.setText(msg);
    }
}

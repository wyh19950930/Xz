package com.chuzhi.xzyx.widget;

/**
 * @Author : wyh
 * @Time : On 2023/11/21 10:35
 * @Description : DrawView1 电池电量
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class DrawView extends View {
    private float percent = 0f;
    // 电池电量里面的绿色
    Paint paint = new Paint();
    // 电池电量外面的大白框
    Paint paint1 = new Paint();
    // 电池头部
    Paint paint2 = new Paint();

    public DrawView(Context context, AttributeSet set) {
        super(context, set);
        // 去锯齿
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(dip2px(1f));
        paint1.setColor(Color.GREEN);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.GREEN);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;

        //以分辨率为720*1080准，计算宽高比值
        float ratioWidth = (float) mScreenWidth / 720;
        float ratioHeight = (float) mScreenHeight / 1080;
        float ratioMetrics = Math.min(ratioWidth, ratioHeight);
        int textSize = Math.round(20 * ratioMetrics);
//        paint2.setTextSize(textSize);
        paint2.setTextSize(0);
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @SuppressLint("DrawAllocation")
    @Override
    // 重写该方法,进行绘图
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 大于百分之30时绿色，否则为红色
        if (percent > 0.2f) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.RED);
        }

        int a = getWidth() - dip2px(2f);
        int b = getHeight() - dip2px(1.5f);
        // 根据电量百分比画图
        float d = a * percent;
        float left = dip2px(0.5f);
        float top = dip2px(0.5f);
        float right = dip2px(2.5f);
        float bottom = dip2px(1.5f);

        RectF re1 = new RectF(left, top, d - right, b + bottom); //电量填充
        RectF re2 = new RectF(1, 1, a - right, b + bottom-1); //电池边框
        RectF re3 = new RectF(a - right, b / 4, a, b + bottom - b / 4);  //电池正极

        // 绘制圆角矩形
        canvas.drawRect(re1, paint);
        canvas.drawRoundRect(re2,5f,5f, paint1);
        canvas.drawRoundRect(re3,5f,5f, paint2);
        //文字的起点为(getWidth()/2,getHeight()/2)
        canvas.drawText(String.valueOf((int) (percent * 100)), getWidth() / 3 - dip2px(3), getHeight() - getHeight() / 4, paint2);
    }

    // 每次检测电量都重绘，在检测电量的地方调用
    public synchronized void setProgress(int percent) {
        this.percent = (float) (percent / 100.0);
        postInvalidate();
    }
}
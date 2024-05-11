package com.chuzhi.xzyx.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chuzhi.xzyx.R;

/**
 * @Author : wyh
 * @Time : On 2023/10/7 16:52
 * @Description : DrawHookView 对勾动画
 */
public class CustomTickView extends View {
    private int mCustomSize;//画布大小
    private int mRadius;
    private int mCheckBaseColor;//选中状态基本颜色
    private int mCheckTickColor;//选中状态对号颜色
    private int mUnCheckTickColor;//未选中状态对号颜色
    private int mUnCheckBaseColor;//未选中状态基本颜色
    private Paint mCheckPaint;//选中状态画笔 下面的背景圆
    private Paint mCheckArcPaint;//选中状态画笔 下面的背景圆圆弧
    private Paint mCheckDeclinePaint;//选中状态画笔  （上面的随动画缩减的圆盖在上面）  和对号
    private Paint mUnCheckPaint;//未选中状态画笔
    private Paint mCheckTickPaint;//选中对号画笔
    private Paint mCheckPaintArc;//回弹圆画笔 设置不同宽度已达到回弹圆动画目的
    private boolean isCheckd = false;//选中状态
    private float[] mPoints;
    private int mCenter;
    private RectF mRectF;
    private int mRingCounter;
    private int mCircleCounter = 0;//盖在上面的背景色圆逐渐缩小  逆向思维模拟向圆心收缩动画
    private int mAlphaCount = 0;
    private int scaleCounter = 50;
    private RectF mRectArc;
    private ImageView iv_ing;
    private TextView title;

    public CustomTickView(Context context) {
        super(context);
    }

    public CustomTickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);//获取自定义属性
        initPaint();//初始化画笔
    }


    public CustomTickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mCustomSize, mCustomSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCustomSize > 0) {
            if (!isCheckd) {
                canvas.drawCircle(mCenter, mCenter, mRadius, mUnCheckPaint);//未选中状态的圆
                canvas.drawLines(mPoints, mUnCheckPaint);
                return;
            }
            mRingCounter += 10;
            if (mRingCounter >= 360) {
                mRingCounter = 360;
            }
            canvas.drawArc(mRectF, 90, mRingCounter, false, mCheckArcPaint);
            if (mRingCounter == 360) {
                //先绘制指定颜色的圆
                canvas.drawCircle(mCenter, mCenter, mRadius, mCheckPaint);
                //然后在指定颜色的图层上，再绘制背景色的圆(半径不断缩小) 半径不断缩小，背景就不断露出来，达到向中心收缩的效果
                mCircleCounter += 10;
                canvas.drawCircle(mCenter, mCenter, mRadius - mCircleCounter, mCheckDeclinePaint);
                if (mCircleCounter >= mRadius + 100) {
                    mAlphaCount += 20;
                    if (mAlphaCount >= 255) mAlphaCount = 255; //显示对号（外加一个透明的渐变）
                    mCheckTickPaint.setAlpha(mAlphaCount);//设置透明度
                    //画白色的对号
                    canvas.drawLines(mPoints, mCheckTickPaint);
                    scaleCounter -= 4;//获取是否回弹
                    if (scaleCounter <= -50) {//scaleCounter从大于0到小于0的过程中 画笔宽度也是由增加到减少最后减为0 实现了圆放大收缩的回弹效果
                        scaleCounter = -50;
                    }
                    //放大并回弹，设置画笔的宽度
                    float strokeWith = mCheckArcPaint.getStrokeWidth() +
                            (scaleCounter > 0 ? 6 : -6);
                    System.out.println(strokeWith);
                    mCheckArcPaint.setStrokeWidth(strokeWith);
                    canvas.drawArc(mRectArc, 90, 360, false, mCheckArcPaint);
                }

            }
            postInvalidate();//重绘
        }

    }

    /**
     * 获取自定义属性
     *
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTickView);
        mCustomSize = (int) typedArray.getDimension(R.styleable.CustomTickView_custom_size, dip2px(100));
        mCheckBaseColor = typedArray.getColor(R.styleable.CustomTickView_check_base_color, mCheckBaseColor);
        mCheckTickColor = typedArray.getColor(R.styleable.CustomTickView_check_tick_color, mCheckTickColor);
        mUnCheckBaseColor = typedArray.getColor(R.styleable.CustomTickView_uncheck_base_color, mUnCheckBaseColor);
        mUnCheckTickColor = typedArray.getColor(R.styleable.CustomTickView_uncheck_tick_color, mUnCheckTickColor);
        typedArray.recycle();
        mCenter = mCustomSize / 2;
        mRadius = mCenter - 50;//缩小圆半径大小 防止回弹动画弹出画布
        mPoints = new float[8];
        //简易模拟对号 未做适配
        mPoints[0] = mCenter - mCenter / 3;
        mPoints[1] = mCenter;
        mPoints[2] = mCenter;
        mPoints[3] = mCenter + mCenter / 4;
        mPoints[4] = mCenter - 8;
        mPoints[5] = mCenter + mCenter / 4;
        mPoints[6] = mCenter + mCenter / 2;
        mPoints[7] = mCenter - mCenter / 5;

        mRectF = new RectF(mCenter - mRadius, mCenter - mRadius, mCenter + mRadius, mCenter + mRadius);//选中状态的圆弧 动画
        mRectArc = new RectF(mCenter - mRadius, mCenter - mRadius, mCenter + mRadius, mCenter + mRadius);//选中状态的圆弧 动画


    }

    /***
     * 初始化画笔
     */
    private void initPaint() {
        mCheckPaint = new Paint();
        mCheckPaint.setAntiAlias(true);
        mCheckPaint.setColor(mCheckBaseColor);

        mCheckPaintArc = new Paint();
        mCheckPaintArc.setAntiAlias(true);
        mCheckPaintArc.setColor(mCheckBaseColor);


        mCheckArcPaint = new Paint();
        mCheckArcPaint.setAntiAlias(true);
        mCheckArcPaint.setColor(mCheckBaseColor);
        mCheckArcPaint.setStyle(Paint.Style.STROKE);
        mCheckArcPaint.setStrokeWidth(20);


        mCheckDeclinePaint = new Paint();
        mCheckDeclinePaint.setAntiAlias(true);
        mCheckDeclinePaint.setColor(Color.parseColor("#3E3E3E"));

        mUnCheckPaint = new Paint();
        mUnCheckPaint.setAntiAlias(true);
        mUnCheckPaint.setColor(mUnCheckBaseColor);
        mUnCheckPaint.setStyle(Paint.Style.STROKE);
        mUnCheckPaint.setStrokeWidth(20);

        mCheckTickPaint = new Paint();
        mCheckTickPaint.setAntiAlias(true);
        mCheckTickPaint.setColor(mCheckTickColor);
        mCheckTickPaint.setStyle(Paint.Style.STROKE);
        mCheckTickPaint.setStrokeWidth(20);


    }

    /**
     * 重置
     */
    private void reset() {
        mRingCounter = 0;
        mCircleCounter = 0;
        mAlphaCount = 0;
        scaleCounter = 50;
        mCheckArcPaint.setStrokeWidth(20); //画笔宽度重置
        postInvalidate();
    }

    /**
     * dp转px
     *
     * @param dpValue
     * @return
     */
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 初始化点击事件
     */
    public void setUpEvent() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isCheckd = !isCheckd;
                reset();
                if (mOnCheckedChangeListener != null) {
                    //此处回调
                    mOnCheckedChangeListener.onCheckedChanged((CustomTickView) view, isCheckd);
                }
            }
        });
    }

    public void setDown(){
        isCheckd = true;
        reset();
    }

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CustomTickView tickView, boolean isCheckd);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }

}

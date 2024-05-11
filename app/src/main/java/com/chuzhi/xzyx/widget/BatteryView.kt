package com.chuzhi.xzyx.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**

 * @Author : wyh

 * @Time : On 2023/11/21 9:44

 * @Description : BatteryView 仿ios电池电量 没用到

 */
class BatteryView : View {
    private var mMaxLevel = 100
    private var mLinePaint: Paint? = null
    private var mBatteryPaint: Paint? = null
    private var mRectPaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var mRectF: RectF? = null
    private var mWidth = 0
    private var mHeight = 0
    private var mPerPartWidth = 0
    private var mBatteryLevel = 0
    private var mHeaderHeight = 0 //电池头部高度

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED
            || MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST
        ) {
            mWidth = 150
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED
            || MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST
        ) {
            mHeight = 80
        }
        mPerPartWidth = (mWidth - BATTERY_HEADER_WIDTH) / PART_COUNT
        mHeaderHeight = mHeight / 3
        setMeasuredDimension(mWidth, mHeight)
    }

    private fun init() {
        mLinePaint = Paint()
        mLinePaint!!.isAntiAlias = true
        mLinePaint!!.color = Color.BLACK
        mBatteryPaint = Paint()
        mBatteryPaint!!.isAntiAlias = true
        mBatteryPaint!!.color = Color.WHITE
        mRectPaint = Paint()
        mRectPaint!!.isAntiAlias = true
        mRectPaint!!.color = Color.GRAY
        mRectPaint!!.style = Paint.Style.FILL
        mTextPaint = Paint()
        mTextPaint!!.color = Color.BLACK
        mTextPaint!!.textSize = 20f
        mTextPaint!!.style = Paint.Style.FILL
        mTextPaint!!.textAlign = Paint.Align.CENTER
        mRectF = RectF()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val batteryWidth = mWidth - BATTERY_HEADER_WIDTH

        //绘制电池背景
        mRectF!!.right = batteryWidth.toFloat()
        mRectF!!.bottom = mHeight.toFloat()
        canvas.drawRoundRect(mRectF!!, 20f, 20f, mRectPaint!!)

        //绘制当前电量
        canvas.save()
        canvas.clipRect(0, 0, batteryWidth * mBatteryLevel / mMaxLevel, mHeight) //裁剪矩形
        canvas.drawRoundRect(mRectF!!, 20f, 20f, mBatteryPaint!!)
        canvas.restore()
        if (DRAW_PART_LINE) {
            //绘制电池分格线
            for (i in 1 until PART_COUNT) {
                canvas.drawLine(
                    (mPerPartWidth * i).toFloat(),
                    0f,
                    (mPerPartWidth * i).toFloat(),
                    mHeight.toFloat(),
                    mLinePaint!!
                )
            }
        }

        //绘制电量文字
        val fontMetrics = mTextPaint!!.fontMetrics
        val top = fontMetrics.top //基线到字体上边框的距离
        val bottom = fontMetrics.bottom //基线到字体下边框的距离
        val baseLineY = (mRectF!!.centerY() - top / 2 - bottom / 2).toInt() //基线中间点的y轴
        canvas.drawText(
            mBatteryLevel.toString(),
            mRectF!!.centerX(),
            baseLineY.toFloat(),
            mTextPaint!!
        )

        //绘制右边电池头部
        mRectF!!.left = batteryWidth.toFloat()
        mRectF!!.top = (mHeight / 2 - mHeaderHeight / 2).toFloat()
        mRectF!!.right = (mRectF!!.left + BATTERY_HEADER_WIDTH)
        mRectF!!.bottom = (mHeight / 2 + mHeaderHeight / 2).toFloat()
        mRectPaint!!.style = Paint.Style.FILL
        canvas.drawRect(mRectF!!, mRectPaint!!)
    }

    fun setBatteryLevel(level: Int) {
        mBatteryLevel = level
        if (level <= 10) {
            mBatteryPaint!!.color = Color.RED
        } else {
            mBatteryPaint!!.color = Color.BLACK
        }
        postInvalidate()
    }

    fun setMaxLevel(maxLevel: Int) {
        mMaxLevel = maxLevel
        postInvalidate()
    }

    companion object {
        private const val DRAW_PART_LINE = false //是否绘制分格线
        private const val PART_COUNT = 4 //分格总数
        private const val BATTERY_HEADER_WIDTH = 8 //右边电池头宽度
    }
}

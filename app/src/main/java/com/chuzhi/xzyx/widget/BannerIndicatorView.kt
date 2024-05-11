package com.chuzhi.xzyx.widget

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import com.chuzhi.xzyx.R


/**

 * @Author : wyh

 * @Time : On 2023/5/25 9:45

 * @Description : BannerIndicatorView轮播图指示器

 */

class BannerIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    // 指示器颜色，这里不局限于颜色，可设置 xml
    private var selectColor = 0
    private var normalColor = 0

    // 指示器个数
    private var indicatorCount = 0

    // 选中/未选中 指示器宽高
    private var indicatorSelectWidth = 0f
    private var indicatorSelectHeight = 0f
    private var indicatorNormalWidth = 0f
    private var indicatorRadius = 0f
    private var indicatorNormalHeight = 0f

    private var indicatorMargin = 10f
    private var mPaint : Paint?=null

    private fun initXmlAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicatorView)
        indicatorSelectWidth = typedArray.getDimension(R.styleable.BannerIndicatorView_indicator_select_width, 0f)
        indicatorSelectHeight = typedArray.getDimension(R.styleable.BannerIndicatorView_indicator_select_height, 0f)
        indicatorNormalWidth = typedArray.getDimension(R.styleable.BannerIndicatorView_indicator_normal_width, 0f)
        indicatorRadius = typedArray.getDimension(R.styleable.BannerIndicatorView_indicator_radius, 0f)
        indicatorNormalHeight = typedArray.getDimension(R.styleable.BannerIndicatorView_indicator_normal_height, 0f)
        indicatorMargin = typedArray.getDimension(R.styleable.BannerIndicatorView_indicator_margins, 0f)
        selectColor = typedArray.getResourceId(
            R.styleable.BannerIndicatorView_indicator_select_color,
            R.drawable.shape_indicator_select_tint
        )
        normalColor = typedArray.getResourceId(
            R.styleable.BannerIndicatorView_indicator_normal_color,
            R.drawable.shape_indicator_unselect_tint
        )
        typedArray.recycle()
        mPaint = Paint()
    }

    // 初始化指示器
    private fun initIndicatorView(context: Context) {

        removeAllViews()

        for (i in 0 until indicatorCount) {
            val ivIndicator = AppCompatImageView(context)

            val widthParam = if (i == 0) indicatorSelectWidth.toInt() else indicatorNormalWidth.toInt()
            val heightParam = if (i == 0) indicatorSelectHeight.toInt() else indicatorNormalHeight.toInt()

            val lp = LayoutParams(widthParam, heightParam)
            lp.leftMargin = if (i == 0) 0 else indicatorMargin.toInt()

            ivIndicator.layoutParams = lp

            // 默认第一个指示器设置选中
            ivIndicator.setBackgroundResource(if (i == 0) selectColor else normalColor)

            addView(ivIndicator)

        }

    }

    // 设置指示器个数
    fun initIndicatorCount(count: Int) {
        this.indicatorCount = count
        initIndicatorView(context)
    }

    // 切换指示器选中未选中
    fun changeIndicator(position: Int) {

        val count = childCount
        for (i in 0 until count) {

            val ivIndicator = getChildAt(i) as AppCompatImageView

            val widthParam =
                if (i == position) indicatorSelectWidth.toInt() else indicatorNormalWidth.toInt()
            val heightParam =
                if (i == position) indicatorSelectHeight.toInt() else indicatorNormalHeight.toInt()

            val lp = LayoutParams(widthParam, heightParam)
            lp.leftMargin = if (i == 0) 0 else indicatorMargin.toInt()

            ivIndicator.layoutParams = lp

            ivIndicator.setBackgroundResource(if (i == position) selectColor else normalColor)
        }

    }

    init {
        initXmlAttrs(context, attrs)
    }

}
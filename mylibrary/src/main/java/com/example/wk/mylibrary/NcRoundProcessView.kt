package com.example.wk.mylibrary

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.annotation.ColorInt
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import java.math.BigDecimal
import java.text.NumberFormat

/**
 * 创建日期：2018/11/28 on 16:08
 * 描述:饼图进度
 * 作者:wangKangXu
 */
class NcRoundProcessView : View {
    private var mPaint: Paint? = null
    private var mProcessPaint: Paint? = null
    private var mTextPaint: TextPaint? = null
    private var mHeight: Int = 0
    private var mWeight: Int = 0
    private var mRatio: Float = 0f
    private var mProcessColor: Int = 0
    private var mMaxColor: Int = 0
    private var mProcessWeight: Float = 0F
    private var mShowProcessText: Boolean = false
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }


    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private fun init(context: Context?, attrs: AttributeSet?) {
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.NcRoundProcessView)
        mProcessColor = attributes?.getColor(R.styleable.NcRoundProcessView_processColor, Color.GREEN) ?: Color.GREEN
        mMaxColor = attributes?.getColor(R.styleable.NcRoundProcessView_maxColor, Color.TRANSPARENT) ?: Color.TRANSPARENT
        mProcessWeight = attributes?.getDimension(R.styleable.NcRoundProcessView_processWidth, 4F) ?: 4F
        val textColor = attributes?.getColor(R.styleable.NcRoundProcessView_processTextColor, Color.BLUE)
                ?: Color.BLUE
        val textSize = attributes?.getDimensionPixelSize(R.styleable.NcRoundProcessView_processTextSize, 30)
                ?: 30
        mShowProcessText = attributes?.getBoolean(R.styleable.NcRoundProcessView_showProcessText, false) ?: false
        attributes?.recycle()
        mPaint = Paint()
        mProcessPaint = Paint()
        mTextPaint = TextPaint()
        mPaint?.let { paint ->
            paint.color = mMaxColor
            paint.style = Paint.Style.STROKE
            paint.isAntiAlias = true
            paint.strokeWidth = dp2Px(mProcessWeight, getContext()).toFloat()
        }
        mProcessPaint?.let { paint ->
            paint.color = mProcessColor
            paint.style = Paint.Style.STROKE
            paint.isAntiAlias = true
            paint.strokeWidth = dp2Px(mProcessWeight, getContext()).toFloat()
        }
        mTextPaint?.let { paint ->
            paint.textSize = textSize.toFloat()
            paint.color = textColor
            paint.isAntiAlias = true
        }
    }

    private var mProcess: Float = 0f

    private var mMaxProcess: Float = 0f

    /**
     * 设置数据
     */
    fun setProcess(maxProcess: BigDecimal = BigDecimal.valueOf(100), process: BigDecimal, @ColorInt processColor: Int) {
        if (processColor <= 0) {
            mProcessPaint?.color = mProcessColor
        } else {
            mProcessPaint?.color = processColor
        }
        this.mProcess = process.toFloat()
        this.mMaxProcess = maxProcess.toFloat()
        mRatio = if (process.compareTo(maxProcess) == 1) {
            1f
        } else {
            if (process.compareTo(BigDecimal.ZERO) == 0) {
                0f
            } else {
                maxProcess.divide(process, 2, BigDecimal.ROUND_HALF_UP).toFloat()
            }
        }
        postInvalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mHeight = measuredHeight
        mWeight = measuredWidth
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        val dp2Px = dp2Px(mProcessWeight, context)
        //第一层
        val rectF = RectF(
                ((dp2Px / 2) + 0.5).toFloat(),
                ((dp2Px / 2) + 0.5).toFloat(),
                ((mWeight - (dp2Px / 2)) - 0.5).toFloat(),
                ((mHeight - (dp2Px / 2)) - 0.5).toFloat()
        )
        canvas?.let { canvas ->
            canvas.drawArc(rectF, 0f, 360f, false, mPaint)
            canvas.drawArc(rectF, -90F, if (mRatio == 0f) {
                0f
            } else {
                360 / mRatio
            }, false, mProcessPaint)
            if (mShowProcessText) {
                val percentInstance = NumberFormat.getPercentInstance()
                percentInstance.maximumFractionDigits = 2
                val text = percentInstance.format(if (mProcess == 0f && mMaxProcess == 0f) {
                    0
                } else {
                    mProcess / mMaxProcess
                })
                val rect = Rect()
                mTextPaint?.getTextBounds(text, 0, text.length, rect)
                canvas.drawText(text, (mWeight / 2 - rect.width() / 2).toFloat(), (mHeight / 2 + rect.height() / 2).toFloat(), mTextPaint)
            }

        }

    }

    fun dp2Px(dp: Float, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp * density + 0.5f)
    }

}
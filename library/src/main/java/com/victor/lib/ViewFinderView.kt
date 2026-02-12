package com.victor.lib

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.victor.lib.data.FrameGravity
import com.victor.lib.data.LaserStyle
import com.victor.lib.data.TextLocation
import com.victor.lib.data.ViewFinderStyle
import com.victor.lib.interfaces.OnScanPointClickListener
import com.victor.lib.interfaces.OnTorchStateChangeListener
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViewFinderView
 * Author: Victor
 * Date: 2026/2/12 10:44
 * Description: 
 * -----------------------------------------------------------------
 */

class ViewFinderView: View {
    private val TAG = "ViewFinderView"
    /**
     * 默认范围比例，之所以默认为 1.2 是因为内切圆半径和外切圆半径之和的二分之一（即：（1 + √2) / 2 ≈ 1.2）
     */
    private val DEFAULT_RANGE_RATIO: Float = 1.2f
    /**
     * 最大缩放比例
     */
    private val MAX_ZOOM_RATIO: Float = 1.2f
    /**
     * 动画间隔
     */
    private val POINT_ANIMATION_INTERVAL: Int = 3000

    // 画笔
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
    }
    // 文本画笔
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
    }
    /**
     * 闪光灯文字画笔
     */
    private val torchTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }


    /**
     * 扫描框外面遮罩颜色
     */
    private var maskColor = 0

    /**
     * 扫描框域边框颜色
     */
    private var frameColor = 0

    /**
     * 扫描线颜色
     */
    private var laserColor = 0

    /**
     * 扫描框四角颜色
     */
    private var frameCornerColor = 0

    /**
     * 提示文本与扫描框的边距
     */
    private var labelTextPadding = 0f

    /**
     * 提示文本的宽度
     */
    private var labelTextWidth = 0

    /**
     * 提示文本的位置
     */
    private var labelTextLocation: TextLocation? = null

    /**
     * 扫描区域提示文本
     */
    private var labelText: String? = null

    /**
     * 扫描区域提示文本颜色
     */
    private var labelTextColor = 0

    /**
     * 提示文本字体大小
     */
    private var labelTextSize = 0f

    /**
     * 闪光灯状态
     */
    private var isTorchOn = false

    /**
     * 闪光灯图标
     */
    private var torchOnBitmap: Bitmap? = null
    private var torchOffBitmap: Bitmap? = null

    /**
     * 闪光灯图标内边距
     */
    private var torchIconPadding = 0f

    /**
     * 是否显示闪光灯图标
     */
    private var isShowTorchIcon = true

    /**
     * 闪光灯触摸区域
     */
    private var torchTouchRect: RectF? = null

    /**
     * 闪光灯提示文字
     */
    private var torchOnText: String = "点击关闭手电筒"
    private var torchOffText: String = "点击开启手电筒"

    /**
     * 闪光灯提示文字颜色
     */
    private var torchTextColor: Int = Color.WHITE

    /**
     * 闪光灯提示文字大小
     */
    private var torchTextSize: Float = 14f

    /**
     * 闪光灯图标与文字间距
     */
    private var torchIconTextSpacing: Float = 12f
    /**
     * 闪光灯距离扫描框的间距
     */
    private var torchIconTextPadding: Float = 12f

    /**
     * 扫描线开始位置
     */
    private var scannerStart = 0f

    /**
     * 扫描线结束位置
     */
    private var scannerEnd = 0f

    /**
     * 扫描框宽
     */
    private var frameWidth = 0

    /**
     * 扫描框高
     */
    private var frameHeight = 0

    /**
     * 扫描框圆角半径
     */
    private var frameCornerRadius = 0f

    /**
     * 激光扫描风格
     */
    private var laserStyle: LaserStyle? = null

    /**
     * 网格列数
     */
    private var laserGridColumn = 0

    /**
     * 网格高度
     */
    private var laserGridHeight = 0f

    /**
     * 渐变高度
     */
    private var gradientHeightRatio = 0f

    /**
     * 光晕线高度
     */
    private var glowEffectHeight = 0f

    /**
     * 扫描网格线条的宽
     */
    private var laserGridStrokeWidth = 0f

    /**
     * 扫描框
     */
    private var frame: RectF? = null

    /**
     * 扫描框边角的宽
     */
    private var frameCornerStrokeWidth = 0f

    /**
     * 扫描框边角的高
     */
    private var frameCornerSize = 0f

    /**
     * 扫描线每次移动距离
     */
    private var laserMovementSpeed = 0f

    /**
     * 扫描线高度
     */
    private var laserLineHeight = 0f

    /**
     * 扫描动画延迟间隔时间 默认20毫秒
     */
    private var laserAnimationInterval = 0

    /**
     * 是否完全刷新
     */
    private var fullRefresh = false

    /**
     * 边框线宽度
     */
    private var frameLineStrokeWidth = 0f

    /**
     * 扫描框占比
     */
    private var frameRatio = 0f

    /**
     * 扫描框内间距
     */
    private var framePaddingLeft = 0f
    private var framePaddingTop = 0f
    private var framePaddingRight = 0f
    private var framePaddingBottom = 0f

    /**
     * 扫描框对齐方式
     */
    private var frameGravity: FrameGravity? = null

    /**
     * 扫描框图片
     */
    private var frameBitmap: Bitmap? = null

    /**
     * 结果点颜色
     */
    private var pointColor = 0

    /**
     * 结果点描边颜色
     */
    private var pointStrokeColor = 0

    /**
     * 结果点图片
     */
    private var pointBitmap: Bitmap? = null

    /**
     * 是否显示结果点缩放动画
     */
    private var isPointAnimation = true

    /**
     * 结果点动画间隔时间
     */
    private var pointAnimationInterval = 0

    /**
     * 结果点半径
     */
    private var pointRadius = 0f

    /**
     * 结果点外圈描边的半径与结果点半径的比例
     */
    private var pointStrokeRatio = 0f

    /**
     * 设置结果点外圈描边的半径
     */
    private var pointStrokeRadius = 0f

    /**
     * 当前缩放比例
     */
    private var currentZoomRatio = 1.0f

    /**
     * 最后一次缩放比例（即上一次缩放比例）
     */
    private var lastZoomRatio = 0f

    /**
     * 缩放速度
     */
    private var zoomSpeed = 0.02f

    private var zoomCount = 0

    /**
     * 结果点有效点击范围半径
     */
    private var pointRangeRadius = 0f

    private var laserBitmap: Bitmap? = null

    private var laserBitmapRatio = 0f

    private var laserBitmapWidth = 0f

    private var viewfinderStyle: Int = ViewFinderStyle.CLASSIC

    private var pointList: ArrayList<Point>? = null

    private var isShowPoints = false

    private var minDimension = 0

    var mOnScanPointClickListener: OnScanPointClickListener? = null
    var mOnTorchStateChangeListener: OnTorchStateChangeListener? = null

    private var gestureDetector: GestureDetector? = null

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context,attrs)
    }

    fun initAttrs (context: Context, attrs: AttributeSet?) {
        val a: TypedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.ViewFinderView, 0, 0)

        try {
            val displayMetrics = resources.displayMetrics

            viewfinderStyle = a.getInt(R.styleable.ViewFinderView_ViewFinderStyle, ViewFinderStyle.CLASSIC)
            maskColor = a.getColor(R.styleable.ViewFinderView_vfvMaskColor, getColor(context, R.color.viewfinder_mask))
            frameColor = a.getColor(R.styleable.ViewFinderView_vfvFrameColor, getColor(context, R.color.viewfinder_frame))
            frameWidth = a.getDimensionPixelSize(R.styleable.ViewFinderView_vfvFrameWidth, 0)
            frameHeight = a.getDimensionPixelSize(R.styleable.ViewFinderView_vfvFrameHeight, 0)
            frameRatio = a.getFloat(R.styleable.ViewFinderView_vfvFrameRatio, 0.625f)
            frameLineStrokeWidth = a.getDimension(R.styleable.ViewFinderView_vfvFrameLineStrokeWidth,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, displayMetrics))
            framePaddingLeft = a.getDimension(R.styleable.ViewFinderView_vfvFramePaddingLeft, 0f)
            framePaddingTop = a.getDimension(R.styleable.ViewFinderView_vfvFramePaddingTop, 0f)
            framePaddingRight = a.getDimension(R.styleable.ViewFinderView_vfvFramePaddingRight, 0f)
            framePaddingBottom = a.getDimension(R.styleable.ViewFinderView_vfvFramePaddingBottom, 0f)
            frameGravity = FrameGravity.fromInt(a.getInt(R.styleable.ViewFinderView_vfvFrameGravity, FrameGravity.CENTER.value))
            frameCornerColor = a.getColor(R.styleable.ViewFinderView_vfvFrameCornerColor, getColor(context, R.color.viewfinder_corner))
            frameCornerSize = a.getDimension(R.styleable.ViewFinderView_vfvFrameCornerSize,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, displayMetrics))
            frameCornerStrokeWidth = a.getDimension(R.styleable.ViewFinderView_vfvFrameCornerStrokeWidth,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, displayMetrics))
            frameCornerRadius = a.getDimension(R.styleable.ViewFinderView_vfvFrameCornerRadius, 0f)
            val frameDrawable = a.getDrawable(R.styleable.ViewFinderView_vfvFrameDrawable)

            laserLineHeight = a.getDimension(R.styleable.ViewFinderView_vfvLaserLineHeight,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, displayMetrics))
            laserMovementSpeed = a.getDimension(R.styleable.ViewFinderView_vfvLaserMovementSpeed,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics))
            laserAnimationInterval = a.getInteger(R.styleable.ViewFinderView_vfvLaserAnimationInterval, 20)

            laserGridColumn = a.getInt(R.styleable.ViewFinderView_vfvLaserGridColumn, 20)
            laserGridHeight = a.getDimension(R.styleable.ViewFinderView_vfvLaserGridHeight,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, displayMetrics))
            gradientHeightRatio = a.getFloat(R.styleable.ViewFinderView_vfvGradientHeightRatio, 0.6f)
            glowEffectHeight = a.getDimension(R.styleable.ViewFinderView_vfvGlowEffectHeight,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics))
            laserGridStrokeWidth = a.getDimension(R.styleable.ViewFinderView_vfvLaserGridStrokeWidth,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, displayMetrics))

            laserColor = a.getColor(R.styleable.ViewFinderView_vfvLaserColor, getColor(context, R.color.viewfinder_laser))
            laserStyle = LaserStyle.fromInt(a.getInt(R.styleable.ViewFinderView_vfvLaserStyle, LaserStyle.LINE.value))
            laserBitmapRatio = a.getFloat(R.styleable.ViewFinderView_vfvLaserDrawableRatio, 0.625f)
            val laserDrawable = a.getDrawable(R.styleable.ViewFinderView_vfvLaserDrawable)

            labelText = a.getString(R.styleable.ViewFinderView_vfvLabelText)
            labelTextColor = a.getColor(R.styleable.ViewFinderView_vfvLabelTextColor, getColor(context, R.color.viewfinder_label_text))
            labelTextSize = a.getDimension(R.styleable.ViewFinderView_vfvLabelTextSize,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, displayMetrics))
            labelTextPadding = a.getDimension(R.styleable.ViewFinderView_vfvLabelTextPadding,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, displayMetrics))
            labelTextWidth = a.getDimensionPixelSize(R.styleable.ViewFinderView_vfvLabelTextWidth, 0)
            labelTextLocation = TextLocation.getFromInt(a.getInt(R.styleable.ViewFinderView_vfvLabelTextLocation, 1))

            pointColor = a.getColor(R.styleable.ViewFinderView_vfvPointColor, getColor(context, R.color.viewfinder_point))
            pointStrokeColor = a.getColor(R.styleable.ViewFinderView_vfvPointStrokeColor, getColor(context, R.color.viewfinder_point_stroke))
            pointRadius = a.getDimension(R.styleable.ViewFinderView_vfvPointRadius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, displayMetrics))
            pointStrokeRatio = a.getFloat(R.styleable.ViewFinderView_vfvPointStrokeRatio, DEFAULT_RANGE_RATIO)
            val pointDrawable = a.getDrawable(R.styleable.ViewFinderView_vfvPointDrawable)

            isPointAnimation = a.getBoolean(R.styleable.ViewFinderView_vfvPointAnimation, true)
            pointAnimationInterval = a.getInt(R.styleable.ViewFinderView_vfvPointAnimationInterval, POINT_ANIMATION_INTERVAL)
            fullRefresh = a.getBoolean(R.styleable.ViewFinderView_vfvFullRefresh, false)

            isShowTorchIcon = a.getBoolean(R.styleable.ViewFinderView_vfvShowTorchIcon, true)
            torchIconPadding = a.getDimension(R.styleable.ViewFinderView_vfvTorchIconPadding,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, displayMetrics))

            // 闪光灯文字属性
            torchTextColor = a.getColor(R.styleable.ViewFinderView_vfvTorchTextColor, Color.WHITE)
            torchTextSize = a.getDimension(R.styleable.ViewFinderView_vfvTorchTextSize,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, displayMetrics))
            torchIconTextSpacing = a.getDimension(R.styleable.ViewFinderView_vfvTorchIconTextSpacing,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, displayMetrics))
            torchIconTextPadding = a.getDimension(R.styleable.ViewFinderView_vfvTorchIconTextPadding,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, displayMetrics))

            // 初始化文字画笔
            torchTextPaint.color = torchTextColor
            torchTextPaint.textSize = torchTextSize

            // 自定义文字
            val torchOnTextStr = a.getString(R.styleable.ViewFinderView_vfvTorchOnText)
            val torchOffTextStr = a.getString(R.styleable.ViewFinderView_vfvTorchOffText)

            torchOnTextStr?.let {
                torchOnText = it
            }
            torchOffTextStr?.let {
                torchOffText = it
            }

            val torchOnDrawable = a.getDrawable(R.styleable.ViewFinderView_vfvTorchOnDrawable)
            val torchOffDrawable = a.getDrawable(R.styleable.ViewFinderView_vfvTorchOffDrawable)

            torchOnDrawable?.let {
                torchOnBitmap = getBitmapFormDrawable(it)
            }

            torchOffDrawable?.let {
                torchOffBitmap = getBitmapFormDrawable(it)
            }

            frameDrawable?.let {
                frameBitmap = getBitmapFormDrawable(it)
            }

            laserDrawable?.let {
                laserBitmap = getBitmapFormDrawable(it)
            }

            pointDrawable?.let {
                pointBitmap = getBitmapFormDrawable(it)
                pointRangeRadius = (pointBitmap!!.width + pointBitmap!!.height) / 4f * DEFAULT_RANGE_RATIO
            } ?: run {
                pointStrokeRadius = pointRadius * pointStrokeRatio
                pointRangeRadius = pointStrokeRadius * DEFAULT_RANGE_RATIO
            }

            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    // 先检查是否点击了闪光灯图标
                    if (isShowTorchIcon && checkTorchClick(e.x, e.y)) {
                        return true
                    }

                    if (isShowPoints && checkSingleTap(e.x, e.y)) {
                        return true
                    }
                    return super.onSingleTapUp(e)
                }
            })
        } finally {
            a.recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initFrame(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        if (isShowPoints) {
            // 显示结果点
            drawMask(canvas, width, height)
            pointList?.let { drawResultPoints(canvas, it) }
            if (isPointAnimation) {
                // 显示动画并且结果点标记的图片为空时，支持缩放动画
                calcPointZoomAnimation()
            }
            return
        }

        val frame = frame ?: return

        if (scannerStart == 0f) {
            scannerStart = frame.top
        }

        scannerEnd = frame.bottom - laserLineHeight

        // CLASSIC样式：经典样式（带扫描框）
        if (viewfinderStyle == ViewFinderStyle.CLASSIC) {
            // 绘制模糊区域
            drawExterior(canvas, frame, width, height)
            // 绘制扫描动画
            drawLaserScanner(canvas, frame)
            // 绘制取景区域框
            drawFrame(canvas, frame)
            // 绘制提示信息
            drawTextInfo(canvas, frame)
            // 绘制闪光灯图标和文字
            if (isShowTorchIcon) {
                drawTorchIconWithText(canvas, frame)
            }

            if (fullRefresh) {
                // 完全刷新
                postInvalidateDelayed(laserAnimationInterval.toLong())
            } else {
                // 局部刷新，更高效
                postInvalidateDelayed(
                    laserAnimationInterval.toLong(),
                    frame.left.toInt(),
                    frame.top.toInt(),
                    frame.right.toInt(),
                    frame.bottom.toInt()
                )
            }
        } else if (viewfinderStyle == ViewFinderStyle.POPULAR) {
            // POPULAR样式：类似于新版的微信全屏扫描（不带扫描框）
            // 绘制扫描动画
            drawLaserScanner(canvas, frame)
            // 绘制提示信息
            drawTextInfo(canvas, frame)
            if (isShowTorchIcon) {
                drawTorchIconWithText(canvas,frame)
            }

            postInvalidateDelayed(laserAnimationInterval.toLong())
        }
    }

    /**
     * 绘制文本
     */
    private fun drawTextInfo(canvas: Canvas, frame: RectF) {
        if (!TextUtils.isEmpty(labelText)) {
            textPaint.color = labelTextColor
            textPaint.textSize = labelTextSize
            textPaint.textAlign = Paint.Align.CENTER

            val staticLayout = StaticLayout(
                labelText,
                textPaint,
                labelTextWidth,
                Layout.Alignment.ALIGN_NORMAL,
                1.2f,
                0.0f,
                true
            )

            val saveCount = canvas.save()
            try {
                if (labelTextLocation == TextLocation.BOTTOM) {
                    canvas.translate(frame.centerX(), frame.bottom + labelTextPadding)
                } else {
                    canvas.translate(frame.centerX(), frame.top - labelTextPadding - staticLayout.height)
                }
                staticLayout.draw(canvas)
            } finally {
                canvas.restoreToCount(saveCount)
            }
        }
    }

    /**
     * 绘制闪光灯图标和文字
     */
    private fun drawTorchIconWithText(canvas: Canvas, frame: RectF) {
        val torchBitmap = if (isTorchOn) torchOnBitmap else torchOffBitmap

        torchBitmap?.let { bitmap ->
            // 绘制闪光灯图标
            canvas.drawBitmap(
                bitmap,
                frame.centerX() - bitmap.width / 2,
                frame.bottom + torchIconTextPadding,
                paint
            )

            // 当前显示的提示文字
            val tipText = if (isTorchOn) torchOnText else torchOffText

            torchTextPaint.color = torchTextColor
            torchTextPaint.textSize = torchTextSize
            torchTextPaint.textAlign = Paint.Align.CENTER

            val staticLayout = StaticLayout(
                tipText,
                torchTextPaint,
                labelTextWidth,
                Layout.Alignment.ALIGN_NORMAL,
                1.2f,
                0.0f,
                true
            )
            val saveCount = canvas.save()
            try {
                if (labelTextLocation == TextLocation.BOTTOM) {
                    canvas.translate(frame.centerX(), frame.top - torchIconTextSpacing - staticLayout.height)
                } else {
                    val torchTextStartY = frame.bottom + torchIconTextPadding + bitmap.height + torchIconTextSpacing
                    canvas.translate(frame.centerX(), torchTextStartY)
                }
                staticLayout.draw(canvas)
            } finally {
                canvas.restoreToCount(saveCount)
            }

            // 测量文字宽度
            val textWidth = torchTextPaint.measureText(tipText)
            val textHeight = torchTextPaint.descent() - torchTextPaint.ascent()
            // 更新触摸区域（包含图标和文字背景）
            torchTouchRect = RectF(
                frame.centerX() - textWidth / 2,
                frame.bottom + torchIconTextPadding,
                frame.centerX() + textWidth / 2,
                frame.bottom + torchIconTextPadding + bitmap.height + torchIconTextSpacing + textHeight
            )
        }
    }

    /**
     * 绘制扫描框边角
     */
    private fun drawFrameCorner(canvas: Canvas, frame: RectF) {
        paint.color = frameCornerColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = frameCornerStrokeWidth

        val padding = (frameCornerStrokeWidth - frameLineStrokeWidth) / 2
        val cornerFrame = RectF(
            frame.left + padding,
            frame.top + padding,
            frame.right - padding,
            frame.bottom - padding
        )

        // 绘制圆角
        if (frameCornerRadius > 0f) {
            val diameter = 2 * frameCornerRadius
            // 左上角（从180度开始，画90度）
            val topLeft = RectF(
                cornerFrame.left,
                cornerFrame.top,
                cornerFrame.left + diameter,
                cornerFrame.top + diameter
            )
            canvas.drawArc(topLeft, 180f, 90f, false, paint)

            // 右上角（从270度开始，画90度）
            val topRight = RectF(
                cornerFrame.right - diameter,
                cornerFrame.top,
                cornerFrame.right,
                cornerFrame.top + diameter
            )
            canvas.drawArc(topRight, 270f, 90f, false, paint)

            // 右下角（从0度开始，画90度）
            val bottomRight = RectF(
                cornerFrame.right - diameter,
                cornerFrame.bottom - diameter,
                cornerFrame.right,
                cornerFrame.bottom
            )
            canvas.drawArc(bottomRight, 0f, 90f, false, paint)

            // 左下角（从90度开始，画90度）
            val bottomLeft = RectF(
                cornerFrame.left,
                cornerFrame.bottom - diameter,
                cornerFrame.left + diameter,
                cornerFrame.bottom
            )
            canvas.drawArc(bottomLeft, 90f, 90f, false, paint)
        }

        val length = frameCornerSize - frameCornerRadius
        // 绘制边角纵横延长线
        if (length > 0f) {
            // 左上
            canvas.drawLine(cornerFrame.left - padding + frameCornerRadius, cornerFrame.top,
                cornerFrame.left + frameCornerSize, cornerFrame.top, paint)
            canvas.drawLine(cornerFrame.left, cornerFrame.top - padding + frameCornerRadius,
                cornerFrame.left, cornerFrame.top + frameCornerSize, paint)

            // 右上
            canvas.drawLine(cornerFrame.right - frameCornerSize, cornerFrame.top,
                cornerFrame.right + padding - frameCornerRadius, cornerFrame.top, paint)
            canvas.drawLine(cornerFrame.right, cornerFrame.top - padding + frameCornerRadius,
                cornerFrame.right, cornerFrame.top + frameCornerSize, paint)

            // 右下
            canvas.drawLine(cornerFrame.right + padding - frameCornerRadius, cornerFrame.bottom,
                cornerFrame.right - frameCornerSize, cornerFrame.bottom, paint)
            canvas.drawLine(cornerFrame.right, cornerFrame.bottom + padding - frameCornerRadius,
                cornerFrame.right, cornerFrame.bottom - frameCornerSize, paint)

            // 左下
            canvas.drawLine(cornerFrame.left + frameCornerSize, cornerFrame.bottom,
                cornerFrame.left - padding + frameCornerRadius, cornerFrame.bottom, paint)
            canvas.drawLine(cornerFrame.left, cornerFrame.bottom + padding - frameCornerRadius,
                cornerFrame.left, cornerFrame.bottom - frameCornerSize, paint)
        }
    }

    /**
     * 绘制扫描动画
     */
    private fun drawImageScanner(canvas: Canvas, frame: RectF) {
        if (laserBitmap != null) {
            canvas.drawBitmap(
                laserBitmap!!,
                (width - laserBitmap!!.width) / 2f,
                scannerStart,
                paint
            )
        } else {
            drawLineScanner(canvas, frame)
        }
    }

    /**
     * 绘制激光扫描线
     */
    private fun drawLaserScanner(canvas: Canvas, frame: RectF) {
        paint.style = Paint.Style.FILL
        paint.color = laserColor
        when (laserStyle) {
            LaserStyle.LINE -> drawLineScanner(canvas, frame)
            LaserStyle.GRID -> drawGridScanner(canvas, frame)
            LaserStyle.IMAGE -> drawImageScanner(canvas, frame)
            LaserStyle.GRADIENT -> gradient(canvas, frame)
            else -> {}
        }
        // 更新扫描位置
        if (scannerStart < scannerEnd) {
            scannerStart += laserMovementSpeed
        } else {
            scannerStart = frame.top
        }
        // 清除shader
        paint.setShader(null)
    }

    /**
     * 绘制线性式扫描
     */
    private fun drawLineScanner(canvas: Canvas, frame: RectF) {
        // 线性渐变
        val linearGradient = LinearGradient(
            frame.centerX(), scannerStart,
            frame.centerX(), scannerStart + laserLineHeight,
            intArrayOf(shadeColor(laserColor,0f), laserColor),
            null,
            TileMode.CLAMP
        )

        paint.shader = linearGradient
        // 椭圆
        val rectF = RectF(
            frame.left + frameCornerSize,
            scannerStart,
            frame.right - frameCornerSize,
            scannerStart + laserLineHeight
        )
        canvas.drawOval(rectF, paint)
    }

    /**
     * 绘制网格式扫描
     */
    private fun drawGridScanner(canvas: Canvas, frame: RectF) {
        paint.strokeWidth = laserGridStrokeWidth
        paint.style = Paint.Style.STROKE

        // 计算Y轴开始位置
        val startY = if (laserGridHeight > 0 && scannerStart - frame.top > laserGridHeight) {
            scannerStart - laserGridHeight
        } else {
            frame.top
        }

        // 渐变设置
        val linearGradient = LinearGradient(
            frame.centerX(), startY,
            frame.centerX(), scannerStart,
            intArrayOf(shadeColor(laserColor,0f), laserColor),
            null,
            TileMode.CLAMP
        )
        paint.shader = linearGradient

        val gridItemSize = frame.width() / laserGridColumn
        val gridPath = Path()

        // 绘制纵向线
        for (i in 1 until laserGridColumn) {
            val x = frame.left + i * gridItemSize
            gridPath.moveTo(x, startY)
            gridPath.lineTo(x, scannerStart)
        }

        // 绘制横向线
        val visibleHeight = scannerStart - startY
        val horizontalLineCount = ceil(visibleHeight / gridItemSize).toInt()
        val padding = frameLineStrokeWidth / 2f
        for (i in 0..horizontalLineCount) {
            val y = scannerStart - i * gridItemSize
            gridPath.moveTo(frame.left + padding, y)
            gridPath.lineTo(frame.right - padding, y)
        }

        canvas.drawPath(gridPath, paint)
    }

    /**
     * 绘制扫描背景渐变
     */
    private fun gradient(canvas: Canvas, frame: RectF) {
        // 保存当前paint状态
        val oldStyle = paint.style
        val oldShader = paint.shader

        // 设置为填充模式绘制背景
        paint.style = Paint.Style.FILL

        // 计算Y轴开始位置
        val gradientHeight = (gradientHeightRatio * frame.top).toInt()
        val startY = if (gradientHeight > 0 && scannerStart - frame.top > gradientHeight) {
            scannerStart - gradientHeight
        } else {
            frame.top
        }

        var startColorAlpha = 0.2f //顶部不透明度默认0.2
        if (gradientHeight > 0) {
            startColorAlpha = 0f
        }
        // 1. 主渐变 - 从上到下的扫描渐变
        val mainGradient = LinearGradient(
            frame.centerX(), startY,
            frame.centerX(), scannerStart,
            intArrayOf(
                shadeColor(laserColor, startColorAlpha), // 顶部不透明度
                shadeColor(laserColor, 0.3f), // 中间不透明度0.3
                shadeColor(laserColor, 0.6f)  // 扫描线处不透明度0.6
            ),
            floatArrayOf(0.0f, 0.7f, 1.0f),
            TileMode.CLAMP
        )

        paint.shader = mainGradient

        // 绘制整个扫描区域渐变背景
        canvas.drawRect(
            frame.left,
            startY,
            frame.right,
            scannerStart,
            paint
        )

        // 添加光晕效果
        if (glowEffectHeight > 0) {
            drawGlowEffect(canvas, frame, startY)
        }

        // 恢复paint状态
        paint.style = oldStyle
        paint.shader = oldShader
    }

    /**
     * 绘制网格线
     */
    private fun drawGridLines(canvas: Canvas, frame: RectF, startY: Float) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.shader = null

        // 网格线颜色 - 使用半透明的扫描线颜色
        val gridColor = shadeColor(laserColor, 0.3f)
        paint.color = gridColor

        val gridItemSize = frame.width() / laserGridColumn
        val visibleHeight = scannerStart - startY
        val horizontalLineCount = ceil(visibleHeight / gridItemSize).toInt()
        val padding = frameLineStrokeWidth / 2f

        // 绘制纵向线
        for (i in 1 until laserGridColumn) {
            val x = frame.left + i * gridItemSize
            canvas.drawLine(x, startY, x, scannerStart, paint)
        }

        // 绘制横向线
        for (i in 0..horizontalLineCount) {
            val y = scannerStart - i * gridItemSize
            if (y >= startY) {
                canvas.drawLine(frame.left + padding, y, frame.right - padding, y, paint)
            }
        }
    }

    /**
     * 绘制光晕效果
     */
    private fun drawGlowEffect(canvas: Canvas, frame: RectF, startY: Float) {
        // 在扫描线位置添加光晕
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }

        // 扫描线光晕渐变
        val glowGradient = LinearGradient(
            frame.left, scannerStart - glowEffectHeight,
            frame.left, scannerStart + glowEffectHeight,
            intArrayOf(
                Color.TRANSPARENT,
                shadeColor(laserColor, 0.5f),
                Color.TRANSPARENT
            ),
            null,
            TileMode.CLAMP
        )
        glowPaint.shader = glowGradient

        // 绘制一条细长的光带
        canvas.drawRect(
            frame.left,
            scannerStart - glowEffectHeight,
            frame.right,
            scannerStart + glowEffectHeight,
            glowPaint
        )
    }

    /**
     * 辅助方法：带透明度的颜色
     */
    private fun shadeColor(color: Int, alphaFactor: Float): Int {
        val alpha = (Color.alpha(color) * alphaFactor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    /**
     * 绘制扫描框边框
     */
    private fun drawFrame(canvas: Canvas, frame: RectF) {
        paint.color = frameColor
        paint.style = Paint.Style.STROKE
        if (frameBitmap != null) {
            canvas.drawBitmap(frameBitmap!!, null, frame, paint)
        } else {
            paint.strokeWidth = frameLineStrokeWidth
            // 绘制圆角边框
            canvas.drawRoundRect(frame, frameCornerRadius, frameCornerRadius, paint)
            // 绘制扫描框边角
            drawFrameCorner(canvas, frame)
        }
    }

    /**
     * 绘制模糊区域
     */
    private fun drawExterior(canvas: Canvas, frame: RectF, width: Int, height: Int) {
        if (maskColor == 0) {
            return
        }
        paint.color = maskColor
        paint.style = Paint.Style.FILL

        val path = Path()
        path.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)

        val framePath = Path()
        framePath.addRoundRect(frame, frameCornerRadius, frameCornerRadius, Path.Direction.CW)

        // 使用 DIFFERENCE 模式挖空扫描取景区域
        path.op(framePath, Path.Op.DIFFERENCE)
        // 绘制最终路径
        canvas.drawPath(path, paint)
    }

    /**
     * 绘制遮罩层
     */
    private fun drawMask(canvas: Canvas, width: Int, height: Int) {
        if (maskColor != 0) {
            paint.style = Paint.Style.FILL
            paint.color = maskColor
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }

    /**
     * 根据结果点集合绘制结果点
     */
    private fun drawResultPoints(canvas: Canvas, points: List<Point>) {
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        for (point in points) {
            drawResultPoint(canvas, point, currentZoomRatio)
        }
    }

    /**
     * 计算点的缩放动画
     */
    private fun calcPointZoomAnimation() {
        if (currentZoomRatio <= 1F) {
            lastZoomRatio = currentZoomRatio
            currentZoomRatio += zoomSpeed

            if (zoomCount < 2) {
                // 记住缩放回合次数
                zoomCount++
            } else {
                zoomCount = 0
            }
        } else if (currentZoomRatio >= MAX_ZOOM_RATIO) {
            lastZoomRatio = currentZoomRatio
            currentZoomRatio -= zoomSpeed
        } else {
            if (lastZoomRatio > currentZoomRatio) {
                lastZoomRatio = currentZoomRatio
                currentZoomRatio -= zoomSpeed
            } else {
                lastZoomRatio = currentZoomRatio
                currentZoomRatio += zoomSpeed
            }
        }

        // 每间隔3秒触发一套缩放动画，一套动画缩放三个回合(即：每次zoomCount累加到2后重置为0时)
        postInvalidateDelayed(
            if (zoomCount == 0 && lastZoomRatio == 1f) pointAnimationInterval.toLong()
            else (laserAnimationInterval * 2L)
        )
    }

    /**
     * 绘制结果点
     */
    private fun drawResultPoint(canvas: Canvas, point: Point, currentZoomRatio: Float) {
        if (pointBitmap != null) {
            val left = point.x - pointBitmap!!.width / 2.0f
            val top = point.y - pointBitmap!!.height / 2.0f
            if (isPointAnimation) {
                val dstW = (pointBitmap!!.width * currentZoomRatio).roundToInt()
                val dstH = (pointBitmap!!.height * currentZoomRatio).roundToInt()
                val dstLeft = point.x - (dstW / 2f).roundToInt()
                val dstTop = point.y - (dstH / 2f).roundToInt()
                val dstRect = Rect(dstLeft, dstTop, dstLeft + dstW, dstTop + dstH)
                canvas.drawBitmap(pointBitmap!!, null, dstRect, paint)
            } else {
                canvas.drawBitmap(pointBitmap!!, left, top, paint)
            }
        } else {
            paint.color = pointStrokeColor
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), pointStrokeRadius * currentZoomRatio, paint)

            paint.color = pointColor
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), pointRadius * currentZoomRatio, paint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector?.onTouchEvent(event)
        return if (isShowPoints || isShowTorchIcon) true else super.onTouchEvent(event)
    }

    /**
     * 检查是否点击了闪光灯图标
     */
    private fun checkTorchClick(x: Float, y: Float): Boolean {
        torchTouchRect?.let { rect ->
            if (rect.contains(x, y)) {
                toggleTorch()
                return true
            }
        }
        return false
    }

    /**
     * 检查是否点击了结果点
     */
    private fun checkSingleTap(x: Float, y: Float): Boolean {
        pointList?.let { points ->
            for (i in points.indices) {
                val point = points[i]
                val distance = getDistance(x, y, point.x.toFloat(), point.y.toFloat())
                if (distance <= pointRangeRadius) {
                    mOnScanPointClickListener?.OnScanPointClick(i)
                    return true
                }
            }
        }
        return false
    }

    /**
     * 获取两点之间的距离
     */
    private fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2))
    }

    fun toggleTorch() {
        Log.d(TAG, "toggleTorch called, current isTorchOn = $isTorchOn")
        isTorchOn = !isTorchOn
        Log.d(TAG, "toggleTorch after, new isTorchOn = $isTorchOn")
        mOnTorchStateChangeListener?.onTorchStateChanged(isTorchOn)
        // 刷新视图
        invalidate()
    }

    //region Public Methods

    /**
     * 显示扫描动画
     */
    fun showScanner() {
        isShowPoints = false
        invalidate()
    }

    /**
     * 显示结果点
     *
     * @param points 结果点
     */
    fun showResultPoints(points: ArrayList<Point>) {
        pointList = points
        isShowPoints = true
        zoomCount = 0
        lastZoomRatio = 0f
        currentZoomRatio = 1f
        invalidate()
    }

    /**
     * 设置 扫描区外遮罩的颜色
     *
     * @param maskColor 遮罩颜色
     */
    fun setMaskColor(@ColorInt maskColor: Int) {
        this.maskColor = maskColor
    }

    /**
     * 设置 扫描框边框的颜色
     *
     * @param frameColor 扫描框边框的颜色
     */
    fun setFrameColor(@ColorInt frameColor: Int) {
        this.frameColor = frameColor
    }

    /**
     * 设置扫描区激光线的颜色
     *
     * @param laserColor 激光线的颜色
     */
    fun setLaserColor(@ColorInt laserColor: Int) {
        this.laserColor = laserColor
    }

    /**
     * 设置扫描框边角的颜色
     *
     * @param frameCornerColor 扫描框边角的颜色
     */
    fun setFrameCornerColor(@ColorInt frameCornerColor: Int) {
        this.frameCornerColor = frameCornerColor
    }

    /**
     * 设置提示文本距离扫描区的间距
     *
     * @param labelTextPadding 提示文本距离扫描框的间距
     */
    fun setLabelTextPadding(labelTextPadding: Float) {
        this.labelTextPadding = labelTextPadding
    }

    /**
     * 设置提示文本距离扫描框的间距
     *
     * @param labelTextPadding 提示文本距离扫描框的间距
     * @param unit             单位；比如：{@link TypedValue#COMPLEX_UNIT_DIP}；如需了解更多可查看：{@link TypedValue}
     */
    fun setLabelTextPadding(labelTextPadding: Float, unit: Int) {
        this.labelTextPadding = TypedValue.applyDimension(unit, labelTextPadding, resources.displayMetrics)
    }

    /**
     * 设置提示文本的宽度，默认为View的宽度
     *
     * @param labelTextWidth 提示文本的宽度
     */
    fun setLabelTextWidth(labelTextWidth: Int) {
        this.labelTextWidth = labelTextWidth
    }

    /**
     * 设置提示文本显示位置
     *
     * @param labelTextLocation 提示文本显示位置
     */
    fun setLabelTextLocation(labelTextLocation: TextLocation) {
        this.labelTextLocation = labelTextLocation
    }

    /**
     * 设置提示文本信息
     *
     * @param labelText 提示文本信息
     */
    fun setLabelText(labelText: String?) {
        this.labelText = labelText
    }

    /**
     * 设置提示文本字体颜色
     *
     * @param color 提示文本字体颜色
     */
    fun setLabelTextColor(@ColorInt color: Int) {
        this.labelTextColor = color
    }

    /**
     * 设置提示文本字体颜色
     *
     * @param id 提示文本字体颜色资源ID
     */
    fun setLabelTextColorResource(@ColorRes id: Int) {
        this.labelTextColor = getColor(context, id)
    }

    /**
     * 设置提示文本字体大小
     *
     * @param textSize 提示文本字体大小
     */
    fun setLabelTextSize(textSize: Float) {
        this.labelTextSize = textSize
    }

    /**
     * 设置提示文本字体大小
     *
     * @param textSize 提示文本字体大小
     * @param unit     单位；比如：{@link TypedValue#COMPLEX_UNIT_SP}；如需了解更多可查看：{@link TypedValue}
     */
    fun setLabelTextSize(textSize: Float, unit: Int) {
        this.labelTextSize = TypedValue.applyDimension(unit, textSize, resources.displayMetrics)
    }

    /**
     * 设置激光样式
     *
     * @param laserStyle 激光样式
     */
    fun setLaserStyle(laserStyle: LaserStyle) {
        this.laserStyle = laserStyle
    }

    /**
     * 设置网格激光扫描列数
     *
     * @param laserGridColumn 网格激光扫描列数
     */
    fun setLaserGridColumn(laserGridColumn: Int) {
        this.laserGridColumn = laserGridColumn
    }

    /**
     * 设置网格激光扫描高度，为0时，表示动态铺满
     *
     * @param laserGridHeight 网格激光扫描高度
     */
    fun setLaserGridHeight(laserGridHeight: Int) {
        this.laserGridHeight = laserGridHeight.toFloat()
    }

    /**
     * 设置扫描框边角的宽
     *
     * @param frameCornerStrokeWidth 扫描框边角的宽
     */
    fun setFrameCornerStrokeWidth(frameCornerStrokeWidth: Int) {
        this.frameCornerStrokeWidth = frameCornerStrokeWidth.toFloat()
    }

    /**
     * 设置扫描框边角的高
     *
     * @param frameCornerSize 扫描框边角的高
     */
    fun setFrameCornerSize(frameCornerSize: Int) {
        this.frameCornerSize = frameCornerSize.toFloat()
    }

    /**
     * 设置扫描框边角的高
     *
     * @param frameCornerSize 扫描框边角的高
     * @param unit            单位；比如：{@link TypedValue#COMPLEX_UNIT_DIP}；如需了解更多可查看：{@link TypedValue}
     */
    fun setFrameCornerSize(frameCornerSize: Int, unit: Int) {
        this.frameCornerSize = TypedValue.applyDimension(unit, frameCornerSize.toFloat(), resources.displayMetrics)
    }

    /**
     * 设置扫描框圆角半径
     *
     * @param frameCornerRadius 扫描框圆角半径
     */
    fun setFrameCornerRadius(frameCornerRadius: Int) {
        this.frameCornerRadius = frameCornerRadius.toFloat()
    }

    /**
     * 设置扫描框圆角半径
     *
     * @param frameCornerRadius 扫描框圆角半径
     * @param unit              单位；比如：{@link TypedValue#COMPLEX_UNIT_DIP}；如需了解更多可查看：{@link TypedValue}
     */
    fun setFrameCornerRadius(frameCornerRadius: Int, unit: Int) {
        this.frameCornerRadius = TypedValue.applyDimension(unit, frameCornerRadius.toFloat(), resources.displayMetrics)
    }

    /**
     * 设置激光扫描的速度：即：每次移动的距离
     *
     * @param laserMovementSpeed 激光扫描的速度
     */
    fun setLaserMovementSpeed(laserMovementSpeed: Int) {
        this.laserMovementSpeed = laserMovementSpeed.toFloat()
    }

    /**
     * 设置扫描线高度
     *
     * @param laserLineHeight 扫描线高度
     */
    fun setLaserLineHeight(laserLineHeight: Int) {
        this.laserLineHeight = laserLineHeight.toFloat()
    }

    /**
     * 设置边框线宽度
     *
     * @param frameLineStrokeWidth 边框线宽度
     */
    fun setFrameLineStrokeWidth(frameLineStrokeWidth: Int) {
        this.frameLineStrokeWidth = frameLineStrokeWidth.toFloat()
    }

    /**
     * 设置扫描框图片
     *
     * @param drawableResId 扫描框图片资源ID
     */
    fun setFrameDrawable(@DrawableRes drawableResId: Int) {
        setFrameBitmap(BitmapFactory.decodeResource(resources, drawableResId))
    }

    /**
     * 设置扫描框图片
     *
     * @param frameBitmap 扫描框图片
     */
    fun setFrameBitmap(frameBitmap: Bitmap?) {
        this.frameBitmap = frameBitmap
    }

    /**
     * 设置扫描动画延迟间隔时间，单位：毫秒
     *
     * @param laserAnimationInterval 扫描动画延迟间隔时间
     */
    fun setLaserAnimationInterval(laserAnimationInterval: Int) {
        this.laserAnimationInterval = laserAnimationInterval
    }

    /**
     * 设置结果点的颜色
     *
     * @param pointColor 结果点的颜色
     */
    fun setPointColor(@ColorInt pointColor: Int) {
        this.pointColor = pointColor
    }

    /**
     * 设置结果点描边的颜色
     *
     * @param pointStrokeColor 结果点描边的颜色
     */
    fun setPointStrokeColor(@ColorInt pointStrokeColor: Int) {
        this.pointStrokeColor = pointStrokeColor
    }

    /**
     * 设置结果点的半径
     *
     * @param pointRadius 结果点的半径
     */
    fun setPointRadius(pointRadius: Float) {
        this.pointRadius = pointRadius
    }

    /**
     * 设置结果点的半径
     *
     * @param pointRadius 结果点的半径
     * @param unit        单位；比如：{@link TypedValue#COMPLEX_UNIT_DIP}；如需了解更多可查看：{@link TypedValue}
     */
    fun setPointRadius(pointRadius: Float, unit: Int) {
        this.pointRadius = TypedValue.applyDimension(unit, pointRadius, resources.displayMetrics)
    }

    /**
     * 设置激光扫描自定义图片
     *
     * @param drawableResId 激光扫描自定义图片资源ID
     */
    fun setLaserDrawable(@DrawableRes drawableResId: Int) {
        setLaserBitmap(BitmapFactory.decodeResource(resources, drawableResId))
    }

    /**
     * 设置激光扫描自定义图片
     *
     * @param laserBitmap 激光扫描自定义图片
     */
    fun setLaserBitmap(laserBitmap: Bitmap?) {
        this.laserBitmap = laserBitmap
        scaleLaserBitmap()
    }

    /**
     * 设置结果点图片
     *
     * @param drawableResId 结果点图片资源ID
     */
    fun setPointDrawable(@DrawableRes drawableResId: Int) {
        setPointBitmap(BitmapFactory.decodeResource(resources, drawableResId))
    }

    /**
     * 设置结果点图片
     *
     * @param bitmap 结果点图片
     */
    fun setPointBitmap(bitmap: Bitmap?) {
        pointBitmap = bitmap
        pointBitmap?.let {
            pointRangeRadius = (it.width + it.height) / 4f * DEFAULT_RANGE_RATIO
        }
    }

    /**
     * 设置结果点的动画间隔时长；单位：毫秒
     *
     * @param pointAnimationInterval 结果点的动画间隔时长
     */
    fun setPointAnimationInterval(pointAnimationInterval: Int) {
        this.pointAnimationInterval = pointAnimationInterval
    }

    /**
     * 设置取景框样式；支持：classic：经典样式（带扫描框那种）、popular：流行样式（不带扫描框）
     *
     * @param viewfinderStyle 取景框样式
     */
    fun setViewfinderStyle(@ViewFinderStyle viewfinderStyle: Int) {
        this.viewfinderStyle = viewfinderStyle
    }

    /**
     * 设置扫描框的宽度
     *
     * @param frameWidth 扫描框的宽度
     */
    fun setFrameWidth(frameWidth: Int) {
        this.frameWidth = frameWidth
    }

    /**
     * 设置扫描框的高度
     *
     * @param frameHeight 扫描框的高度
     */
    fun setFrameHeight(frameHeight: Int) {
        this.frameHeight = frameHeight
    }

    /**
     * 设置扫描框的与视图宽的占比；默认：0.625
     *
     * @param frameRatio 扫描框的与视图宽的占比
     */
    fun setFrameRatio(frameRatio: Float) {
        this.frameRatio = frameRatio
    }

    /**
     * 设置扫描框左边的间距
     *
     * @param framePaddingLeft 扫描框左边的间距
     */
    fun setFramePaddingLeft(framePaddingLeft: Float) {
        this.framePaddingLeft = framePaddingLeft
    }

    /**
     * 设置扫描框顶部的间距
     *
     * @param framePaddingTop 扫描框顶部的间距
     */
    fun setFramePaddingTop(framePaddingTop: Float) {
        this.framePaddingTop = framePaddingTop
    }

    /**
     * 设置扫描框右边的间距
     *
     * @param framePaddingRight 扫描框右边的间距
     */
    fun setFramePaddingRight(framePaddingRight: Float) {
        this.framePaddingRight = framePaddingRight
    }

    /**
     * 设置扫描框的间距
     *
     * @param left   扫描框左边的间距
     * @param top    扫描框顶部的间距
     * @param right  扫描框左边的间距
     * @param bottom 扫描框底部的间距
     */
    fun setFramePadding(left: Float, top: Float, right: Float, bottom: Float) {
        this.framePaddingLeft = left
        this.framePaddingTop = top
        this.framePaddingRight = right
        this.framePaddingBottom = bottom
    }

    /**
     * 设置扫描框底部的间距
     *
     * @param framePaddingBottom 扫描框底部的间距
     */
    fun setFramePaddingBottom(framePaddingBottom: Float) {
        this.framePaddingBottom = framePaddingBottom
    }

    /**
     * 设置扫描框的对齐方式；默认居中对齐；即：{@link FrameGravity#CENTER}
     *
     * @param frameGravity 扫描框的对齐方式
     */
    fun setFrameGravity(frameGravity: FrameGravity) {
        this.frameGravity = frameGravity
    }

    /**
     * 设置是否显示结果点缩放动画；默认为：true
     *
     * @param pointAnimation 是否显示结果点缩放动画
     */
    fun setPointAnimation(pointAnimation: Boolean) {
        isPointAnimation = pointAnimation
    }

    /**
     * 设置结果点外圈描边的半径；默认为：{@link #pointRadius} 的 {@link #pointStrokeRatio} 倍
     *
     * @param pointStrokeRadius 结果点外圈描边的半径
     */
    fun setPointStrokeRadius(pointStrokeRadius: Float) {
        this.pointStrokeRadius = pointStrokeRadius
    }

    /**
     * 设置显示结果点动画的缩放速度；默认为：0.02 /  {@link #laserAnimationInterval}
     *
     * @param zoomSpeed 显示结果点动画的缩放速度
     */
    fun setZoomSpeed(zoomSpeed: Float) {
        this.zoomSpeed = zoomSpeed
    }

    /**
     * 设置结果点有效点击范围半径；默认为：{@link #pointStrokeRadius} 的 {@link #DEFAULT_RANGE_RATIO} 倍；
     * 需要注意的是，因为有效点击范围是建立在结果点的基础之上才有意义的；其主要目的是为了支持一定的容错范围；所以如果在此方法之后；
     * 有直接或间接有调用{@link #setPointBitmap(Bitmap)}方法的话，那么 {@link #pointRangeRadius}的值将会被覆盖。
     *
     * @param pointRangeRadius 结果点有效点击范围半径
     */
    fun setPointRangeRadius(pointRangeRadius: Float) {
        this.pointRangeRadius = pointRangeRadius
    }

    /**
     * 设置扫描线位图的宽度比例；默认为：0.625；此方法会改变{@link #laserBitmapWidth}
     *
     * @param laserBitmapRatio 扫描线位图的宽度比例
     */
    fun setLaserBitmapRatio(laserBitmapRatio: Float) {
        this.laserBitmapRatio = laserBitmapRatio
        if (minDimension > 0) {
            laserBitmapWidth = minDimension * laserBitmapRatio
            scaleLaserBitmap()
        }
    }

    /**
     * 设置扫描线位图的宽度
     *
     * @param laserBitmapWidth 扫描线位图的宽度
     */
    fun setLaserBitmapWidth(laserBitmapWidth: Float) {
        this.laserBitmapWidth = laserBitmapWidth
        scaleLaserBitmap()
    }

    /**
     * 设置是否完全刷新；适用于当 {@link #viewfinderStyle} 为 {@link ViewfinderStyle#CLASSIC} 时；
     * {@link #fullRefresh}的默认值为：{@code false}；
     * 当设置为{@code false}时，则使用局部刷新；即：视图只刷新扫描区域；
     * 当设置为{@code true}时，则使用完全刷新，即：视图会刷新全部区域。
     *
     * @param fullRefresh 是否完全刷新
     */
    fun setFullRefresh(fullRefresh: Boolean) {
        this.fullRefresh = fullRefresh
    }

    // Kotlin 扩展函数：用于将 Float 四舍五入取整
    private fun Float.roundToInt(): Int = kotlin.math.round(this).toInt()

    /**
     * 缩放扫描线位图
     */
    private fun scaleLaserBitmap() {
        laserBitmap?.let { bitmap ->
            if (laserBitmapWidth > 0) {
                val ratio = laserBitmapWidth / bitmap.width
                val matrix = Matrix()
                matrix.postScale(ratio, ratio)
                val w = bitmap.width
                val h = bitmap.height
                laserBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
            }
        }
    }

    /**
     * 初始化扫描框
     */
    private fun initFrame(width: Int, height: Int) {
        minDimension = minOf(width, height)
        val size = (minDimension * frameRatio).toInt()

        if (laserBitmapWidth <= 0) {
            laserBitmapWidth = minDimension * laserBitmapRatio
            scaleLaserBitmap()
        }

        if (frameWidth <= 0 || frameWidth > width) {
            frameWidth = size
        }

        if (frameHeight <= 0 || frameHeight > height) {
            frameHeight = size
        }

        if (labelTextWidth <= 0) {
            labelTextWidth = width - paddingLeft - paddingRight
        }

        var leftOffsets = (width - frameWidth) / 2f + framePaddingLeft - framePaddingRight
        var topOffsets = (height - frameHeight) / 2f + framePaddingTop - framePaddingBottom

        when (frameGravity) {
            FrameGravity.LEFT -> leftOffsets = framePaddingLeft
            FrameGravity.TOP -> topOffsets = framePaddingTop
            FrameGravity.RIGHT -> leftOffsets = width - frameWidth + framePaddingRight
            FrameGravity.BOTTOM -> topOffsets = height - frameHeight + framePaddingBottom
            else -> {}
        }

        frame = RectF(leftOffsets, topOffsets, leftOffsets + frameWidth, topOffsets + frameHeight)
    }


    /**
     * 获取颜色
     */
    @Suppress("DEPRECATION")
    private fun getColor(context: Context, @ColorRes id: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(id)
        } else {
            context.resources.getColor(id)
        }
    }

    /**
     * 根据 drawable 获取对应的 bitmap
     */
    private fun getBitmapFormDrawable(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, bitmap.width, bitmap.height)
        drawable.draw(canvas)
        return bitmap
    }
}
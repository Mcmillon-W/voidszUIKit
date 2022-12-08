package com.hp.voidszuikit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.StyleRes

/**
 * @author j4zib on 18/11/22.
 */
open class SquircleFrameLayout @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(ctx, attrs, defStyleAttr, defStyleRes) {

    private var cornerRadiusList: MutableList<Float> = mutableListOf(0f, 0f, 0f, 0f)
    private var path = Path()

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SquircleFrameLayout,
            defStyleAttr,
            defStyleRes
        )
        try {
            val cornerRadius = a.getDimension(R.styleable.SquircleFrameLayout_cornerRadius, -1f)
            val cornerRadiusTopLeft =
                a.getDimension(R.styleable.SquircleFrameLayout_cornerRadiusTopLeft, -1f)
            val cornerRadiusTopRight =
                a.getDimension(R.styleable.SquircleFrameLayout_cornerRadiusTopRight, -1f)
            val cornerRadiusBottomLeft =
                a.getDimension(R.styleable.SquircleFrameLayout_cornerRadiusBottomLeft, -1f)
            val cornerRadiusBottomRight =
                a.getDimension(R.styleable.SquircleFrameLayout_cornerRadiusBottomRight, -1f)
            if (cornerRadius > 0) {
                cornerRadiusList = mutableListOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius)
            }
            cornerRadiusTopLeft.takeIf { it > 0f }?.let {
                cornerRadiusList[0] = it
            }
            cornerRadiusTopRight.takeIf { it > 0f }?.let {
                cornerRadiusList[1] = it
            }
            cornerRadiusBottomRight.takeIf { it > 0f }?.let {
                cornerRadiusList[2] = it
            }
            cornerRadiusBottomLeft.takeIf { it > 0f }?.let {
                cornerRadiusList[3] = it
            }
        } finally {
            a.recycle()
        }
    }

    fun setCornerRadius(cornerRadiusList: List<Float>) {
        this.cornerRadiusList = cornerRadiusList.toMutableList()
        invalidate()
    }

    fun setCornerRadius(cornerRadius: Float) {
        this.cornerRadiusList = mutableListOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        path = SquircleHelper.getSquirclePath(
            cornerRadiusList, RectF(0f, 0f, width.toFloat(), height.toFloat())
        )
        this.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setConvexPath(path)
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
    }
}
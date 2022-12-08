package com.hp.voidszuikit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import androidx.annotation.ColorInt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


/**
 * Squircle implementation that mimics iOS squircles
 * Squircle is basically smoothening of rounded corners so that rounded shapes look more
 * organic and natural
 *
 * To smoothen the corners we are taking two radius for each corner: originalRadius, and finalRadius
 * finalRadius is basically originalRadius * SMOOTHENING_FACTOR, this is done to have some head start
 * while drawing a corner so that the roundness is not starting abruptly
 *
 * We are using three bezier curve point for each corner to mimic squircle
 * the first bezier curve start point sits at finalRadius, the middle bezier curve start point sits at
 * (r/sqrt(2), r/sqrt(2)) on the circle formed by the originalRadius,
 * the final bezier curve sits at finalRadius on the perpendicular side
 * we will also draw tangent to the originalRadius circle from (r/sqrt(2), r/sqrt(2)) and the point at which this
 * tangent intersects the sides of the rectangle will be the control points
 * two other control points will be at originalRadius of each side
 *
 * the tangent is drawn using original circle so that we can control radius of the squircle
 *
 */

object SquircleHelper {
    private const val DEFAULT_CORNER_RADIUS_SMOOTHENING = 3.5f
    fun getSquirclePath(radius: List<Float>, bounds: RectF): Path {
        var topLeftRadiusOrig = (radius.getSafely(0) ?: 0f)
        var topRightRadiusOrig = if (radius.size == 8) (radius.getSafely(2) ?: 0f) else (radius.getSafely(1) ?: 0f)
        var bottomRightRadiusOrig = if (radius.size == 8) (radius.getSafely(4) ?: 0f) else (radius.getSafely(2) ?: 0f)
        var bottomLeftRadiusOrig = if (radius.size == 8) (radius.getSafely(6) ?: 0f) else (radius.getSafely(3) ?: 0f)

        val topLeftRadius = topLeftRadiusOrig * DEFAULT_CORNER_RADIUS_SMOOTHENING
        val topRightRadius = topRightRadiusOrig * DEFAULT_CORNER_RADIUS_SMOOTHENING
        val bottomRightRadius = bottomRightRadiusOrig * DEFAULT_CORNER_RADIUS_SMOOTHENING
        val bottomLeftRadius = bottomLeftRadiusOrig * DEFAULT_CORNER_RADIUS_SMOOTHENING

        val topLeftRadiusFinal = min(
            min(topLeftRadius, max(bounds.width() - topRightRadius, bounds.width() / 2f)),
            min(topLeftRadius, max(bounds.height() - bottomLeftRadius, bounds.height() / 2f))
        )
        val topRightRadiusFinal = min(
            min(topRightRadius, max(bounds.width() - topLeftRadius, bounds.width() / 2f)),
            min(topRightRadius, max(bounds.height() - bottomRightRadius, bounds.height() / 2f))
        )
        val bottomLeftRadiusFinal = min(
            min(bottomLeftRadius, max(bounds.width() - bottomRightRadius, bounds.width() / 2f)),
            min(bottomLeftRadius, max(bounds.height() - topLeftRadius, bounds.height() / 2f))
        )
        val bottomRightRadiusFinal = min(
            min(bottomRightRadius, max(bounds.width() - bottomLeftRadius, bounds.width() / 2f)),
            min(bottomRightRadius, max(bounds.height() - topRightRadius, bounds.height() / 2f))
        )

        topLeftRadiusOrig = min(topLeftRadiusOrig, topLeftRadiusFinal)
        topRightRadiusOrig = min(topRightRadiusOrig, topRightRadiusFinal)
        bottomRightRadiusOrig = min(bottomRightRadiusOrig, bottomRightRadiusFinal)
        bottomLeftRadiusOrig = min(bottomLeftRadiusOrig, bottomLeftRadiusFinal)

        val newPath = Path()
        newPath.moveTo(bounds.left + topLeftRadiusFinal, bounds.top)
        newPath.lineTo(bounds.right - topRightRadiusFinal, bounds.top)
        newPath.cubicTo(
            bounds.right - topRightRadiusOrig,
            bounds.top,
            bounds.right - (topRightRadiusOrig * (2f - sqrt(2f))),
            bounds.top,
            bounds.right - (topRightRadiusOrig * (sqrt(2f) - 1) / sqrt(2f)),
            bounds.top + (topRightRadiusOrig * (sqrt(2f) - 1) / sqrt(2f))
        )
        newPath.cubicTo(
            bounds.right,
            bounds.top + (topRightRadiusOrig * (2f - sqrt(2f))),
            bounds.right,
            bounds.top + topRightRadiusOrig,
            bounds.right,
            bounds.top + topRightRadiusFinal
        )
        newPath.lineTo(bounds.right, bounds.bottom - bottomRightRadiusFinal)
        newPath.cubicTo(
            bounds.right,
            bounds.bottom - bottomRightRadiusOrig,
            bounds.right,
            bounds.bottom - (bottomRightRadiusOrig * (2f - sqrt(2f))),
            bounds.right - (bottomRightRadiusOrig * (sqrt(2f) - 1) / sqrt(2f)),
            bounds.bottom - (bottomRightRadiusOrig * (sqrt(2f) - 1) / sqrt(2f))
        )
        newPath.cubicTo(
            bounds.right - (bottomRightRadiusOrig * (2f - sqrt(2f))),
            bounds.bottom,
            bounds.right - bottomRightRadiusOrig,
            bounds.bottom,
            bounds.right - bottomRightRadiusFinal,
            bounds.bottom
        )
        newPath.lineTo(bounds.left + bottomLeftRadiusFinal, bounds.bottom)
        newPath.cubicTo(
            bounds.left + bottomLeftRadiusOrig,
            bounds.bottom,
            bounds.left + (bottomLeftRadiusOrig * (2f - sqrt(2f))),
            bounds.bottom,
            bounds.left + (bottomLeftRadiusOrig * (sqrt(2f) - 1) / sqrt(2f)),
            bounds.bottom - (bottomLeftRadiusOrig * (sqrt(2f) - 1) / sqrt(2f))
        )
        newPath.cubicTo(
            bounds.left,
            bounds.bottom - (bottomLeftRadiusOrig * (2f - sqrt(2f))),
            bounds.left,
            bounds.bottom - bottomLeftRadiusOrig,
            bounds.left,
            bounds.bottom - bottomLeftRadiusFinal
        )
        newPath.lineTo(bounds.left, bounds.top + topLeftRadiusFinal)
        newPath.cubicTo(
            bounds.left,
            bounds.top + topLeftRadiusOrig,
            bounds.left,
            bounds.top + (topLeftRadiusOrig * (2f - sqrt(2f))),
            bounds.left + (topLeftRadiusOrig * (sqrt(2f) - 1) / sqrt(2f)),
            bounds.top + (topLeftRadiusOrig * (sqrt(2f) - 1) / sqrt(2f))
        )
        newPath.cubicTo(
            bounds.left + (topLeftRadiusOrig * (2f - sqrt(2f))),
            bounds.top,
            bounds.left + topLeftRadiusOrig,
            bounds.top,
            bounds.left + topLeftRadiusFinal,
            bounds.top
        )
        newPath.close()
        return newPath
    }

    class SquircleShape(val radius: List<Float>, val strokeWidth: Float? = null) : Shape() {

        var path = Path()

        override fun onResize(width: Float, height: Float) {
            super.onResize(width, height)
            val strokePadding = (strokeWidth ?: 0f) / 2f
            val bounds = RectF(
                0f + strokePadding,
                0f + strokePadding,
                width - strokePadding,
                height - strokePadding
            )
            path = getSquirclePath(radius, bounds)
        }

        override fun draw(canvas: Canvas?, paint: Paint?) {
            if (paint != null) {
                canvas?.drawPath(path, paint)
            }
        }
    }

    class ShapeDrawableWithBorder(
        shape: Shape?,
        @ColorInt val fillColor: Int? = null,
        @ColorInt val strokeColor: Int? = null,
        val strokeWidth: Float? = null,
        val dashGap: Float? = null,
        val dashWidth: Float? = null,
        @ColorInt val gradientColors: List<Int>? = null,
        val gradientDirection: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT
    ) : ShapeDrawable(shape) {

        private val fillPaint: Paint = this.paint
        private val strokePaint: Paint = Paint(fillPaint)

        init {
            fillPaint.isAntiAlias = true
            strokePaint.isAntiAlias = true
            strokePaint.style = Paint.Style.STROKE
            if (strokeWidth != null)
                strokePaint.strokeWidth = strokeWidth
            if (strokeColor != null)
                strokePaint.color = strokeColor
            if (dashWidth != null && dashGap != null) {
                strokePaint.pathEffect = DashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
            }
        }

        private fun setGradientPaint() {
            if (gradientColors != null) {
                var x0 = 0f
                var y0 = 0f
                var x1 = 0f
                var y1 = 0f
                when (gradientDirection) {
                    GradientDrawable.Orientation.TOP_BOTTOM -> {
                        x0 = bounds.left.toFloat()
                        y0 = bounds.top.toFloat()
                        x1 = bounds.left.toFloat()
                        y1 = bounds.bottom.toFloat()
                    }
                    GradientDrawable.Orientation.TR_BL -> {
                        x0 = bounds.right.toFloat()
                        y0 = bounds.top.toFloat()
                        x1 = bounds.left.toFloat()
                        y1 = bounds.bottom.toFloat()
                    }
                    GradientDrawable.Orientation.RIGHT_LEFT -> {
                        x0 = bounds.right.toFloat()
                        y0 = bounds.top.toFloat()
                        x1 = bounds.left.toFloat()
                        y1 = bounds.top.toFloat()
                    }
                    GradientDrawable.Orientation.BR_TL -> {
                        x0 = bounds.right.toFloat()
                        y0 = bounds.bottom.toFloat()
                        x1 = bounds.left.toFloat()
                        y1 = bounds.top.toFloat()
                    }
                    GradientDrawable.Orientation.BOTTOM_TOP -> {
                        x0 = bounds.left.toFloat()
                        y0 = bounds.bottom.toFloat()
                        x1 = bounds.left.toFloat()
                        y1 = bounds.top.toFloat()
                    }
                    GradientDrawable.Orientation.BL_TR -> {
                        x0 = bounds.left.toFloat()
                        y0 = bounds.bottom.toFloat()
                        x1 = bounds.right.toFloat()
                        y1 = bounds.top.toFloat()
                    }
                    GradientDrawable.Orientation.LEFT_RIGHT -> {
                        x0 = bounds.left.toFloat()
                        y0 = bounds.top.toFloat()
                        x1 = bounds.right.toFloat()
                        y1 = bounds.top.toFloat()
                    }
                    GradientDrawable.Orientation.TL_BR -> {
                        x0 = bounds.left.toFloat()
                        y0 = bounds.top.toFloat()
                        x1 = bounds.right.toFloat()
                        y1 = bounds.bottom.toFloat()
                    }
                    else -> {
                        x0 = bounds.left.toFloat()
                        y0 = bounds.top.toFloat()
                        x1 = bounds.right.toFloat()
                        y1 = bounds.top.toFloat()
                    }
                }
                val gradient = LinearGradient(
                    x0,
                    y0,
                    x1,
                    y1,
                    gradientColors.toIntArray(),
                    null,
                    Shader.TileMode.CLAMP
                )
                fillPaint.color = -1
                fillPaint.shader = gradient
            } else {
                fillPaint.color = fillColor ?: Color.TRANSPARENT
            }
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            setGradientPaint()
        }

        override fun onDraw(shape: Shape?, canvas: Canvas?, paint: Paint?) {
            shape?.draw(canvas, fillPaint)
            if (strokeWidth != null && strokeColor != null)
                shape?.draw(canvas, strokePaint)
        }
    }

    fun getSquircleShapeDrawable(
        cornerRadii: List<Float>,
        fillColor: Int? = null,
        strokeColor: Int? = null,
        strokeWidth: Float? = null,
        dashGap: Float? = null,
        dashWidth: Float? = null,
        @ColorInt gradientColors: List<Int>? = null,
        gradientDirection: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT
    ): ShapeDrawableWithBorder {
        return ShapeDrawableWithBorder(SquircleShape(cornerRadii, strokeWidth), fillColor, strokeColor, strokeWidth, dashGap, dashWidth, gradientColors, gradientDirection)
    }
}
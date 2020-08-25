package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting

class IconLinkSpan(
    private val linkDrawable: Drawable,
    @ColorInt
    private val iconColor: Int,
    @Px
    private val padding: Float,
    @ColorInt
    private val textColor: Int,
    dotWidth: Float = 6f
) : ReplacementSpan() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var iconSize = 0
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var textWidth = 0f
    private val dashs = DashPathEffect(floatArrayOf(dotWidth, dotWidth), 0f)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path() // TODO: Что это?

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val textStart = x + iconSize + padding
        paint.forLine {
            path.reset() // TODO: Зачем?
            path.moveTo(textStart, bottom.toFloat()) // TODO: Понять как это расчитывается
            path.lineTo(textStart + textWidth, bottom.toFloat())
            canvas.drawPath(path, paint)
        }

        paint.forIcon {
            canvas.save()
            val trY = bottom - linkDrawable.bounds.bottom // смещение по Y
            canvas.translate(x, trY.toFloat())
            linkDrawable.draw(canvas)
            canvas.restore()
        }

        paint.forText {
            canvas.drawText(text, start, end, textStart, y.toFloat(), paint) // TODO: Понять как это расчитывается
        }
    }


    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int { // TODO: Понять как это расчитывается
        if (fm != null) {
            iconSize = fm.descent - fm.ascent //fontsize
            linkDrawable.setBounds(0, 0, iconSize, iconSize)
            linkDrawable.setTint(iconColor)
        }
        textWidth = paint.measureText(text.toString(), start, end)
        return (iconSize + padding + textWidth).toInt()
    }


    private inline fun Paint.forLine(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        val oldWidth = strokeWidth

        // TODO: Что это?
        pathEffect = dashs
        color = textColor
        style = Paint.Style.STROKE
        strokeWidth = 0f

        block()

        color = oldColor
        style = oldStyle
        strokeWidth = oldWidth
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldColor = color

        color = textColor

        block()

        color = oldColor
    }

    private inline fun Paint.forIcon(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style

        color = textColor
        style = Paint.Style.STROKE

        block()

        color = oldColor
        style = oldStyle
    }
}
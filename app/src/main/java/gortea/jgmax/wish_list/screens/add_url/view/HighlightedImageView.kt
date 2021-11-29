package gortea.jgmax.wish_list.screens.add_url.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView

class HighlightedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var isHighlightingEnabled = false

    private var listener: HighlightingListener? = null

    private var isDrawing = false
    private var startDrawingX = 0f
    private var startDrawingY = 0f
    private var endDrawingX = 0f
    private var endDrawingY = 0f

    // Selection Zone
    private val left: Float
        get() = startDrawingX.coerceAtLeast(endDrawingX)
    private val right: Float
        get() = endDrawingX.coerceAtMost(startDrawingX)
    private val top: Float
        get() = startDrawingY.coerceAtLeast(endDrawingY)
    private val bottom: Float
        get() = endDrawingY.coerceAtMost(startDrawingY)

    private val outerFillColor = 0x77000000
    private val foregroundFillPaint = Paint().apply {
        color = outerFillColor
        style = Paint.Style.FILL
    }

    private val foregroundPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
    }

    var bitmap: Bitmap? = null
        private set

    fun enableHighlighting(listener: HighlightingListener) {
        isHighlightingEnabled = true
        this.listener = listener
    }

    fun disableHighlighting() {
        isHighlightingEnabled = false
        listener = null
        restoreHighlighting()
    }

    private fun restoreHighlighting() {
        isDrawing = false
        startDrawingX = 0f
        startDrawingY = 0f
        endDrawingX = 0f
        endDrawingY = 0f
        super.setImageBitmap(bitmap)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        if (!isHighlightingEnabled) {
            bitmap = bm
            super.setImageBitmap(bm)
        } else if (bitmap != bm) {
            throw IllegalStateException("HighlightedImageView is in highlighting mode")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isHighlightingEnabled) {
            event?.let {
                val handled = when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startDrawingX = event.x
                        startDrawingY = event.y
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        isDrawing = true
                        endDrawingX = event.x
                        endDrawingY = event.y
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isDrawing = false
                        listener?.onComplete(left, top, right, bottom)
                        true
                    }
                    else -> super.onTouchEvent(event)
                }
                if (isDrawing) {
                    drawHighlighting()
                }
                return handled
            }
        } else {
            isDrawing = false
        }
        return super.onTouchEvent(event)
    }

    private fun drawHighlighting() {
        bitmap?.let {
            val foregroundBitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            val foregroundCanvas = Canvas(foregroundBitmap)

            foregroundFillPaint.xfermode = null
            foregroundCanvas.drawPaint(foregroundFillPaint)
            foregroundFillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            foregroundCanvas.drawRect(left, top, right, bottom, foregroundFillPaint)

            val backgroundBitmap = Bitmap.createBitmap(it)
            val backgroundCanvas = Canvas(backgroundBitmap)
            backgroundCanvas.drawBitmap(foregroundBitmap, 0f, 0f, foregroundPaint)
            super.setImageBitmap(backgroundBitmap)
        }
    }

    fun interface HighlightingListener {
        fun onComplete(left: Float, top: Float, right: Float, bottom: Float)
    }
}
package gortea.jgmax.wish_list.screens.add_url.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.parcelize.Parcelize


class SelectableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var isSelectionEnabled = false

    private var listener: SelectionListener? = null

    private var isDrawing = false
    private var startDrawingX = 0f
    private var startDrawingY = 0f
    private var endDrawingX = 0f
    private var endDrawingY = 0f

    private var maxX = 0
    private var maxY = 0

    // Selection Zone
    private val left: Float
        get() = startDrawingX.coerceAtMost(endDrawingX).coerceIn(0f, maxX.toFloat())
    private val right: Float
        get() = endDrawingX.coerceAtLeast(startDrawingX).coerceIn(0f, maxX.toFloat())
    private val top: Float
        get() = startDrawingY.coerceAtMost(endDrawingY).coerceIn(0f, maxY.toFloat())
    private val bottom: Float
        get() = endDrawingY.coerceAtLeast(startDrawingY).coerceIn(0f, maxY.toFloat())

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

    fun enableSelection() {
        isSelectionEnabled = true
        drawSelection()
    }

    fun setSelectionListener(listener: SelectionListener?) {
        this.listener = listener
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState())
        bundle.putParcelable(
            SELECTED_POSITION_STATE_KEY,
            SelectedPosition(left, top, right, bottom)
        )
        bundle.putBoolean(IS_SELECTION_ENABLED_KEY, isSelectionEnabled)
        bundle.putParcelable(BACKGROUND_BITMAP_KEY, bitmap)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            bitmap = state.getParcelable(BACKGROUND_BITMAP_KEY)
            isSelectionEnabled = state.getBoolean(IS_SELECTION_ENABLED_KEY, false)
            if (isSelectionEnabled) {
                state.getParcelable<SelectedPosition>(SELECTED_POSITION_STATE_KEY)?.let { pos ->
                    highlight(pos, true)
                } ?: drawSelection()
            }
            super.onRestoreInstanceState(state.getParcelable(SUPER_STATE_KEY))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun highlight(position: SelectedPosition, force: Boolean) {
        position.let {
            if (
                isSelectionEnabled
                && (force || it.left != left || it.right != right || it.top != top || it.bottom != bottom)
            ) {
                startDrawingX = it.left
                endDrawingX = it.right
                startDrawingY = it.top
                endDrawingY = it.bottom
                drawSelection()
            }
        }
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        maxX = xNew
        maxY = yNew
        if (isSelectionEnabled) {
            drawSelection()
        }
    }

    fun highlight(position: SelectedPosition) {
        highlight(position, false)
    }

    fun disableSelection() {
        isSelectionEnabled = false
        removeSelection()
    }

    private fun removeSelection() {
        isDrawing = false
        startDrawingX = 0f
        startDrawingY = 0f
        endDrawingX = 0f
        endDrawingY = 0f
        super.setImageBitmap(bitmap)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        if (!isSelectionEnabled && bm != bitmap) {
            bitmap = bm
            super.setImageBitmap(bitmap)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isSelectionEnabled) {
            event?.let {
                val handled = when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startDrawingX = event.x
                        startDrawingY = event.y
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        endDrawingX = event.x
                        endDrawingY = event.y
                        isDrawing = left < right && top < bottom
                        if (isDrawing) {
                            listener?.inProcess(this, SelectedPosition(left, top, right, bottom))
                        }
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        if (isDrawing) {
                            listener?.onComplete(this, SelectedPosition(left, top, right, bottom))
                        }
                        isDrawing = false
                        true
                    }
                    else -> super.onTouchEvent(event)
                }
                if (isDrawing) {
                    drawSelection()
                }
                return handled
            }
        } else {
            isDrawing = false
        }
        return super.onTouchEvent(event)
    }

    private fun drawSelection() {
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

    @Parcelize
    data class SelectedPosition(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float
    ) : Parcelable

    interface SelectionListener {
        fun inProcess(view: SelectableImageView, position: SelectedPosition)
        fun onComplete(view: SelectableImageView, position: SelectedPosition)
    }

    private companion object {
        private const val SUPER_STATE_KEY = "SUPER_STATE_KEY"
        private const val SELECTED_POSITION_STATE_KEY = "SELECTED_POSITION_STATE_KEY"
        private const val IS_SELECTION_ENABLED_KEY = "IS_SELECTION_ENABLED_KEY"
        private const val BACKGROUND_BITMAP_KEY = "BACKGROUND_BITMAP_KEY"
    }
}
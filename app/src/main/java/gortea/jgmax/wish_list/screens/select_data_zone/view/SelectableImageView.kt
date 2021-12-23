package gortea.jgmax.wish_list.screens.select_data_zone.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView
import gortea.jgmax.wish_list.extentions.cache
import gortea.jgmax.wish_list.extentions.decodeBitmapFromCache
import gortea.jgmax.wish_list.extentions.removeBitmapCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


class SelectableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    var isSelectionEnabled = false
        private set
    private var isScrollEnabled: Boolean
        get() = !isSelectionEnabled
        set(value) {
            isSelectionEnabled = !value
        }

    private var restoreSelection = true

    private var topScrollY = 0

    private val maxViewHeight = resources.displayMetrics.heightPixels

    private var listener: SelectionListener? = null

    private var isDrawing = false
    private var startDrawingX = 0
    private var startDrawingY = 0
    private var endDrawingX = 0
    private var endDrawingY = 0

    private var maxX = 0
    private var maxY = 0

    // Selection Zone
    private val leftSelection: Int
        get() = startDrawingX.coerceAtMost(endDrawingX).coerceIn(0..maxX)
    private val rightSelection: Int
        get() = endDrawingX.coerceAtLeast(startDrawingX).coerceIn(0..maxX)
    private val topSelection: Int
        get() = startDrawingY.coerceAtMost(endDrawingY).coerceIn(0..maxY)
    private val bottomSelection: Int
        get() = endDrawingY.coerceAtLeast(startDrawingY).coerceIn(0..maxY)

    private val outerFillColor = 0x77000000
    private val foregroundFillPaint = Paint().apply {
        color = outerFillColor
        style = Paint.Style.FILL
    }

    private val foregroundPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
    }

    private var visibleBitmap: Bitmap? = null
    private var fullBitmap: Bitmap? = null
        set(value) {
            field = value
            CoroutineScope(Dispatchers.IO).launch {
                removeBitmapCache(FULL_BITMAP_CACHE_FILE, context.cacheDir)
                value?.cache(FULL_BITMAP_CACHE_FILE, context.cacheDir)
            }
        }
        get() {
            if (field == null) {
                field = decodeBitmapFromCache(FULL_BITMAP_CACHE_FILE, context.cacheDir)
            }
            return field
        }

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
            SelectedPosition(
                left = leftSelection,
                top = topSelection,
                right = rightSelection,
                bottom = bottomSelection
            )
        )
        bundle.putInt(SCROLL_POSITION_KEY, topScrollY)
        bundle.putBoolean(IS_SELECTION_ENABLED_KEY, isSelectionEnabled)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val topY = state.getInt(SCROLL_POSITION_KEY, 0)

            topScrollY = fullBitmap?.let {
                topY.coerceAtMost(
                    (it.height - maxViewHeight).coerceAtLeast(0)
                )
            } ?: 0

            drawScroll(topScrollY)
            if (restoreSelection) {
                isSelectionEnabled = state.getBoolean(IS_SELECTION_ENABLED_KEY, false)
                if (isSelectionEnabled) {
                    state.getParcelable<SelectedPosition>(SELECTED_POSITION_STATE_KEY)?.let { pos ->
                        startDrawingX = pos.left
                        endDrawingX = pos.right
                        startDrawingY = pos.top
                        endDrawingY = pos.bottom
                        drawSelection()
                    }
                }
            }
            super.onRestoreInstanceState(state.getParcelable(SUPER_STATE_KEY))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        maxX = xNew
        maxY = yNew
        if (isSelectionEnabled) {
            drawSelection()
        } else if (isScrollEnabled) {
            drawScroll(topScrollY)
        }
    }

    fun disableSelection() {
        isSelectionEnabled = false
        removeSelection()
    }

    private fun removeSelection() {
        isDrawing = false
        startDrawingX = 0
        startDrawingY = 0
        endDrawingX = 0
        endDrawingY = 0
        super.setImageBitmap(visibleBitmap)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        if (!isSelectionEnabled && bm?.sameAs(fullBitmap) != true) {
            restoreSelection = false
            val prevFull = fullBitmap
            fullBitmap = bm
            topScrollY = 0
            drawScroll(topScrollY)
            prevFull?.recycle()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isSelectionEnabled) {
            return handleDrawing(event)
        } else if (isScrollEnabled) {
            return handleScroll(event)
        }
        return super.onTouchEvent(event)
    }

    private fun handleDrawing(event: MotionEvent?): Boolean {
        return event?.let {
            val handled = when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startDrawingX = event.x.toInt()
                    startDrawingY = event.y.toInt()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    endDrawingX = event.x.toInt()
                    endDrawingY = event.y.toInt()
                    isDrawing = leftSelection < rightSelection && topSelection < bottomSelection
                    if (isDrawing) {
                        listener?.inProcess(
                            fullBitmap,
                            SelectedPosition(
                                leftSelection,
                                topSelection + topScrollY,
                                rightSelection,
                                bottomSelection + topScrollY
                            )
                        )
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isDrawing) {
                        listener?.onComplete(
                            fullBitmap,
                            SelectedPosition(
                                leftSelection,
                                topSelection + topScrollY,
                                rightSelection,
                                bottomSelection + topScrollY
                            )
                        )
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
        } ?: super.onTouchEvent(event)
    }

    private var startMovement = 0
    private var topScrollYMovement = 0
    private fun handleScroll(event: MotionEvent?): Boolean {
        return event?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startMovement = event.y.toInt()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    fullBitmap?.let {
                        topScrollYMovement = topScrollY + startMovement - event.y.toInt()
                        val max = (it.height - maxViewHeight.coerceAtMost(it.height))
                        topScrollYMovement = topScrollYMovement.coerceIn(0..max)
                        drawScroll(topScrollYMovement)
                    }
                    true
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    topScrollY = topScrollYMovement
                    drawScroll(topScrollY)
                    true
                }
                else -> super.onTouchEvent(event)
            }
        } ?: super.onTouchEvent(event)
    }

    private fun drawScroll(topY: Int) {
        fullBitmap?.let {
            val prevVisible = visibleBitmap
            val height = maxViewHeight.coerceAtMost(it.height)
            visibleBitmap = Bitmap.createBitmap(it, 0, topY, it.width, height)
            super.setImageBitmap(visibleBitmap)
            prevVisible?.recycle()
        }
    }

    private fun drawSelection() {
        visibleBitmap?.let {
            val foregroundBitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            val foregroundCanvas = Canvas(foregroundBitmap)

            foregroundFillPaint.xfermode = null
            foregroundCanvas.drawPaint(foregroundFillPaint)
            foregroundFillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            foregroundCanvas.drawRect(
                leftSelection.toFloat(),
                topSelection.toFloat(),
                rightSelection.toFloat(),
                bottomSelection.toFloat(),
                foregroundFillPaint
            )

            val backgroundBitmap = Bitmap.createBitmap(it)
            val backgroundCanvas = Canvas(backgroundBitmap)
            backgroundCanvas.drawBitmap(foregroundBitmap, 0f, 0f, foregroundPaint)
            super.setImageBitmap(backgroundBitmap)
            true
        }
    }

    @Parcelize
    data class SelectedPosition(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    ) : Parcelable

    interface SelectionListener {
        fun inProcess(bitmap: Bitmap?, position: SelectedPosition)
        fun onComplete(bitmap: Bitmap?, position: SelectedPosition)
    }

    private companion object {
        private const val SUPER_STATE_KEY = "SUPER_STATE_KEY"
        private const val SELECTED_POSITION_STATE_KEY = "SELECTED_POSITION_STATE_KEY"
        private const val IS_SELECTION_ENABLED_KEY = "IS_SELECTION_ENABLED_KEY"
        private const val SCROLL_POSITION_KEY = "SCROLL_POSITION_KEY"
        private const val FULL_BITMAP_CACHE_FILE = "FULL_BITMAP_CACHE_FILE"
    }
}

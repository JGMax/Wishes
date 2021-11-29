package gortea.jgmax.wish_list.screens.add_url.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.core.widget.NestedScrollView

class LockableScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {
    var isScrollable = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> isScrollable && super.onTouchEvent(ev)
            else -> super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isScrollable && super.onInterceptTouchEvent(ev)
    }
}
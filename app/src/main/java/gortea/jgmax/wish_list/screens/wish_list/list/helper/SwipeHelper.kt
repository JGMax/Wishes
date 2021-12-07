package gortea.jgmax.wish_list.screens.wish_list.list.helper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeHelper(
    val onItemSwiped: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE,
    ItemTouchHelper.LEFT
) {
    private val paint = Paint()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = if (viewHolder.adapterPosition == RecyclerView.NO_POSITION)
            viewHolder.oldPosition
        else
            viewHolder.adapterPosition
        onItemSwiped(position)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.3f

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView: View = viewHolder.itemView
        // Draw background
        c.drawRect(
            itemView.right.toFloat() + dX,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat(),
            paint.apply { color = Color.RED }
        )
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}

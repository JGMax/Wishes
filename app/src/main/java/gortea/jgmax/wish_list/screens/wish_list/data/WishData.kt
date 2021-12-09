package gortea.jgmax.wish_list.screens.wish_list.data

import android.graphics.Bitmap
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel

data class WishData(
    val url: String,
    val title: String,
    val initialPrice: String,
    val currentPrice: String,
    val targetPrice: String,
    val icon: Bitmap?,
    val change: Long
) {
    val changeString: String
        get() = if (change <= 0) change.toString() else "+$change"
    val changeColor: Int
        get() = when {
            change < 0 -> R.color.positive_change
            change > 0 -> R.color.negative_change
            else -> R.color.no_change
        }

    companion object {
        fun fromModel(model: WishModel): WishData {
            val currentPrice = model.currentPrice ?: 0
            val initialPrice = model.params.initialPrice ?: 1
            val change = ((currentPrice.toFloat() / initialPrice.toFloat()) - 1) * 100
            return WishData(
                url = model.url,
                title = model.title,
                initialPrice = model.params.initialPrice?.toString() ?: "",
                currentPrice = model.currentPrice?.toString() ?: "",
                targetPrice = model.params.targetPrice?.toString() ?: "",
                change = change.toLong(),
                icon = model.params.icon
            )
        }
        val Empty = WishData(
            url = "",
            title = "",
            initialPrice = "",
            currentPrice = "",
            targetPrice = "",
            change = 0L,
            icon = null
        )
    }
}

package gortea.jgmax.wish_list.app.data.repository.models.wish

import android.graphics.Bitmap

data class Params(
    val targetPrice: Long?,
    val position: Position?,
    val initialPrice: Long?,
    val icon: Bitmap?,
    val notificationFrequency: Int
)

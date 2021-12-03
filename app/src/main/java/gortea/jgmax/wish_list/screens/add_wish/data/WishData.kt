package gortea.jgmax.wish_list.screens.add_wish.data

import gortea.jgmax.wish_list.app.data.repository.models.wish.Params
import gortea.jgmax.wish_list.app.data.repository.models.wish.Position
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.app.data.repository.models.wish.constants.DEFAULT_NOTIFICATION_FREQUENCY

data class WishData(
    val url: String,
    val title: String,
    val targetPrice: String,
    val currentPrice: String,
    val position: Position?
) {
    fun toModel(): WishModel {
        return WishModel(
            url = url,
            title = title,
            currentPrice = currentPrice.toLongOrNull(),
            params = Params(
                targetPrice = targetPrice.toLongOrNull(),
                notificationFrequency = DEFAULT_NOTIFICATION_FREQUENCY,
                position = position
            )
        )
    }

    companion object {
        fun fromModel(model: WishModel): WishData {
            return WishData(
                url = model.url,
                title = model.title,
                targetPrice = model.params.targetPrice?.toString() ?: "",
                currentPrice = model.currentPrice?.toString() ?: "",
                position = model.params.position
            )
        }
        val Default = WishData(
            url = "",
            title = "",
            targetPrice = "",
            currentPrice = "",
            position = null
        )
    }
}
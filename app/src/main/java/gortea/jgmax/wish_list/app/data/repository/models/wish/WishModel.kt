package gortea.jgmax.wish_list.app.data.repository.models.wish

import gortea.jgmax.wish_list.app.data.local.entity.Wish

data class WishModel(
    val title: String,
    val imageUrl: String,
    val group: String,
    val currentPrice: Long,
    val params: Params
) {
    companion object {
        fun fromEntity(entity: Wish): WishModel {
            return WishModel(
                title = entity.title,
                imageUrl = entity.imageUrl,
                group = entity.group,
                currentPrice = entity.currentPrice,
                params = Params(
                    url = entity.url,
                    targetPrice = entity.targetPrice,
                    notificationFrequency = entity.notificationFrequency
                )
            )
        }
    }
}
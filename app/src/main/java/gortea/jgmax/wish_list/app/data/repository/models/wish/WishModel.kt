package gortea.jgmax.wish_list.app.data.repository.models.wish

import gortea.jgmax.wish_list.app.data.local.entity.Wish

data class WishModel(
    val url: String,
    val title: String,
    val currentPrice: Long?,
    val params: Params
) {
    fun toEntity(): Wish {
        return Wish(
            url = url,
            title = title,
            currentPrice = requireNotNull(currentPrice),
            targetPrice = requireNotNull(params.targetPrice),
            notificationFrequency = params.notificationFrequency,
            priceLeft = requireNotNull(params.position?.left),
            priceTop = requireNotNull(params.position?.top),
            priceRight = requireNotNull(params.position?.right),
            priceBottom = requireNotNull(params.position?.bottom)
        )
    }

    companion object {
        fun fromEntity(entity: Wish): WishModel {
            return WishModel(
                url = entity.url,
                title = entity.title,
                currentPrice = entity.currentPrice,
                params = Params(
                    targetPrice = entity.targetPrice,
                    notificationFrequency = entity.notificationFrequency,
                    position = Position(
                        left = entity.priceLeft,
                        top = entity.priceTop,
                        right = entity.priceRight,
                        bottom = entity.priceBottom
                    )
                )
            )
        }
    }
}
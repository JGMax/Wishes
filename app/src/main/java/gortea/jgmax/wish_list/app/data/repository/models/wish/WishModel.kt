package gortea.jgmax.wish_list.app.data.repository.models.wish

import gortea.jgmax.wish_list.app.data.local.room.entity.Wish

data class WishModel(
    val id: Long? = null,
    val url: String,
    val title: String,
    val currentPrice: Long?,
    val params: Params
) {
    fun toEntity(): Wish {
        return Wish(
            id = id ?: Wish.DEFAULT_ID,
            url = url,
            title = title,
            icon = params.icon,
            currentPrice = requireNotNull(currentPrice),
            targetPrice = requireNotNull(params.targetPrice),
            initialPrice = requireNotNull(params.initialPrice),
            priceLeft = requireNotNull(params.position?.left),
            priceTop = requireNotNull(params.position?.top),
            priceRight = requireNotNull(params.position?.right),
            priceBottom = requireNotNull(params.position?.bottom)
        )
    }

    companion object {
        fun fromEntity(entity: Wish): WishModel {
            return WishModel(
                id = entity.id,
                url = entity.url,
                title = entity.title,
                currentPrice = entity.currentPrice,
                params = Params(
                    targetPrice = entity.targetPrice,
                    initialPrice = entity.initialPrice,
                    icon = entity.icon,
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

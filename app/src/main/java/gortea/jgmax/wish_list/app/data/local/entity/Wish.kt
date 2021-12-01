package gortea.jgmax.wish_list.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel

@Entity
data class Wish(
    @PrimaryKey val url: String,
    val title: String,
    val currentPrice: Long,
    val targetPrice: Long,
    val notificationFrequency: Int,
    val priceLeft: Int,
    val priceTop: Int,
    val priceRight: Int,
    val priceBottom: Int
) {
    companion object {
        fun fromModel(model: WishModel): Wish {
            return Wish(
                url = model.params.url,
                title = model.title,
                currentPrice = model.currentPrice,
                targetPrice = model.params.targetPrice,
                notificationFrequency = model.params.notificationFrequency,
                priceLeft = model.params.pricePosition.left,
                priceTop = model.params.pricePosition.top,
                priceRight = model.params.pricePosition.right,
                priceBottom = model.params.pricePosition.bottom
            )
        }
    }
}

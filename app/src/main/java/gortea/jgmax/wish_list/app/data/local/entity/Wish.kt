package gortea.jgmax.wish_list.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel

@Entity
data class Wish(
    @PrimaryKey val url: String,
    val title: String,
    val imageUrl: String,
    val group: String,
    val currentPrice: Long,
    val targetPrice: Long,
    val notificationFrequency: Int
) {
    companion object {
        fun fromModel(model: WishModel): Wish {
            return Wish(
                url = model.params.url,
                title = model.title,
                imageUrl = model.imageUrl,
                group = model.group,
                currentPrice = model.currentPrice,
                targetPrice = model.params.targetPrice,
                notificationFrequency = model.params.notificationFrequency
            )
        }
    }
}

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
    val initialPrice: Long,
    val notificationFrequency: Int,
    val priceLeft: Int,
    val priceTop: Int,
    val priceRight: Int,
    val priceBottom: Int
)

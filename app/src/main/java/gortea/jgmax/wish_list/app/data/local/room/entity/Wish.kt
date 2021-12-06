package gortea.jgmax.wish_list.app.data.local.room.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Wish(
    @PrimaryKey val url: String,
    val title: String,
    val currentPrice: Long,
    val targetPrice: Long,
    val initialPrice: Long,
    val notificationFrequency: Int,
    val icon: Bitmap?,
    val priceLeft: Int,
    val priceTop: Int,
    val priceRight: Int,
    val priceBottom: Int
)

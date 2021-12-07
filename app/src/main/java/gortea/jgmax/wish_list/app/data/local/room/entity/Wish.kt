package gortea.jgmax.wish_list.app.data.local.room.entity

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Wish(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val url: String,
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
) {
    companion object {
        const val DEFAULT_ID = 0L
    }
}

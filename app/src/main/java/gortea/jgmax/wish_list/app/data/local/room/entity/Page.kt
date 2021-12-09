package gortea.jgmax.wish_list.app.data.local.room.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Page(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val bitmap: Bitmap?,
    val url: String
)

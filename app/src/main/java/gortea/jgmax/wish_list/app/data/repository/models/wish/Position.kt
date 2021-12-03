package gortea.jgmax.wish_list.app.data.repository.models.wish

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Position(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) : Parcelable

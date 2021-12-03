package gortea.jgmax.wish_list.screens.select_data_zone.data

import android.os.Parcelable
import gortea.jgmax.wish_list.app.data.repository.models.wish.Position
import kotlinx.parcelize.Parcelize

@Parcelize
data class Result(
    val value: String,
    val position: Position
) : Parcelable

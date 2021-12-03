package gortea.jgmax.wish_list.features.select_data_zone.action

import android.graphics.Bitmap
import androidx.annotation.StringRes
import gortea.jgmax.wish_list.app.data.repository.models.wish.Position
import gortea.jgmax.wish_list.mvi.domain.Action

sealed class SelectDataZoneAction : Action {
    class ReturnValue(
        val value: String,
        val position: Position
    ) : SelectDataZoneAction()

    object LoadingFailed : SelectDataZoneAction()
    class RenderBitmap(val bitmap: Bitmap) : SelectDataZoneAction()

    object SelectionIsNotFull : SelectDataZoneAction()
}
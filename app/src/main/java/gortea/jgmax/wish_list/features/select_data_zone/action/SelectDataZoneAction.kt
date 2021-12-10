package gortea.jgmax.wish_list.features.select_data_zone.action

import android.graphics.Bitmap
import androidx.annotation.StringRes
import gortea.jgmax.wish_list.app.data.repository.models.wish.Position
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.domain.Action

sealed class SelectDataZoneAction : Action {
    object SelectionIsNotFull : SelectDataZoneAction()
    object LoadingFailed : SelectDataZoneAction()
    object UnknownWish : SelectDataZoneAction()
    object RecognitionFailed : SelectDataZoneAction()
    object WishUpdated : SelectDataZoneAction()

    class ReturnValue(
        val value: String,
        val position: Position
    ) : SelectDataZoneAction()
    class RenderBitmap(val bitmap: Bitmap) : SelectDataZoneAction()
    class RecognitionResult(val result: String) : SelectDataZoneAction()
    class ReturnWish(val wishModel: WishModel) : SelectDataZoneAction()
}

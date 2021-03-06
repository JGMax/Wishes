package gortea.jgmax.wish_list.features.select_data_zone.event

import android.graphics.Bitmap
import gortea.jgmax.wish_list.app.data.repository.models.wish.Position
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.domain.Event
import gortea.jgmax.wish_list.screens.select_data_zone.event.SelectDataViewEvent

sealed class SelectDataZoneEvent : Event {
    // View Events
    class LoadUrl(val url: String) : SelectDataZoneEvent()
    class ReloadUrl(val url: String) : SelectDataZoneEvent()
    class RecognizeText(val bitmap: Bitmap) : SelectDataZoneEvent()
    class AcceptSelection(val value: String?, val position: Position?) : SelectDataZoneEvent()
    class GetWish(val url: String) : SelectDataZoneEvent()
    class UpdateWish(val wishModel: WishModel) : SelectDataZoneEvent()

    // Side Events
    object WishUpdated : SelectDataZoneEvent()
    object RecognitionInProcess : SelectDataZoneEvent()
    object RecognitionFailed : SelectDataZoneEvent()
    object Loading : SelectDataZoneEvent()
    object LoadingFailed : SelectDataZoneEvent()
    object UnknownWish : SelectDataZoneEvent()

    class RecognitionSucceed(val text: String) : SelectDataZoneEvent()
    class LoadingInProgress(val progress: Int) : SelectDataZoneEvent()
    class BitmapLoaded(val url: String, val bitmap: Bitmap) : SelectDataZoneEvent()
    class ReturnWish(val wishModel: WishModel) : SelectDataZoneEvent()
}

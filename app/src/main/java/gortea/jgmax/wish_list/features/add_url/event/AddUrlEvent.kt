package gortea.jgmax.wish_list.features.add_url.event

import android.graphics.Bitmap
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.domain.Event

sealed class AddUrlEvent : Event {
    // View Events
    class AddUrl(val url: String) : AddUrlEvent()
    class RecognizeText(val bitmap: Bitmap) : AddUrlEvent()
    class AddWish(val model: WishModel) : AddUrlEvent()

    // Side Events
    object UrlAlreadyAdded : AddUrlEvent()
    object RecognitionInProcess : AddUrlEvent()
    object RecognitionFailed : AddUrlEvent()

    class AddNewUrl(val url: String) : AddUrlEvent()
    class CheckUrl(val url: String) : AddUrlEvent()
    class RecognitionSucceed(val text: String) : AddUrlEvent()
    class LoadingInProgress(val url: String, val progress: Int) : AddUrlEvent()
    class LoadingFailed(val url: String) : AddUrlEvent()
    class LoadingStarted(val url: String) : AddUrlEvent()
    class BitmapLoaded(val url: String, val bitmap: Bitmap) : AddUrlEvent()
}
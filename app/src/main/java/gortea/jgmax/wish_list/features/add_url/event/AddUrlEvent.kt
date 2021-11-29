package gortea.jgmax.wish_list.features.add_url.event

import android.graphics.Bitmap
import gortea.jgmax.wish_list.features.add_url.params.ProductParams
import gortea.jgmax.wish_list.features.add_url.params.ProductParsingResult
import gortea.jgmax.wish_list.mvi.domain.Event

sealed class AddUrlEvent : Event {
    // View Events
    class AddUrl(val url: String) : AddUrlEvent()
    class AddParams(val params: ProductParams) : AddUrlEvent()
    class ParseUrl(val url: String, val html: String) : AddUrlEvent()

    // Side Events
    object UrlAlreadyAdded : AddUrlEvent()
    object ParsingFailed : AddUrlEvent()



    class LoadingInProgress(val url: String, val progress: Int) : AddUrlEvent()
    class LoadingFailed(val url: String) : AddUrlEvent()
    class LoadingStarted(val url: String) : AddUrlEvent()
    class BitmapLoaded(val url: String, val bitmap: Bitmap) : AddUrlEvent()
    class ParseHtml(
        val url: String,
        val html: String,
        val priceSelector: String,
        val titleSelector: String,
        val imageSelector: String
    ) : AddUrlEvent()

    class ParsingSucceeded(val parsingResult: ProductParsingResult) : AddUrlEvent()
}
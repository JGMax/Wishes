package gortea.jgmax.wish_list.screens.add_url.event

import android.graphics.Bitmap
import gortea.jgmax.wish_list.mvi.view.ViewEvent
import gortea.jgmax.wish_list.screens.add_url.view.SelectableImageView

sealed class AddUrlViewEvent : ViewEvent {
    class AddUrl(val url: String) : AddUrlViewEvent()
    class RecognizeText(val bitmap: Bitmap, val position: SelectableImageView.SelectedPosition) :
        AddUrlViewEvent()

    class AddWish(val url: String) : AddUrlViewEvent()
}
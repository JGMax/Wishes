package gortea.jgmax.wish_list.screens.select_data_zone.event

import android.graphics.Bitmap
import gortea.jgmax.wish_list.mvi.view.ViewEvent
import gortea.jgmax.wish_list.screens.select_data_zone.view.SelectableImageView

sealed class SelectDataViewEvent : ViewEvent {
    object LoadUrl : SelectDataViewEvent()
    object UrlIsNull : SelectDataViewEvent()
    object OnFABClick : SelectDataViewEvent()
    object OnBackPressed : SelectDataViewEvent()
    object ReloadUrl : SelectDataViewEvent()

    class RecognizeText(
        val bitmap: Bitmap,
        val position: SelectableImageView.SelectedPosition
    ) : SelectDataViewEvent()
}

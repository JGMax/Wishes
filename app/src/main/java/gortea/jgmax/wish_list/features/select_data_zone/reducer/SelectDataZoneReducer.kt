package gortea.jgmax.wish_list.features.select_data_zone.reducer

import gortea.jgmax.wish_list.features.select_data_zone.action.SelectDataZoneAction
import gortea.jgmax.wish_list.features.select_data_zone.event.SelectDataZoneEvent
import gortea.jgmax.wish_list.features.select_data_zone.state.SelectDataZoneState
import gortea.jgmax.wish_list.mvi.domain.Reducer

class SelectDataZoneReducer :
    Reducer<SelectDataZoneState, SelectDataZoneEvent, SelectDataZoneAction> {
    override fun reduce(
        event: SelectDataZoneEvent,
        state: SelectDataZoneState
    ): Pair<SelectDataZoneState?, SelectDataZoneAction?> {
        var newAction: SelectDataZoneAction? = null
        var newState: SelectDataZoneState? = null
        when (event) {
            is SelectDataZoneEvent.WishUpdated -> {
                newAction = SelectDataZoneAction.WishUpdated
            }
            is SelectDataZoneEvent.ReturnWish -> {
                newAction = SelectDataZoneAction.ReturnWish(event.wishModel)
            }
            is SelectDataZoneEvent.UnknownWish -> {
                newAction = SelectDataZoneAction.UnknownWish
            }
            is SelectDataZoneEvent.BitmapLoaded -> {
                newState = state.copy(
                    isLoading = false,
                    isLoadingFailed = false,
                    loadingProgress = 100,
                    pageBitmap = event.bitmap,
                    pageUrl = event.url
                )
                newAction = SelectDataZoneAction.RenderBitmap(event.bitmap)
            }
            is SelectDataZoneEvent.Loading -> {
                newState = state.copy(
                    isLoading = true,
                    isLoadingFailed = false,
                    recognitionResult = null
                )
            }
            is SelectDataZoneEvent.LoadingFailed -> {
                newState = state.copy(
                    isLoading = false,
                    isLoadingFailed = true,
                    loadingProgress = 100,
                    pageBitmap = null
                )
                newAction = SelectDataZoneAction.LoadingFailed
            }
            is SelectDataZoneEvent.LoadingInProgress -> {
                newState = state.copy(
                    isLoading = true,
                    loadingProgress = event.progress
                )
            }
            is SelectDataZoneEvent.RecognitionSucceed -> {
                newState = state.copy(
                    recognitionInProcess = false,
                    recognitionResult = event.text
                )
                newAction = SelectDataZoneAction.RecognitionResult(event.text)
            }
            is SelectDataZoneEvent.RecognitionInProcess -> {
                newState = state.copy(
                    recognitionInProcess = true
                )
            }
            is SelectDataZoneEvent.RecognitionFailed -> {
                newState = state.copy(
                    recognitionInProcess = false,
                    recognitionResult = null
                )
                newAction = SelectDataZoneAction.RecognitionFailed
            }
            is SelectDataZoneEvent.AcceptSelection -> {
                newAction = if (event.value?.toLongOrNull() != null && event.position != null) {
                    SelectDataZoneAction.ReturnValue(event.value, event.position)
                } else {
                    SelectDataZoneAction.SelectionIsNotFull
                }
            }
        }
        return newState to newAction
    }
}
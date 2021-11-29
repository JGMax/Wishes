package gortea.jgmax.wish_list.features.add_url.reducer

import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.features.add_url.action.AddUrlAction
import gortea.jgmax.wish_list.features.add_url.event.AddUrlEvent
import gortea.jgmax.wish_list.features.add_url.state.AddUrlState
import gortea.jgmax.wish_list.mvi.domain.Reducer

class AddUrlReducer : Reducer<AddUrlState, AddUrlEvent, AddUrlAction> {
    override fun reduce(event: AddUrlEvent, state: AddUrlState): Pair<AddUrlState?, AddUrlAction?> {
        var newAction: AddUrlAction? = null
        var newState: AddUrlState? = null
        when (event) {
            is AddUrlEvent.BitmapLoaded -> {
                newState = state.copy(
                    isLoading = false,
                    isLoadingFailed = false,
                    loadingProgress = 100,
                    pageBitmap = event.bitmap,
                    pageUrl = event.url
                )
                newAction = AddUrlAction.RenderBitmap(event.bitmap)
            }
            is AddUrlEvent.LoadingStarted -> {
                newState = state.copy(
                    isLoading = true,
                    isLoadingFailed = false,
                    loadingProgress = 0,
                    pageBitmap = null,
                    pageUrl = event.url
                )
            }
            is AddUrlEvent.LoadingFailed -> {
                newState = state.copy(
                    isLoading = false,
                    isLoadingFailed = true,
                    loadingProgress = 100,
                    pageBitmap = null,
                    pageUrl = event.url
                )
                newAction = AddUrlAction.ShowError(R.string.loading_failed)
            }
            is AddUrlEvent.LoadingInProgress -> {
                newState = state.copy(
                    isLoading = true,
                    loadingProgress = event.progress,
                    pageUrl = event.url
                )
            }
        }
        return newState to newAction
    }
}
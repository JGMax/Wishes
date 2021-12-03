package gortea.jgmax.wish_list.features.add_wish.reducer

import gortea.jgmax.wish_list.features.add_wish.action.AddWishAction
import gortea.jgmax.wish_list.features.add_wish.event.AddWishEvent
import gortea.jgmax.wish_list.features.add_wish.state.AddWishState
import gortea.jgmax.wish_list.mvi.domain.Reducer

class AddWishReducer : Reducer<AddWishState, AddWishEvent, AddWishAction> {
    override fun reduce(
        event: AddWishEvent,
        state: AddWishState
    ): Pair<AddWishState?, AddWishAction?> {
        var newState: AddWishState? = null
        var newAction: AddWishAction? = null
        when (event) {
            is AddWishEvent.CheckIncorrectUrl -> {
                newState = state.copy(
                    isLoading = false,
                    url = event.url,
                    isUrlAccepted = false,
                    wish = null,
                    isWishAdded = null
                )
                newAction = AddWishAction.IncorrectUrl
            }
            is AddWishEvent.CheckUrlAlreadyAdded -> {
                newState = state.copy(
                    isLoading = false,
                    url = event.url,
                    isUrlAccepted = false,
                    wish = null,
                    isWishAdded = null
                )
                newAction = AddWishAction.UrlAlreadyAdded
            }
            is AddWishEvent.LoadUrl -> {
                newState = state.copy(
                    url = event.url,
                    isUrlAccepted = true
                )
            }
            is AddWishEvent.Loading -> {
                newState = state.copy(
                    isLoading = true
                )
            }
            is AddWishEvent.LoadingInProcess -> {
                newState = state.copy(
                    isLoading = true,
                    loadingProgress = event.progress
                )
            }
            is AddWishEvent.LoadingUrlSuccess -> {
                newState = state.copy(
                    isLoading = false,
                    url = event.url
                )
                newAction = AddWishAction.UrlIsLoaded
            }
            is AddWishEvent.LoadingFailed -> {
                newState = state.copy(
                    isLoading = false,
                    isWishAdded = null
                )
                newAction = AddWishAction.LoadingUrlFailed
            }
            is AddWishEvent.CheckWishFailed -> {
                newState = state.copy(
                    isLoading = false,
                    wish = event.wishModel,
                    isWishAdded = false
                )
                newAction = AddWishAction.WishCheckFailed
            }
            is AddWishEvent.AddSuccessful -> {
                newState = state.copy(
                    isLoading = false,
                    wish = event.wishModel,
                    isWishAdded = true
                )
                newAction = AddWishAction.WishSuccessfulAdded
            }
        }
        return newState to newAction
    }
}
package gortea.jgmax.wish_list.features.wish_list.reducer

import gortea.jgmax.wish_list.features.wish_list.action.WishListAction
import gortea.jgmax.wish_list.features.wish_list.event.WishListEvent
import gortea.jgmax.wish_list.features.wish_list.state.WishListState
import gortea.jgmax.wish_list.mvi.domain.Reducer

class WishListReducer : Reducer<WishListState, WishListEvent, WishListAction> {
    override fun reduce(
        event: WishListEvent,
        state: WishListState
    ): Pair<WishListState?, WishListAction?> {
        var newState: WishListState? = null
        var newAction: WishListAction? = null

        when (event) {
            is WishListEvent.ReturnFlow -> {
                newState = state.copy(
                    isLoading = false
                )
                newAction = WishListAction.CollectList(event.list)
            }
            is WishListEvent.Loading -> {
                newState = state.copy(
                    isLoading = true
                )
            }
            is WishListEvent.WishRemoved -> {
                newAction = WishListAction.ItemDeleted(event.wishModel)
            }
            is WishListEvent.EnqueueWork -> {
                newAction = WishListAction.EnqueueWorkRequest(event.request)
            }
        }
        return newState to newAction
    }
}

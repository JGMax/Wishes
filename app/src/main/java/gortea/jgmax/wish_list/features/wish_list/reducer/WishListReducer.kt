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
            is WishListEvent.ReturnList -> {
                newState = state.copy(
                    isLoading = false,
                    list = event.list.toList()
                )
            }
            is WishListEvent.Loading -> {
                newState = state.copy(
                    isLoading = true
                )
            }
        }
        return newState to newAction
    }
}
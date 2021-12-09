package gortea.jgmax.wish_list.features.wish_list.state

import gortea.jgmax.wish_list.mvi.domain.State

data class WishListState(
    val isLoading: Boolean
) : State {
    companion object {
        val Default = WishListState(
            isLoading = false
        )
    }
}

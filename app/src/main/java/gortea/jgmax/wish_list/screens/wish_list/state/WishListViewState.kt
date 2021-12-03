package gortea.jgmax.wish_list.screens.wish_list.state

import gortea.jgmax.wish_list.mvi.view.ViewState
import gortea.jgmax.wish_list.screens.wish_list.data.WishData


data class WishListViewState(
    val isLoading: Boolean,
    val list: List<WishData>
) : ViewState {
    companion object {
        val Default = WishListViewState(
            isLoading = false,
            list = listOf()
        )
    }
}

package gortea.jgmax.wish_list.features.wish_list.state

import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.domain.State

data class WishListState(
    val isLoading: Boolean,
    val list: List<WishModel>
) : State {
    companion object {
        val Default = WishListState(
            isLoading = true,
            list = listOf()
        )
    }
}

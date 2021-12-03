package gortea.jgmax.wish_list.features.add_wish.state

import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.domain.State

data class AddWishState(
    val isLoading: Boolean,
    val loadingProgress: Int,
    val url: String?,
    val isUrlAccepted: Boolean?,
    val wish: WishModel?,
    val isWishAdded: Boolean?
) : State {
    companion object {
        val Default = AddWishState(
            isLoading = false,
            loadingProgress = 0,
            url = null,
            isUrlAccepted = null,
            wish = null,
            isWishAdded = null
        )
    }
}

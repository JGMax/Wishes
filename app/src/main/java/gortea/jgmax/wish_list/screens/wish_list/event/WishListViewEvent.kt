package gortea.jgmax.wish_list.screens.wish_list.event

import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.view.ViewEvent

sealed class WishListViewEvent : ViewEvent {
    object GetListFlow : WishListViewEvent()
    object OnAddWishClick : WishListViewEvent()
    object RefreshList : WishListViewEvent()

    class AddItem(val wish: WishModel) : WishListViewEvent()
    class OnItemWishClick(val url: String) : WishListViewEvent()
    class OnDeleteWishClick(val url: String) : WishListViewEvent()
    class OnUpdateWishClick(val url: String) : WishListViewEvent()
}

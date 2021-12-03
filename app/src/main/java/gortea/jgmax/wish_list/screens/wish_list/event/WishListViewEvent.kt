package gortea.jgmax.wish_list.screens.wish_list.event

import gortea.jgmax.wish_list.mvi.view.ViewEvent

sealed class WishListViewEvent : ViewEvent {
    object GetList : WishListViewEvent()
    object OnSettingsClick : WishListViewEvent()
    object OnAddWishClick : WishListViewEvent()

    class OnItemWishClick(val url: String) : WishListViewEvent()
    class OnDeleteWishClick(val url: String) : WishListViewEvent()
    class OnUpdateWishClick(val url: String) : WishListViewEvent()
}

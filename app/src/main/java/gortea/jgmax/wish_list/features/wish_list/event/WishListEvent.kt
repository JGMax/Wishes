package gortea.jgmax.wish_list.features.wish_list.event

import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.domain.Event

sealed class WishListEvent : Event {
    // View Events
    object GetList : WishListEvent()

    class RemoveWish(val url: String) : WishListEvent()

    // Side Events
    object Loading : WishListEvent()

    class ReturnList(val list: List<WishModel>) : WishListEvent()
}

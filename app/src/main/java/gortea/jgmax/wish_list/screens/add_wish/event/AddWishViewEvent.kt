package gortea.jgmax.wish_list.screens.add_wish.event

import gortea.jgmax.wish_list.mvi.view.ViewEvent

sealed class AddWishViewEvent : ViewEvent {
    object OnPriceSelectionClick : AddWishViewEvent()
    object OnAcceptClick : AddWishViewEvent()
    object LoadUrl : AddWishViewEvent()
}

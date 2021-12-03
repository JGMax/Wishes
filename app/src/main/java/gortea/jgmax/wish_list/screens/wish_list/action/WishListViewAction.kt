package gortea.jgmax.wish_list.screens.wish_list.action

import gortea.jgmax.wish_list.mvi.view.ViewAction

sealed class WishListViewAction : ViewAction {
    class OpenUrlInBrowser(val url: String) : WishListViewAction()
}

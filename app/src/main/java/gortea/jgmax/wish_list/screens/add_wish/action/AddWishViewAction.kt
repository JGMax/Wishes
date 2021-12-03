package gortea.jgmax.wish_list.screens.add_wish.action

import androidx.annotation.StringRes
import gortea.jgmax.wish_list.mvi.view.ViewAction

sealed class AddWishViewAction : ViewAction {
    class ShowMessage(@StringRes val message: Int) : AddWishViewAction()
}

package gortea.jgmax.wish_list.screens.add_url.action

import androidx.annotation.StringRes
import gortea.jgmax.wish_list.mvi.view.ViewAction

sealed class AddUrlViewAction : ViewAction {
    class ShowError(@StringRes val message: Int) : AddUrlViewAction()
}
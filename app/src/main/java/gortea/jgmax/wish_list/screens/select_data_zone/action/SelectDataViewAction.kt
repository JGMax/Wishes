package gortea.jgmax.wish_list.screens.select_data_zone.action

import androidx.annotation.StringRes
import gortea.jgmax.wish_list.app.data.repository.models.wish.Position
import gortea.jgmax.wish_list.mvi.view.ViewAction

sealed class SelectDataViewAction : ViewAction {
    class ShowMessage(@StringRes val message: Int) : SelectDataViewAction()
}
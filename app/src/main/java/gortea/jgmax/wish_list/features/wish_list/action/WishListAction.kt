package gortea.jgmax.wish_list.features.wish_list.action

import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.mvi.domain.Action

sealed class WishListAction : Action {
    class ItemDeleted(val wish: WishModel) : WishListAction()
}
